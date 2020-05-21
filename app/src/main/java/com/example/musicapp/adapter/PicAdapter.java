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

public class PicAdapter extends RecyclerView.Adapter<PicAdapter.ViewHolder> {

    private List<Pic> mPicList;

    ImageSelectionActivity imageSelection;

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

    public PicAdapter(List<Pic> picList, ImageSelectionActivity imageSelectionActivity) {
        this.imageSelection = imageSelectionActivity;
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
                imageSelection.getInfo(pic);//图片的对象传给ImageSelectionActivity以便取出数据
                imageSelection = (ImageSelectionActivity) v.getContext();
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
