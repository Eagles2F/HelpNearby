package cmu.helpnearby.nearby;

import android.app.Application;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.firebase.client.Firebase;

/**
 * Created by evan on 8/2/15.
 */
public class HelpNearbyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        //set up firebase
        Firebase.setAndroidContext(this);
        //set up Fresco
        Fresco.initialize(this);
    }
}
