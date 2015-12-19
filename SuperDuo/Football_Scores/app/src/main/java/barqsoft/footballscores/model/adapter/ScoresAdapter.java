package barqsoft.footballscores.model.adapter;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
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
public final class ScoresAdapter extends CursorAdapter {
    private double detailMatchId = 0;

    public ScoresAdapter(Context context, Cursor cursor, int flags) {
        super(context,cursor,flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.scores_list_item, parent, false);
        ViewHolder holder = new ViewHolder(view);
        view.setTag(holder);
        return view;
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        final ViewHolder viewHolder = (ViewHolder) view.getTag();
        viewHolder.homeName.setText(cursor.getString(DatabaseContract.ScoresTable.INDEX_HOME));
        viewHolder.awayName.setText(cursor.getString(DatabaseContract.ScoresTable.INDEX_AWAY));

        long timeStamp = cursor.getLong(DatabaseContract.ScoresTable.INDEX_DATE);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeStamp);

        viewHolder.date.setText(calendar.get(Calendar.HOUR_OF_DAY) + ":" + String.format("%02d", calendar.get(Calendar.MINUTE)));
        viewHolder.score.setText(Utils.getScores(cursor.getInt(DatabaseContract.ScoresTable.INDEX_HOME_GOALS), cursor.getInt(DatabaseContract.ScoresTable.INDEX_AWAY_GOALS)));
        viewHolder.matchId = cursor.getDouble(DatabaseContract.ScoresTable.INDEX_MATCH_ID);
        viewHolder.homeCrest.setImageResource(Utils.getTeamCrestByTeamName(cursor.getString(DatabaseContract.ScoresTable.INDEX_HOME)));
        viewHolder.awayCrest.setImageResource(Utils.getTeamCrestByTeamName(cursor.getString(DatabaseContract.ScoresTable.INDEX_AWAY)));

        LayoutInflater vi = (LayoutInflater) context.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = vi.inflate(R.layout.detail_fragment, null);
        ViewGroup container = (ViewGroup) view.findViewById(R.id.details_fragment_container);
        if(viewHolder.matchId == detailMatchId) {
            container.addView(v, 0, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            TextView matchDay = (TextView) v.findViewById(R.id.matchday_textview);
            matchDay.setText(Utils.getMatchDay(cursor.getInt(DatabaseContract.ScoresTable.INDEX_MATCH_DAY), cursor.getInt(DatabaseContract.ScoresTable.INDEX_LEAGUE)));
            TextView league = (TextView) v.findViewById(R.id.league_textview);
            league.setText(Utils.getLeague(cursor.getInt(DatabaseContract.ScoresTable.INDEX_LEAGUE)));
            Button shareButton = (Button) v.findViewById(R.id.share_button);

            shareButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    context.startActivity(createShareForecastIntent(viewHolder.homeName.getText() + " " + viewHolder.score.getText() + " " + viewHolder.awayName.getText()));
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

    public double getDetailMatchId() {
        return detailMatchId;
    }

    public void setDetailMatchId(double detailMatchId) {
        this.detailMatchId = detailMatchId;
    }
}
