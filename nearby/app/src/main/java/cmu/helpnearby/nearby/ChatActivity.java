package cmu.helpnearby.nearby;

import android.content.Context;
import android.content.Intent;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import cmu.helpnearby.nearby.Adapter.ChatListAdapter;
import cmu.helpnearby.nearby.Model.Chat;
import cmu.helpnearby.nearby.Utils.Session;

/**
 * Created by evan on 8/2/15.
 */
public class ChatActivity extends ActionBarActivity {
    public static String ANDROID_ID = "android_id";

    private String mUsername;
    private Firebase mFirebaseRef;
    private ValueEventListener mConnectedListener;
    private ChatListAdapter mChatListAdapter;

    ListView mListView;

    public static Intent createIntent(Context context, String androidId) {
        Intent intent = new Intent(context, ChatActivity.class);
        intent.putExtra(ANDROID_ID, androidId);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_chat);

            mListView = (ListView) findViewById(R.id.list_chat);

            // Make sure we have a mUsername
            setupUsername();
            setTitle("Chatting as " + mUsername);

            // Setup our Firebase mFirebaseRef
            mFirebaseRef = new Firebase("https://helpnearby.firebaseio.com/chat/"
                    + getIntent().getStringExtra(ANDROID_ID));

            // Setup our input methods. Enter key on the keyboard or pushing the send button
            EditText inputText = (EditText) findViewById(R.id.messageInput);
            inputText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                    if (actionId == EditorInfo.IME_NULL && keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                        sendMessage();
                    }
                    return true;
                }
            });

            findViewById(R.id.sendButton).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    sendMessage();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onStart() {
        try {
            super.onStart();
            // Setup our view and list adapter. Ensure it scrolls to the bottom as data changes
            // Tell our list adapter that we only want 50 messages at a time
            mChatListAdapter = new ChatListAdapter(mFirebaseRef.limit(50), this, R.layout.chat_message, mUsername);
            mListView.setAdapter(mChatListAdapter);
            mChatListAdapter.registerDataSetObserver(new DataSetObserver() {
                @Override
                public void onChanged() {
                    super.onChanged();
                    mListView.setSelection(mChatListAdapter.getCount() - 1);
                }
            });

            // Finally, a little indication of connection status
            mConnectedListener = mFirebaseRef.getRoot().child(".info/connected").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    boolean connected = (Boolean) dataSnapshot.getValue();
                    if (connected) {
                        Toast.makeText(ChatActivity.this, "Connected to Firebase", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(ChatActivity.this, "Disconnected from Firebase", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {
                    // No-op
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        mFirebaseRef.getRoot().child(".info/connected").removeEventListener(mConnectedListener);
        mChatListAdapter.cleanup();
    }

    private void setupUsername() {
        mUsername = Session.getInstance().getmCurrentUser().getmName();
    }

    private void sendMessage() {
        EditText inputText = (EditText) findViewById(R.id.messageInput);
        String input = inputText.getText().toString();
        if (!input.equals("")) {
            // Create our 'model', a Chat object
            Chat chat = new Chat(input, mUsername);
            // Create a new, auto-generated child of that chat location, and save our chat data there
            mFirebaseRef.push().setValue(chat);
            inputText.setText("");
        }
    }
}
