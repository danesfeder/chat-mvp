package com.nesfeder.chatmvp.view;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nesfeder.chatmvp.R;
import com.nesfeder.chatmvp.model.ChatObject;
import com.nesfeder.chatmvp.view.viewholders.BaseViewHolder;
import com.nesfeder.chatmvp.view.viewholders.ChatInputVH;
import com.nesfeder.chatmvp.view.viewholders.ChatResponseVH;

import java.util.ArrayList;

public class ChatAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    private ArrayList<ChatObject> chatObjects;

    public ChatAdapter(ArrayList<ChatObject> chatObjects) {
        this.chatObjects = chatObjects;
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        // Create the ViewHolder based on the viewType
        View itemView;
        switch (viewType) {
            case ChatObject.INPUT_OBJECT:
                itemView = inflater.inflate(R.layout.chat_input_layout, parent, false);
                return new ChatInputVH(itemView);
            case ChatObject.RESPONSE_OBJECT:
                itemView = inflater.inflate(R.layout.chat_response_layout, parent, false);
                return new ChatResponseVH(itemView);
            default:
                itemView = inflater.inflate(R.layout.chat_response_layout, parent, false);
                return new ChatResponseVH(itemView);
        }
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        holder.onBindView(chatObjects.get(position));
    }

    @Override
    public int getItemViewType(int position) {
        return chatObjects.get(position).getType();
    }

    @Override
    public int getItemCount() {
        return chatObjects.size();
    }
}
