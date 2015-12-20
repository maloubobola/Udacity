package barqsoft.footballscores.service;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.Vector;

import barqsoft.footballscores.BuildConfig;
import barqsoft.footballscores.commons.Network;
import barqsoft.footballscores.contract.APIContract;
import barqsoft.footballscores.contract.DatabaseContract;
import barqsoft.footballscores.R;
import barqsoft.footballscores.contract.HttpContract;

/**
 * Created by yehya khaled on 3/2/2015.
 */
public class FetchService extends IntentService {
    public static final String TAG = FetchService.class.getSimpleName();
    public static final String ACTION_DATA_UPDATED = "cbarqsoft.footballscores.ACTION_DATA_UPDATED";

    public FetchService() {
        super("FetchService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        getData("n2");
        getData("p2");
        updateWidgets();
        return;
    }

    public void getData (String timeFrame) {
        Uri uri = Uri.parse(APIContract.BASE_URL).buildUpon().appendQueryParameter(APIContract.TIME_FRAME_PARAM, timeFrame).build();

        String jsonData = null;
        if(Network.isAvailable(getApplicationContext())) {
            try {
                jsonData = Network.executeGetRequest(uri, BuildConfig.FOOTBALL_SCORES_API_TOKEN);
                processJSONdata(jsonData, getApplicationContext(), true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(getApplicationContext(),getString(R.string.no_network_error), Toast.LENGTH_LONG).show();
        }
    }

    private void processJSONdata (String JSONdata,Context mContext, boolean isReal) {
        //Match data
        String league = null;
        String date = null;
        String home = null;
        String away = null;
        String homeGoals = null;
        String awayGoals = null;
        String matchId = null;
        String matchDay = null;

        try {
            JSONArray matches = new JSONObject(JSONdata).getJSONArray(APIContract.FIXTURES);

            Vector<ContentValues> values = new Vector <ContentValues> (matches.length());
            for(int i = 0;i < matches.length();i++) {

                JSONObject matchData = matches.getJSONObject(i);
                league = matchData.getJSONObject(APIContract.LINKS).getJSONObject(APIContract.SOCCER_SEASON).getString("href");
                league = league.replace(APIContract.SEASON_URL,"");

                if( league.equals(APIContract.PREMIER_LEAGUE) || league.equals(APIContract.SERIE_A) || league.equals(APIContract.BUNDESLIGA1) || league.equals(APIContract.BUNDESLIGA2) || league.equals(APIContract.PRIMERA_DIVISION)     ) {
                    matchId = matchData.getJSONObject(APIContract.LINKS).getJSONObject(APIContract.SELF).getString("href");
                    matchId = matchId.replace(APIContract.MATCH_URL, "");

                    date = matchData.getString(APIContract.MATCH_DATE);
                    SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
                    isoFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

                    Date parsedDate = isoFormat.parse(date);

                    home = matchData.getString(APIContract.HOME_TEAM);
                    away = matchData.getString(APIContract.AWAY_TEAM);
                    homeGoals = matchData.getJSONObject(APIContract.RESULT).getString(APIContract.HOME_GOALS);
                    awayGoals = matchData.getJSONObject(APIContract.RESULT).getString(APIContract.AWAY_GOALS);
                    matchDay = matchData.getString(APIContract.MATCH_DAY);

                    ContentValues matchValues = new ContentValues();
                    matchValues.put(DatabaseContract.ScoresTable.MATCH_ID, matchId);
                    matchValues.put(DatabaseContract.ScoresTable.DATE_COL, parsedDate.getTime());
                    matchValues.put(DatabaseContract.ScoresTable.HOME_COL, home);
                    matchValues.put(DatabaseContract.ScoresTable.AWAY_COL, away);
                    matchValues.put(DatabaseContract.ScoresTable.HOME_GOALS_COL, homeGoals);
                    matchValues.put(DatabaseContract.ScoresTable.AWAY_GOALS_COL, awayGoals);
                    matchValues.put(DatabaseContract.ScoresTable.LEAGUE_COL, league);
                    matchValues.put(DatabaseContract.ScoresTable.MATCH_DAY, matchDay);

                    values.add(matchValues);
                }
            }
            int insertedData = 0;
            ContentValues[] insertData = new ContentValues[values.size()];
            values.toArray(insertData);
            insertedData = mContext.getContentResolver().bulkInsert(DatabaseContract.BASE_CONTENT_URI,insertData);
        }
        catch (JSONException e) {
            Log.e(TAG,e.getMessage());
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void updateWidgets() {
        Context context = getApplicationContext();
        Intent dataUpdatedIntent = new Intent(ACTION_DATA_UPDATED).setPackage(context.getPackageName());
        context.sendBroadcast(dataUpdatedIntent);
    }
}

