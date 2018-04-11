package com.yx.sreader.activity;

import android.Manifest;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.yx.sreader.R;
import com.yx.sreader.adapter.FileAdapter;
import com.yx.sreader.database.BookList;
import com.yx.sreader.util.FileUtil;

import org.litepal.crud.DataSupport;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

/**
 * Created by iss on 2018/3/27.
 */

public class FileActivity extends AppCompatActivity {
    public static final int EXTERNAL_STORAGE_REQ_CODE = 10 ;
    private static Button chooseAllButton, deleteButton, addfileButton;
    private File root;
    private ListView listView;
    private ImageButton returnBtn;
    private TextView titleView;
    private static Stack<String> pathStack; //path的堆栈，开始是主目录的绝对路径，随着点击目录层级进行动态装入删除
    protected List<AsyncTask<Void, Void, Boolean>> myAsyncTasks = new ArrayList<AsyncTask<Void, Void, Boolean>>();
    private List<File> listFile;
    private static HashMap<Integer,Boolean> isSelected;  //标志是否复选
    public static ArrayList<String> paths = new ArrayList<String>();  //后缀为.txt的文件集合
    public static int checkNum = 0; // 记录选中的条目数量
    private static FileAdapter adapter;
    public static Map<String,Integer> mapin = new HashMap<String, Integer>() ;//.txt文件paths和总体位置的对应集合








    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_main);
        Toolbar toolbar = (Toolbar)this.findViewById(R.id.toolbar_file);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.mipmap.return_button);
        getSupportActionBar().setTitle("导入图书");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE, EXTERNAL_STORAGE_REQ_CODE);
        }
        root = Environment.getExternalStorageDirectory();
        initView();
        handledButtonListener();


    }

    private void initView() {
        listView = (ListView) findViewById(R.id.local_File_drawer);
        adapter = new FileAdapter(this, listFile, isSelected);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new DrawerItemClickListener());
        listView.setOnItemLongClickListener(new DrawerItemClickListener());
        returnBtn = (ImageButton) findViewById(R.id.local_File_return_btn);
        titleView = (TextView) findViewById(R.id.local_File_title);
        chooseAllButton = (Button) findViewById(R.id.choose_all);
        deleteButton = (Button) findViewById(R.id.delete);
        addfileButton = (Button) findViewById(R.id.add_file);
        searchData(root.getAbsolutePath());
        addPath(root.getAbsolutePath());
    }


    private void searchData(String path) {
        searchViewData(path);
        titleView.setText(path);
    }

    public void putAsyncTask(AsyncTask<Void, Void, Boolean> asyncTask) {
        myAsyncTasks.add(asyncTask.execute());
    }

    private void searchViewData(final String path) {
        putAsyncTask(new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... params) {
                try {
                    listFile = FileUtil.getFileListByPath(path);
                }catch (Exception e){
                    return null;
                }
                return true;
            }
            @Override
            protected void onPostExecute(Boolean result){
                super.onPostExecute(result);
                if(result){
                    adapter.setFiles(listFile);  //list值传到adapter
                    isSelected = new HashMap<Integer, Boolean>();//异步线程后checkBox初始赋值
                    for (int i = 0; i < listFile.size(); i++) {
                        isSelected.put(i, false);
                    }
                    //adapter.setSelectedPosition(-1);
                    addfileButton.setText("加入书架(" + 0 + ")项");//异步线程后重新执行初始化
                    adapter.notifyDataSetChanged();
                }
                else {
                    Toast.makeText(FileActivity.this,"查询失败",Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public static HashMap<Integer,Boolean> getIsSelected() {
        return isSelected;
    }

    public static void dataChanged() {
        // 通知listView刷新
        adapter.notifyDataSetChanged();
        // TextView显示最新的选中数目
        addfileButton.setText("加入书架("+ checkNum + ")项");

    }

    private void handledButtonListener() {
        /**
         * 返回上一文件层
         * */
        returnBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getLastPath().equals(root.getAbsolutePath())) {
                    return;
                }
                FileUtil.folderNum = 0;
                FileActivity.paths.clear();
                mapin.clear();
                removeLastPath();//从栈中移除当前路径，得到上一文件层路径
                searchData(getLastPath());
            }
        });
        /**
         * 全选
         * */
        chooseAllButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int num = FileUtil.getFileNum(listFile);
                int j = listFile.size()-num;  //获得选择时的position
                for ( int k = j ; k < listFile.size() ; k++) {
                    FileActivity.getIsSelected().put(k, true);

                    if(!mapin.containsKey(paths.get(k-j))) {
                        mapin.put(paths.get(k-j),k);
                    }
                }
                checkNum = FileAdapter.CheckNum(FileActivity.getIsSelected());
                // 刷新listview和TextView的显示
                dataChanged();
            }
        });
        /**
         * 取消选择
         * */
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < listFile.size(); i++) {
                    FileActivity.getIsSelected().put(i, false);
                    checkNum = FileAdapter.CheckNum(FileActivity.getIsSelected());

                    mapin.clear();

                    dataChanged();
                }
            }
        });
        /**
         * 把已经选择的书加入书架
         * */
        addfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String, Integer> map = mapin;

                int addNum = FileAdapter.CheckNum(FileActivity.getIsSelected());
                if (addNum > 0) {
                    for (String key : map.keySet()) {
                        BookList bookList = new BookList();
                        File file = new File(key);
                        String bookName = FileUtil.getFileNameNoEx(file.getName());
                        bookList.setBookname(bookName);
                        bookList.setBookpath(key);
                        saveBooktoSqlite(bookName, key, bookList);//开启线程存储书到数据库
                    }
                }
            }
        });
    }

    private class DrawerItemClickListener implements
            ListView.OnItemClickListener,ListView.OnItemLongClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {

            adapter.setSelectedPosition(position);

            selectItem(position);

        }

        @Override
        public boolean  onItemLongClick (AdapterView<?> parent, View view, int position,
                                         long id) {

            return true;
        }

    }
    private void selectItem(int position) {
        BookList bookList = new BookList();
        String filePath = adapter.getFiles().get(position).getAbsolutePath();
        String fileName = adapter.getFiles().get(position).getName();

        if (adapter.getFiles().get(position).isDirectory()) {
            FileUtil.folderNum = 0;
            mapin.clear();
            FileActivity.paths.clear();
            searchData(filePath);
            addPath(filePath);
        } else if (adapter.getFiles().get(position).isFile()) {
            String sql = "SELECT id FROM booklist WHERE bookname =? ";
            Cursor cursor = DataSupport.findBySQL(sql, fileName);
            if (!cursor.moveToFirst()) {
                bookList.setBookname(fileName);
                bookList.setBookpath(filePath);
                bookList.save();//如果没有添加到数据库则添加到数据库
                Intent intent = new Intent();
                intent.setClass(FileActivity.this, ReadActivity.class);
                intent.putExtra("bookpath", filePath);
                intent.putExtra("bookname",fileName);
                startActivity(intent);
            }else {
                Intent intent = new Intent();
                intent.setClass(FileActivity.this, ReadActivity.class);
                intent.putExtra("bookpath", filePath);
                intent.putExtra("bookname",fileName);
                startActivity(intent);
            }
        }
    }

    public void saveBooktoSqlite (final String bookName,final String key,final BookList bookList ) {

        putAsyncTask(new AsyncTask<Void, Void, Boolean>() {

            @Override
            protected void onPreExecute() {
                //可以进行界面上的初始化操作
            }

            @Override
            protected Boolean doInBackground(Void... params) {

                try {
                    String sql = "SELECT id FROM booklist WHERE bookname =? and bookpath =?";
                    Cursor cursor = DataSupport.findBySQL(sql, bookName, key);
                    if (!cursor.moveToFirst()) { //This method will return false if the cursor is empty
                        bookList.save();
                    } else {
                        return false;
                    }

                } catch (Exception e) {
                    //  return false;
                }
                return true;
            }

            @Override
            protected void onPostExecute(Boolean result) {
                if (result) {
                } else {
                    Toast.makeText(getApplicationContext(), bookName+"已在书架了", Toast.LENGTH_SHORT).show();
                }
            }

        });
    }

    /**
     * 添加路径到堆栈
     *
     * @param path
     */
    public void addPath(String path) {

        if (pathStack == null) {
            pathStack = new Stack<String>();
        }
        pathStack.add(path);
    }

    /**
     * 获取堆栈最上层的路径
     *
     * @return
     */
    public String getLastPath() {
        return pathStack.lastElement();
    }

    /**
     * 移除堆栈最上层路径
     */
    public void removeLastPath() {
        pathStack.remove(getLastPath());
    }

    private void checkPermission(FileActivity fileActivity, String permission, int requestCode) {

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        if (id==android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);//Navigate Up to Parent Activity
            // Log.d("FileActivity", "home");
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


}
