package cmu.helpnearby.nearby;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.connection.AppIdentifier;
import com.google.android.gms.nearby.connection.AppMetadata;
import com.google.android.gms.nearby.connection.Connections;
import com.google.android.gms.nearby.connection.ConnectionsStatusCodes;

import java.util.ArrayList;
import java.util.List;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class AdvertisingService extends IntentService
        implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        Connections.ConnectionRequestListener {
    private static final long TIMEOUT_ADVERTISE = 1000L * 300L;

    /** GoogleApiClient for connecting to the Nearby Connections API **/
    private GoogleApiClient mGoogleApiClient;

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startAdvertisingService(Context context) {
        Intent intent = new Intent(context, AdvertisingService.class);
        context.startService(intent);
    }

    public AdvertisingService() {
        super("AdvertisingService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
                // Initialize Google API Client for Nearby Connections. Note: if you are using Google+
                // sign-in with your project or any other API that requires Authentication you may want
                // to use a separate Google API Client for Nearby Connections.  This API does not
                // require the user to authenticate so it can be used even when the user does not want to
                // sign in or sign-in has failed.
                mGoogleApiClient = new GoogleApiClient.Builder(this)
                        .addConnectionCallbacks(this)
                        .addOnConnectionFailedListener(this)
                        .addApiIfAvailable(Nearby.CONNECTIONS_API)
                        .build();
                Log.d("evan", "onStart");
                mGoogleApiClient.connect();
        }
    }

    public void onStop() {
        Log.d("evan", "onStop");

        // Disconnect the Google API client and stop any ongoing discovery or advertising. When the
        // GoogleAPIClient is disconnected, any connected peers will get an onDisconnected callback.
        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }
    }

    /**
     * Begin advertising for Nearby Connections, if possible.
     */
    private void startAdvertising() {
        Log.d("evan", "startAdvertising");
        if (!isConnectedToNetwork()) {
            Log.d("evan", "startAdvertising: not connected to WiFi network.");
            return;
        }

        // Advertising with an AppIdentifer lets other devices on the network discover
        // this application and prompt the user to install the application.
        List<AppIdentifier> appIdentifierList = new ArrayList<>();
        appIdentifierList.add(new AppIdentifier(getPackageName()));
        AppMetadata appMetadata = new AppMetadata(appIdentifierList);

        // Advertise for Nearby Connections. This will broadcast the service id defined in
        // AndroidManifest.xml. By passing 'null' for the name, the Nearby Connections API
        // will construct a default name based on device model such as 'LGE Nexus 5'.
        String uid = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        Nearby.Connections.startAdvertising(mGoogleApiClient, uid, appMetadata, TIMEOUT_ADVERTISE,
                this).setResultCallback(new ResultCallback<Connections.StartAdvertisingResult>() {
            @Override
            public void onResult(Connections.StartAdvertisingResult result) {
                Log.d("evan", "startAdvertising:onResult:" + result);
                if (result.getStatus().isSuccess()) {
                    Log.d("evan", "startAdvertising:onResult: SUCCESS");

                } else {
                    Log.d("evan", "startAdvertising:onResult: FAILURE ");

                    // If the user hits 'Advertise' multiple times in the timeout window,
                    // the error will be STATUS_ALREADY_ADVERTISING
                    int statusCode = result.getStatus().getStatusCode();
                    if (statusCode == ConnectionsStatusCodes.STATUS_ALREADY_ADVERTISING) {
                        Log.d("evan", "STATUS_ALREADY_ADVERTISING");
                    } else {
                    }
                }
            }
        });
    }

    /**
     * Check if the device is connected (or connecting) to a WiFi network.
     * @return true if connected or connecting, false otherwise.
     */
    private boolean isConnectedToNetwork() {
        ConnectivityManager connManager = (ConnectivityManager)
                getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo info = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        return (info != null && info.isConnectedOrConnecting());
    }

    @Override
    public void onConnected(Bundle bundle) {
        try {
            startAdvertising();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onConnectionRequest(String s, String s1, String s2, byte[] bytes) {

    }
}
