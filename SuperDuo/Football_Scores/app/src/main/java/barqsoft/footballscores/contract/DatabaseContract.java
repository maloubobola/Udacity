package barqsoft.footballscores.contract;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by yehya khaled on 2/25/2015.
 */
public class DatabaseContract {
    public static final String SCORES_TABLE = "ScoresTable";

    public static final class ScoresTable implements BaseColumns {
        public static final String LEAGUE_COL = "league";
        public static final String DATE_COL = "date";
        public static final String TIME_COL = "time";
        public static final String HOME_COL = "home";
        public static final String AWAY_COL = "away";
        public static final String HOME_GOALS_COL = "home_goals";
        public static final String AWAY_GOALS_COL = "away_goals";
        public static final String MATCH_ID = "match_id";
        public static final String MATCH_DAY = "match_day";

        public static final int INDEX_ID = 0;
        public static final int INDEX_DATE = 1;
        public static final int INDEX_TIME = 2;
        public static final int INDEX_HOME = 3;
        public static final int INDEX_AWAY = 4;
        public static final int INDEX_LEAGUE = 5;
        public static final int INDEX_HOME_GOALS = 6;
        public static final int INDEX_AWAY_GOALS = 7;
        public static final int INDEX_MATCH_ID = 8;
        public static final int INDEX_MATCH_DAY = 9;

        public static final String FOOTBALL_SCORES_HASHTAG = "#Football_Scores";

        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH;

        public static Uri buildScoreWithLeague() {
            return BASE_CONTENT_URI.buildUpon().appendPath("league").build();
        }
        public static Uri buildScoreWithId() {
            return BASE_CONTENT_URI.buildUpon().appendPath("id").build();
        }
        public static Uri buildScoreWithDate() {
            return BASE_CONTENT_URI.buildUpon().appendPath("date").build();
        }
    }

    //URI data
    public static final String CONTENT_AUTHORITY = "barqsoft.footballscores";
    public static final String PATH = "scores";
    public static Uri BASE_CONTENT_URI = Uri.parse("content://"+CONTENT_AUTHORITY);
}
