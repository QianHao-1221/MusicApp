package com.example.musicapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
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

public class RegisterActivity extends AppCompatActivity {

    public static final int LIM_LENGTH = 1;

    public static final int LIM_NO = 2;

    public static final int NAME_LENGTH = 3;

    public static final int REPEAT = 4;

    public static final int PSW_LEN = 5;

    public static final int NOT_CODE = 6;

    public static final int MUST_INPUT_SP = 7;

    private EditText regUserNo, regUserName, regPassword, cfmPassword, superPassword, codeEditText;

    private Bitmap bitmap;

    private String code;

    private ImageView codeImage;

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case LIM_LENGTH:
                    regUserNo.setError("由数字组成且长度限制至少为6位");
                    regUserNo.requestFocus();
                    break;
                case LIM_NO:
                    regUserNo.setError("ID已注册");
                    regUserNo.requestFocus();
                    break;
                case NAME_LENGTH:
                    regUserName.setError("用户名不能为空");
                    regUserName.requestFocus();
                    break;
                case REPEAT:
                    cfmPassword.setError("两次密码不一致");
                    cfmPassword.requestFocus();
                    cfmPassword.setText("");
                    break;
                case PSW_LEN:
                    regPassword.setError("密码至少为6位");
                    regPassword.requestFocus();
                    break;
                case MUST_INPUT_SP:
                    superPassword.setError("必须输入您的超级密码");
                    superPassword.requestFocus();
                    break;
                case NOT_CODE:
                    codeEditText.setError("验证码错误");
                    bitmap = CodeUtil.getInstance().createBitmap();
                    codeImage.setImageBitmap(bitmap);
                    codeEditText.setText("");
                    codeEditText.requestFocus();
                    break;
                default:
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.back);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        codeImage = (ImageView) findViewById(R.id.code_image_view);
        //获取工具类生成的图片验证码对象
        bitmap = CodeUtil.getInstance().createBitmap();
        //获取当前图片验证码的对应内容用于校验
        code = CodeUtil.getInstance().getCode();

        regUserNo = (EditText) findViewById(R.id.reg_user_no);
        regUserName = (EditText) findViewById(R.id.reg_user_name);
        regPassword = (EditText) findViewById(R.id.reg_password);
        cfmPassword = (EditText) findViewById(R.id.cfm_password);
        codeEditText = (EditText) findViewById(R.id.code_edit_text);
        superPassword = (EditText) findViewById(R.id.reg_super_password);
        Button register = (Button) findViewById(R.id.register_button);

        //no输入框失去焦点事件
        regUserNo.setOnFocusChangeListener(new android.view.View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, final boolean b) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                String inputNo = regUserNo.getText().toString();
                                Pattern p = Pattern.compile("[0-9]*");
                                Matcher m = p.matcher(inputNo);
                                Message message = new Message();
                                if (b) {
                                    // 此处为得到焦点时的处理内容
                                } else {
                                    if (inputNo.length() < 6 || !m.matches()) { //输入小于6为且不是数字
                                        message.what = LIM_LENGTH;
                                        handler.sendMessage(message);
                                    } else { //是否已经存在
                                        List<User> users = LitePal.where("user_no = ?", "" + inputNo).find(User.class);
                                        for (User user : users) {
                                            if (inputNo.equals(user.getUser_no())) {
                                                message.what = LIM_NO;
                                                handler.sendMessage(message);
                                            }
                                        }
                                    }
                                }
                            }
                        });
                    }
                }).start();
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Message message = new Message();

                                final String inputNo = regUserNo.getText().toString();
                                String inputName = regUserName.getText().toString();
                                String fstPassword = regPassword.getText().toString();
                                String secPassword = cfmPassword.getText().toString();
                                String inputCode = codeEditText.getText().toString().toLowerCase();//验证码转换小写
                                String superPSW = superPassword.getText().toString();

                                code = CodeUtil.getInstance().getCode().toLowerCase();

                                List<User> users = LitePal.where("user_no = ?", "" + inputNo).find(User.class);//先查询用户输入id是否已经注册

                                if (users.size() != 0) {
                                    //返回数组长度不为1，则表示已经注册过
                                    message.what = LIM_NO;
                                    handler.sendMessage(message);
                                } else if ("".equals(inputNo)) {
                                    message.what = LIM_LENGTH;
                                    handler.sendMessage(message);
                                } else if ("".equals(inputName)) {
                                    message.what = NAME_LENGTH;
                                    handler.sendMessage(message);
                                } else if (fstPassword.length() < 6) {
                                    message.what = PSW_LEN;
                                    handler.sendMessage(message);
                                } else if (!fstPassword.equals(secPassword)) {
                                    message.what = REPEAT;
                                    handler.sendMessage(message);
                                } else if ("".equals(superPSW)) {
                                    message.what = MUST_INPUT_SP;
                                    handler.sendMessage(message);
                                } else if (!inputCode.equals(code)) {
                                    message.what = NOT_CODE;
                                    handler.sendMessage(message);
                                } else {
                                    User regUser = new User();
                                    int id = 0;

                                    //用户输入满足规则后
                                    regUser.setUser_no(inputNo);//获取用户输入的id并存入user表
                                    regUser.setUser_name(inputName);//获取用户输入的用户名称
                                    regUser.setUser_password(fstPassword);//获取用户输入的密码
                                    regUser.setSuper_password(superPSW);//获取用户输入的超级密码
                                    regUser.setPic_id(R.drawable.bean);//用户注册时默认头像
                                    regUser.setCustom_color("blue");//用户注册时默认个性化颜色
                                    regUser.setUser_level(1);//用户的等级
                                    regUser.save();//存入数据库

                                    //获取注册用户的数量+1
                                    Cursor c = LitePal.findBySQL("select * from user");
                                    if (c.moveToFirst()) {
                                        do {
                                            id = c.getCount();
                                        } while (c.moveToNext());
                                    }
                                    c.close();
                                    AlertDialog.Builder dialog = new AlertDialog.Builder(RegisterActivity.this);
                                    dialog.setTitle("注册成功！");
                                    dialog.setMessage("欢迎您成为本软件的第" + id + "位用户！");
                                    dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            Intent intent = new Intent();
                                            intent.putExtra("returnUserNo", inputNo);
                                            setResult(RESULT_OK, intent);
                                            RegisterActivity.this.finish();
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

        //验证码图片切换
        codeImage.setImageBitmap(bitmap);
        codeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bitmap = CodeUtil.getInstance().createBitmap();
                codeImage.setImageBitmap(bitmap);
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
