package com.yx.sreader.util;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.Log;

import com.yx.sreader.R;
import com.yx.sreader.activity.ReadActivity;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Vector;

/**
 * Created by iss on 2018/3/29.
 */

public class BookPageFactory {

    private String m_strCharsetName = "GBK";
    private static final String TAG = "BookPageFactory";



    private int m_textColor = Color.rgb(50, 65, 78);

    private static int m_mbBufLen = 0; // 图书总长度
    private MappedByteBuffer m_mbBuf = null;// 内存中的图书字符
    private int m_mbBufBegin = 0;// 当前页起始位置
    private int m_mbBufEnd = 0;// 当前页终点位置


    private int mWidth;
    private int mHeight;
    private Context mcontext;

    private int m_fontSize;
    private SimpleDateFormat sdf;
    private String date;
    private DecimalFormat df;

    private float mBorderWidth;
    private int marginHeight ; // 上下与边缘的距离
    private int marginWidth ; // 左右与边缘的距离


    private float mVisibleHeight; // 绘制内容的宽
    private float mVisibleWidth; // 绘制内容的宽
    private Intent batteryInfoIntent;
    private static Typeface typeface;
    private Paint mPaint;
    private Paint mBatterryPaint ;
    private int mLineCount; // 每页可以显示的行数
    private Bitmap m_book_bg = null;
    private File book_file = null;

    private StringBuilder word;
    private Vector<String> m_lines = new Vector<String>();
    private int m_backColor = 0xffff9e85; // 背景颜色








    public BookPageFactory(int w, int h,Context context) {
        mWidth = w;
        mHeight = h;
        mcontext = context;
        m_fontSize = (int) context.getResources().getDimension(R.dimen.reading_default_text_size);
        sdf = new SimpleDateFormat("HH:mm");//HH:mm为24小时制,hh:mm为12小时制
        date = sdf.format(new java.util.Date());
        df = new DecimalFormat("#0.0");
        mBorderWidth = context.getResources().getDimension(R.dimen.reading_board_battery_border_width);
        marginWidth = (int) context.getResources().getDimension(R.dimen.readingMarginWidth);
        marginHeight = (int) context.getResources().getDimension(R.dimen.readingMarginHeight);
        typeface = Typeface.createFromAsset(context.getAssets(), "font/QH.ttf");
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);// 画笔
        mPaint.setTextAlign(Paint.Align.LEFT);// 左对齐
        mPaint.setTextSize(m_fontSize);// 字体大小
        mPaint.setColor(m_textColor);// 字体颜色
        mPaint.setTypeface(typeface);
        mPaint.setSubpixelText(true);// 设置该项为true，将有助于文本在LCD屏幕上的显示效果
        mVisibleWidth = mWidth - marginWidth * 2;
        mVisibleHeight = mHeight - marginHeight * 2;
        mLineCount = (int) (mVisibleHeight / m_fontSize) - 1; // 可显示的行数,-1是因为底部显示进度的位置容易被遮住
        batteryInfoIntent = context.getApplicationContext().registerReceiver(null,
                new IntentFilter(Intent.ACTION_BATTERY_CHANGED)) ;//注册广播,随时获取到电池电量信息

        mBatterryPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBatterryPaint.setTextSize(CommonUtil.sp2px(context, 12));
        mBatterryPaint.setTypeface(typeface);
        mBatterryPaint.setTextAlign(Paint.Align.LEFT);
        mBatterryPaint.setColor(m_textColor);

