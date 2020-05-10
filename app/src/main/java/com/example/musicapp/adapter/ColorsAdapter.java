package com.example.musicapp.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.musicapp.ColorSelectionActivity;
import com.example.musicapp.R;
import com.example.musicapp.db.Colors;

import java.util.List;

public class ColorsAdapter extends RecyclerView.Adapter<PicAdapter.ViewHolder> {
    private List<Colors> mColors;

    ColorSelectionActivity colorSelection;

    static class ViewHolder extends RecyclerView.ViewHolder {
        View colorView;
        ImageView colorImage;
        TextView colorNo;

        public ViewHolder(View view) {
            super(view);
            colorView = view;
            colorImage = (ImageView) view.findViewById(R.id.pic_image);
            colorNo = (TextView) view.findViewById(R.id.pic_no);
        }
    }

    public ColorsAdapter(List<Colors> colorsList, ColorSelectionActivity colorSelection) {
        this.colorSelection = colorSelection;
        mColors = colorsList;
    }

    @Override
    public PicAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.pic, parent, false);
        final PicAdapter.ViewHolder holder = new PicAdapter.ViewHolder(view);
        holder.picView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                Colors colors = mColors.get(position);

                //获取名字主要是，然后传给ColorSelectionActivity，然后再传给MainActivity
                colorSelection.getColorsName(colors);

                colorSelection = (ColorSelectionActivity) v.getContext();
                colorSelection.finish();
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(PicAdapter.ViewHolder holder, int position) {
        Colors colors = mColors.get(position);
        holder.picImage.setImageResource(colors.getImageId());
        holder.picNo.setText(colors.getName());
    }

    @Override
    public int getItemCount() {
        return mColors.size();
    }
}
