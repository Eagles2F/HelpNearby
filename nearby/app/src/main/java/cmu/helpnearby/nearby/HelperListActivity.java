package cmu.helpnearby.nearby;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.FirebaseError;
import com.linkedin.platform.DeepLinkHelper;
import com.linkedin.platform.errors.LIDeepLinkError;
import com.linkedin.platform.listeners.DeepLinkListener;

import java.util.ArrayList;
import java.util.List;

import cmu.helpnearby.nearby.Adapter.AudienceListAdapter;
import cmu.helpnearby.nearby.Database.HelpNearbyFirebase;
import cmu.helpnearby.nearby.Model.User;

public class HelperListActivity extends AppCompatActivity implements
        AudienceListAdapter.peopleClickListener,
        AudienceListAdapter.pitchButtonClickListener {

    private RecyclerView mList;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<User> mUserList = new ArrayList<>();

    public static Intent createIntent(Context context) {
        return new Intent(context, HelperListActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_helper_list);
            mList = (RecyclerView) findViewById(R.id.people_list);
            // use a linear layout manager
            mLayoutManager = new LinearLayoutManager(this);
            mList.setLayoutManager(mLayoutManager);
            HelpNearbyFirebase.usersFirebase.addChildEventListener(new ChildEventListener() {
                //retrieve new user
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    User user = dataSnapshot.getValue(User.class);
                    mUserList.add(user);
                    mAdapter.notifyItemInserted(mAdapter.getItemCount() - 1);
                    Log.d("evan", "user added:" + user.getmName());
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                    Log.d("evan", "user change");
                    User user = dataSnapshot.getValue(User.class);
                    if (user.ismVisible()) {
                        if (!existUser(user)) {
                            mUserList.add(user);
                            mAdapter.notifyItemInserted(mAdapter.getItemCount() - 1);
                        }
                    } else {
                        if (existUser(user)) {
                            int index = 0;
                            for (User oldUser : mUserList) {
                                if (oldUser.getmId().equals(user.getmId())) {
                                    index = mUserList.indexOf(oldUser);
                                }
                            }
                            mUserList.remove(index);
                            mAdapter.notifyItemRemoved(index);
                        }
                    }
                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {

                }
            });

            // specify an adapter (see also next example)
            mAdapter = new AudienceListAdapter(mUserList, this, this);
            mList.setAdapter(mAdapter);
            mList.setItemAnimator(new DefaultItemAnimator());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean existUser(User user) {
        for (User oldUser:mUserList) {
            if (oldUser.getmId().equals(user.getmId())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onPeopleClicked(int position) {
        final String id = mUserList.get(position).getmId();
        DeepLinkHelper deepLinkHelper = DeepLinkHelper.getInstance();

        deepLinkHelper.openOtherProfile(this, id, new DeepLinkListener() {
            @Override
            public void onDeepLinkSuccess() {
                Toast.makeText(getApplicationContext(),
                        "Check out the profile, Decide whether to pitch or not!", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onDeepLinkError(LIDeepLinkError error) {
                Toast.makeText(getApplicationContext(),
                        "Having problem with opening the profile!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onRealTimePitchClicked(int position) {
        //start the chat room
        startActivity(ChatActivity.createIntent(this, mUserList.get(position).getmAndroidId()));
    }
}
