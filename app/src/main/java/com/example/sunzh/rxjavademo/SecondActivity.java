package com.example.sunzh.rxjavademo;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.Arrays;

import static android.os.Environment.DIRECTORY_PICTURES;

public class SecondActivity extends Activity implements View.OnClickListener {

    /**
     * 显示信息
     */
    private Button mBtnShowMsg;
    private TextView mTvMsg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        initView();

    }

    private void initView() {
        mBtnShowMsg = (Button) findViewById(R.id.btn_show_msg);
        mBtnShowMsg.setOnClickListener(this);
        mTvMsg = (TextView) findViewById(R.id.tv_msg);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_show_msg:
                StringBuilder sb = new StringBuilder();
                String path1 = getFilesDir().getAbsolutePath();
                sb.append("getFilesDir(): " + path1);
                String path2 = getCacheDir().getAbsolutePath();
                sb.append("\ngetCacheDir(): " + path2);
                File rxjavademo2 = getDir("rxjavademo2", MODE_WORLD_READABLE);
                String path8 = rxjavademo2.getAbsolutePath();
                String[] fileList = fileList();
                sb.append("\nfileList(): " + Arrays.toString(fileList));
                String externalStorageState = Environment.getExternalStorageState();
                sb.append("\nexternalStorageState: " + externalStorageState);
                if (Environment.MEDIA_MOUNTED.equals(externalStorageState)) {
                    Toast.makeText(SecondActivity.this, "外存挂载中", Toast.LENGTH_SHORT).show();
                    String path3 = Environment.getExternalStorageDirectory().getAbsolutePath();
                    String path6 = Environment.getExternalStoragePublicDirectory(DIRECTORY_PICTURES).getAbsolutePath();
                    sb.append("\nEnvironment.getExternalStorageDirectory():  " + path3);
                    sb.append("\nEnvironment.getExternalStoragePublicDirectory(DIRECTORY_PICTURES):  " + path6);
                }
                String path4 = Environment.getDataDirectory().getAbsolutePath();
                String path5 = Environment.getDownloadCacheDirectory().getAbsolutePath();
                String path7 = Environment.getRootDirectory().getAbsolutePath();
                sb.append("\nEnvironment.getDataDirectory():  " + path4);
                sb.append("\nEnvironment.getDownloadCacheDirectory():  " + path5);
                sb.append("\nEnvironment.getRootDirectory():  " + path7);
                sb.append("\ngetDir(\"rxjavademo2\", MODE_WORLD_READABLE):  " + path8);
                mTvMsg.setText(sb);
                break;
        }
    }
}
