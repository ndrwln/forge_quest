package forge.gamemodes.quest;

import com.google.common.collect.ImmutableList;
import forge.LobbyPlayer;
import forge.game.GameEndReason;
import forge.game.GameOutcome;
import forge.game.GameView;
import forge.game.player.*;
import forge.gamemodes.quest._thos.Boosters;
import forge.gamemodes.quest.bazaar.QuestItemType;
import forge.gamemodes.quest.data.QuestPreferences;
import forge.gamemodes.quest.data.QuestPreferences.QPref;
import forge.gui.GuiBase;
import forge.gui.interfaces.IButton;
import forge.gui.interfaces.IWinLoseView;
import forge.gui.util.SGuiChoose;
import forge.item.*;
import forge.item.generation.BoosterSlots;
import forge.localinstance.properties.ForgePreferences.FPref;
import forge.localinstance.skin.FSkinProp;
import forge.model.FModel;
import forge.player.GamePlayerUtil;
import forge.util.Localizer;
import forge.util.MyRandom;
import forge.util.TextUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

public class QuestWinLoseController {
    public final GameView lastGame;
    private final IWinLoseView<? extends IButton> view;
    public final transient boolean wonMatch;
    private final transient boolean isAnte;
    private final transient QuestController qData;
    private final transient QuestEvent qEvent;

    public QuestWinLoseController(final GameView game0, final IWinLoseView<? extends IButton> view0) {
        lastGame = game0;
        view = view0;
        qData = FModel.getQuest();
        qEvent = qData.getCurrentEvent();
        wonMatch = lastGame.isMatchWonBy(GamePlayerUtil.getQuestPlayer());
        isAnte = FModel.getPreferences().getPrefBoolean(FPref.UI_ANTE);
    }

    public void showRewards() {
        view.getBtnRestart().setVisible(false);
        final QuestController qc = FModel.getQuest();

        final LobbyPlayer questLobbyPlayer = GamePlayerUtil.getQuestPlayer();
        PlayerView player = null;
        for (final PlayerView p : lastGame.getPlayers()) {
            if (p.isLobbyPlayer(questLobbyPlayer)) {
                player = p;
            }
        }

        final boolean matchIsNotOver = !lastGame.isMatchOver();
        if (matchIsNotOver) {
            view.getBtnQuit().setText(Localizer.getInstance().getMessage("lblQuitByPayCredits"));
        }
        else {
            view.getBtnContinue().setVisible(false);
            if (wonMatch) {
                view.getBtnQuit().setText(Localizer.getInstance().getMessage("lblGreat") + "!");
            }
            else {
                view.getBtnQuit().setText(Localizer.getInstance().getMessage("lblOK"));
            }
        }

        //give controller a chance to run remaining logic on a separate thread
        view.showRewards(new Runnable() {
            @Override
            public void run() {
                if (matchIsNotOver) { return; } //skip remaining logic if match isn't over yet

                QuestUtil_MatchData.NUM_PROGRESS += 1;
                QuestUtil_MatchData.MATCH_RESULT = wonMatch ? QuestUtil_MatchData.MatchResult.WIN : QuestUtil_MatchData.MatchResult.LOSS;

                if (wonMatch)
                    Boosters.INSTANCE.reward(QuestUtil_MatchData.ENEMY_TITLE);
                else
                    Boosters.INSTANCE.punish(QuestUtil_MatchData.ENEMY_TITLE);

            }
        });
    }

    private void anteReport(final List<PaperCard> cardsWon, final List<PaperCard> cardsLost) {
        // Generate Swing components and attach.
        if (cardsWon != null && !cardsWon.isEmpty()) {
            view.showCards(Localizer.getInstance().getMessage("lblSpoilsWonAnteCard"), cardsWon);
        }
        if (cardsLost != null && !cardsLost.isEmpty()) {
            view.showCards(Localizer.getInstance().getMessage("lblLootedLostAnteCard"), cardsLost);
        }
    }

