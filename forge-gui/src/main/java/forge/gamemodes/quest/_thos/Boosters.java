package forge.gamemodes.quest._thos;

import forge._thos.Economy;
import forge.gamemodes.quest.QuestController;
import forge.gamemodes.quest.QuestUtil_MatchData;
import forge.item.PaperCard;
import forge.model.FModel;
import lombok.Getter;
import lombok.experimental.Accessors;

import java.util.*;

public class Boosters {

    public static Boosters INSTANCE;

    public static HashMap<Type, List<PaperCard>> boosters = new HashMap<>();
    private static Random rand = new Random();

    public Boosters()
    {
        if (INSTANCE != null) return; INSTANCE = this;

        //Init booster packs
        HashSet<String> unlocked_cards = FModel.getQuest().getAssets().getCards_unlocked();
        for (Type t : Type.values())
        {
            if (!t.is_dropping_rewards) continue;

            List<PaperCard> cards = QuestController.getPrecons().get(t.id()).getDeck().getMain().to_unique_list();

            for (int i = cards.size() - 1; i >= 0; i--)
            {
                if (!unlocked_cards.contains( cards.get(i).getName()) ) continue;
                cards.remove(i);
            }

            boosters.put(t, cards);
        }
    }

    public void reward(String name)
    {
        Object[] o = parse(name);
        Type t = (Type) o[0];
        Difficulty d = (Difficulty) o[1];

        if (!t.is_dropping_rewards()) return;

        reward_card(t, d);
        //award credits based on difficulty
        reward_crystals(d);

    }

    public void punish(String name)
    {
        Object[] o = parse(name);
        Type t = (Type) o[0];
        Difficulty d = (Difficulty) o[1];

        if (!t.is_dropping_rewards()) return;

        //TODO:

//        punish_health(d);
    }

    public void reward_card(Type type, Difficulty difficulty)
    {
        //determine what pool we're rolling chance for here
        List<PaperCard> booster = boosters.get(type);
        if (booster.isEmpty()) booster = boosters.get(type.alternate);


        //Get chance, swap out type if pool is empty
        int chance = Economy.CHANCE_BOOSTER_BASE;
        if (type.has_requirement())
        {
            chance += Economy.CHANCE_BOOSTER_HAS_KNOWLEDGE;
            switch(difficulty)
            {
                case ADEPT: chance += Economy.CHANCE_BOOSTER_ENEMY_ADEPT; break;
                case QUASI_WIZARD: chance += Economy.CHANCE_BOOSTER_ENEMY_ADEPT + Economy.CHANCE_BOOSTER_ENEMY_QUASI_WIZARD; break;
            }
        }

        System.out.println(chance);

        //roll
        if (rand.nextInt( 100) + 1 > chance) return; //if 1 to 100 > chance, roll failed


        //If the both the default and alternate pools have no cards to give, reward crystals
        if (booster.isEmpty())
        {
            extra_crystals = true;
            return;
        }

        //Roll for card and exclude it from future rolls
        PaperCard card = booster.remove(rand.nextInt(booster.size()));
        FModel.getQuest().getAssets().getCards_unlocked().add(card.getName());

        //Add 4 of card to inventory
        FModel.getQuest().getCards().addSingleCard(card, 4);

        //Update reward UI
        QuestUtil_MatchData.CARDS = new ArrayList<>();
        QuestUtil_MatchData.CARDS.add(card);
    }


    public static boolean extra_crystals = false;
    public void reward_crystals(Difficulty d)
    {
        int amount = 0;
        switch(d)
        {
            case NEOPHYTE:
                amount += Economy.CRYSTALS_ENEMY_NEOPHYTE;
                break;
            case ADEPT:
                amount += Economy.CRYSTALS_ENEMY_ADEPT;
                break;
            case QUASI_WIZARD:
                amount += Economy.CRYSTALS_ENEMY_QUASI_WIZARD;
                break;
        }

        FModel.getQuest().getAssets().addCredits(amount);
        QuestUtil_MatchData.STR_CRYSTALS = "You gained " + amount + " crystals";
        extra_crystals = false;
    }


    //HELPER

    public Object[] parse(String name)
    {
        //Vampire [Neophyte] [Vampire] [Black] - Control - Tresspasser's Curse

        Object[] ret = new Object[2];
        boolean is_finding_type = true;
        boolean is_finding_difficulty = true;

        for(String token : name.split(" "))
        {
            if (token.charAt(0) != '[') continue;

            if (is_finding_type)
            {
                Type t = Type.ids.get(token);
                if (t != null)
                {
                    ret[0] = t;
                    is_finding_type = false;
                }
            }

            if (is_finding_difficulty)
            {
                Difficulty d = Difficulty.ids.get(token);
                if (d != null)
                {
                    ret[1] = d;
                    is_finding_difficulty = false;
                }
            }

        }
        return ret;
    }

    @Getter @Accessors(fluent = true, chain = false) public enum Type {

        BLACK("_[BLACK]", null, Research.Knowledge.DARKNESS),
        WHITE("_[WHITE]", null, null),
        RED("_[RED]", null, null),
        BLUE("_[BLUE]", null, null),
        GREEN("_[GREEN]", null, Research.Knowledge.FOREST),
//        PRISMATIC("_[GREEN]", null),
//        ARTIFACT("_[GREEN]", null),
//        ELDRAZI("_[GREEN]", null),

        VAMPIRE("[VAMPIRE]", BLACK, Research.Knowledge.VAMPIRES),
        NONE("[NONE]", null, false, null),



        ;


        //HELPER CODE
        private final String id;
        private final Type alternate;
        private final boolean is_dropping_rewards;
        private final Research.Knowledge requirement;


        Type(final String id, final Type alternate, Research.Knowledge requirement) {
            this.id = id;
            this.alternate = alternate;
            this.is_dropping_rewards = true;
            this.requirement = requirement;
        }

        Type(final String id, final Type alternate, final boolean is_dropping_rewards, Research.Knowledge requirement) {
            this.id = id;
            this.alternate = alternate;
            this.is_dropping_rewards = is_dropping_rewards;
            this.requirement = requirement;
        }


        public static final Map<String, Type> ids = new HashMap<>();
        static {
            for (Type t : Type.values()) ids.put(t.id, t);
        }

        public boolean has_requirement()
        {
            return FModel.getQuest().getAssets().getKnowledge_unlocked().contains(this.requirement.deckName());
        }


    }

    @Getter @Accessors(fluent = true, chain = false) public enum Difficulty {

        NEOPHYTE("[NEOPHYTE]"),
        ADEPT("[ADEPT]"),
        QUASI_WIZARD("[QUASI_WIZARD]"),
        WIZARD("[WIZARD]"),

        ;


        //HELPER CODE
        private final String id;
        Difficulty(final String id) {
            this.id = id;
        }



        public static final Map<String, Difficulty> ids = new HashMap<>();
        static {
            for (Difficulty t : Difficulty.values()) ids.put(t.id, t);
        }

    }

}
