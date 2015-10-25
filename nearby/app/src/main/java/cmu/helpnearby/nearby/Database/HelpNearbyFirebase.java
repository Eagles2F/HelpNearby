package cmu.helpnearby.nearby.Database;

import com.firebase.client.Firebase;

/**
 * Created by evan on 7/26/15.
 */
public class HelpNearbyFirebase {
    public static final Firebase rootFirebase = new Firebase("https://helpnearby.firebaseio.com/");
    public static final Firebase usersFirebase = new Firebase("https://helpnearby.firebaseio.com/users");
}