        // Log.d("BookPageFactory","mBorderWidth"+mBorderWidth);
    }

    /**
     *
     * @param strFilePath
     * @param begin
     *            表示书签记录的位置，读取书签时，将begin值给m_mbBufEnd，在读取nextpage，及成功读取到了书签
     *            记录时将m_mbBufBegin开始位置作为书签记录
     *
     * @throws IOException
     */
    @SuppressWarnings("resource")
    public void openbook(String strFilePath, int begin) throws IOException {
        book_file = new File(strFilePath);
        long lLen = book_file.length();
        m_mbBufLen = (int) lLen;
        m_mbBuf = new RandomAccessFile(book_file, "r").getChannel().map(
                FileChannel.MapMode.READ_ONLY, 0, lLen);
        // Log.d(TAG, "total lenth：" + m_mbBufLen);
        // 设置已读进度
        if (begin >= 0) {
            m_mbBufBegin = begin;
            m_mbBufEnd = begin;
        } else {
        }
    }

    public void onDraw(Canvas c){
        word = new StringBuilder();
        int size = getM_fontSize();
        mPaint.setTextSize(size);
        mPaint.setColor(m_textColor);
        if(m_lines.size() == 0){
            m_lines = pageDown();
        }
        if(m_lines.size() > 0){
            if(m_book_bg == null){
                c.drawColor(m_backColor);
            }else{
                c.drawBitmap(m_book_bg, 0, 0, null);

            }
            int y = marginHeight;
            for(String strLine : m_lines){
                y += m_fontSize;
                c.drawText(strLine,marginWidth,y,mPaint);
                word.append(strLine);
            }
            ReadActivity.words=word.toString();
            word=null;
        }
    }

    protected Vector<String> pageDown() {
        mPaint.setTextSize(m_fontSize);
        mPaint.setColor(m_textColor);
        String strParagraph = "";
        Vector<String> lines = new Vector<String>();
        while (lines.size() < mLineCount && m_mbBufEnd < m_mbBufLen) {
            byte[] paraBuf = readParagraphForward(m_mbBufEnd);
            m_mbBufEnd += paraBuf.length;// 每次读取后，记录结束点位置，该位置是段落结束位置
            try {
                strParagraph = new String(paraBuf, m_strCharsetName);// 转换成制定GBK编码
            } catch (UnsupportedEncodingException e) {
                Log.e(TAG, "pageDown->转换编码失败", e);
            }
            String strReturn = "";
            // 替换掉回车换行符,防止段落发生错乱
            if (strParagraph.indexOf("\r\n") != -1) {   //windows
                strReturn = "\r\n";
                strParagraph = strParagraph.replaceAll("\r\n","");
            } else if (strParagraph.indexOf("\n") != -1) {    //linux
                strReturn = "\n";
                strParagraph = strParagraph.replaceAll("\n", "");
            }

            if (strParagraph.length() == 0) {
                lines.add(strParagraph);
            }
            while (strParagraph.length() > 0) {
                // 画一行文字
                int nSize = mPaint.breakText(strParagraph, true, mVisibleWidth,
                        null);
                lines.add(strParagraph.substring(0, nSize));
                strParagraph = strParagraph.substring(nSize);// 得到剩余的文字
                // 超出最大行数则不再画
                if (lines.size() >= mLineCount) {

                    break;
                }
            }
            //lines.add("\n\n");//段落间加一个空白行
            // 如果该页最后一段只显示了一部分，则重新定位结束点位置
            if (strParagraph.length() != 0) {
                try {
                    m_mbBufEnd -= (strParagraph + strReturn)
                            .getBytes(m_strCharsetName).length;
                } catch (UnsupportedEncodingException e) {
                    Log.e(TAG, "pageDown->记录结束点位置失败", e);
                }
            }
        }

        return lines;

    }

    protected byte[] readParagraphForward(int nFromPos) {
        int nStart = nFromPos;
        int i = nStart;
        byte b0, b1;
        // 根据编码格式判断换行
        if (m_strCharsetName.equals("UTF-16LE")) {
            while (i < m_mbBufLen - 1) {
                b0 = m_mbBuf.get(i++);
                b1 = m_mbBuf.get(i++);
                if (b0 == 0x0a && b1 == 0x00) {
                    break;
                }
            }
        } else if (m_strCharsetName.equals("UTF-16BE")) {
            while (i < m_mbBufLen - 1) {
                b0 = m_mbBuf.get(i++);
                b1 = m_mbBuf.get(i++);
                if (b0 == 0x00 && b1 == 0x0a) {
                    break;
                }
            }
        } else {
            while (i < m_mbBufLen) {
                b0 = m_mbBuf.get(i++);
                if (b0 == 0x0a) {
                    break;
                }
            }
        }
        int nParaSize = i - nStart; //段落长度
        byte[] buf = new byte[nParaSize];
        for (i = 0; i < nParaSize; i++) {
            buf[i] = m_mbBuf.get(nFromPos + i);
        }
        return buf;
    }

    public void setM_textColor(int m_textColor) {
        this.m_textColor = m_textColor;
    }
    public void setBgBitmap(Bitmap BG) {
        m_book_bg = BG;
    }
    public int getM_fontSize() {
        return this.m_fontSize; //2016.1.4
    }
}
