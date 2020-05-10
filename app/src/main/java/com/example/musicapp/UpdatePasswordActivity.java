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

import org.litepal.LitePal;

import java.util.List;

public class UpdatePasswordActivity extends AppCompatActivity {

    private EditText userNo, oldPwd, newPwd, newCfmPwd;

    public static final int OLD_PWD_WRONG = 1;

    public static final int NEW_NOT_NULL = 2;

    public static final int NOT_SAME = 3;

    private String dbPwd;

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case OLD_PWD_WRONG:
                    oldPwd.setError("您的密码输入有误");
                    oldPwd.requestFocus();
                    break;
                case NEW_NOT_NULL:
                    newPwd.setError("长度大于6位");
                    newPwd.requestFocus();
                    break;
                case NOT_SAME:
                    newCfmPwd.setError("两次输入密码不一致");
                    newCfmPwd.setText("");
                    newCfmPwd.requestFocus();
                    break;
                default:
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_password);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.back);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        Intent intent = getIntent();
        userNo = (EditText) findViewById(R.id.update_no);

        //Id不可选中且更改
        userNo.setFocusable(false);
        userNo.setFocusableInTouchMode(false);
        userNo.setText(intent.getStringExtra("userNo"));

        oldPwd = (EditText) findViewById(R.id.old_pwd);

        newPwd = (EditText) findViewById(R.id.new_password);
        final String npwd = newPwd.getText().toString();
        newCfmPwd = (EditText) findViewById(R.id.new_cfm_password);


        final Button updatePwd = (Button) findViewById(R.id.update_pwd_button);
        updatePwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Message message = new Message();
                                String no = userNo.getText().toString();
                                String pwd = oldPwd.getText().toString();
                                String npwd = newPwd.getText().toString();
                                String cpwd = newCfmPwd.getText().toString();
                                List<User> users = LitePal.where("user_no = ?", "" + no).find(User.class);
                                for (User user : users) {
                                    dbPwd = user.getUser_password();
                                }
                                if (!pwd.equals(dbPwd)) {
                                    message.what = OLD_PWD_WRONG;
                                    handler.sendMessage(message);
                                } else if ("".equals(pwd)) {
                                    message.what = OLD_PWD_WRONG;
                                    handler.sendMessage(message);
                                } else if (npwd.length() < 6) {
                                    message.what = NEW_NOT_NULL;
                                    handler.sendMessage(message);
                                } else if (!cpwd.equals(npwd)) {
                                    message.what = NOT_SAME;
                                    handler.sendMessage(message);
                                } else {
                                    User user = new User();
                                    user.setUser_password(npwd);
                                    user.updateAll("user_no = ?", no);

                                    AlertDialog.Builder dialog = new AlertDialog.Builder(UpdatePasswordActivity.this);
                                    dialog.setTitle("密码修改成功");
                                    dialog.setMessage(no + "，您的密码已经修改好啦！");
                                    dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            finish();
                                        }
                                    });
                                    dialog.show();
                                }
                            }
                        });
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
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
