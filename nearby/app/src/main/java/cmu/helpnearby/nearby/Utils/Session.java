package cmu.helpnearby.nearby.Utils;


import cmu.helpnearby.nearby.Model.User;

/**
 * Created by evan on 8/2/15.
 */
public class Session {
    public static Session sSession = null;

    private User mCurrentUser;

    private Session() {
    }

    public static synchronized Session getInstance() {
        if (sSession == null) {
            sSession = new Session();
        }
        return sSession;
    }

    public void setmCurrentUser(User currentUser) {
        mCurrentUser = currentUser;
    }

    public User getmCurrentUser() {
        return mCurrentUser;
    }
}
