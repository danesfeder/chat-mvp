package com.nesfeder.chatmvp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.nesfeder.chatmvp.presenter.ChatPresenter;
import com.nesfeder.chatmvp.view.ChatAdapter;

public class ChatActivity extends AppCompatActivity implements ChatContract.View {

    // View
    private RecyclerView rvChatList;
    private EditText etSearchBox;
    private ChatAdapter chatAdapter;

    // Presenter
    private ChatPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rvChatList = (RecyclerView) findViewById(R.id.rv_chat);
        etSearchBox = (EditText) findViewById(R.id.et_search_box);
        etSearchBox.setOnEditorActionListener(searchBoxListener);

        // Instantiate presenter and attach view
        this.presenter = new ChatPresenter();
        presenter.attachView(this);

        // Instantiate the adapter and give it the list of chat objects
        this.chatAdapter = new ChatAdapter(presenter.getChatObjects());

        // Set up the RecyclerView with adapter and layout manager
        rvChatList.setAdapter(chatAdapter);
        rvChatList.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        rvChatList.setItemAnimator(new DefaultItemAnimator());
    }

    @Override
    public void notifyAdapterObjectAdded(int position) {
        this.chatAdapter.notifyItemInserted(position);
    }

    @Override
    public void scrollChatDown() {
        this.rvChatList.scrollToPosition(presenter.getChatObjects().size() - 1);
    }

    private EditText.OnEditorActionListener searchBoxListener = new EditText.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView tv, int actionId, KeyEvent keyEvent) {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                if (!TextUtils.isEmpty(tv.getText())) {
                    presenter.onEditTextActionDone(tv.getText().toString());
                    etSearchBox.getText().clear();
                    return true;
                }
            }
            return false;
        }
    };
}
