package forge.gamemodes.quest._thos;

import forge.localinstance.properties.PreferencesStore;

import java.io.Serializable;

public class PreferencesResearch extends PreferencesStore<PreferencesResearch.Knowledge> implements Serializable {

    public enum Knowledge {

        //Black
        THE_PALE_LORE_I("false", "The Pale Lore I", null, false),
        THE_PALE_LORE_II("false", "The Pale Lore II", new Knowledge[] {THE_PALE_LORE_I}, false),
        THE_PALE_LORE_III("false", "The Pale Lore III", new Knowledge[] {THE_PALE_LORE_II}, false),

        THE_FLOWING_LORE_I("false", "The Flowing Lore I", null, false),
        THE_FLOWING_LORE_II("false", "The Flowing Lore II", new Knowledge[] {THE_FLOWING_LORE_I}, false),
        THE_FLOWING_LORE_III("false", "The Flowing Lore III", new Knowledge[] {THE_FLOWING_LORE_II}, false),

        THE_SPROUTING_LORE_I("false", "The Sprouting Lore I", null, false),
        THE_SPROUTING_LORE_II("false", "The Sprouting Lore II", new Knowledge[] {THE_SPROUTING_LORE_I}, false),
        THE_SPROUTING_LORE_III("false", "The Sprouting Lore III", new Knowledge[] {THE_SPROUTING_LORE_II}, false),

        THE_FURNACE_LORE_I("false", "The Furnace Lore I", null, false),
        THE_FURNACE_LORE_II("false", "The Furnace Lore II", new Knowledge[] {THE_FURNACE_LORE_I}, false),
        THE_FURNACE_LORE_III("false", "The Furnace Lore III", new Knowledge[] {THE_FURNACE_LORE_II}, false),

        THE_MERCILESS_LORE_I("false", "The Merciless Lore I", null, false),
        THE_MERCILESS_LORE_II("false", "The Merciless Lore II", new Knowledge[] {THE_MERCILESS_LORE_I}, false),
        THE_MERCILESS_LORE_III("false", "The Merciless Lore III", new Knowledge[] {THE_MERCILESS_LORE_II}, false),


        //BLACK
        VAMPIRES("false", "Tales of Vampires", new Knowledge[] {THE_PALE_LORE_I}, "Tales of Vampires - Starter", true),
        VAMPIRES_I("false", "Tales of Vampires - Vampire Nighthawk", new Knowledge[] {VAMPIRES}, false),
        VAMPIRES_II("false", "Tales of Vampires - Bloodghast", new Knowledge[] {VAMPIRES}, false),
        VAMPIRES_III("false", "Tales of Vampires - Master of Dark Rites", new Knowledge[] {VAMPIRES}, false),
        VAMPIRES_IV("false", "Tales of Vampires - The Liberator of Malakir", new Knowledge[] {VAMPIRES}, false),

        ZOMBIES("false", "Tales of Zombies", new Knowledge[] {THE_PALE_LORE_I}, "Tales of Zombies - Starter", true),
        ZOMBIES_I("false", "Tales of Zombies - Diregraf Colossus", new Knowledge[] {ZOMBIES}, false),
        ZOMBIES_II("false", "Tales of Zombies - Geralf's Messenger", new Knowledge[] {ZOMBIES}, false),
        ZOMBIES_III("false", "Tales of Zombies - Stitcher's Supplier", new Knowledge[] {ZOMBIES}, false),

        DARKNESS("false", "Tales of Darkness...", new Knowledge[] {THE_PALE_LORE_I}, "Tales of Darkness - Starter", true),
        DARKNESS_I("false", "Tales of Darkness - Dauthi Voidwalker", new Knowledge[] {DARKNESS}, false),
        DARKNESS_II("false", "Tales of Darkness - Painful Quandary", new Knowledge[] {DARKNESS}, false),
        DARKNESS_III("false", "Tales of Darkness - The Arena", new Knowledge[] {DARKNESS}, false),

        SORIN_I("false", "Tales of Sorin Markov I", new Knowledge[] {VAMPIRES, THE_PALE_LORE_II}, false),
        SORIN_II("false", "Tales of Sorin Markov II", new Knowledge[] {SORIN_I, THE_PALE_LORE_II}, false),

        //GREEN
        FOREST("false", "Tales of the Forest", new Knowledge[] {THE_SPROUTING_LORE_I}, "Tales of the Forest - Starter", true),
        FOREST_I("false", "Tales of the Forest - Tyrranax Rex", new Knowledge[] {FOREST}, false),
        FOREST_II("false", "Tales of the Forest - Traproot Kami", new Knowledge[] {FOREST}, false),
        FOREST_III("false", "Tales of the Forest - Huatli, the Sun's Heart", new Knowledge[] {FOREST}, false),
        FOREST_IV("false", "Tales of the Forest - Shaper's Sanctuary", new Knowledge[] {FOREST}, false),


        ;










        //HELPER CODE

        private final String strDefaultVal;
        private final Knowledge[] requirements;
        private final String deckName;
        private final String add_alternate_deckName;
        private final boolean add_decklist;


        Knowledge(final String s0, String deckName, Knowledge[] parent, boolean add_decklist) {
            this.strDefaultVal = s0;
            this.requirements = parent;
            this.deckName = deckName;
            this.add_alternate_deckName = null;
            this.add_decklist = add_decklist;
        }

        Knowledge(final String s0, String deckName, Knowledge[] parent, String add_alternate_deckName, boolean add_decklist) {
            this.strDefaultVal = s0;
            this.requirements = parent;
            this.deckName = deckName;
            this.add_alternate_deckName = add_alternate_deckName;
            this.add_decklist = add_decklist;
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
        public String getAlternateDeckName() {return this.add_alternate_deckName;}
        public boolean add_decklist() {return this.add_decklist;}
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
