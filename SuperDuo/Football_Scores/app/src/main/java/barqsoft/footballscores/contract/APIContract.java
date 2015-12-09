package barqsoft.footballscores.contract;

/**
 * Created by thomasthiebaud on 09/12/15.
 */
public final class APIContract {

    private APIContract() {}

    public static final String BASE_URL = "http://api.football-data.org/alpha/fixtures";
    public static final String TIME_FRAME_PARAM = "timeFrame";


    public static final String BUNDESLIGA1 = "394";
    public static final String BUNDESLIGA2 = "395";
    public static final String PREMIER_LEAGUE = "398";
    public static final String PRIMERA_DIVISION = "399";
    public static final String SERIE_A = "401";

    public static final String SEASON_URL = "http://api.football-data.org/alpha/soccerseasons/";
    public static final String MATCH_URL = "http://api.football-data.org/alpha/fixtures/";
    public static final String FIXTURES = "fixtures";
    public static final String LINKS = "_links";
    public static final String SOCCER_SEASON = "soccerseason";
    public static final String SELF = "self";
    public static final String MATCH_DATE = "date";
    public static final String HOME_TEAM = "homeTeamName";
    public static final String AWAY_TEAM = "awayTeamName";
    public static final String RESULT = "result";
    public static final String HOME_GOALS = "goalsHomeTeam";
    public static final String AWAY_GOALS = "goalsAwayTeam";
    public static final String MATCH_DAY = "matchday";
}
