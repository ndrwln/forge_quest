package forge.gamemodes.quest._thos;

import forge.gamemodes.quest.QuestController;
import forge.gamemodes.quest.QuestUtil_MatchData;
import forge.gamemodes.quest.data.QuestAssets;
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
        if (INSTANCE != null) return;

        INSTANCE = this;

        //Init booster packs
        for (Type t : Type.values())
        {
            if (!t.is_dropping_rewards) continue;

            List<PaperCard> cards = QuestController.getPrecons().get(t.id()).getDeck().getMain().to_unique_list();

            QuestController q = FModel.getQuest();
            QuestAssets a = q.getAssets();
            HashSet<String> unlocked_cards = FModel.getQuest().getAssets().getCards_unlocked();

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

        //TODO: logic for booster chance based on lore
        PaperCard card = reward_card(t);

        //award credits based on difficulty
        reward_crystals(d);

    }

    public void punish(String name)
    {

    }

    public PaperCard reward_card(Type type)
    {
        //Roll for card and exclude it from future rolls
        List<PaperCard> booster = boosters.get(type);
        PaperCard card = booster.remove(rand.nextInt(booster.size()));
        FModel.getQuest().getAssets().getCards_unlocked().add(card.getName());

        //Add 4 of card to inventory
        FModel.getQuest().getCards().addSingleCard(card, 4);

        //Update reward UI
        QuestUtil_MatchData.CARDS = new ArrayList<>();
        QuestUtil_MatchData.CARDS.add(card);

        return card;
    }

    public void reward_crystals(Difficulty d)
    {
        int amount = 0;
        switch(d)
        {
            case NEOPHYTE:
                amount += 5;
                break;
            case ADEPT:
                amount += 10;
                break;
            case QUASI_WIZARD:
                amount += 20;
                break;
        }

        FModel.getQuest().getAssets().addCredits(amount);
        QuestUtil_MatchData.STR_CRYSTALS = "Scavenged materials can sell for " + amount + " crystals";

    }

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

        BLACK("_[BLACK]", null),
        WHITE("_[WHITE]", null),
        RED("_[RED]", null),
        BLUE("_[BLUE]", null),
        GREEN("_[GREEN]", null),
//        PRISMATIC("_[GREEN]", null),
//        ARTIFACT("_[GREEN]", null),
//        ELDRAZI("_[GREEN]", null),

        VAMPIRE("[VAMPIRE]", new Type[] {BLACK}),
        NONE("[NONE]", null, false),


        ;


        //HELPER CODE
        private final String id;
        private final Type[] alternates;
        private final boolean is_dropping_rewards;


        Type(final String id, final Type[] alternates) {
            this.id = id;
            this.alternates = alternates;
            this.is_dropping_rewards = true;
        }

        Type(final String id, final Type[] alternates, final boolean is_dropping_rewards) {
            this.id = id;
            this.alternates = alternates;
            this.is_dropping_rewards = is_dropping_rewards;
        }



        public static final Map<String, Type> ids = new HashMap<>();
        static {
            for (Type t : Type.values()) ids.put(t.id, t);
        }

    }

    @Getter @Accessors(fluent = true, chain = false) public enum Difficulty {

        NEOPHYTE("[NEOPHYTE]"),
        ADEPT("[ADEPT]"),
        QUASI_WIZARD("[QUASI-WIZARD]"),
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
