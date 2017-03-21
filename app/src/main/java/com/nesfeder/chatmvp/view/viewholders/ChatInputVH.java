package com.nesfeder.chatmvp.view.viewholders;

import android.view.View;
import android.widget.TextView;

import com.nesfeder.chatmvp.R;
import com.nesfeder.chatmvp.model.ChatObject;

public class ChatInputVH extends BaseViewHolder {

    private TextView tvInputText;

    public ChatInputVH(View itemView) {
        super(itemView);
        this.tvInputText = (TextView) itemView.findViewById(R.id.tv_input_text);
    }

    @Override
    public void onBindView(ChatObject object) {
        this.tvInputText.setText(object.getText());
    }
}
