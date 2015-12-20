package barqsoft.footballscores.commons;

import barqsoft.footballscores.R;
import barqsoft.footballscores.contract.APIContract;

/**
 * Created by yehya khaled on 3/3/2015.
 */
public class Utils {
    public static int getLeague(int leagueNum) {
        switch (leagueNum + "") {
            case APIContract.SERIE_A : return R.string.seriaa;
            case APIContract.PREMIER_LEAGUE : return R.string.premierleague;
            case APIContract.CHAMPIONS_LEAGUE : return R.string.champions_league;
            case APIContract.PRIMERA_DIVISION : return R.string.primeradivison;
            case APIContract.BUNDESLIGA1 : return R.string.primeradivison;
            case APIContract.BUNDESLIGA2 : return R.string.bundesliga;
            default: return R.string.unknown_league_error;
        }
    }

    public static int getMatchDay(int matchDay, int leagueNum) {
        if((leagueNum + "").equals(APIContract.CHAMPIONS_LEAGUE)) {
            if (matchDay <= 6)
                return R.string.cl_group_stage_text;
            else if(matchDay == 7 || matchDay == 8)
                return R.string.first_knockout_round;
            else if(matchDay == 9 || matchDay == 10)
                return R.string.quarter_final;
            else if(matchDay == 11 || matchDay == 12)
                return R.string.semi_final;
            else
                return R.string.final_text;
        }
        else {
            return R.string.matchday;
        }
    }

    public static String getScores(int home_goals,int awaygoals) {
        if(home_goals < 0 || awaygoals < 0) {
            return " - ";
        }
        else {
            return String.valueOf(home_goals) + " - " + String.valueOf(awaygoals);
        }
    }

    public static int getTeamCrestByTeamName (String teamname) {
        if (teamname==null){return R.drawable.no_icon;}
        switch (teamname) { //This is the set of icons that are currently in the app. Feel free to find and add more
            //as you go.
            case "Arsenal London FC" : return R.drawable.arsenal;
            case "Manchester United FC" : return R.drawable.manchester_united;
            case "Swansea City" : return R.drawable.swansea_city_afc;
            case "Leicester City" : return R.drawable.leicester_city_fc_hd_logo;
            case "Everton FC" : return R.drawable.everton_fc_logo1;
            case "West Ham United FC" : return R.drawable.west_ham;
            case "Tottenham Hotspur FC" : return R.drawable.tottenham_hotspur;
            case "West Bromwich Albion" : return R.drawable.west_bromwich_albion_hd_logo;
            case "Sunderland AFC" : return R.drawable.sunderland;
            case "Stoke City FC" : return R.drawable.stoke_city;
            default: return R.drawable.no_icon;
        }
    }
}
