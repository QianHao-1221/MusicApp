package com.example.musicapp.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.musicapp.R;
import com.example.musicapp.db.Comment;
import com.example.musicapp.db.CommentController;

import org.litepal.LitePal;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by 贺宁昊 on 2020/5/18.
 */

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {

    private Context mContext;

    private List<CommentController> mCommentController;

    private OnLongClickListener mLongClickListener;

    private CommentController commentController;

    static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView userName, date, content;
        CircleImageView imageView;

        public ViewHolder(View view) {
            super(view);
            cardView = (CardView) view;
            userName = (TextView) view.findViewById(R.id.comment_name);
            date = (TextView) view.findViewById(R.id.comment_date);
            content = (TextView) view.findViewById(R.id.comment_content);
            imageView = (CircleImageView) view.findViewById(R.id.comment_img);
        }
    }

    public CommentAdapter(List<CommentController> commentControllers) {
        mCommentController = commentControllers;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mContext == null) {
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_comment, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = holder.getAdapterPosition();
                commentController = mCommentController.get(position);
                //开始下载
            }
        });
        return holder;
    }


    public void setLongClickListener(OnLongClickListener longClickListener) {
        mLongClickListener = longClickListener;
    }

    public interface OnLongClickListener {
        boolean onLongClick(int position);
    }

    public void agree(int position, final String userNo, final int musicId) {
        commentController = mCommentController.get(position);
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<Comment> comments = LitePal.where("user_no = ? and music_id = ? and date = ? and comment_text = ?", userNo, String.valueOf(musicId), commentController.getDate(), commentController.getText()).find(Comment.class);
                for (Comment comment : comments) {
                    Comment c = new Comment();
                    c.setAgree(comment.getAgree() + 1);
                    c.updateAll("user_no = ? and music_id = ? and date = ? and comment_text = ?", userNo, String.valueOf(musicId), commentController.getDate(), commentController.getText());
                }
            }
        }).start();
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.cardView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                int position = holder.getAdapterPosition();
                return mLongClickListener.onLongClick(position);
            }
        });
        CommentController commentController = mCommentController.get(position);
        holder.userName.setText(commentController.getUserName());
        holder.date.setText(commentController.getDate());
        holder.content.setText(commentController.getText());
        holder.imageView.setImageResource(commentController.getImgId());
    }

    @Override
    public int getItemCount() {
        return mCommentController.size();
    }
}
