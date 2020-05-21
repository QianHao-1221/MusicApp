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

//个性化选择适配器
public class ColorsAdapter extends RecyclerView.Adapter<ColorsAdapter.ViewHolder> {

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
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.pic, parent, false);//最基础的布局
        final ViewHolder holder = new ViewHolder(view);
        //cardView点击事件
        holder.colorView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                Colors colors = mColors.get(position);

                //获取名字主要是，然后传给ColorSelectionActivity，然后再传给MainActivity
                colorSelection.getColorsName(colors);

                //获取ColorSelectionActivity，调用它的的finish方法
                colorSelection = (ColorSelectionActivity) v.getContext();
                colorSelection.finish();
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Colors colors = mColors.get(position);
        holder.colorImage.setImageResource(colors.getImageId());
        holder.colorNo.setText(colors.getName());
    }

    @Override
    public int getItemCount() {
        return mColors.size();
    }
}
