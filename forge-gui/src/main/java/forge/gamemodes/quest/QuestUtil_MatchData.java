package forge.gamemodes.quest;

public class QuestUtil_MatchData {
    public static QuestUtil_MatchData.MatchResult MATCH_RESULT = QuestUtil_MatchData.MatchResult.NONE;
    public static int NUM_PROGRESS = 0;
    public static String STR_CRYSTALS;
    public static String STR_CRYSTALS_LOSS;
    public static String STR_HEALTH_LOSS;







    public static void flush()
    {
        MATCH_RESULT = MatchResult.NONE;
        NUM_PROGRESS = 0;

        STR_CRYSTALS = null;
        STR_CRYSTALS_LOSS = null;
        STR_HEALTH_LOSS = null;
    }


    public enum MatchResult {
        NONE,
        WIN,
        LOSS
    }


}
