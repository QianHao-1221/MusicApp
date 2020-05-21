package com.example.musicapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.musicapp.adapter.CommentAdapter;
import com.example.musicapp.db.Comment;
import com.example.musicapp.db.CommentController;
import com.example.musicapp.db.User;

import org.litepal.LitePal;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CommentActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText commentText;

    private List<CommentController> commentControllers = new ArrayList<>();

    private int userSituation, musicId;

    private String userNo;

    private CommentAdapter adapter;

    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        Toast.makeText(CommentActivity.this, "长按可以点赞哦，无限次！", Toast.LENGTH_SHORT).show();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.back);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.comment_list);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 1);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new CommentAdapter(commentControllers);
        recyclerView.setAdapter(adapter);

        commentText = (EditText) findViewById(R.id.comment_edit_text);

        Intent intent = getIntent();
        userSituation = intent.getIntExtra("userSituation", 100);
        musicId = intent.getIntExtra("musicId", 100);

        initComment();

        if (userSituation == 0) {
            commentText.setInputType(InputType.TYPE_NULL);
            commentText.setHint("请先登录");
            commentText.setEnabled(false);
        } else {
            SharedPreferences preferences = getSharedPreferences("data", MODE_PRIVATE);
            userNo = preferences.getString("userNo", "");
        }

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_comment);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshComment();
            }
        });

        adapter.setLongClickListener(new CommentAdapter.OnLongClickListener() {
            @Override
            public boolean onLongClick(int position) {
                adapter.agree(position, musicId);
                Toast.makeText(CommentActivity.this, "点赞成功！", Toast.LENGTH_SHORT).show();
                return true;
            }
        });
    }

    private void refreshComment() {
        //下拉刷新操作
        new Thread(new Runnable() {
            @Override
            public void run() {
                initComment();
                swipeRefreshLayout.setRefreshing(false);
            }
        }).start();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.comment_button:
                //提交评论
                if (userSituation == 0) {
                    Toast.makeText(CommentActivity.this, "登陆后可进行评论", Toast.LENGTH_SHORT).show();
                } else {
                    saveComment();
                }
                break;
            case R.id.comment_clear:
                //清空输入
                if (userSituation == 0) {
                    Toast.makeText(CommentActivity.this, "请先登录", Toast.LENGTH_SHORT).show();
                } else {
                    commentText.setText("");
                }
                break;
            default:
        }
    }

    private void saveComment() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");// HH:mm:ss
                        Date date = new Date(System.currentTimeMillis());
                        Comment comment = new Comment();
                        List<User> userList = LitePal.where("user_no = ?", userNo).find(User.class);
                        for (User user : userList) {
                            if ("".equals(commentText.getText().toString())) {
                                Toast.makeText(CommentActivity.this, "写点什么吧！", Toast.LENGTH_SHORT).show();
                            } else {
                                comment.setMusic_id(musicId);
                                comment.setUser_name(user.getUser_name());
                                comment.setUser_img(user.getPic_id());
                                comment.setComment_text(commentText.getText().toString());
                                comment.setAgree(0);
                                comment.setDate(simpleDateFormat.format(date));
                                comment.setUser_no(userNo);
                                comment.save();
                            }
                        }
                        commentText.setText("");
                        Toast.makeText(CommentActivity.this, "发表成功！", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).start();
    }

    private void initComment() {
        commentControllers.clear();
        new Thread(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        List<Comment> comments = LitePal.where("music_id = ?", String.valueOf(musicId)).order("agree desc").find(Comment.class);//查表根据时间逆序输出数据
                        for (Comment comment : comments) {
                            CommentController commentController = new CommentController(comment.getUser_img(), comment.getUser_name(), comment.getDate(), comment.getComment_text());
                            commentControllers.add(commentController);
                        }
                    }
                });
            }
        }).start();
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

}
