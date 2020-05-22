package com.example.musicapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.example.musicapp.adapter.ColorsAdapter;
import com.example.musicapp.db.Colors;
import com.example.musicapp.db.Pic;

import java.util.ArrayList;
import java.util.List;

public class ColorSelectionActivity extends AppCompatActivity {

    private List<Colors> colorsList = new ArrayList<>();

    private List<Pic> pics = new ArrayList<>();

    private String colorsName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_color_selection);

        //获取Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.back);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        //颜色初始化
        initColors();

        //RecyclerView获取
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.color_recycler_view);
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);//3列，垂直
        recyclerView.setLayoutManager(layoutManager);
        ColorsAdapter adapter = new ColorsAdapter(colorsList);
        recyclerView.setAdapter(adapter);
    }

    private void initColors() {
        Colors blue = new Colors("blue", R.drawable.custom_blue);
        colorsList.add(blue);

        Colors green = new Colors("green", R.drawable.custom_green);
        colorsList.add(green);

        Colors pink = new Colors("pink", R.drawable.custom_pink);
        colorsList.add(pink);

        Colors red = new Colors("red", R.drawable.custom_red);
        colorsList.add(red);

        Colors black = new Colors("black", R.drawable.custom_black);
        colorsList.add(black);

        Colors yellow = new Colors("yellow", R.drawable.custom_yellow);
        colorsList.add(yellow);
    }

    public void getColorsName(Colors colors) {
        colorsName = colors.getName();
        Intent intent = new Intent();
        intent.putExtra("colorsName", colorsName);
        setResult(RESULT_OK, intent);
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
    }
}
