package com.nesfeder.chatmvp.view.viewholders;

import android.view.View;
import android.widget.TextView;

import com.nesfeder.chatmvp.R;
import com.nesfeder.chatmvp.model.ChatObject;

public class ChatResponseVH extends BaseViewHolder {

    private TextView tvResponseText;

    public ChatResponseVH(View itemView) {
        super(itemView);
        this.tvResponseText = (TextView) itemView.findViewById(R.id.tv_response_text);
    }

    @Override
    public void onBindView(ChatObject object) {
        this.tvResponseText.setText(object.getText());
    }
}
