package com.example.musicapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.example.musicapp.adapter.MessagesAdapter;
import com.example.musicapp.db.Messages;

import java.util.ArrayList;
import java.util.List;

public class MessageActivity extends AppCompatActivity {

    private List<Messages> messagesList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        Intent intent = getIntent();
        int userSituation = intent.getIntExtra("userSituation", 100);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.back);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        initMsg(userSituation);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.msg_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        MessagesAdapter adapter = new MessagesAdapter(messagesList, userSituation, this);
        recyclerView.setAdapter(adapter);
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

    private void initMsg(int i) {
        String empty = "                                                                                               ";
        String official = "官方通知：";
        Messages messages;
        if (i == 0) {
            String noLogin = official + "\n" + "         欢迎进入Yao RecMusicList！您貌似还没有登陆？";
            messages = new Messages(noLogin);
            messagesList.add(messages);
        } else {
            for (i = 0; i < 1; i++) {
                String noLogin = official + "\n" + "         现在下载歌曲只需要1000元/首" + "\n"
                        + empty +
                        "点击查看>>>";
                messages = new Messages(noLogin);
                messagesList.add(messages);
            }
        }
    }

    @Override
    public void finish() {
        super.finish();
    }
}