    public void actionOnQuit() {
        final int x = FModel.getQuestPreferences().getPrefInt(QPref.PENALTY_LOSS);

        // Record win/loss in quest data
        if (wonMatch) {
            qData.getAchievements().addWin();
        }
        else {
            qData.getAchievements().addLost();
        }

        // Reset cards and zeppelin use
        if (qData.getAssets().hasItem(QuestItemType.ZEPPELIN)) {
            qData.getAssets().setItemLevel(QuestItemType.ZEPPELIN, 1);
        }

//        if (qEvent instanceof QuestEventChallenge) {
//            if (wonMatch || (!((QuestEventChallenge)qEvent).isPersistent())) {
//                final String id = ((QuestEventChallenge) qEvent).getId();
//                qData.getAchievements().getCurrentChallenges().remove(id);
//                qData.getAchievements().addLockedChallenge(id);
//
//                // Increment challenge counter to limit challenges available
//                qData.getAchievements().addChallengesPlayed();
//            }
//        }
//
//        qData.setCurrentEvent(null);
        qData.save();
        FModel.getQuestPreferences().save();
    }

    /**
     * <p>
     * awardEventCredits.
     * </p>
     * Generates and displays standard rewards for gameplay and skill level.
     *
     */
    public void awardEventCredits() {
        // TODO use q.qdPrefs to write bonus credits in prefs file
        final StringBuilder sb = new StringBuilder();

        int credTotal;
        int credBase;
        int credGameplay = 0;
        int credUndefeated = 0;
        int credEstates;

        // Basic win bonus
        final int base = FModel.getQuestPreferences().getPrefInt(QPref.REWARDS_BASE);
        final double multiplier = qEvent.getDifficulty().getMultiplier();

        credBase = (int) (base * multiplier);

        sb.append(StringUtils.capitalize(qEvent.getDifficulty().getTitle()));
        sb.append(" opponent: ").append(credBase).append(" credits.\n");

        if(qEvent.getIsRandomMatch()){
            sb.append("Random Opponent Bonus: ").append(credBase).append(" credit").append(credBase > 1 ? "s." : ".").append("\n");
            credBase += credBase;
        }

        final int winMultiplier = Math.min(qData.getAchievements().getWin(), FModel.getQuestPreferences().getPrefInt(QPref.REWARDS_WINS_MULTIPLIER_MAX));
        final int creditsForPreviousWins = (int) ((Double.parseDouble(FModel.getQuestPreferences()
                .getPref(QPref.REWARDS_WINS_MULTIPLIER)) * winMultiplier));

        credBase += creditsForPreviousWins;

        sb.append("Bonus for previous wins: ").append(creditsForPreviousWins).append(
                creditsForPreviousWins != 1 ? " credits.\n" : " credit.\n");

        // Gameplay bonuses (for each game win)
        boolean hasNeverLost = true;
        int lifeDifferenceCredits = 0;

        final LobbyPlayer localHuman = GamePlayerUtil.getQuestPlayer();
        for (final GameOutcome game : lastGame.getOutcomesOfMatch()) {
            if (!game.isWinner(localHuman)) {
                hasNeverLost = false;
                continue; // no rewards for losing a game
            }
            // Alternate win

            // final PlayerStatistics aiRating = game.getStatistics(computer.getName());
            PlayerStatistics humanRating = null;
            for (final Entry<RegisteredPlayer, PlayerStatistics> kvRating : game) {
                if (kvRating.getKey().getPlayer().equals(localHuman)) {
                    humanRating = kvRating.getValue();
                    continue;
                }

                final PlayerOutcome outcome = kvRating.getValue().getOutcome();
                final GameLossReason whyAiLost = outcome.lossState;
                int altReward = getCreditsRewardForAltWin(whyAiLost);

                String winConditionName = "Unknown (bug)";
                if (game.getWinCondition() == GameEndReason.WinsGameSpellEffect) {
                    winConditionName = game.getWinSpellEffect();
                    altReward = getCreditsRewardForAltWin(null);
                }
                else {
                    switch (whyAiLost) {
                    case Poisoned:
                        winConditionName = "Poison";
                        break;
                    case Milled:
                        winConditionName = "Milled";
                        break;
                    case SpellEffect:
                        winConditionName = outcome.loseConditionSpell;
                        break;
                    default:
                        break;
                    }
                }

                if (altReward > 0) {
                    credGameplay += altReward;
                    sb.append(GuiBase.getInterface().isLibgdxPort()
                            ? TextUtil.concatNoSpace("Alternate win condition: ", winConditionName, "! Bonus: ", String.valueOf(altReward), " credits.\n")
                            : TextUtil.concatNoSpace("Alternate win condition: <u>", winConditionName, "</u>! Bonus: ", String.valueOf(altReward), " credits.\n"));
                }
            }
            // Mulligan to zero
            final int cntCardsHumanStartedWith = humanRating.getOpeningHandSize();
            final int mulliganReward = FModel.getQuestPreferences().getPrefInt(QPref.REWARDS_MULLIGAN0);

            if (0 == cntCardsHumanStartedWith) {
                credGameplay += mulliganReward;
                sb.append(TextUtil.concatNoSpace("Mulliganed to zero and still won! Bonus: ", String.valueOf(mulliganReward), " credits.\n"));
            }

            // Early turn bonus
            final int winTurn = game.getLastTurnNumber();
            final int turnCredits = getCreditsRewardForWinByTurn(winTurn);

            if (winTurn == 0) {
                sb.append("Won on turn zero!");
            }
            else if (winTurn == 1) {
                sb.append("Won in one turn!");
            }
            else if (winTurn <= 5) {
                sb.append("Won by turn 5!");
            }
            else if (winTurn <= 10) {
                sb.append("Won by turn 10!");
            }
            else if (winTurn <= 15) {
                sb.append("Won by turn 15!");
            }

            if (turnCredits > 0) {
                credGameplay += turnCredits;
                sb.append(TextUtil.concatNoSpace(" Bonus: ", String.valueOf(turnCredits), " credits.\n"));
            }

            if (game.getLifeDelta() >= 50) {
                lifeDifferenceCredits += Math.max(Math.min((game.getLifeDelta() - 46) / 4, FModel.getQuestPreferences().getPrefInt(QPref.REWARDS_HEALTH_DIFF_MAX)), 0);
            }

        } // End for(game)

        if (lifeDifferenceCredits > 0) {
            sb.append(TextUtil.concatNoSpace("Life total difference: ", String.valueOf(lifeDifferenceCredits), " credits.\n"));
        }

        // Undefeated bonus
        if (hasNeverLost) {
            credUndefeated += FModel.getQuestPreferences().getPrefInt(QPref.REWARDS_UNDEFEATED);
            final int reward = FModel.getQuestPreferences().getPrefInt(QPref.REWARDS_UNDEFEATED);
            sb.append(TextUtil.concatNoSpace("You have not lost once! Bonus: ", String.valueOf(reward), " credits.\n"));
        }

        // Estates bonus
        credTotal = credBase + credGameplay + credUndefeated + lifeDifferenceCredits;
        double estateValue = 0;
        switch (qData.getAssets().getItemLevel(QuestItemType.ESTATES)) {
        case 1:
            estateValue = .1;
            break;
        case 2:
            estateValue = .15;
            break;
        case 3:
            estateValue = .2;
            break;
        default:
            break;
        }
        if (estateValue > 0) {
            credEstates = (int) (estateValue * credTotal);
            sb.append("Estates bonus (").append((int) (100 * estateValue)).append("%): ").append(credEstates).append(" credits.\n");
            credTotal += credEstates;
        }

        // Final output
        sb.append("\n");
        if (credTotal < 100) {
            sb.append("You've earned ");
        }
        else if (credTotal < 250) {
            sb.append("Could be worse: ");
        }
        else if (credTotal < 500) {
            sb.append("A respectable ");
        }
        else if (credTotal < 750) {
            sb.append("An impressive ");
        }
        else {
            sb.append("Spectacular match! ");
        }

        sb.append(TextUtil.concatWithSpace(String.valueOf(credTotal), "credits in total."));
        qData.getAssets().addCredits(credTotal);

//        view.showMessage(sb.toString(), Localizer.getInstance().getMessage("lblGameplayResults"), FSkinProp.ICO_QUEST_GOLD);
        QuestUtil_MatchData.STR_CRYSTALS = sb.toString();
    }

