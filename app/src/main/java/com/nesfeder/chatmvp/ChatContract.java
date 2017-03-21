package com.nesfeder.chatmvp;

import com.nesfeder.chatmvp.model.ChatObject;

import java.util.ArrayList;

public interface ChatContract {

    interface View {

        void notifyAdapterObjectAdded(int position);

        void scrollChatDown();
    }

    interface Presenter {

        void attachView(ChatContract.View view);

        ArrayList<ChatObject> getChatObjects();

        void onEditTextActionDone(String inputText);
    }
}
