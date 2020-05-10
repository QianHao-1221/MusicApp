package com.example.musicapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.example.musicapp.adapter.PicAdapter;
import com.example.musicapp.db.Pic;

import java.util.ArrayList;
import java.util.List;

public class ImageSelectionActivity extends AppCompatActivity {

    private List<Pic> picturesList = new ArrayList<>();

    private int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_selection);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.back);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        initPictures();
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.image_recycler_view);
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        PicAdapter adapter = new PicAdapter(picturesList, this);
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

    private void initPictures() {
        Pic bean = new Pic("", R.drawable.bean);
        picturesList.add(bean);

        Pic blackCat = new Pic("", R.drawable.blackcat);
        picturesList.add(blackCat);

        Pic cat = new Pic("", R.drawable.cat);
        picturesList.add(cat);

        Pic com = new Pic("", R.drawable.com);
        picturesList.add(com);

        Pic duck1 = new Pic("", R.drawable.duck1);
        picturesList.add(duck1);

        Pic duck2 = new Pic("", R.drawable.duck2);
        picturesList.add(duck2);

        Pic egg = new Pic("", R.drawable.egg);
        picturesList.add(egg);

        Pic fat = new Pic("", R.drawable.fat);
        picturesList.add(fat);

        Pic heart = new Pic("", R.drawable.heart);
        picturesList.add(heart);

        Pic hug = new Pic("", R.drawable.hug);
        picturesList.add(hug);

        Pic no = new Pic("", R.drawable.no);
        picturesList.add(no);

        Pic nohand = new Pic("", R.drawable.nohand);
        picturesList.add(nohand);

        Pic org = new Pic("", R.drawable.org);
        picturesList.add(org);

        Pic panda = new Pic("", R.drawable.panda);
        picturesList.add(panda);

        Pic play = new Pic("", R.drawable.play);
        picturesList.add(play);
    }

    public void getInfo(Pic pic) {
        id = pic.getImageId();
        Intent intent = new Intent();
        intent.putExtra("ID", id);
        setResult(RESULT_OK, intent);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.bottom_silent, R.anim.bottom_out);
    }
}
