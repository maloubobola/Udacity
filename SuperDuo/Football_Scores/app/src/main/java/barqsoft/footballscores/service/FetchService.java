package barqsoft.footballscores.service;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

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

    private void updateWidgets() {
        Context context = getApplicationContext();
        Intent dataUpdatedIntent = new Intent(ACTION_DATA_UPDATED).setPackage(context.getPackageName());
        context.sendBroadcast(dataUpdatedIntent);
    }

    public void getData (String timeFrame) {
        Uri fetch_build = Uri.parse(APIContract.BASE_URL).buildUpon().appendQueryParameter(APIContract.TIME_FRAME_PARAM, timeFrame).build();

        HttpURLConnection connection = null;
        BufferedReader reader = null;
        String jsonData = null;

        try {
            URL fetch = new URL(fetch_build.toString());
            connection = (HttpURLConnection) fetch.openConnection();
            connection.setRequestMethod(HttpContract.GET_METHOD);
            connection.addRequestProperty(HttpContract.TOKEN_PROPERTY, getString(R.string.api_key));
            connection.connect();

            // Read the input stream into a String
            InputStream inputStream = connection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                return;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line + "\n");
            }
            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return;
            }
            jsonData = buffer.toString();
        }
        catch (Exception e) {
            Log.e(TAG,e.getMessage());
        }
        finally {
            if(connection != null) {
                connection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                }
                catch (IOException e) {
                    Log.e(TAG,"Error Closing Stream : " + e.getMessage());
                }
            }
        }

        try {
            if (jsonData != null) {
                //This bit is to check if the data contains any matches. If not, we call processJson on the dummy data
                /*
                JSONArray matches = new JSONObject(jsonData).getJSONArray("fixtures");

                if (matches.length() == 0) {
                    //if there is no data, call the function on dummy data
                    //this is expected behavior during the off season.
                    processJSONdata(getString(R.string.dummy_data), getApplicationContext(), false);
                    return;
                }
                */
                processJSONdata(jsonData, getApplicationContext(), true);
            } else {
                Log.d(TAG, "Could not connect to server.");
            }
        }
        catch(Exception e) {
            Log.e(TAG,e.getMessage());
        }
    }

    private void processJSONdata (String JSONdata,Context mContext, boolean isReal) {
        //Match data
        String League = null;
        String mDate = null;
        String Home = null;
        String Away = null;
        String Home_goals = null;
        String Away_goals = null;
        String match_id = null;
        String match_day = null;

        try {
            JSONArray matches = new JSONObject(JSONdata).getJSONArray(APIContract.FIXTURES);

            //ContentValues to be inserted
            Vector<ContentValues> values = new Vector <ContentValues> (matches.length());
            for(int i = 0;i < matches.length();i++) {

                JSONObject match_data = matches.getJSONObject(i);
                League = match_data.getJSONObject(APIContract.LINKS).getJSONObject(APIContract.SOCCER_SEASON).getString("href");
                League = League.replace(APIContract.SEASON_URL,"");
                //This if statement controls which leagues we're interested in the data from.
                //add leagues here in order to have them be added to the DB.
                // If you are finding no data in the app, check that this contains all the leagues.
                // If it doesn't, that can cause an empty DB, bypassing the dummy data routine.
                if( League.equals(APIContract.PREMIER_LEAGUE) || League.equals(APIContract.SERIE_A) || League.equals(APIContract.BUNDESLIGA1) || League.equals(APIContract.BUNDESLIGA2) || League.equals(APIContract.PRIMERA_DIVISION)     ) {
                    match_id = match_data.getJSONObject(APIContract.LINKS).getJSONObject(APIContract.SELF).getString("href");
                    match_id = match_id.replace(APIContract.MATCH_URL, "");

                    mDate = match_data.getString(APIContract.MATCH_DATE);
                    SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
                    isoFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

                    Date parsedDate = isoFormat.parse(mDate);

                    Home = match_data.getString(APIContract.HOME_TEAM);
                    Away = match_data.getString(APIContract.AWAY_TEAM);
                    Home_goals = match_data.getJSONObject(APIContract.RESULT).getString(APIContract.HOME_GOALS);
                    Away_goals = match_data.getJSONObject(APIContract.RESULT).getString(APIContract.AWAY_GOALS);
                    match_day = match_data.getString(APIContract.MATCH_DAY);

                    ContentValues match_values = new ContentValues();
                    match_values.put(DatabaseContract.ScoresTable.MATCH_ID,match_id);
                    match_values.put(DatabaseContract.ScoresTable.DATE_COL, parsedDate.getTime());
                    match_values.put(DatabaseContract.ScoresTable.HOME_COL,Home);
                    match_values.put(DatabaseContract.ScoresTable.AWAY_COL,Away);
                    match_values.put(DatabaseContract.ScoresTable.HOME_GOALS_COL,Home_goals);
                    match_values.put(DatabaseContract.ScoresTable.AWAY_GOALS_COL,Away_goals);
                    match_values.put(DatabaseContract.ScoresTable.LEAGUE_COL,League);
                    match_values.put(DatabaseContract.ScoresTable.MATCH_DAY,match_day);

                    values.add(match_values);
                }
            }
            int inserted_data = 0;
            ContentValues[] insert_data = new ContentValues[values.size()];
            values.toArray(insert_data);
            inserted_data = mContext.getContentResolver().bulkInsert(DatabaseContract.BASE_CONTENT_URI,insert_data);
        }
        catch (JSONException e) {
            Log.e(TAG,e.getMessage());
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}

