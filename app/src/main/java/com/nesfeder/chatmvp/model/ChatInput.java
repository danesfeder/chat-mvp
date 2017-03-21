package com.nesfeder.chatmvp.model;

public class ChatInput extends ChatObject {

    @Override
    public int getType() {
        return ChatObject.INPUT_OBJECT;
    }
}
