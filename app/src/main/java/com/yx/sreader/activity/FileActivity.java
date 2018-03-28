package com.yx.sreader.activity;

import android.Manifest;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.yx.sreader.R;
import com.yx.sreader.util.FileUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
    private static Stack<String> pathStack;
    protected List<AsyncTask<Void, Void, Boolean>> myAsyncTasks = new ArrayList<AsyncTask<Void, Void, Boolean>>();
    private List<File> listFile;
    private static HashMap<Integer,Boolean> isSelected;
    public static ArrayList<String> paths = new ArrayList<String>();






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

    }

    private void initView() {
        listView = (ListView) findViewById(R.id.local_File_drawer);
        //adapter = new FileAdapter(this, listFile, isSelected);
        //listView.setAdapter(adapter);
        //listView.setOnItemClickListener(new DrawerItemClickListener());
        //listView.setOnItemLongClickListener(new DrawerItemClickListener());//
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
                    //adapter.setFiles(listFile);  //list值传到adapter
                    isSelected = new HashMap<Integer, Boolean>();//异步线程后checkBox初始赋值
                    for (int i = 0; i < listFile.size(); i++) {
                        isSelected.put(i, false);
                    }
                    //adapter.setSelectedPosition(-1);
                    addfileButton.setText("加入书架(" + 0 + ")项");//异步线程后重新执行初始化
                    //adapter.notifyDataSetChanged();
                }
                else {
                    Toast.makeText(FileActivity.this,"查询失败",Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void addPath(String path) {
        if (pathStack == null) {
            pathStack = new Stack<String>();
        }
        pathStack.add(path);
    }


    private void checkPermission(FileActivity fileActivity, String permission, int requestCode) {

    }


}
