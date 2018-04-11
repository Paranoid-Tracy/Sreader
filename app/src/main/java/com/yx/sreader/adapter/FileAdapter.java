package com.yx.sreader.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yx.sreader.R;
import com.yx.sreader.activity.FileActivity;
import com.yx.sreader.util.FileUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by iss on 2018/3/28.
 */

public class FileAdapter extends BaseAdapter {
    private List<File> files;
    private Context context;
    private HashMap<Integer,Boolean> IsSelected;
    private int selectedPosition = -1;



    public FileAdapter(Context context, List<File> files, HashMap<Integer,Boolean> IsSelected){
        this.context = context;
        this.files = files;
        this.IsSelected = IsSelected;
    }
    @Override
    public int getCount() {
        if(files == null) {
            return 0;
        }
        return files.size();
    }

    @Override
    public Object getItem(int position) {
        return files.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if(convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.activity_file_item,null);
            viewHolder = new ViewHolder();
            viewHolder.textView = (TextView) convertView
                    .findViewById(R.id.local_file_text);
            viewHolder.textSize = (TextView) convertView
                    .findViewById(R.id.local_file_text_size);
            viewHolder.fileIcon = (ImageView) convertView
                    .findViewById(R.id.local_file_icon);
            viewHolder.checkBox = (CheckBox) convertView
                    .findViewById(R.id.local_file_image);
            viewHolder.linearLayout = (LinearLayout) convertView
                    .findViewById(R.id.local_file_lin);
            convertView.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        if(selectedPosition == position){
            viewHolder.textView.setSelected(true);
        }
        else{
            viewHolder.textView.setSelected(false);
            viewHolder.linearLayout.setBackgroundColor(Color.TRANSPARENT);
        }
        viewHolder.textView.setText(files.get(position).getName());
        viewHolder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                FileActivity.getIsSelected().put(position,viewHolder.checkBox.isChecked());
                FileActivity.checkNum = FileAdapter.CheckNum(FileActivity.getIsSelected());
                String p =FileActivity.paths.get(position- FileUtil.folderNum);//减去文件夹的数量
                if (viewHolder.checkBox.isChecked() == true) {

                    if (!FileActivity.mapin.containsKey(p)) {

                        FileActivity.mapin.put(p,position-FileUtil.folderNum);
                    }

                }else if (viewHolder.checkBox.isChecked() == false) {

                    FileActivity.mapin.remove(p);
                }

                FileActivity.dataChanged();
                //  Log.d("FileAdapter",p + "");
            }

        });
        if (files.get(position).isDirectory()) {
            viewHolder.fileIcon.setImageResource(R.drawable.folder);
            //  viewHolder.fileImage.setImageResource(R.drawable.file_folder);
            viewHolder.checkBox.setVisibility(View.INVISIBLE);
            viewHolder.textSize.setText("项");

        } else {
            viewHolder.fileIcon.setImageResource(R.drawable.file_type_txt);
            viewHolder.checkBox.setVisibility(View.VISIBLE);
            viewHolder.checkBox.setChecked(FileActivity.getIsSelected().get(position));
            viewHolder.textSize.setText(FileUtil.formatFileSize(files.get(position).length()));

        }


        return convertView;
    }
    class ViewHolder {
        TextView textView;
        TextView textSize;
        ImageView fileIcon;
        CheckBox checkBox;
        LinearLayout linearLayout;
    }
    public static int CheckNum (HashMap<Integer,Boolean> isSelected ) {
        int i = 0 ;
        HashMap<Integer,Boolean> map = isSelected ;
        List<Boolean> isCheck = new ArrayList<>();
        //遍历Key Value
        for (Map.Entry<Integer, Boolean> entry : map.entrySet()) {
            //   System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());
            isCheck.add(entry.getValue());
        }
        //取出所有为true的数量
        for (int j =0 ; j <isCheck.size();j++) {
            if (isCheck.get(j)) {
                i++;
            }
        }
        return i;
    }
    public int getSelectedPosition() {
        return selectedPosition;
    }

    public void setSelectedPosition(int selectedPosition) {
        this.selectedPosition = selectedPosition;
    }

    public List<File> getFiles() {
        return files;
    }

    public void setFiles(List<File> files) {
        this.files = files;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }



}
