package com.example.class_management_android.adapter;

import android.content.Context;
import android.media.Image;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.class_management_android.R;
import com.example.class_management_android.model.Chat;
import com.example.class_management_android.model.Student;

import java.util.List;

public class MessageAdapter extends ArrayAdapter<Chat> {
    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT = 1;

    private Context mContext;
    private int mResourceId;
    private List<Chat> mChat;
    private String sendId;

    public MessageAdapter(Context mContext, int resource, List<Chat> mChat, String sendId){
        super(mContext, resource, mChat);
        this.mChat = mChat;
        this.mResourceId = resource;
        this.mContext = mContext;
        this.sendId = sendId;
    }

    private class viewHolder{
        TextView show_message;
        ImageView profile_image;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        MessageAdapter.viewHolder holder;
        Chat chat = mChat.get(position);
        if(convertView == null || convertView.getTag() == null){
            convertView = View.inflate(mContext, mResourceId,null);

            if(chat.getSender().equals(sendId)){
                convertView = View.inflate(mContext, R.layout.chat_item_right, null);
            }else
            {
                convertView = View.inflate(mContext, R.layout.chat_item_left, null);
            }

            holder = new MessageAdapter.viewHolder();
            holder.show_message = (TextView) convertView.findViewById(R.id.show_message);
            holder.profile_image = (ImageView) convertView.findViewById(R.id.profile_image);
            convertView.setTag(holder);
        }else
            holder = (MessageAdapter.viewHolder) convertView.getTag();

        holder.show_message.setText(chat.getMessage());
        holder.profile_image.setImageResource(R.drawable.image_user);

        return convertView;
    }



}
