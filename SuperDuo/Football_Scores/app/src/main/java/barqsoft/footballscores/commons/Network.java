package barqsoft.footballscores.commons;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;

import barqsoft.footballscores.contract.APIContract;
import barqsoft.footballscores.contract.HttpContract;

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

    public static final String executeGetRequest(Uri uri, String token) throws IOException {
        return executeGetRequest(uri.toString(), token);
    }

    public static final String executeGetRequest(String url, String token) throws IOException {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(url)
                .addHeader(HttpContract.TOKEN_PROPERTY, token)
                .build();

        Response response = client.newCall(request).execute();
        return response.body().string();
    }
}
