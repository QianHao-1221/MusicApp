package com.example.musicapp.layout;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.musicapp.R;
import com.example.musicapp.adapter.UserMusicListAdapter;
import com.example.musicapp.db.RecMusicList;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SecondLayout extends Fragment {

    private View view;

    private RecMusicList[] musicList = {
            new RecMusicList("Music1", R.drawable.test), new RecMusicList("Music1", R.drawable.test),
            new RecMusicList("Music1", R.drawable.test), new RecMusicList("Music1", R.drawable.test),
            new RecMusicList("Music1", R.drawable.test), new RecMusicList("Music1", R.drawable.test),
            new RecMusicList("Music1", R.drawable.test), new RecMusicList("Music1", R.drawable.test),
            new RecMusicList("Music1", R.drawable.test), new RecMusicList("Music1", R.drawable.test)};

    private List<RecMusicList> recMusicLists = new ArrayList<>();

    private UserMusicListAdapter adapter;

//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
//            i = ((MainActivity) getActivity()).getUS();
//        }
//    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.layout2, container, false);
//        initPic(a);

        initMusicList();

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.user_music_list);
        GridLayoutManager layoutManager = new GridLayoutManager(view.getContext(), 1);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new UserMusicListAdapter(recMusicLists);
        recyclerView.setAdapter(adapter);

        return view;
    }

    private void initMusicList() {
        recMusicLists.clear();
        for (int i = 0; i < 10; i++) {
            Random random = new Random();
            int index = random.nextInt(musicList.length);
            recMusicLists.add(musicList[index]);
        }
    }
//    public void getUS(String i) {
//        a = i;
//        Log.e("OOOOOOOOOOOO", "" + a);
//    }
//
//    public void initPic(String a) {
//        if (a.equals("blue")){
//            imageView = (ImageView) view.findViewById(R.id.layout2_pic);
//            imageView.setImageResource(R.drawable.suiji);
//        }
//    }
}
