package com.example.musicapp.adapter;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.musicapp.ImageSelectionActivity;
import com.example.musicapp.R;
import com.example.musicapp.db.Pic;

import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder> {

    private List<Pic> mPicList;

    static class ViewHolder extends RecyclerView.ViewHolder {
        View picView;
        ImageView picImage;
        TextView picNo;

        public ViewHolder(View view) {
            super(view);
            picView = view;
            picImage = (ImageView) view.findViewById(R.id.pic_image);
            picNo = (TextView) view.findViewById(R.id.pic_no);
        }
    }

    public ImageAdapter(List<Pic> picList) {
        mPicList = picList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.pic, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        holder.picView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();

                Pic pic = mPicList.get(position);
                ImageSelectionActivity imageSelection = (ImageSelectionActivity) v.getContext();
                imageSelection.getInfo(pic);
                imageSelection.finish();
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Pic pic = mPicList.get(position);
        holder.picImage.setImageResource(pic.getImageId());
        holder.picNo.setText(pic.getName());
    }

    @Override
    public int getItemCount() {
        return mPicList.size();
    }
}
