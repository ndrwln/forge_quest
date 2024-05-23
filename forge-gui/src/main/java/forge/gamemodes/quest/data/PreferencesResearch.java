package forge.gamemodes.quest.data;

import forge.localinstance.properties.PreferencesStore;

import java.io.Serializable;

public class PreferencesResearch extends PreferencesStore<PreferencesResearch.Knowledge> implements Serializable {

    public enum Knowledge {

        //Black
        THE_PALE_LORE("false", "The Pale Lore I", null),
        THE_PALE_LORE_II("false", "The Pale Lore II", new Knowledge[] {THE_PALE_LORE}),
        THE_PALE_LORE_III("false", "The Pale Lore III", new Knowledge[] {THE_PALE_LORE_II}),



        VAMPIRES_I("false", "Tales of Vampires", new Knowledge[] {THE_PALE_LORE}),
        VAMPIRES_II("false", "Tales of Vampires II", new Knowledge[] { VAMPIRES_I }),

        SORIN_I("false", "Sorin Markov I", new Knowledge[] { VAMPIRES_I }),
        SORIN_II("false", "Sorin Markov II", new Knowledge[] { VAMPIRES_I })

        ;










        //HELPER CODE

        private final String strDefaultVal;
        private final Knowledge[] requirements;
        private final String deckName;


        Knowledge(final String s0, String deckName, Knowledge[] parent) {
            this.strDefaultVal = s0;
            this.requirements = parent;
            this.deckName = deckName;
        }

        public String getDefault() {
            return this.strDefaultVal;
        }
        public Knowledge[] getRequirements() {
            return this.requirements;
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