    /**
     * <p>
     * awardRandomRare.
     * </p>
     * Generates and displays a random rare win case.
     *
     */
    private void awardRandomRare(final String message) {
        final PaperCard c = qData.getCards().addRandomRare();
        final List<PaperCard> cardsWon = new ArrayList<>();
        cardsWon.add(c);

        view.showCards(message, cardsWon);
    }

    /**
     * <p>
     * awardWinStreakBonus.
     * </p>
     * Generates and displays a reward for maintaining a win streak.
     *
     */
    private void awardWinStreakBonus() {
        final int currentStreak = (qData.getAchievements().getWinStreakCurrent() + 1) % 50;

        final List<PaperCard> cardsWon = new ArrayList<>();
        List<PaperCard> cardsToAdd;
        String typeWon;
        boolean addDraftToken = false;

        switch (currentStreak) {
        case 3:
            cardsWon.addAll(qData.getCards().addRandomCommon(1));
            typeWon = "common";
            break;
        case 5:
            cardsWon.addAll(qData.getCards().addRandomUncommon(1));
            typeWon = "uncommon";
            break;
        case 7:
            cardsWon.addAll(qData.getCards().addRandomRareNotMythic(1));
            typeWon = "rare";
            break;
        case 10:
            cardsToAdd = qData.getCards().addRandomMythicRare(1);
            if (cardsToAdd != null) {
                cardsWon.addAll(cardsToAdd);
                typeWon = "mythic rare";
            } else {
                cardsWon.addAll(qData.getCards().addRandomRareNotMythic(3));
                typeWon = "rare";
            }
            break;
        case 25:
            cardsToAdd = qData.getCards().addRandomMythicRare(5);
            if (cardsToAdd != null) {
                cardsWon.addAll(cardsToAdd);
                typeWon = "mythic rare";
            } else {
                cardsWon.addAll(qData.getCards().addRandomRareNotMythic(15));
                typeWon = "rare";
            }
            addDraftToken = true;
            break;
        case 0: //The 50th win in the streak is 0, since (50 % 50 == 0)
            cardsToAdd = qData.getCards().addRandomMythicRare(10);
            if (cardsToAdd != null) {
                cardsWon.addAll(cardsToAdd);
                typeWon = "mythic rare";
            } else {
                cardsWon.addAll(qData.getCards().addRandomRareNotMythic(30));
                typeWon = "rare";
            }
            addDraftToken = true;
            break;
        default:
            return;
        }

        if (addDraftToken) {
            view.showMessage(Localizer.getInstance().getMessage("lblAchieving25WinStreakAwarded"), Localizer.getInstance().getMessage("lblBonusDraftTokenReward"), FSkinProp.ICO_QUEST_COIN);
            qData.getAchievements().addDraftToken();
        }

        if (!cardsWon.isEmpty()) {
            view.showCards(Localizer.getInstance().getMessage("lblAchievedNWinStreakWinMTypeCards", (currentStreak == 0 ? "50" : String.valueOf(currentStreak)), String.valueOf(cardsWon.size()), typeWon), cardsWon);
        }
    }

