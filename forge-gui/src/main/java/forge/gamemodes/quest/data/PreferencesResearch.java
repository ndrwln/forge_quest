package forge.gamemodes.quest.data;

import forge.localinstance.properties.PreferencesStore;

import java.io.Serializable;

public class PreferencesResearch extends PreferencesStore<PreferencesResearch.Knowledge> implements Serializable {

    public enum Knowledge {
        //Black
        BLACK_LANDS("false", "Memories of a Distant Land - Swamps", null),

        VAMPIRES_I("false", "Tales of Vampires I", BLACK_LANDS),
        VAMPIRES_II("false", "Tales of Vampires II", VAMPIRES_I),

        SORIN_I("false", "Legends of Sorin Markov I", VAMPIRES_I),
        SORIN_II("false", "Legends of Sorin Markov II", SORIN_I),
        SORIN_III("false", "Legends of Sorin Markov III", SORIN_II);

        private final String strDefaultVal;
        private final Knowledge parent;
        private final String deckName;


        Knowledge(final String s0, String deckName, Knowledge parent) {
            this.strDefaultVal = s0;
            this.parent = parent;
            this.deckName = deckName;
        }

        public String getDefault() {
            return this.strDefaultVal;
        }
        public Knowledge getParent() {
            return this.parent;
        }

        public String getDeckName() {
            return this.deckName;
        }
    }


    //Helper
    public PreferencesResearch(String PATH) {
        super(PATH, Knowledge.class);
    }

    @Override
    public Knowledge[] getEnumValues() {return Knowledge.values();}
    @Override protected Knowledge valueOf(String name) {try {return Knowledge.valueOf(name);} catch (final Exception e) {return null;}}
    @Override protected String getPrefDefault(Knowledge key) {return key.getDefault();}

}
