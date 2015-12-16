package barqsoft.footballscores.model.adapter;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.Calendar;

import barqsoft.footballscores.R;
import barqsoft.footballscores.commons.Utils;
import barqsoft.footballscores.contract.DatabaseContract;
import barqsoft.footballscores.model.holder.ViewHolder;

/**
 * Created by yehya khaled on 2/26/2015.
 */
public class ScoresAdapter extends CursorAdapter {
    public double detail_match_id = 0;
    public ScoresAdapter(Context context, Cursor cursor, int flags)
    {
        super(context,cursor,flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View mItem = LayoutInflater.from(context).inflate(R.layout.scores_list_item, parent, false);
        ViewHolder mHolder = new ViewHolder(mItem);
        mItem.setTag(mHolder);
        return mItem;
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        final ViewHolder mHolder = (ViewHolder) view.getTag();
        mHolder.home_name.setText(cursor.getString(DatabaseContract.ScoresTable.INDEX_HOME));
        mHolder.away_name.setText(cursor.getString(DatabaseContract.ScoresTable.INDEX_AWAY));

        long timeStamp = cursor.getLong(DatabaseContract.ScoresTable.INDEX_DATE);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeStamp);

        mHolder.date.setText(calendar.get(Calendar.HOUR) + ":" + calendar.get(Calendar.MINUTE));

        mHolder.score.setText(Utils.getScores(cursor.getInt(DatabaseContract.ScoresTable.INDEX_HOME_GOALS), cursor.getInt(DatabaseContract.ScoresTable.INDEX_AWAY_GOALS)));
        mHolder.match_id = cursor.getDouble(DatabaseContract.ScoresTable.INDEX_MATCH_ID);
        mHolder.home_crest.setImageResource(Utils.getTeamCrestByTeamName(cursor.getString(DatabaseContract.ScoresTable.INDEX_HOME)));
        mHolder.away_crest.setImageResource(Utils.getTeamCrestByTeamName(cursor.getString(DatabaseContract.ScoresTable.INDEX_AWAY)));
        LayoutInflater vi = (LayoutInflater) context.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = vi.inflate(R.layout.detail_fragment, null);
        ViewGroup container = (ViewGroup) view.findViewById(R.id.details_fragment_container);
        if(mHolder.match_id == detail_match_id) {
            container.addView(v, 0, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            TextView match_day = (TextView) v.findViewById(R.id.matchday_textview);
            match_day.setText(Utils.getMatchDay(cursor.getInt(DatabaseContract.ScoresTable.INDEX_MATCH_DAY), cursor.getInt(DatabaseContract.ScoresTable.INDEX_LEAGUE)));
            TextView league = (TextView) v.findViewById(R.id.league_textview);
            league.setText(Utils.getLeague(cursor.getInt(DatabaseContract.ScoresTable.INDEX_LEAGUE)));
            Button share_button = (Button) v.findViewById(R.id.share_button);

            share_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    context.startActivity(createShareForecastIntent(mHolder.home_name.getText() + " " + mHolder.score.getText() +" " + mHolder.away_name.getText()));
                }
            });
        }
        else {
            container.removeAllViews();
        }
    }

    public Intent createShareForecastIntent(String ShareText) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, ShareText + DatabaseContract.ScoresTable.FOOTBALL_SCORES_HASHTAG);
        return shareIntent;
    }

}