    /**
     * <p>
     * awardJackpot.
     * </p>
     * Generates and displays jackpot win case.
     *
     */
    private void awardJackpot() {
        final List<PaperCard> cardsWon = qData.getCards().addRandomRare(10);
        view.showCards(Localizer.getInstance().getMessage("lblJustWonTenRandomRares"), cardsWon);
    }


    public void awardBooster() {}

    private SealedProduct.Template getBoosterTemplate() {
        return new SealedProduct.Template(ImmutableList.of(
                Pair.of(BoosterSlots.COMMON, FModel.getQuestPreferences().getPrefInt(QPref.BOOSTER_COMMONS)),
                Pair.of(BoosterSlots.UNCOMMON, FModel.getQuestPreferences().getPrefInt(QPref.BOOSTER_UNCOMMONS)),
                Pair.of(BoosterSlots.RARE_MYTHIC, FModel.getQuestPreferences().getPrefInt(QPref.BOOSTER_RARES))
        ));
    }

    /**
     * <p>
     * awardChallengeWin.
     * </p>
     * Generates and displays win case for challenge event.
     *
     */
    private void awardChallengeWin() {
        final long questRewardCredits = ((QuestEventChallenge) qEvent).getCreditsReward();

        String winMessage = ((QuestEventChallenge)qEvent).getWinMessage();
        if (!winMessage.isEmpty()) {
            view.showMessage(winMessage.replace("\\n", "\n"), Localizer.getInstance().getMessage("lblCongratulations"), FSkinProp.ICO_QUEST_NOTES);
        }

        qData.getAssets().addCredits(questRewardCredits);

        view.showMessage(Localizer.getInstance().getMessage("lblChallengeCompletedBountyIS", String.valueOf(questRewardCredits)), Localizer.getInstance().getMessage("lblChallengeRewardsForEvent", qEvent.getTitle()), FSkinProp.ICO_QUEST_BOX);

        awardSpecialReward(null);
    }

