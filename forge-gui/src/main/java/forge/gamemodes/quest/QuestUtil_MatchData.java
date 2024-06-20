package forge.gamemodes.quest;

import forge.item.PaperCard;

import java.util.List;

public class QuestUtil_MatchData {
    public static int NUM_PROGRESS = 0;

    //Volatile
    public static QuestUtil_MatchData.MatchResult MATCH_RESULT = QuestUtil_MatchData.MatchResult.NONE;
    public static String ENEMY_TITLE;

        //Rewards
    public static String STR_CRYSTALS;
    public static String STR_CRYSTALS_LOSS;
    public static String STR_HEALTH_LOSS;
    public static List<PaperCard> CARDS;







    public static void flush()
    {
        MATCH_RESULT = MatchResult.NONE;
        ENEMY_TITLE = null;
        NUM_PROGRESS = 0;

        STR_CRYSTALS = null;
        STR_CRYSTALS_LOSS = null;
        STR_HEALTH_LOSS = null;
        CARDS = null;
    }

    public static void flush_volatile()
    {
        MATCH_RESULT = MatchResult.NONE;
        ENEMY_TITLE = null;

        STR_CRYSTALS = null;
        STR_CRYSTALS_LOSS = null;
        STR_HEALTH_LOSS = null;
        CARDS = null;
    }


    public enum MatchResult {
        NONE,
        WIN,
        LOSS
    }


}
