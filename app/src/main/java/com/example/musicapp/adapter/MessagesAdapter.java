package com.example.musicapp.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.musicapp.MessageActivity;
import com.example.musicapp.R;
import com.example.musicapp.db.Messages;

import java.util.List;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.ViewHolder> {

    private List<Messages> mMessages;

    private int userSituation;

    private MessageActivity messageActivity;

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;

        public ViewHolder(View view) {
            super(view);
            textView = (TextView) view.findViewById(R.id.msg_text);
        }
    }

    public MessagesAdapter(List<Messages> messagesList, int userSituation, MessageActivity messageActivity) {
        mMessages = messagesList;
        this.userSituation = userSituation;
        this.messageActivity = messageActivity;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.msg, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        holder.textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userSituation == 0) {
                    Toast.makeText(v.getContext(), "登陆后可查看更多哦", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(v.getContext(), "这是真的！", Toast.LENGTH_SHORT).show();
                }
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Messages messages = mMessages.get(position);
        holder.textView.setText(messages.getText());
    }

    @Override
    public int getItemCount() {
        return mMessages.size();
    }
}