    /**
     * <p>
     * awardSpecialReward.
     * </p>
     * This builds the card reward based on the string data.
     * @param message String, reward text to be displayed, if any
     */
    private void awardSpecialReward(String message) {
        final List<InventoryItem> itemsWon = qEvent.getCardRewardList();

        if (itemsWon == null || itemsWon.isEmpty()) {
            return;
        }

        final List<PaperCard> cardsWon = new ArrayList<>();

        for (final InventoryItem ii : itemsWon) {
            if (ii instanceof PaperCard) {
                cardsWon.add((PaperCard) ii);
            }
            else if (ii instanceof TournamentPack || ii instanceof BoosterPack) {
                final List<PaperCard> boosterCards = new ArrayList<>();
                SealedProduct booster;
                if (ii instanceof BoosterPack) {
                    booster = (BoosterPack) ((BoosterPack) ii).clone();
                    boosterCards.addAll(booster.getCards());
                } else {
                    booster = (TournamentPack) ((TournamentPack) ii).clone();
                    boosterCards.addAll(booster.getCards());
                }
                if (!boosterCards.isEmpty()) {
                    qData.getCards().addAllCards(boosterCards);
                    view.showCards("Extra " + ii.getName() + "!", boosterCards);
                }
            }
            else if (ii instanceof IQuestRewardCard) {
                final List<PaperCard> cardChoices = ((IQuestRewardCard) ii).getChoices();
                final PaperCard chosenCard = (null == cardChoices ? null : SGuiChoose.one("Choose " + ii.getName(), cardChoices));
                if (null != chosenCard) {
                    cardsWon.add(chosenCard);
                }
            }
        }
        if (!cardsWon.isEmpty()) {
            if (message == null) {
                message = "Cards Won";
            }
            view.showCards(message, cardsWon);
            qData.getCards().addAllCards(cardsWon);
        }
    }

    private void penalizeLoss() {
        final int x = FModel.getQuestPreferences().getPrefInt(QPref.PENALTY_LOSS);
        QuestUtil_MatchData.STR_CRYSTALS_LOSS = Localizer.getInstance().getMessage("lblYouHaveLostNCredits", String.valueOf(x));
    }

    /**
     * <p>
     * getLuckyCoinResult.
     * </p>
     * A chance check, for rewards like random rares.
     *
     * @return boolean
     */
    private boolean getLuckyCoinResult() {
        final boolean hasCoin = qData.getAssets().getItemLevel(QuestItemType.LUCKY_COIN) >= 1;

        return MyRandom.getRandom().nextFloat() <= (hasCoin ? 0.65f : 0.5f);
    }

    /**
     * <p>
     * getCreditsRewardForAltWin.
     * </p>
     * Retrieves credits for win under special conditions.
     *
     * @param whyAiLost GameLossReason
     * @return int
     */
    private static int getCreditsRewardForAltWin(final GameLossReason whyAiLost) {
        final QuestPreferences qp = FModel.getQuestPreferences();
        if (null == whyAiLost) {
            // Felidar, Helix Pinnacle, etc.
            return qp.getPrefInt(QPref.REWARDS_ALTERNATIVE);
        }
        switch (whyAiLost) {
        case LifeReachedZero:
            return 0; // nothing special here, ordinary kill
        case Milled:
            return qp.getPrefInt(QPref.REWARDS_MILLED);
        case Poisoned:
            return qp.getPrefInt(QPref.REWARDS_POISON);
        case SpellEffect: // Door to Nothingness, etc.
            return qp.getPrefInt(QPref.REWARDS_ALTERNATIVE);
        default:
            return 0;
        }
    }

    /**
     * <p>
     * getCreditsRewardForWinByTurn.
     * </p>
     * Retrieves credits for win on or under turn count.
     *
     * @param iTurn int - turn count
     * @return int credits won
     */
    private static int getCreditsRewardForWinByTurn(final int iTurn) {
        int credits;

        if (iTurn <= 1) {
            credits = FModel.getQuestPreferences().getPrefInt(QPref.REWARDS_TURN1);
        }
        else if (iTurn <= 5) {
            credits = FModel.getQuestPreferences().getPrefInt(QPref.REWARDS_TURN5);
        }
        else if (iTurn <= 10) {
            credits = FModel.getQuestPreferences().getPrefInt(QPref.REWARDS_TURN10);
        }
        else if (iTurn <= 15) {
            credits = FModel.getQuestPreferences().getPrefInt(QPref.REWARDS_TURN15);
        }
        else {
            credits = 0;
        }

        return credits;
    }
}
