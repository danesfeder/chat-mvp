##### This simple tutorial will cover how to build a chat-style user interface using a ``RecyclerView`` and Model-View-Presenter architectural pattern.

Before we get into creating the layout files, we are going to need to add a couple dependcies to our ``build.gradle`` to allow us to use the Android ``RecyclerView`` and ``CardView``:

```
compile 'com.android.support:appcompat-v7:25.2.0'
compile 'com.android.support:design:25.2.0'
compile 'com.android.support:cardview-v7:25.2.0'
```

``AppCompat``should have already been included if you created an empty activity Android Studio project. 


First thing we will start with is the resource XMLs needed for the UI.  We will need one main XML for the activity holding the ``RecyclerView`` and ``EditText`` used for user input:

##### ``activity_main.xml``
```xml
<?xml version="1.0" encoding="utf-8"?>
<RelativeLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools"
    android:id="@+id/activity_main"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:background="#E0E0E0"
    tools:context="com.nesfeder.chatmvp.ChatActivity">

    <android.support.v7.widget.RecyclerView
        android:id="@+id/rv_chat"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:paddingTop="20dp"
        android:paddingStart="20dp"
        android:paddingEnd="20dp"
        android:layout_marginBottom="60dp"
        android:clipToPadding="false">

    </android.support.v7.widget.RecyclerView>

    <android.support.v7.widget.CardView
        android:layout_width="match_parent"
        android:layout_height="50dp"
        android:layout_margin="8dp"
        android:layout_alignParentBottom="true">

        <EditText
            android:id="@+id/et_search_box"
            android:layout_width="match_parent"
            android:layout_height="match_parent"
            android:paddingLeft="8dp"
            android:paddingRight="8dp"
            android:inputType="text"
            android:imeOptions="actionDone"
            android:backgroundTint="#00FFFFFF"
            android:hint="@string/ask_something_here"/>

    </android.support.v7.widget.CardView>

</RelativeLayout>
```
![](http://i.imgur.com/BCWAqDj.png)

Second and third XMLs will be for the the two different chat bubbles we will display in the ``RecyclerView``. Both of these will be inflated in their respective ``RecyclerView.ViewHolders``:

```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:orientation="vertical"
    android:gravity="end">

    <LinearLayout
        android:orientation="vertical"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_marginStart="60dp"
        android:layout_marginEnd="20dp"
        android:paddingStart="10dp"
        android:paddingEnd="18dp"
        android:background="@drawable/chat_input_background">

        <TextView
            android:id="@+id/tv_input_text"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:layout_marginTop="8dp"
            android:layout_marginBottom="8dp"
            android:textSize="16sp"
            android:textColor="#FFF"
            android:text="Input Text"/>

    </LinearLayout>

    <Space
        android:layout_width="match_parent"
        android:layout_height="10dp" />

</LinearLayout>
```

![](http://i.imgur.com/Nha0DfF.png)

The code for the ``@drawable/chat_input_background`` is:

```xml
<?xml version="1.0" encoding="utf-8"?>
<layer-list xmlns:android="http://schemas.android.com/apk/res/android">
    <item>
        <rotate
            android:fromDegrees="45"
            android:pivotX="100%"
            android:pivotY="0%"
            android:toDegrees="0" >
            <shape android:shape="rectangle" >
                <solid android:color="@color/botPrimary" />
            </shape>
        </rotate>
    </item>
    <item android:right="8dp">
        <shape android:shape="rectangle" >
            <solid android:color="@color/botPrimary" />
            <corners android:radius="4dp" />
        </shape>
    </item>
</layer-list>
``` 
Let's also create some simple data objects.  We will use a simple ``ChatObject.java`` class that can be subclassed to create specific data objects that will populate our ``RecyclerView.Viewholder``s:

```java
public abstract class ChatObject {

    public static final int INPUT_OBJECT = 0;
    public static final int RESPONSE_OBJECT = 1;

    private String text;

    @NonNull
    public String getText() {
        return text;
    }

    public void setText(@NonNull String text) {
        this.text = text;
    }

    public abstract int getType();
}
```

So each time we extend ``ChatObject``, we will need to implement ``getType()`` and provide a type for the object.  This will later be used by our ``RecyclerView.Adapter`` to determine what ViewHolder is needed to be created for the given object:

```java
public class ChatInput extends ChatObject {

    @Override
    public int getType() {
        return ChatObject.INPUT_OBJECT;
    }
}
```

Now that we have the data objects / resources needed, we will look at the ``RecyclerView.ViewHolder`` and ``RecyclerView.Adapter``.  We can create a "base" ViewHolder class that will extend ``RecyclerView.ViewHolder``:

```java
public abstract class BaseViewHolder extends RecyclerView.ViewHolder {

    BaseViewHolder(View itemView) {
        super(itemView);
    }

    public abstract void onBindView(ChatObject object);
}
```

``BaseViewHolder.java`` will then be subclassed by any specific ViewHolder we want to show in our ``RecyclerView``:

```java
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
```
We will have Viewholders for a user input as well as a response (like a text message in a chat or a message from a bot).  With our ViewHolders ready to go, let's take a look at our ``RecyclerView.Adapter``:

```java
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
```

Next, we can take a look at adhering to a model-view-presenter architectual pattern.  Let's look at the contract that the view and presenter will follow:

```java
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
```

``ChatContract.View`` will be implement by our main activity ``ChatActivity.java`` and our presenter, ``ChatPresenter.java`` will implement ``ChatContract.Presenter``:  

```java
public class ChatPresenter implements ChatContract.Presenter {

    private ArrayList<ChatObject> chatObjects;
    private ChatContract.View view;

    public ChatPresenter() {
        // Create the ArrayList for the chat objects
        this.chatObjects = new ArrayList<>();

        // Add an initial greeting message
        ChatResponse greetingMsg = new ChatResponse();
        greetingMsg.setText("Hello, world!");
        chatObjects.add(greetingMsg);
    }

    @Override
    public void attachView(ChatContract.View view) {
        this.view = view;
    }

    @Override
    public ArrayList<ChatObject> getChatObjects() {
        return this.chatObjects;
    }

    @Override
    public void onEditTextActionDone(String inputText) {
        // Create new input object
        ChatInput inputObject = new ChatInput();
        inputObject.setText(inputText);

        // Add it to the list and tell the adapter we added something
        this.chatObjects.add(inputObject);
        view.notifyAdapterObjectAdded(chatObjects.size() - 1);

        // Also scroll down if we aren't at the bottom already
        view.scrollChatDown();
    }
}
```

When we construct the presenter, we create the list for the data objects that will be displayed in our ``RecyclerView``.  The presenter has methods to attach the view implemented by ``ChatActivity`` as well as handle input from the ``EditText`` in our main layout.  ``onCreate()`` in ``ChatActivity`` will look like this:

```java
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
```  

Giving the ``RecyclerView`` a ``DefaultItemAnimator`` gives us animations that make adding / removing objects from the list look a lot smoother:

![](http://i.imgur.com/IllzZDx.gif)

It's important to note that when you add or remove items from the ``ArrayList`` in the presenter, you most also notify the adapter that we have updated our dataset.  

##### ``ChatPresenter.java``
```java
// Add it to the list and tell the adapter we added something
this.chatObjects.add(inputObject);
view.notifyAdapterObjectAdded(chatObjects.size() - 1);
```
##### ``ChatActivity.java``
```java
@Override
public void notifyAdapterObjectAdded(int position) {
    this.chatAdapter.notifyItemInserted(position);
}
```
The end product from this code should look something like this when we enter something in our ``EditText``:

![](http://i.imgur.com/L65lZqD.gif)

For the complete Android Studio project, check out this Git repo: [Chat MVP] (https://github.com/danesfeder/chat-mvp/)

Happy coding! -Dan



 




