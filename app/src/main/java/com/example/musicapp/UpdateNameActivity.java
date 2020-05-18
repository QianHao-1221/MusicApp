package com.example.musicapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.musicapp.db.User;

public class UpdateNameActivity extends AppCompatActivity {

    private String userNo, userName;

    private EditText newUserName;

    public static final int NOT_NULL = 1;

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case NOT_NULL:
                    newUserName.setError("用户名不能为空");
                    newUserName.requestFocus();
                    break;
                default:
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_name);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.back);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        Intent intent = getIntent();
        userNo = intent.getStringExtra("userNo");
        userName = intent.getStringExtra("userName");

        newUserName = (EditText) findViewById(R.id.new_user_name);

        final Button updateButton = (Button) findViewById(R.id.update_name_button);
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        userName = newUserName.getText().toString();
                        Message message = new Message();
                        if ("".equals(userName)) {
                            message.what = NOT_NULL;
                            handler.sendMessage(message);
                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    User user = new User();
                                    user.setUser_name(userName);
                                    user.updateAll("user_no = ?", userNo);

                                    AlertDialog.Builder dialog = new AlertDialog.Builder(UpdateNameActivity.this);
                                    dialog.setTitle("昵称修改成功");
                                    dialog.setMessage(userName + "，您的昵称已经修改好啦！");
                                    dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            finish();
                                        }
                                    });
                                    dialog.show();
                                }
                            });
                        }
                    }
                }).start();

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            default:
        }
        return true;
    }

    @Override
    public void finish() {
        Intent intent = new Intent();
        intent.putExtra("newUserName",userName);
        setResult(RESULT_OK,intent);
        super.finish();
        overridePendingTransition(R.anim.bottom_silent, R.anim.bottom_out);
    }
}
