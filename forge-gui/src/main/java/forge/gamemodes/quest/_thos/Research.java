package forge.gamemodes.quest._thos;

import forge.model.FModel;
import lombok.Getter;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class Research {

    public static Research INSTANCE;
    public static HashMap<String, ArrayList<Knowledge>> shops = new HashMap<>();

    public Research()
    {
        if (INSTANCE != null) return; INSTANCE = this;

        //Init booster packs, put knowledge into lists based on location key
        HashSet<String> unlocked_knowledge = FModel.getQuest().getAssets().getKnowledge_unlocked();
        for (Knowledge k : Knowledge.values())
        {
            if (unlocked_knowledge.contains( k.deckName()) ) continue;

            if (!shops.containsKey(k.location())) shops.put(k.location(), new ArrayList<>());

            shops.get(k.location()).add(k);
        }
    }

    @Getter @Accessors(fluent = true, chain = false) public enum Knowledge {

        //ABYSSAL_BONE_FOREST
        THE_PALE_LORE_I("ABYSSAL_BONE_FOREST", "The Pale Lore I", null, false),
        THE_PALE_LORE_II("ABYSSAL_BONE_FOREST", "The Pale Lore II", new Knowledge[] {THE_PALE_LORE_I}, false),
        THE_PALE_LORE_III("ABYSSAL_BONE_FOREST", "The Pale Lore III", new Knowledge[] {THE_PALE_LORE_II}, false),

        THE_SPROUTING_LORE_I("ABYSSAL_BONE_FOREST", "The Sprouting Lore I", null, false),
        THE_SPROUTING_LORE_II("ABYSSAL_BONE_FOREST", "The Sprouting Lore II", new Knowledge[] {THE_SPROUTING_LORE_I}, false),
        THE_SPROUTING_LORE_III("ABYSSAL_BONE_FOREST", "The Sprouting Lore III", new Knowledge[] {THE_SPROUTING_LORE_II}, false),


        //TODO: enemy decks
        VAMPIRES("ABYSSAL_BONE_FOREST", "Tales of Vampires", new Knowledge[] {THE_PALE_LORE_I}, "Tales of Vampires - Starter", true),
        VAMPIRES_I("ABYSSAL_BONE_FOREST", "Tales of Vampires - Vampire Nighthawk", new Knowledge[] {VAMPIRES}, false),
        VAMPIRES_II("ABYSSAL_BONE_FOREST", "Tales of Vampires - Bloodghast", new Knowledge[] {VAMPIRES}, false),
        VAMPIRES_III("ABYSSAL_BONE_FOREST", "Tales of Vampires - Master of Dark Rites", new Knowledge[] {VAMPIRES}, false),
        VAMPIRES_IV("ABYSSAL_BONE_FOREST", "Tales of Vampires - The Liberator of Malakir", new Knowledge[] {VAMPIRES}, false),
        SORIN_I("ABYSSAL_BONE_FOREST", "Tales of Sorin Markov I", new Knowledge[] {VAMPIRES, THE_PALE_LORE_II}, false),
        SORIN_II("ABYSSAL_BONE_FOREST", "Tales of Sorin Markov II", new Knowledge[] {SORIN_I, THE_PALE_LORE_II}, false),

        //TODO: images, descriptions, prices, enemy decks//redo zombie deck
        ZOMBIES("ABYSSAL_BONE_FOREST", "Formula - Zombie", new Knowledge[] {THE_PALE_LORE_I}, "Formula - Zombie - Starter", true),
        ZOMBIES_I("ABYSSAL_BONE_FOREST", "Formula - Zombie - Ghoulcaller's Chant", new Knowledge[] {ZOMBIES}, false),
        ZOMBIES_II("ABYSSAL_BONE_FOREST", "Formula - Zombie - Prized Amalgam", new Knowledge[] {ZOMBIES}, false),
        ZOMBIES_III("ABYSSAL_BONE_FOREST", "Formula - Zombie - Unearth", new Knowledge[] {ZOMBIES}, false),
        ZOMBIES_IV("ABYSSAL_BONE_FOREST", "Formula - Zombie - Village Rites", new Knowledge[] {ZOMBIES}, false),
        ZOMBIES_V("ABYSSAL_BONE_FOREST", "Formala - Zombie - Tergrid",
                new Knowledge[] {ZOMBIES, ZOMBIES_I, ZOMBIES_II, ZOMBIES_III, ZOMBIES_IV, THE_PALE_LORE_II}, false),

        //TODO: images, descriptions, prices, enemy decks//redo darkness deck
        DARKNESS("ABYSSAL_BONE_FOREST", "Tales of Darkness...", new Knowledge[] {THE_PALE_LORE_I}, "Tales of Darkness - Starter", true),
        DARKNESS_I("ABYSSAL_BONE_FOREST", "Tales of Darkness - Dauthi Voidwalker", new Knowledge[] {DARKNESS}, false),
        DARKNESS_II("ABYSSAL_BONE_FOREST", "Tales of Darkness - Painful Quandary", new Knowledge[] {DARKNESS}, false),
        DARKNESS_III("ABYSSAL_BONE_FOREST", "Tales of Darkness - The Arena", new Knowledge[] {DARKNESS}, false),




        FOREST("ABYSSAL_BONE_FOREST", "Tales of the Forest", new Knowledge[] {THE_SPROUTING_LORE_I}, "Tales of the Forest - Starter", true),
        FOREST_I("ABYSSAL_BONE_FOREST", "Tales of the Forest - Tyrranax Rex", new Knowledge[] {FOREST}, false),
        FOREST_II("ABYSSAL_BONE_FOREST", "Tales of the Forest - Traproot Kami", new Knowledge[] {FOREST}, false),
        FOREST_III("ABYSSAL_BONE_FOREST", "Tales of the Forest - Huatli, the Sun's Heart", new Knowledge[] {FOREST}, false),
        FOREST_IV("ABYSSAL_BONE_FOREST", "Tales of the Forest - Shaper's Sanctuary", new Knowledge[] {FOREST}, false),

        //TODO: images, descriptions, prices, enemy decks
        ELVES("ABYSSAL_BONE_FOREST", "Tales of Elves", new Knowledge[] {THE_SPROUTING_LORE_I}, "Tales of Elves - Starter", true),
        ELVES_I("ABYSSAL_BONE_FOREST", "Tales of Elves - Collected Company", new Knowledge[] {ELVES}, false),
        ELVES_II("ABYSSAL_BONE_FOREST", "Tales of Elves - Elvish Mystic", new Knowledge[] {ELVES}, false),
        ELVES_III("ABYSSAL_BONE_FOREST", "Tales of Elves - Elvish Warmaster", new Knowledge[] {ELVES}, false),
        ELVES_IV("ABYSSAL_BONE_FOREST", "Tales of Elves - Steel Leaf Champion", new Knowledge[] {ELVES}, false),



        //INNER_FLAME_PEAK
        THE_FURNACE_LORE_I("INNER_FLAME_PEAK", "The Furnace Lore I", null, false),
        THE_FURNACE_LORE_II("INNER_FLAME_PEAK", "The Furnace Lore II", new Knowledge[] {THE_FURNACE_LORE_I}, false),
        THE_FURNACE_LORE_III("INNER_FLAME_PEAK", "The Furnace Lore III", new Knowledge[] {THE_FURNACE_LORE_II}, false),

        THE_MERCILESS_LORE_I("INNER_FLAME_PEAK", "The Merciless Lore I", null, false),
        THE_MERCILESS_LORE_II("INNER_FLAME_PEAK", "The Merciless Lore II", new Knowledge[] {THE_MERCILESS_LORE_I}, false),
        THE_MERCILESS_LORE_III("INNER_FLAME_PEAK", "The Merciless Lore III", new Knowledge[] {THE_MERCILESS_LORE_II}, false),


        //BLUE_TOWER
        THE_FLOWING_LORE_I("BLUE_TOWER", "The Flowing Lore I", null, false),
        THE_FLOWING_LORE_II("BLUE_TOWER", "The Flowing Lore II", new Knowledge[] {THE_FLOWING_LORE_I}, false),
        THE_FLOWING_LORE_III("BLUE_TOWER", "The Flowing Lore III", new Knowledge[] {THE_FLOWING_LORE_II}, false),
        ;



        //HELPER CODE
        private final String location;
        private final Knowledge[] requirements;
        private final String deckName;
        private final String alternate_deck_name;
        private final boolean add_decklist;

        Knowledge(final String location, String deckName, Knowledge[] parent, boolean add_decklist) {
            this.location = location;
            this.requirements = parent;
            this.deckName = deckName;
            this.alternate_deck_name = null;
            this.add_decklist = add_decklist;
        }

        Knowledge(final String location, String deckName, Knowledge[] parent, String alternate_deck_name, boolean add_decklist) {
            this.location = location;
            this.requirements = parent;
            this.deckName = deckName;
            this.alternate_deck_name = alternate_deck_name;
            this.add_decklist = add_decklist;
        }

        public void learn()
        {
            FModel.getQuest().getAssets().getKnowledge_unlocked().add(deckName);
            shops.get(location).remove(this);
        }

        public boolean has_prerequisites()
        {
            if (this.requirements == null) return true;
            HashSet<String> unlocked_knowledge = FModel.getQuest().getAssets().getKnowledge_unlocked();
            for (Research.Knowledge req : this.requirements) if (!unlocked_knowledge.contains(req.deckName())) return false;
            return true;
        }

        public static final Map<String, Knowledge> deck_to_enum = new HashMap<>();
        static {
            for (Knowledge k : Knowledge.values()) deck_to_enum.put(k.deckName, k);
        }

    }
}
