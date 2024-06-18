package forge.gamemodes.quest;

import forge.gamemodes.quest.data.QuestPreferences;
import forge.gamemodes.quest.data.QuestPreferences.DifficultyPrefs;
import forge.gamemodes.quest.data.QuestPreferences.QPref;
import forge.gamemodes.quest.io.MainWorldDuelReader;
import forge.model.FModel;
import forge.util.CollectionSuppliers;
import forge.util.MyRandom;
import forge.util.maps.EnumMapOfLists;
import forge.util.maps.MapOfLists;
import forge.util.storage.IStorage;
import forge.util.storage.StorageBase;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MainWorldEventDuelManager implements QuestEventDuelManagerInterface {

    public final MapOfLists<DuelBucket, QuestEventDuel> sortedDuels = new EnumMapOfLists<>(DuelBucket.class, CollectionSuppliers.arrayLists());
    protected final IStorage<QuestEventDuel> allDuels;

    /**
     * Instantiate all events and difficulty lists.
     *
     * @param dir &emsp; File object
     */
    public MainWorldEventDuelManager(final File dir) {
        allDuels = new StorageBase<>("Quest duels", new MainWorldDuelReader(dir));
        assembleDuelDifficultyLists();
    }

    public Iterable<QuestEventDuel> getAllDuels() {
        return allDuels;
    }

    public Iterable<QuestEventDuel> getDuels(DuelBucket difficulty) {
        return sortedDuels.get(difficulty);
    }

    // define fallback orders if there aren't enough opponents defined for a particular difficultly level
    private static List<DuelBucket> easyOrder = Arrays.asList(DuelBucket.EASY, DuelBucket.MEDIUM, DuelBucket.HARD, DuelBucket.EXPERT);
    private static List<DuelBucket> mediumOrder = Arrays.asList(DuelBucket.MEDIUM, DuelBucket.HARD, DuelBucket.EASY, DuelBucket.EXPERT);
    private static List<DuelBucket> hardOrder = Arrays.asList(DuelBucket.HARD, DuelBucket.MEDIUM, DuelBucket.EASY, DuelBucket.EXPERT);
    private static List<DuelBucket> expertOrder = Arrays.asList(DuelBucket.EXPERT, DuelBucket.HARD, DuelBucket.MEDIUM, DuelBucket.EASY);
    private static List<DuelBucket> wildOrder = Arrays.asList(DuelBucket.WILD);

    private List<DuelBucket> getOrderForDifficulty(DuelBucket d) {
        final List<DuelBucket> difficultyOrder;

        switch (d) {
            case EASY:
                difficultyOrder = easyOrder;
                break;
            case MEDIUM:
                difficultyOrder = mediumOrder;
                break;
            case HARD:
                difficultyOrder = hardOrder;
                break;
            case EXPERT:
                difficultyOrder = expertOrder;
                break;
            case WILD:
                difficultyOrder = wildOrder;
                break;
            default:
                throw new RuntimeException("unhandled difficulty: " + d);
        }

        return difficultyOrder;
    }

    private void addDuel(List<QuestEventDuel> outList, DuelBucket targetDifficulty, int toAdd) {

        // if there's no way we can satisfy the request, return now
        if (allDuels.size() <= toAdd) {
            return;
        }

        final List<DuelBucket> difficultyOrder = getOrderForDifficulty(targetDifficulty);

        for (DuelBucket d : difficultyOrder) { // will add duels from preferred difficulty, will use others if the former has too few options.
            for (QuestEventDuel duel : sortedDuels.get(d)) {
                if (toAdd <= 0) {
                    return;
                }
                if (!outList.contains(duel)) {
                    outList.add(duel);
                    toAdd--;
                }
            }
        }

    }

    private void addRandomDuel(final List<QuestEventDuel> listOfDuels, final DuelBucket difficulty) {

        QuestEventDuel duel = new QuestEventDuel();

        List<DuelBucket> difficultyOrder = getOrderForDifficulty(difficulty);
        List<QuestEventDuel> possibleDuels = new ArrayList<>();
        for (DuelBucket diff : difficultyOrder) {
            possibleDuels = new ArrayList<>(sortedDuels.get(diff));
            if (!possibleDuels.isEmpty()) {
                break;
            }
        }

        QuestEventDuel randomOpponent = possibleDuels.get(((int) (possibleDuels.size() * MyRandom.getRandom().nextDouble())));

        duel.setTitle("Random Opponent");
        duel.setIconImageKey(randomOpponent.getIconImageKey());
        duel.setOpponentName(randomOpponent.getTitle());
        duel.setDifficulty(difficulty);
        duel.setProfile(randomOpponent.getProfile());
        duel.setShowDifficulty(false);
        duel.setDescription("Fight a random opponent");
        duel.setEventDeck(randomOpponent.getEventDeck());

        listOfDuels.add(duel);

    }

    /**
     * Generates an array of new duel opponents based on current win conditions.
     *
     * @return an array of {@link java.lang.String} objects.
     */
    public final List<QuestEventDuel> generateDuels() {

        final QuestPreferences questPreferences = FModel.getQuestPreferences();
        boolean moreDuelChoices = questPreferences.getPrefInt(QPref.MORE_DUEL_CHOICES) > 0;

        if (FModel.getQuest().getAchievements() == null) {
            return null;
        }

        final QuestController qCtrl = FModel.getQuest();
        final int numberOfWins = qCtrl.getAchievements().getWin();

        final int index = qCtrl.getAchievements().getDifficulty();
        final List<QuestEventDuel> duelOpponents = new ArrayList<>();

        DuelBucket randomDuelDifficulty;
        
        double randomDouble = MyRandom.getRandom().nextDouble();

        if (numberOfWins < questPreferences.getPrefInt(DifficultyPrefs.WINS_MEDIUMAI, index)) {
            addDuel(duelOpponents, DuelBucket.EASY, 3);
            if(areWildDecksWanted() && isWildDeckAvailable() && randomDouble * 2 < 1) {
                randomDuelDifficulty = DuelBucket.WILD; 
            } else {
                randomDuelDifficulty = DuelBucket.EASY;                
            }
        } else if (numberOfWins == questPreferences.getPrefInt(DifficultyPrefs.WINS_MEDIUMAI, index)) {
            addDuel(duelOpponents, DuelBucket.EASY, 1);
            addDuel(duelOpponents, DuelBucket.MEDIUM, 2);
            if(areWildDecksWanted() && isWildDeckAvailable() && randomDouble * 2 < 1) {
                randomDuelDifficulty = DuelBucket.WILD; 
            } else {            
                randomDuelDifficulty = DuelBucket.MEDIUM;
            }
        } else if (numberOfWins < questPreferences.getPrefInt(DifficultyPrefs.WINS_HARDAI, index)) {
            addDuel(duelOpponents, DuelBucket.MEDIUM, 3);
            randomDuelDifficulty = DuelBucket.MEDIUM;
        } else if (numberOfWins == questPreferences.getPrefInt(DifficultyPrefs.WINS_HARDAI, index)) {
            addDuel(duelOpponents, DuelBucket.MEDIUM, 1);
            addDuel(duelOpponents, DuelBucket.HARD, 2);
            if(areWildDecksWanted() && isWildDeckAvailable() && randomDouble * 2 < 1) {
                randomDuelDifficulty = DuelBucket.WILD; 
            } else {                        
                randomDuelDifficulty = DuelBucket.HARD;
            }
        } else if (numberOfWins < questPreferences.getPrefInt(DifficultyPrefs.WINS_EXPERTAI, index)) {
            addDuel(duelOpponents, DuelBucket.HARD, 3);
            if(areWildDecksWanted() && isWildDeckAvailable() && randomDouble * 2 < 1) {
                randomDuelDifficulty = DuelBucket.WILD; 
            } else {                        
                randomDuelDifficulty = DuelBucket.HARD;
            }
        } else {
            addDuel(duelOpponents, DuelBucket.HARD, 2);
            addDuel(duelOpponents, DuelBucket.EXPERT, 1);
            if(areWildDecksWanted() && isWildDeckAvailable()) {
                if(randomDouble * 2 < 1) {
                    randomDuelDifficulty = DuelBucket.WILD;                                    
                } else if(randomDouble * 6 < 5) {
                    randomDuelDifficulty = DuelBucket.HARD;                    
                } else {
                    randomDuelDifficulty = DuelBucket.EXPERT;                    
                }
             } else {
                if (randomDouble * 3 < 2) {
                    randomDuelDifficulty = DuelBucket.HARD;
                } else  {
                    randomDuelDifficulty = DuelBucket.EXPERT;
                }                
            }
            
        }

        if (moreDuelChoices) {
            if (numberOfWins == questPreferences.getPrefInt(DifficultyPrefs.WINS_MEDIUMAI, index)) {
                addDuel(duelOpponents, DuelBucket.EASY, 1);
            } else if (numberOfWins < questPreferences.getPrefInt(DifficultyPrefs.WINS_HARDAI, index)) {
                addDuel(duelOpponents, DuelBucket.EASY, 1);
            } else if (numberOfWins == questPreferences.getPrefInt(DifficultyPrefs.WINS_HARDAI, index)) {
                addDuel(duelOpponents, DuelBucket.MEDIUM, 1);
            } else {
                addDuel(duelOpponents, DuelBucket.MEDIUM, 1);
                addDuel(duelOpponents, DuelBucket.EASY, 1);
            }
        }
        
        // Now to add the wild opponents
        addDuel(duelOpponents, DuelBucket.WILD, FModel.getQuestPreferences().getPrefInt(QPref.WILD_OPPONENTS_NUMBER));
        addRandomDuel(duelOpponents, randomDuelDifficulty);

        return duelOpponents;

    }

    private boolean areWildDecksWanted() {
        int wildOpponentsPreference = FModel.getQuestPreferences().getPrefInt(QPref.WILD_OPPONENTS_NUMBER);
        return wildOpponentsPreference > 0;
    }
    
    private boolean isWildDeckAvailable() {
        List<QuestEventDuel> wildList = (List<QuestEventDuel>) sortedDuels.get(DuelBucket.WILD);
        return !wildList.isEmpty();
    }
    
    /**
     * <p>
     * assembleDuelDifficultyLists.
     * </p>
     * Assemble duel deck difficulty lists
     */
    protected void assembleDuelDifficultyLists() {

        sortedDuels.clear();

        for (DuelBucket bucket : DuelBucket.values()) {
            sortedDuels.put(bucket, new ArrayList<>());
        }

        for (final QuestEventDuel qd : allDuels) {
            sortedDuels.add(qd.getDifficulty(), qd);
        }

    }

    /** */
    public void randomizeOpponents() {
        for (DuelBucket qd : sortedDuels.keySet()) {
            List<QuestEventDuel> list = (List<QuestEventDuel>) sortedDuels.get(qd);
            Collections.shuffle(list, MyRandom.getRandom());
        }
    }
    
}
