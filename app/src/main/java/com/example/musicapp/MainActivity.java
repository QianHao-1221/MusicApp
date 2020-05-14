package com.example.musicapp;

import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.musicapp.adapter.MyViewPagerAdapter;
import com.example.musicapp.db.User;
import com.example.musicapp.layout.FirstLayout;
import com.example.musicapp.layout.SecondLayout;
import com.example.musicapp.service.MusicService;
import com.example.musicapp.utils.CustomViewPager;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

@RequiresApi(api = Build.VERSION_CODES.KITKAT)
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private CustomViewPager customViewPager;//定义CustomViewPager，用来显示布局1、2

    private DrawerLayout mDrawerLayout;//定义左滑菜单

    private List<Fragment> fragmentList;//定义数组存储页面

    private List<User> users;//定义用户实例

    private LinearLayout linearOne, linearThree, fav;

    private TextView userName, navBottomMusic, navBottomMyInfo;

    private ImageView imageView, navMusic, navInfo;

    private long exitTime = 0;

    private Dialog bottomDialog;//定义底部弹出菜单

    public int userSituation = 0, musicSituation = 0, returnPicId;//0:未登录、未播放 1：已登录，播放中

    private String returnUserNo, returnColorsName = "blue";

    private Toolbar toolbar;

    private ActionBar actionBar;

    private CircleImageView musicPlayer;

    private ObjectAnimator mAnimator;

    private NavigationView navView;

    private OnToFragmentListener onToFragmentListener;

    public interface OnToFragmentListener{
        void toFragment(String value);
    }

    public void setOnToFragmentListener(OnToFragmentListener listener){
        this.onToFragmentListener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LitePal.getDatabase();//创建数据库
        initView();//初始化页面`

        customViewPager = (CustomViewPager) findViewById(R.id.container);
        customViewPager.setScanScroll(false);//禁止页面滑动

        musicPlayer = (CircleImageView) findViewById(R.id.music);

        initAnimator();//初始化ObjectAnimator
        initMusicRotate(musicSituation);//底部旋转的初始化

        navMusic = (ImageView) findViewById(R.id.nav_music_img);
        navInfo = (ImageView) findViewById(R.id.nav_info_img);
        navBottomMusic = (TextView) findViewById(R.id.nav_bottom_music);
        navBottomMyInfo = (TextView) findViewById(R.id.nav_bottom_my_info);

        //获取toolbar实例
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //toolbar加载
        actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.menu);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        //获取nav_header
        navView = (NavigationView) findViewById(R.id.nav_view);
        View header = navView.getHeaderView(0);

        //获取用户名和头像
        userName = (TextView) header.findViewById(R.id.user_name);
        imageView = (ImageView) header.findViewById(R.id.user_pic);

        userName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (userSituation == 0) {
                    startActivityForResult(new Intent(MainActivity.this, LoginActivity.class), 1);
                    overridePendingTransition(R.anim.bottom_in, R.anim.bottom_silent);
                } else {
                    Intent intent = new Intent(MainActivity.this, UpdateNameActivity.class);
                    intent.putExtra("userNo", returnUserNo);
                    startActivityForResult(intent, 2);
                    overridePendingTransition(R.anim.bottom_in, R.anim.bottom_silent);
                }
            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //登陆状态不同，用户进入的页面不同
                if (userSituation == 0) {
                    startActivityForResult(new Intent(MainActivity.this, LoginActivity.class), 1);
                    overridePendingTransition(R.anim.bottom_in, R.anim.bottom_silent);
                } else {
                    startActivityForResult(new Intent(MainActivity.this, ImageSelectionActivity.class), 3);
                    overridePendingTransition(R.anim.bottom_in, R.anim.bottom_silent);
                }
            }
        });

        //获取DrawerLayout  左滑菜单
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        //左滑菜单点击事件
        navView = (NavigationView) findViewById(R.id.nav_view);
        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_custom:
                        if (userSituation == 0) {
                            Toast.makeText(MainActivity.this, "您还没有登陆", Toast.LENGTH_SHORT).show();
                        } else if (userSituation == 1) {
                            startActivityForResult(new Intent(MainActivity.this, ColorSelectionActivity.class), 4);
                            overridePendingTransition(R.anim.slide_in_right, R.anim.bottom_silent);
                        }
                        break;
                    case R.id.nav_message:
                        Intent intent = new Intent(MainActivity.this, MessageActivity.class);
                        intent.putExtra("userSituation", userSituation);
                        startActivity(intent);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.bottom_silent);
                        break;
                    case R.id.update_pwd:
                        if (userSituation == 0) {
                            Toast.makeText(MainActivity.this, "您还没有登陆", Toast.LENGTH_SHORT).show();
                        } else if (userSituation == 1) {
                            Intent intent1 = new Intent(new Intent(MainActivity.this, UpdatePasswordActivity.class));
                            intent1.putExtra("userNo", returnUserNo);
                            startActivity(intent1);
                            overridePendingTransition(R.anim.slide_in_right, R.anim.bottom_silent);
                        }
                        break;
                    case R.id.nav_time:
                        showDia(1);
                        break;
                    case R.id.nav_login:
                        showDia(2);
                        break;
                    default:
                }
                mDrawerLayout.closeDrawers();
                return false;
            }
        });

        //存页面实例
        fragmentList = new ArrayList<>();

        fragmentList.add(new FirstLayout());
        fragmentList.add(new SecondLayout());

        customViewPager.setAdapter(new MyViewPagerAdapter(getSupportFragmentManager(), fragmentList));
        initColor(userSituation, returnColorsName, 2);
        customViewPager.setCurrentItem(1);//初始页面
        actionBar.setTitle("我的");
    }

    //左滑菜单启动
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                break;
            default:
        }
        return true;
    }

    //初始化objectAnimator
    private void initAnimator() {
        mAnimator = ObjectAnimator.ofFloat(musicPlayer, "rotation", 0.0f, 360.0f);
        mAnimator.setDuration(20000);//设定转一圈的时间
        mAnimator.setRepeatCount(Animation.INFINITE);//设定无限循环
        mAnimator.setRepeatMode(ObjectAnimator.RESTART);// 循环模式
        mAnimator.setInterpolator(new LinearInterpolator());// 匀速
        mAnimator.start();//动画开始
        mAnimator.pause();//动画暂停
    }

    //初始化页面
    private void initView() {
        linearOne = (LinearLayout) findViewById(R.id.nav_bottom_linear_one);
        linearThree = (LinearLayout) findViewById(R.id.nav_bottom_linear_three);

        linearOne.setOnClickListener(this);
        linearThree.setOnClickListener(this);
    }

    //初始化底部music的旋转方式
    private void initMusicRotate(int musicSituation) {
        if (musicSituation == 0) {
            mAnimator.pause();
        } else if (musicSituation == 1) {
            mAnimator.start();
        }
    }

    //初始化用户选择颜色，默认蓝色
    private void initColor(int sit, String a, int b) {
        if (sit == 0) {
            a = "blue";
        } else {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    users = LitePal.where("user_no = ?", returnUserNo).find(User.class);
                }
            }).start();
            for (User user : users) {
                a = user.getCustom_color();
            }
        }
        navMusic.setImageResource(R.drawable.music);
        navInfo.setImageResource(R.drawable.myinfo);
        navBottomMusic.setTextColor(Color.parseColor("#111111"));
        navBottomMyInfo.setTextColor(Color.parseColor("#111111"));
        switch (a) {
            case "blue":
                if (b == 1) {
                    navMusic.setImageResource(R.drawable.music_blue);
                    navBottomMusic.setTextColor(Color.parseColor("#00bfff"));
                } else if (b == 2) {
                    navInfo.setImageResource(R.drawable.myinfo_blue);
                    navBottomMyInfo.setTextColor(Color.parseColor("#00bfff"));
                }
                break;
            case "green":
                if (b == 1) {
                    navMusic.setImageResource(R.drawable.music_green);
                    navBottomMusic.setTextColor(Color.parseColor("#1afa29"));
                } else if (b == 2) {
                    navInfo.setImageResource(R.drawable.myinfo_green);
                    navBottomMyInfo.setTextColor(Color.parseColor("#1afa29"));
                }
                break;
            case "pink":
                if (b == 1) {
                    navMusic.setImageResource(R.drawable.music_pink);
                    navBottomMusic.setTextColor(Color.parseColor("#d4237a"));
                } else if (b == 2) {
                    navInfo.setImageResource(R.drawable.myinfo_pink);
                    navBottomMyInfo.setTextColor(Color.parseColor("#d4237a"));
                }
                break;
            case "red":
                if (b == 1) {
                    navMusic.setImageResource(R.drawable.music_red);
                    navBottomMusic.setTextColor(Color.parseColor("#d81e06"));
                } else if (b == 2) {
                    navInfo.setImageResource(R.drawable.myinfo_red);
                    navBottomMyInfo.setTextColor(Color.parseColor("#d81e06"));
                }
                break;
            case "yellow":
                if (b == 1) {
                    navMusic.setImageResource(R.drawable.music_yellow);
                    navBottomMusic.setTextColor(Color.parseColor("#f4ea2a"));
                } else if (b == 2) {
                    navInfo.setImageResource(R.drawable.myinfo_yellow);
                    navBottomMyInfo.setTextColor(Color.parseColor("#f4ea2a"));
                }
                break;
            case "black":
                if (b == 1) {
                    navMusic.setImageResource(R.drawable.music);
                    navBottomMusic.setTextColor(Color.parseColor("#2c2c2c"));
                } else if (b == 2) {
                    navInfo.setImageResource(R.drawable.myinfo);
                    navBottomMyInfo.setTextColor(Color.parseColor("#2c2c2c"));
                }
                break;
            default:
        }
    }

    //登录成功后
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 1:
                if (resultCode == RESULT_OK) {
                    returnUserNo = data.getStringExtra("returnUserNo");
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    users = LitePal.where("user_no = ?", returnUserNo).find(User.class);
                                    for (User user : users) {
                                        userName.setText(user.getUser_name());
                                        imageView.setImageResource(user.getPic_id());
                                        userSituation = 1;
                                        initColor(userSituation, returnColorsName, 2);
                                        customViewPager.setCurrentItem(1);//初始页面
                                        actionBar.setTitle("我的");
                                        onToFragmentListener.toFragment(returnUserNo);
                                    }
                                }
                            });
                        }
                    }).start();
                }
                break;
            case 2:
                if (resultCode == RESULT_OK) {
                    String newUserName = data.getStringExtra("newUserName");
                    userName.setText(newUserName);
                }
                break;
            case 3:
                if (resultCode == RESULT_OK) {
                    //更新用户头像的选择
                    returnPicId = data.getIntExtra("ID", 1);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    User user = new User();
                                    user.setPic_id(returnPicId);
                                    user.updateAll("user_no = ?", returnUserNo);
                                    imageView.setImageResource(returnPicId);
                                }
                            });
                        }
                    }).start();
                }
                break;
            case 4:
                if (resultCode == RESULT_OK) {
                    //更新用户的个性化选择
                    returnColorsName = data.getStringExtra("colorsName");
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    User user = new User();
                                    user.setCustom_color(returnColorsName);
                                    user.updateAll("user_no = ?", returnUserNo);

//                                    secondLayout.getUS(returnColorsName);
                                    initColor(userSituation, returnColorsName, 2);
                                    AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
                                    dialog.setTitle("个性主题");
                                    dialog.setMessage("眼光不错！我觉得“" + returnColorsName + "”这个主题很适合你哦！");
                                    dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            customViewPager.setCurrentItem(1);//初始页面
                                        }
                                    });
                                    dialog.show();
                                }
                            });
                        }
                    }).start();
                }
                break;
            case 5:
                if (resultCode == RESULT_OK) {
                    //音乐播放状态
                    musicSituation = data.getIntExtra("musicSituation", 100);
                    initMusicRotate(musicSituation);
                }
            default:
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.nav_bottom_linear_one:
                customViewPager.setCurrentItem(0, false);
                //设置高亮色
                initColor(userSituation, returnColorsName, 1);
                actionBar.setTitle("音乐馆");
                break;
            case R.id.nav_bottom_linear_three:
                initColor(userSituation, returnColorsName, 2);
                actionBar.setTitle("我的");
                customViewPager.setCurrentItem(1, false);
                break;
            case R.id.user_logout:
                if (userSituation == 0) {
                    Toast.makeText(MainActivity.this, "您还未登录", Toast.LENGTH_SHORT).show();
                } else if (userSituation == 1) {
                    userName.setText("请先登录");
                    imageView.setImageResource(R.drawable.nologin);
                    userSituation = 0;
                    Toast.makeText(MainActivity.this, "您已退出登录", Toast.LENGTH_SHORT).show();
                    initColor(0, returnColorsName, 2);
                    actionBar.setTitle("我的");
                    customViewPager.setCurrentItem(1);//初始页面
                    bottomDialog.cancel();
                }
                break;
            case R.id.music:
                startActivityForResult(new Intent(MainActivity.this, MusicPlayerActivity.class), 5);
                overridePendingTransition(R.anim.bottom_in, R.anim.bottom_silent);
                break;
            case R.id.five_to_close:
                Toast.makeText(MainActivity.this, "应用将在5分钟后关闭", Toast.LENGTH_SHORT).show();
                exitAppByTime(1, 2000);
                bottomDialog.cancel();
                break;
            case R.id.fifteen_to_close:
                Toast.makeText(MainActivity.this, "应用将在15分钟后关闭", Toast.LENGTH_SHORT).show();
                exitAppByTime(1, 900000);
                bottomDialog.cancel();
                break;
            case R.id.thirty_to_close:
                Toast.makeText(MainActivity.this, "应用将在30分钟后关闭", Toast.LENGTH_SHORT).show();
                exitAppByTime(1, 1800000);
                bottomDialog.cancel();
                break;
            case R.id.close_app:
                bottomDialog.cancel();//关闭底部弹窗
                MainActivity.this.finish();
                System.exit(0);
                break;
            case R.id.cancel_it:
                bottomDialog.cancel();
                break;
            case R.id.fav_music:
                startActivity(new Intent(MainActivity.this, FavActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.bottom_silent);
                break;
            case R.id.local_music:
                startActivity(new Intent(MainActivity.this, LocalMusicActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.bottom_silent);
                break;
            case R.id.buy_music:
                startActivity(new Intent(MainActivity.this, BuyActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.bottom_silent);
                break;
            default:
        }
    }

    //显示底部选择菜单
    private void showDia(int i) {
        bottomDialog = new Dialog(this, R.style.BottomDialog);
        if (i == 1) {
            View contentView = LayoutInflater.from(this).inflate(R.layout.timer, null);
            bottomDialog.setContentView(contentView);
            ViewGroup.LayoutParams layoutParams = contentView.getLayoutParams();
            layoutParams.width = getResources().getDisplayMetrics().widthPixels;
            contentView.setLayoutParams(layoutParams);
        } else if (i == 2) {
            View contentView = LayoutInflater.from(this).inflate(R.layout.choice, null);
            bottomDialog.setContentView(contentView);
            ViewGroup.LayoutParams layoutParams = contentView.getLayoutParams();
            layoutParams.width = getResources().getDisplayMetrics().widthPixels;
            contentView.setLayoutParams(layoutParams);
        }
        bottomDialog.getWindow().setGravity(Gravity.BOTTOM);
        bottomDialog.getWindow().setWindowAnimations(R.style.BottomDialog_Animation);
        bottomDialog.show();
    }

    //定时退出功能
    private void exitAppByTime(int appTime, final int autoCloseTime) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                synchronized (this) {
                    try {
                        wait(autoCloseTime); //5秒
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        MainActivity.this.finish();
                        System.exit(0);
                    }
                });

            }
        }).start();
    }

    //点击两次退出程序
    public void exit() {
        if ((System.currentTimeMillis() - exitTime) > 2000) {
            Toast.makeText(MainActivity.this, "再按一次返回桌面", Toast.LENGTH_SHORT).show();
            mDrawerLayout.closeDrawers();
            exitTime = System.currentTimeMillis();
        } else {
            moveTaskToBack(false);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            //返回事件
            exit();
        }
        return false;
    }

    @Override
    public void finish() {
        super.finish();
        Intent intent = new Intent(MainActivity.this, MusicService.class);
        stopService(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Intent intent = new Intent(MainActivity.this, MusicService.class);
        stopService(intent);
    }
}
