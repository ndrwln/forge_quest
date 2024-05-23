package forge.screens.home.quest.thos;

import forge.gamemodes.quest.data.PreferencesResearch;

import static forge.screens.home.quest.thos.Locations.BLACK_AREA;

public class Research {

    public Research()
    {
        BLACK_AREA.add_lesson(PreferencesResearch.Knowledge.THE_PALE_LORE);
        BLACK_AREA.add_lesson(PreferencesResearch.Knowledge.VAMPIRES_I);
        BLACK_AREA.add_lesson(PreferencesResearch.Knowledge.VAMPIRES_II);
        BLACK_AREA.add_lesson(PreferencesResearch.Knowledge.SORIN_I);
        BLACK_AREA.add_lesson(PreferencesResearch.Knowledge.SORIN_II);
    }
}
