package cmu.helpnearby.nearby;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by evan on 10/24/15.
 */
public class MainActivity extends AppCompatActivity {
    public static Intent createIntent(Context context) {
        return new Intent(context, MainActivity.class);
    }
}
