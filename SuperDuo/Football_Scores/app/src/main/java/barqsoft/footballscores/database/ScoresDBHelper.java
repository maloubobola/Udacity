package barqsoft.footballscores.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import barqsoft.footballscores.contract.DatabaseContract;
import barqsoft.footballscores.contract.DatabaseContract.ScoresTable;

/**
 * Created by yehya khaled on 2/25/2015.
 */
public class ScoresDBHelper extends SQLiteOpenHelper
{
    public static final String DATABASE_NAME = "Scores.db";
    private static final int DATABASE_VERSION = 3;

    public ScoresDBHelper(Context context)
    {
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String CreateScoresTable = "CREATE TABLE " + DatabaseContract.SCORES_TABLE + " ("
                + DatabaseContract.ScoresTable._ID + " INTEGER PRIMARY KEY,"
                + ScoresTable.DATE_COL + " INTEGER NOT NULL,"
                + DatabaseContract.ScoresTable.HOME_COL + " TEXT NOT NULL,"
                + DatabaseContract.ScoresTable.AWAY_COL + " TEXT NOT NULL,"
                + DatabaseContract.ScoresTable.LEAGUE_COL + " INTEGER NOT NULL,"
                + DatabaseContract.ScoresTable.HOME_GOALS_COL + " TEXT NOT NULL,"
                + DatabaseContract.ScoresTable.AWAY_GOALS_COL + " TEXT NOT NULL,"
                + DatabaseContract.ScoresTable.MATCH_ID + " INTEGER NOT NULL,"
                + ScoresTable.MATCH_DAY + " INTEGER NOT NULL,"
                + " UNIQUE ("+ ScoresTable.MATCH_ID+") ON CONFLICT REPLACE"
                + " );";
        db.execSQL(CreateScoresTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseContract.SCORES_TABLE);
    }
}
