package com.example.musicapp;

import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
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
import com.example.musicapp.db.MusicInfo;
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

    public int userSituation = 0, returnPicId, playWay, pickWay, flag, playFlag, way = 0, returnImg;//0:未登录、未播放 1：已登录，播放中

    private String returnUserNo, returnUserName, returnColorsName = "blue", path;

    private Toolbar toolbar;

    private ActionBar actionBar;

    private CircleImageView musicPlayer;

    private ObjectAnimator mAnimator;

    private NavigationView navView;

    private OnToFragmentListener onToFragmentListener;

    private SharedPreferences.Editor editor;

    //写好接口供SecondLayout使用
    public interface OnToFragmentListener {
        void toFragment(String value);
    }

    public void setOnToFragmentListener(OnToFragmentListener listener) {
        this.onToFragmentListener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LitePal.getDatabase();//创建数据库
        initView();//初始化页面`

        MusicInfo musicInfo = new MusicInfo();
        musicInfo.setMusic_package("/storage/emulated/0/gg .mp3");
        musicInfo.updateAll("id = ?", "15");

        customViewPager = (CustomViewPager) findViewById(R.id.container);
        customViewPager.setScanScroll(false);//禁止页面滑动

        musicPlayer = (CircleImageView) findViewById(R.id.music);

        initAnimator();//初始化ObjectAnimator
        initMusicRotate();//底部旋转的初始化

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
                    intent.putExtra("userName", returnUserName);
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
                    case R.id.update_pwd:
                        if (userSituation == 0) {
                            Toast.makeText(MainActivity.this, "您还没有登陆", Toast.LENGTH_SHORT).show();
                        } else if (userSituation == 1) {
                            Intent intent1 = new Intent(new Intent(MainActivity.this, UpdatePasswordActivity.class));
                            intent1.putExtra("userNo", returnUserNo);
                            startActivity(intent1);
                            overridePendingTransition(R.anim.bottom_in, R.anim.bottom_silent);
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

//        saveData();

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
    private void initMusicRotate() {
        MusicService musicService = new MusicService();
        if (musicService.mp.isPlaying()) {
            mAnimator.resume();
        } else {
            mAnimator.pause();
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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.nav_bottom_linear_one:
                //设置高亮色
                initColor(userSituation, returnColorsName, 1);
                actionBar.setTitle("音乐馆");
                customViewPager.setCurrentItem(0, false);
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
                    onToFragmentListener.toFragment("0");
                    //清空SharedPreFerences中的数据
                    editor.clear();
                    editor.commit();
                    initColor(userSituation, returnColorsName, 2);
                    actionBar.setTitle("我的");
                    customViewPager.setCurrentItem(1);//初始页面
                    bottomDialog.cancel();
                }
                break;
            case R.id.music:
                Intent intent = new Intent(new Intent(MainActivity.this, MusicPlayerActivity.class));
                intent.putExtra("userNo", returnUserNo);
                intent.putExtra("playWay", playWay);
                intent.putExtra("pickWay", pickWay);
                intent.putExtra("flag", flag);
                intent.putExtra("userSituation", userSituation);
                intent.putExtra("flb_path", path);
                intent.putExtra("flb_playFlag", playFlag);
                intent.putExtra("flb_way", way);
                intent.putExtra("returnImg", returnImg);
                startActivityForResult(intent, 5);
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
                editor.clear();
                editor.commit();
                MainActivity.this.finish();
                System.exit(0);
                break;
            case R.id.cancel_it:
                bottomDialog.cancel();
                break;
            case R.id.fav_music:
                if (userSituation == 0) {
                    Toast.makeText(MainActivity.this, "登陆后查看", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent1 = new Intent(MainActivity.this, FavActivity.class);
                    intent1.putExtra("userNo", returnUserNo);
                    startActivityForResult(intent1, 6);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.bottom_silent);
                }
                break;
            case R.id.local_music:
                startActivityForResult(new Intent(MainActivity.this, LocalMusicActivity.class), 7);
                overridePendingTransition(R.anim.slide_in_right, R.anim.bottom_silent);
                break;
            case R.id.history_music:
                startActivityForResult(new Intent(MainActivity.this, HistoryActivity.class), 8);
                overridePendingTransition(R.anim.slide_in_right, R.anim.bottom_silent);
                break;
            case R.id.layout1_search:
                startActivityForResult(new Intent(MainActivity.this, SearchActivity.class), 9);
                overridePendingTransition(R.anim.slide_in_right, R.anim.bottom_silent);
                break;
            case R.id.layout1_rank:
                startActivityForResult(new Intent(MainActivity.this, RankActivity.class), 10);
                overridePendingTransition(R.anim.slide_in_right, R.anim.bottom_silent);
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
                                        returnUserName = user.getUser_name();
                                        userName.setText(returnUserName);
                                        imageView.setImageResource(user.getPic_id());
                                        userSituation = 1;
                                        initColor(userSituation, returnColorsName, 2);
                                        customViewPager.setCurrentItem(1);//初始页面
                                        actionBar.setTitle("我的");
                                        onToFragmentListener.toFragment(returnUserNo);

                                        //利用SharedPerferences暂时存储一下用户的id和状态
                                        editor = getSharedPreferences("data", MODE_PRIVATE).edit();
                                        editor.putString("userNo", returnUserNo);
                                        editor.putInt("userSituation", 1);
                                        editor.apply();
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

                                    AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
                                    dialog.setTitle("个性主题");
                                    dialog.setMessage("眼光不错！我觉得“" + returnColorsName + "”这个主题很适合你哦！");
                                    dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            initColor(userSituation, returnColorsName, 2);
                                            actionBar.setTitle("我的");
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
                    playWay = data.getIntExtra("playWay", 100);
                    pickWay = data.getIntExtra("pickWay", 100);
                    flag = data.getIntExtra("flag", 1);
                    way = data.getIntExtra("flb_way", 1);
                    returnImg = data.getIntExtra("returnImg", R.drawable.note);
                    musicPlayer.setImageResource(returnImg);
                    initMusicRotate();
                }
                break;
            case 6:
            case 7:
            case 8:
            case 9:
            case 10:
                if (resultCode == RESULT_OK) {
                    //音乐播放状态
                    path = data.getStringExtra("flb_path");
                    playFlag = data.getIntExtra("flb_playFlag", 100);
                    way = data.getIntExtra("flb_way", 1);
                    returnImg = data.getIntExtra("returnImg", R.drawable.note);
                    musicPlayer.setImageResource(returnImg);
                }
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
                        wait(autoCloseTime); //等待时间
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

//    private void saveData() {
//        SysRecMusicList sysRecMusicList = new SysRecMusicList();
//        sysRecMusicList.setImageId(R.drawable.look);
//        sysRecMusicList.updateAll("id = 8");
//        sysRecMusicList.setList_name("今天你还有“鸭”力吗？");
//        sysRecMusicList.setImageId(R.drawable.duck1);
//
//        sysRecMusicList.setList_name("听听这个能让你放松很多");
//        sysRecMusicList.setImageId(R.drawable.hug);
//
//        sysRecMusicList.setList_name("国外流行乐曲大集合");
//        sysRecMusicList.setImageId(R.drawable.test);
//
//        sysRecMusicList.setList_name("国内流行音乐");
//        sysRecMusicList.setImageId(R.drawable.qfl);
//
//        sysRecMusicList.setList_name("听歌也吸猫");
//        sysRecMusicList.setImageId(R.drawable.blackcat);
//
//        sysRecMusicList.setList_name("哈哈哈这是随便弄的");
//        sysRecMusicList.setImageId(R.drawable.com);
//
//        sysRecMusicList.setList_name("毕设好累，听听这些歌放松一下吧！");
//        sysRecMusicList.setImageId(R.drawable.heart);
//
//        sysRecMusicList.setList_name("看！");
//        sysRecMusicList.setImageId(R.drawable.look);
//
//        sysRecMusicList.setList_name("不许困");
//        sysRecMusicList.setImageId(R.drawable.no);
//
//        sysRecMusicList.setList_name("这“猫”也能吸！");
//        sysRecMusicList.setImageId(R.drawable.panda);
//
//        sysRecMusicList.setList_name("疫情期间，注意距离");
//        sysRecMusicList.setImageId(R.drawable.nohand);
//
//        sysRecMusicList.setList_name("王者荣耀必备单曲！");
//        sysRecMusicList.setImageId(R.drawable.play);
//
//        sysRecMusicList.setList_name("这首歌好听到爆炸了");
//        sysRecMusicList.setImageId(R.drawable.tuijian);
//        sysRecMusicList.save();
//    }

    //点击两次退出程序

    public void exit() {
        if ((System.currentTimeMillis() - exitTime) > 2000) {
            Toast.makeText(MainActivity.this, "再按一次返回桌面", Toast.LENGTH_SHORT).show();
            mDrawerLayout.closeDrawers();//关闭左滑菜单
            exitTime = System.currentTimeMillis();//获取当前系统时间  毫秒单位
        } else {
            moveTaskToBack(false);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "拒绝权限将无法使用程序", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            default:
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
        editor = getSharedPreferences("data", MODE_PRIVATE).edit();
        editor.clear();
        editor.commit();
        Intent intent = new Intent(MainActivity.this, MusicService.class);
        stopService(intent);//停止服务
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        editor = getSharedPreferences("data", MODE_PRIVATE).edit();
        editor.clear();
        editor.commit();
        Intent intent = new Intent(MainActivity.this, MusicService.class);
        stopService(intent);
    }
}
