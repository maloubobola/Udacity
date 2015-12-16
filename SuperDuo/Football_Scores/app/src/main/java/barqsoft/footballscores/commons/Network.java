package barqsoft.footballscores.commons;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by thomasthiebaud on 16/12/15.
 */
public final class Network {

    private Network() {}

    public static final boolean isAvailable(Context c) {
        ConnectivityManager cm =(ConnectivityManager)c.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }
}
