package com.example.musicapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
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
import android.widget.ImageView;

import com.example.musicapp.db.User;
import com.example.musicapp.utils.CodeUtil;

import org.litepal.LitePal;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ForgetActivity extends AppCompatActivity {

    public static final int LIM_LENGTH = 1;

    public static final int NO_NOT_EXIST = 2;

    public static final int FST_NULL = 3;

    public static final int FST_EQU_SEC = 4;

    public static final int CODE_NULL = 5;

    public static final int SCODE_NOT_EXIST = 6;

    private Bitmap bitmap;

    private String code, findSpCode;

    private ImageView imageView;

    private Button updateButton;

    private EditText updInputNo, superCode, updatePassword, updateCfmPassword, codeEditText;

    String updNo, sCode;

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case LIM_LENGTH:
                    updInputNo.setError("由数字组成且长度限制为6");
                    updInputNo.requestFocus();
                    break;
                case NO_NOT_EXIST:
                    updInputNo.setError("ID未注册");
                    updInputNo.requestFocus();
                    break;
                case FST_NULL:
                    updatePassword.setError("密码不能为空且长度需要大于6位");
                    updatePassword.requestFocus();
                    break;
                case FST_EQU_SEC:
                    updateCfmPassword.setError("两次输入密码不一致");
                    updateCfmPassword.setText("");
                    updateCfmPassword.requestFocus();
                    break;
                case CODE_NULL:
                    codeEditText.setError("验证码错误");
                    bitmap = CodeUtil.getInstance().createBitmap();
                    imageView.setImageBitmap(bitmap);
                    codeEditText.setText("");
                    codeEditText.requestFocus();
                    break;
                case SCODE_NOT_EXIST:
                    superCode.setError("超级密码输入有误");
                    superCode.requestFocus();
                    break;
                default:
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.back);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        updInputNo = (EditText) findViewById(R.id.upd_user_no);
        superCode = (EditText) findViewById(R.id.upd_super_password);
        updatePassword = (EditText) findViewById(R.id.upd_password);
        updateCfmPassword = (EditText) findViewById(R.id.upd_cfm_password);
        codeEditText = (EditText) findViewById(R.id.code_edit_text);

        updInputNo.setOnFocusChangeListener(new android.view.View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, final boolean b) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                String input = updInputNo.getText().toString();
                                Pattern p = Pattern.compile("[0-9]*");
                                Matcher m = p.matcher(input);
                                Message message = new Message();
                                if (b) {
                                    // 此处为得到焦点时的处理内容
                                } else {
                                    if (input.length() < 6 || !m.matches()) {
                                        message.what = LIM_LENGTH;
                                        handler.sendMessage(message);
                                    } else {
                                        List<User> users = LitePal.where("user_no = ?", "" + input).find(User.class);
                                        if (users.size() == 0) {
                                            message.what = NO_NOT_EXIST;
                                            handler.sendMessage(message);
                                        }
                                    }
                                }
                            }
                        });
                    }
                }).start();
            }
        });

        superCode.setOnFocusChangeListener(new android.view.View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, final boolean b) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                updNo = updInputNo.getText().toString();
                                sCode = superCode.getText().toString();
                                Message message = new Message();
                                if (b) {
                                    // 此处为得到焦点时的处理内容
                                } else {
                                    List<User> users = LitePal.where("user_no = ?", "" + updNo).find(User.class);
                                    for (User user : users) {
                                        if (sCode.equals(user.getSuper_password())) {

                                        } else {
                                            message.what = SCODE_NOT_EXIST;
                                            handler.sendMessage(message);
                                        }
                                    }
                                }
                            }
                        });
                    }
                }).start();
            }
        });

        imageView = (ImageView) findViewById(R.id.code_image_view);
        //获取工具类生成的图片验证码对象
        bitmap = CodeUtil.getInstance().
                createBitmap();
        //获取当前图片验证码的对应内容用于校验
        code = CodeUtil.getInstance().
                getCode();
        //验证码图片切换
        imageView.setImageBitmap(bitmap);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bitmap = CodeUtil.getInstance().createBitmap();
                imageView.setImageBitmap(bitmap);
            }
        });

        updateButton = (Button) findViewById(R.id.update_button);
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Message message = new Message();
                                updNo = updInputNo.getText().toString();
                                String spCode = superCode.getText().toString();
                                String fstPassword = updatePassword.getText().toString();
                                String cfmPassword = updateCfmPassword.getText().toString();
                                String inputCode = codeEditText.getText().toString().toLowerCase();

                                code = CodeUtil.getInstance().getCode().toLowerCase();

                                List<User> users = LitePal.where("user_no = ?", "" + updNo).find(User.class);

                                if ("".equals(updNo) || users.size() == 0) {
                                    message.what = NO_NOT_EXIST;
                                    handler.sendMessage(message);
                                } else {
                                    for (User user : users) {
                                        findSpCode = user.getSuper_password();
                                    }

                                    if (!findSpCode.equals(spCode)) {
                                        message.what = SCODE_NOT_EXIST;
                                        handler.sendMessage(message);
                                    } else if ("".equals(fstPassword) || fstPassword.length() < 6) {
                                        message.what = FST_NULL;
                                        handler.sendMessage(message);
                                    } else if (!fstPassword.equals(cfmPassword)) {
                                        message.what = FST_EQU_SEC;
                                        handler.sendMessage(message);
                                    } else if (!inputCode.equals(code)) {
                                        message.what = CODE_NULL;
                                        handler.sendMessage(message);
                                    } else {
                                        User user = new User();
                                        user.setUser_password(fstPassword);
                                        user.updateAll("user_no = ?", updNo);
                                        AlertDialog.Builder dialog = new AlertDialog.Builder(ForgetActivity.this);
                                        dialog.setTitle("密码修改成功！");
                                        dialog.setMessage("您的密码已修改成功，请牢记");
                                        dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                Intent intent = new Intent();
                                                intent.putExtra("returnUserNo", updNo);
                                                setResult(RESULT_OK, intent);
                                                ForgetActivity.this.finish();
                                            }
                                        });
                                        dialog.show();
                                    }
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
        overridePendingTransition(R.anim.bottom_silent, R.anim.slide_out_right);
    }
}