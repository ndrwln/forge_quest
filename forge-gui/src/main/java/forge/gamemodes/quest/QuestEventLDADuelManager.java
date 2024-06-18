/*
 * Forge: Play Magic: the Gathering.
 * Copyright (C) 2011  Forge Team
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package forge.gamemodes.quest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import forge.deck.CardArchetypeLDAGenerator;
import forge.deck.io.Archetype;
import forge.game.GameFormat;
import forge.gamemodes.quest.data.QuestPreferences;
import forge.gamemodes.quest.data.QuestPreferences.DifficultyPrefs;
import forge.gamemodes.quest.data.QuestPreferences.QPref;
import forge.model.FModel;
import forge.util.CollectionSuppliers;
import forge.util.MyRandom;
import forge.util.maps.EnumMapOfLists;
import forge.util.maps.MapOfLists;

/**
 * QuestEventManager.
 *
 * @author Forge
 * @version $Id: QuestEventManager.java 20404 2013-03-17 05:34:13Z myk $
 */
public class QuestEventLDADuelManager implements QuestEventDuelManagerInterface {

    private List<Archetype> archetypes;
    private final MapOfLists<DuelBucket, QuestEventDuel> sortedDuels = new EnumMapOfLists<>(DuelBucket.class, CollectionSuppliers.arrayLists());
    private GameFormat baseFormat;

    public QuestEventLDADuelManager(GameFormat baseFormat){
        this.baseFormat = baseFormat;
        archetypes = CardArchetypeLDAGenerator.ldaArchetypes.get(baseFormat.getName());
        assembleDuelDifficultyLists();
    }

    private void assembleDuelDifficultyLists() {

        sortedDuels.clear();
        sortedDuels.put(DuelBucket.EASY, new ArrayList<>());
        sortedDuels.put(DuelBucket.MEDIUM, new ArrayList<>());
        sortedDuels.put(DuelBucket.HARD, new ArrayList<>());
        sortedDuels.put(DuelBucket.EXPERT, new ArrayList<>());

        int i=0;
        for(Archetype archetype : archetypes){
            QuestEventLDADuel duel = new QuestEventLDADuel(archetype,baseFormat);
            duel.setDescription("Randomly generated "+archetype.getName()+" archetype deck.");
            duel.setName(archetype.getName());
            duel.setTitle(archetype.getName());
            duel.setOpponentName(archetype.getName());
            DuelBucket diff = DuelBucket.EASY;
            if(i <= Float.valueOf(archetypes.size())*.1){
                diff = DuelBucket.EXPERT;
            }else if(i <= Float.valueOf(archetypes.size())*.4){
                diff = DuelBucket.HARD;
            }else if(i <= Float.valueOf(archetypes.size())*.7) {
                diff = DuelBucket.MEDIUM;
            }
            duel.setDifficulty(diff);
            sortedDuels.add(diff, duel);
            i++;
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

        DuelBucket randomDuelDifficulty = DuelBucket.EASY;

        if (numberOfWins < questPreferences.getPrefInt(DifficultyPrefs.WINS_MEDIUMAI, index)) {
            addDuel(duelOpponents, DuelBucket.EASY, 3);
            randomDuelDifficulty = DuelBucket.EASY;
        } else if (numberOfWins == questPreferences.getPrefInt(DifficultyPrefs.WINS_MEDIUMAI, index)) {
            addDuel(duelOpponents, DuelBucket.EASY, 1);
            addDuel(duelOpponents, DuelBucket.MEDIUM, 2);
            randomDuelDifficulty = DuelBucket.MEDIUM;
        } else if (numberOfWins < questPreferences.getPrefInt(DifficultyPrefs.WINS_HARDAI, index)) {
            addDuel(duelOpponents, DuelBucket.MEDIUM, 3);
            randomDuelDifficulty = DuelBucket.MEDIUM;
        } else if (numberOfWins == questPreferences.getPrefInt(DifficultyPrefs.WINS_HARDAI, index)) {
            addDuel(duelOpponents, DuelBucket.MEDIUM, 1);
            addDuel(duelOpponents, DuelBucket.HARD, 2);
            randomDuelDifficulty = DuelBucket.HARD;
        } else if (numberOfWins < questPreferences.getPrefInt(DifficultyPrefs.WINS_EXPERTAI, index)) {
            addDuel(duelOpponents, DuelBucket.HARD, 3);
            randomDuelDifficulty = DuelBucket.HARD;
        } else {
            addDuel(duelOpponents, DuelBucket.HARD, 2);
            addDuel(duelOpponents, DuelBucket.EXPERT, 1);
            if (MyRandom.getRandom().nextDouble() * 3 < 2) {
                randomDuelDifficulty = DuelBucket.HARD;
            } else {
                randomDuelDifficulty = DuelBucket.EXPERT;
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

        addRandomDuel(duelOpponents, randomDuelDifficulty);

        return duelOpponents;

    }

    private void addDuel(List<QuestEventDuel> outList, DuelBucket targetDifficulty, int toAdd) {

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

    // define fallback orders if there aren't enough opponents defined for a particular difficultly level
    private static List<DuelBucket> easyOrder = Arrays.asList(DuelBucket.EASY, DuelBucket.MEDIUM, DuelBucket.HARD, DuelBucket.EXPERT);
    private static List<DuelBucket> mediumOrder = Arrays.asList(DuelBucket.MEDIUM, DuelBucket.HARD, DuelBucket.EASY, DuelBucket.EXPERT);
    private static List<DuelBucket> hardOrder = Arrays.asList(DuelBucket.HARD, DuelBucket.MEDIUM, DuelBucket.EASY, DuelBucket.EXPERT);
    private static List<DuelBucket> expertOrder = Arrays.asList(DuelBucket.EXPERT, DuelBucket.HARD, DuelBucket.MEDIUM, DuelBucket.EASY);


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
            default:
                throw new RuntimeException("unhandled difficulty: " + d);
        }

        return difficultyOrder;
    }

    @Override
    public Iterable<QuestEventDuel> getAllDuels() {
        return new ArrayList<>();
    }

    public Iterable<QuestEventDuel> getDuels(DuelBucket difficulty) {
        return sortedDuels.get(difficulty);
    }

    @Override
    /** */
    public void randomizeOpponents() {
        for (DuelBucket qd : sortedDuels.keySet()) {
            List<QuestEventDuel> list = (List<QuestEventDuel>) sortedDuels.get(qd);
            Collections.shuffle(list, MyRandom.getRandom());
        }
    }
}
