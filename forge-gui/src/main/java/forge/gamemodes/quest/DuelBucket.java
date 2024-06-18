package forge.gamemodes.quest;

import org.apache.commons.lang3.StringUtils;

import forge.gamemodes.quest.data.QuestPreferences.QPref;
import forge.model.FModel;

/** 
 * TODO: Write javadoc for this type.
 *
 */
public enum DuelBucket {
    EASY  ("easy",      1. ),
    MEDIUM("medium",    1.5),
    HARD  ("hard",      2. ),
    EXPERT("very hard", 3. ),
    WILD("wild", FModel.getQuestPreferences().getPrefDouble(QPref.WILD_OPPONENTS_MULTIPLIER) ),



    ABYSSAL_BONE_FOREST_I  ("ABYSSAL_BONE_FOREST_I",      1. ),
    ABYSSAL_BONE_FOREST_II  ("ABYSSAL_BONE_FOREST_II",      1. ),
    ABYSSAL_BONE_FOREST_III  ("ABYSSAL_BONE_FOREST_III",      1. ),

    HEAVENLY_COURT_I  ("HEAVENLY_COURT_I",      1. ),
    HEAVENLY_COURT_II  ("HEAVENLY_COURT_II",      1. ),
    HEAVENLY_COURT_III  ("HEAVENLY_COURT_III",      1. ),

    BLUE_TOWER_I  ("BLUE_TOWER_I",      1. ),
    BLUE_TOWER_II  ("BLUE_TOWER_II",      1. ),
    BLUE_TOWER_III  ("BLUE_TOWER_III",      1. ),



    POLLUTION_I  ("POLLUTION_I",      1. ),
    POLLUTION_II  ("POLLUTION_II",      1. ),
    POLLUTION_III  ("POLLUTION_III",      1. ),

    APPRENTICE_I  ("APPRENTICE_I",      1. ),
    APPRENTICE_II  ("APPRENTICE_II",      1. ),
    APPRENTICE_III  ("APPRENTICE_III",      1. ),
    APPRENTICE_NOVICE_RANKING  ("APPRENTICE_NOVICE_RANKING",      1. ),







    ;

    private final String inFile;
    private final double multiplier;

    DuelBucket(final String storedInFile, final double multiplier) {
        inFile = storedInFile;
        this.multiplier = multiplier;
    }

    public final String getTitle() {
        return inFile;
    }

    public final double getMultiplier() {
        return this.multiplier;
    }

    public static DuelBucket fromString(final String src) {
        if ( StringUtils.isBlank(src) )
            return MEDIUM; // player have custom files, that didn't specify a valid difficulty

        for(DuelBucket qd : DuelBucket.values()) {
            if( src.equalsIgnoreCase(qd.inFile) || src.equalsIgnoreCase(qd.name()) )
                return qd;
        }
        return null;
    }
}
