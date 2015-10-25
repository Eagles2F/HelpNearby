package cmu.helpnearby.nearby;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import cmu.helpnearby.nearby.Utils.Session;

public class OfferHelpActivity extends AppCompatActivity {

    public static Intent createIntent(Context context) {
        return new Intent(context, OfferHelpActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_offer_help);

            findViewById(R.id.help_now).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        startActivity(ChatActivity.createIntent(OfferHelpActivity.this,
                                Session.getInstance().getmCurrentUser().getmAndroidId()));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            AdvertisingService.startAdvertisingService(this);
    }
}
