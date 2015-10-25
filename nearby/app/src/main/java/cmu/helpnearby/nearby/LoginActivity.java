package cmu.helpnearby.nearby;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.linkedin.platform.APIHelper;
import com.linkedin.platform.LISessionManager;
import com.linkedin.platform.errors.LIApiError;
import com.linkedin.platform.errors.LIAuthError;
import com.linkedin.platform.listeners.ApiListener;
import com.linkedin.platform.listeners.ApiResponse;
import com.linkedin.platform.listeners.AuthListener;
import com.linkedin.platform.utils.Scope;

import cmu.helpnearby.nearby.Database.HelpNearbyFirebase;
import cmu.helpnearby.nearby.Model.User;
import cmu.helpnearby.nearby.Network.LinkedAPI;
import cmu.helpnearby.nearby.Utils.AuthUtil;
import cmu.helpnearby.nearby.Utils.Session;

// Login Activity

public class LoginActivity extends ActionBarActivity {
    private User mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        final Activity thisActivity = this;

        if (LISessionManager.getInstance(getApplicationContext()).getSession().isValid()) {
            //set up User
            setUpUser();
        } else {
            ImageButton signIn = (ImageButton) findViewById(R.id.sign_in);
            signIn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    LISessionManager.getInstance(getApplicationContext()).init(thisActivity, buildScope(), new AuthListener() {
                        @Override
                        public void onAuthSuccess() {
                            //set up user
                            setUpUser();
                            Toast.makeText(getApplicationContext(),
                                    "success" + LISessionManager.getInstance(getApplicationContext())
                                            .getSession().getAccessToken().toString(),
                                    Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onAuthError(LIAuthError error) {
                            Toast.makeText(getApplicationContext(), "failed " + error.toString(),
                                    Toast.LENGTH_LONG).show();
                        }
                    }, true);
                }
            });
            AuthUtil.printKeyHash(this);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        LISessionManager.getInstance(getApplicationContext()).onActivityResult(this, requestCode, resultCode, data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void checkUserRegistered() {
        HelpNearbyFirebase.usersFirebase.child(mUser.getmId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("evan", "datachanged");
                if (!dataSnapshot.exists()) {
                    //hasn't registered since data snapshot is empty
                    Log.d("evan", "empty");
                    registerUser();
                } else {
                    //this user exists
                    Log.d("evan", "registered user");
                    startActivity(ChooseActivity.createIntent(LoginActivity.this));
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    private void registerUser() {
        if (mUser != null) {
            //write to backend database about this new user
            HelpNearbyFirebase.usersFirebase.child(mUser.getmId()).setValue(mUser);
            startActivity(ChooseActivity.createIntent(LoginActivity.this));
        } else {
            Log.d("evan", "JSON parse error");
        }
    }

    private void setUpUser() {
        //get the user's info from linkedin api
        APIHelper apiHelper = APIHelper.getInstance(getApplicationContext());
        apiHelper.getRequest(LoginActivity.this, LinkedAPI.basicUserInfoUrl, new ApiListener() {
            @Override
            public void onApiSuccess(ApiResponse apiResponse) {
                //Success
                mUser = User.parse(apiResponse.getResponseDataAsJson());
                Session.getInstance().setmCurrentUser(mUser);
                //getting unique id for device
                String uid = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
                mUser.setmDeviceId(uid);
                //check this user has registered or not
                checkUserRegistered();
            }

            @Override
            public void onApiError(LIApiError error) {
                Toast.makeText(getApplicationContext(), "failed " + error.toString(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    private static Scope buildScope() {
        return Scope.build(Scope.R_BASICPROFILE,Scope.R_EMAILADDRESS);
    }
}
