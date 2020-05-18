package com.example.musicapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.musicapp.db.User;

import org.litepal.LitePal;

import java.util.List;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText userNoED = null;
    private EditText userPasswordED = null;

    private String inputNo = null;
    private String inputPassword = null;

    private String userNo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.back);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        userNoED = (EditText) findViewById(R.id.user_id);
        userPasswordED = (EditText) findViewById(R.id.password);
        ImageView login = (ImageView) findViewById(R.id.login_button);

        login.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.login_button:
                inputNo = userNoED.getText().toString();
                inputPassword = userPasswordED.getText().toString();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                List<User> users = LitePal.where("user_no = ?", inputNo).find(User.class);//根据用户输入的id查询user表中是否存在该账号
                                if (users.size() == 0 || "".equals(inputNo)) {
                                    AlertDialog.Builder dialog = new AlertDialog.Builder(LoginActivity.this);
                                    dialog.setTitle("提示");//设置弹窗标题
                                    dialog.setMessage("ID不存在！");//设置弹窗内容
                                    dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            userPasswordED.setText("");//id值清空
                                            userNoED.requestFocus();//焦点设置在id框
                                        }
                                    });
                                    dialog.show();
                                } else if ("".equals(inputPassword)) {
                                    AlertDialog.Builder dialog = new AlertDialog.Builder(LoginActivity.this);
                                    dialog.setTitle("提示");
                                    dialog.setMessage("您还没有输入密码！");
                                    dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            userPasswordED.requestFocus();
                                        }
                                    });
                                    dialog.show();
                                } else {
                                    for (User user : users) {
                                        if (inputPassword.equals(user.getUser_password())) {
                                            userNo = user.getUser_no();

                                            //获取用户id并返回
                                            Intent intent = new Intent();
                                            intent.putExtra("returnUserNo", userNo);
                                            setResult(RESULT_OK, intent);

                                            LoginActivity.this.finish();//关闭登录页面
                                        } else {
                                            //密码输入错误执行此段代码
                                            AlertDialog.Builder dialog = new AlertDialog.Builder(LoginActivity.this);
                                            dialog.setTitle("提示");
                                            dialog.setMessage("密码输入错误！");
                                            dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    userPasswordED.setText("");
                                                    userPasswordED.requestFocus();
                                                }
                                            });
                                            dialog.show();
                                        }
                                    }
                                }
                            }
                        });
                    }
                }).start();
                break;
            case R.id.register_link:
                startActivityForResult(new Intent(LoginActivity.this, RegisterActivity.class), 1);
                overridePendingTransition(R.anim.slide_in_right, R.anim.bottom_silent);
                break;
            case R.id.forget_link:
                startActivityForResult(new Intent(LoginActivity.this, ForgetActivity.class), 1);
                overridePendingTransition(R.anim.slide_in_right, R.anim.bottom_silent);
                break;
            default:
        }
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 1:
                if (resultCode == RESULT_OK) {
                    String returnUserNo = data.getStringExtra("returnUserNo");
                    userNoED.setText(returnUserNo);
                    userPasswordED.requestFocus();
                }
                break;
            default:
        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.bottom_silent, R.anim.bottom_out);
    }
}