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

import com.google.common.collect.ImmutableMap;
import forge.LobbyPlayer;
import forge.card.CardEdition;
import forge.card.CardRules;
import forge.deck.Deck;
import forge.game.GameRules;
import forge.game.GameType;
import forge.game.player.RegisteredPlayer;
import forge.gamemodes.match.HostedMatch;
import forge.gamemodes.quest.bazaar.IQuestBazaarItem;
import forge.gamemodes.quest.bazaar.QuestItemType;
import forge.gamemodes.quest.bazaar.QuestPetController;
import forge.gamemodes.quest.data.QuestAchievements;
import forge.gamemodes.quest.data.QuestAssets;
import forge.gamemodes.quest.data.QuestPreferences;
import forge.gui.FThreads;
import forge.gui.GuiBase;
import forge.gui.interfaces.IButton;
import forge.gui.interfaces.IGuiGame;
import forge.gui.util.SOptionPane;
import forge.item.IPaperCard;
import forge.item.PaperToken;
import forge.localinstance.properties.ForgePreferences.FPref;
import forge.localinstance.skin.FSkinProp;
import forge.model.FModel;
import forge.player.GamePlayerUtil;
import forge.util.Localizer;
import forge.util.maps.MapOfLists;
import org.apache.commons.lang3.tuple.ImmutablePair;

import java.text.DecimalFormat;
import java.util.*;

/**
 * <p>
 * QuestUtil class.
 * </p>
 * MODEL - Static utility methods to help with minor tasks around Quest.
 *
 * @author Forge
 * @version $Id$
 */
public class QuestUtil {
    private static final DecimalFormat CREDITS_FORMATTER = new DecimalFormat("#,###");
    public static String formatCredits(long credits) {
        return CREDITS_FORMATTER.format(credits);
    }
    private static QuestEvent event;
    private static QuestEventDraft draftEvent;


    public static Deck getCurrentDeck() {Deck d = null;if (FModel.getQuest().getAssets() != null) d = FModel.getQuest().getMyDecks().get(FModel.getQuest().getCurrentDeck());return d;}

    public static void notify_start_game() {
        if (is_game_startable()) start_game();
    }

    public static boolean is_game_startable() {
        final Localizer localizer = Localizer.getInstance();

        final Deck deck = getDeckForNewGame();
        if (deck == null) {
            final String msg = localizer.getMessage("lblSelectAQuestDeck");
            SOptionPane.showErrorDialog(msg, localizer.getMessage("lblNoDeck"));
            return false;
        }

        if (FModel.getPreferences().getPrefBoolean(FPref.ENFORCE_DECK_LEGALITY)) {
            final String errorMessage = getDeckConformanceProblemsBeforeGame(deck);
            if (null != errorMessage) {
                SOptionPane.showErrorDialog(localizer.getMessage("lblInvalidDeckDesc").replace("%n",errorMessage), "Invalid Deck");
                return false;
            }
        }

        return true;
    }

    public static String getDeckConformanceProblemsBeforeGame(Deck deck){
        //Check quest mode's generic deck construction rules: minimum cards in deck, sideboard etc
        String errorMessage = GameType.Quest.getDeckFormat().getDeckConformanceProblem(deck);
        if(errorMessage != null) return errorMessage; //return immediately if the deck does not conform to quest requirements

        //Check for this quest- and World's deck construction rules: allowed sets, banned/restricted cards etc
        if (FModel.getQuestPreferences().getPrefInt(QuestPreferences.QPref.WORLD_RULES_CONFORMANCE) == 1) {
            if(FModel.getQuest().getFormat() != null)
                errorMessage = FModel.getQuest().getFormat().getDeckConformanceProblem(deck);
        }return errorMessage;
    }

    public static void start_game() {
        final QuestController qData = FModel.getQuest();
        Random rand = new Random();

        MapOfLists<DuelBucket, QuestEventDuel> duels_map =  ((MainWorldEventDuelManager) FModel.getQuest().getDuelsManager()).sortedDuels;
        List<QuestEventDuel> duels = (List<QuestEventDuel>) duels_map.get(QuestUtilCards.I);
        event = duels.get(rand.nextInt(duels.size()));

        FThreads.invokeInBackgroundThread(new Runnable() {
            @Override
            public void run() {
                //qData.getDuelsManager().randomizeOpponents();
                qData.setCurrentEvent(event);
                qData.save();
            }
        });

        int extraLifeHuman = 0;
        Integer lifeHuman = null;
        boolean useBazaar = true;
        Boolean forceAnte = null;

        //Generate a life modifier based on this quest's variant as held in the Quest Controller's DeckConstructionRules
        int variantLifeModifier = 0;

        switch(FModel.getQuest().getDeckConstructionRules()){
            case Default: break;
            case Commander: variantLifeModifier = 20; break;
        }

        int lifeAI = 20 + variantLifeModifier;

        final RegisteredPlayer humanStart = getRegisteredPlayerByVariant(getDeckForNewGame());
        final RegisteredPlayer aiStart = getRegisteredPlayerByVariant(event.getEventDeck());

        humanStart.setStartingLife(qData.getAssets().getLife(qData.getMode()) + extraLifeHuman);

        if (useBazaar) {
            humanStart.setCardsOnBattlefield(QuestUtil.getHumanStartingCards(qData, event));
            aiStart.setStartingLife(lifeAI);
            aiStart.setCardsOnBattlefield(QuestUtil.getComputerStartingCards(event));
        }

        final List<RegisteredPlayer> starter = new ArrayList<>();
        starter.add(humanStart.setPlayer(GamePlayerUtil.getQuestPlayer()));

        final LobbyPlayer aiPlayer = GamePlayerUtil.createAiPlayer(event.getOpponentName() == null ? event.getTitle() : event.getOpponentName(), event.getProfile());
        starter.add(aiStart.setPlayer(aiPlayer));


        for (final RegisteredPlayer rp : starter) rp.setRandomFoil(false);
        final GameRules rules = new GameRules(GameType.Quest);
        rules.setPlayForAnte(false);
        rules.setMatchAnteRarity(false);
        rules.setGamesPerMatch(qData.getMatchLength());
        rules.setManaBurn(FModel.getPreferences().getPrefBoolean(FPref.UI_MANABURN));
        rules.setUseGrayText(FModel.getPreferences().getPrefBoolean(FPref.UI_GRAY_INACTIVE_TEXT));

        final HostedMatch hostedMatch = GuiBase.getInterface().hostMatch();
        final IGuiGame gui = GuiBase.getInterface().getNewGuiGame();
        gui.setPlayerAvatar(aiPlayer, event);
        FThreads.invokeInEdtNowOrLater(new Runnable(){
            @Override
            public void run() {
                hostedMatch.startMatch(rules, new TreeSet<>(), starter, ImmutableMap.of(humanStart, gui), null);
            }
        });

    }

    /**
     * Uses the appropriate RegisteredPlayer command for generating a RegisteredPlayer based on this quest's variant as
     * held by the QuestController's DeckConstructionRules.
     * @param deck The deck to generate the RegisteredPlayer with
     * @return A newly made RegisteredPlayer specific to the quest's variant
     */
    private static RegisteredPlayer getRegisteredPlayerByVariant(Deck deck){
        switch (FModel.getQuest().getDeckConstructionRules()) {
            case Default:
                return new RegisteredPlayer(deck);
            case Commander:
                return RegisteredPlayer.forCommander(deck);
        }
        return null;
    }

    private static Deck getDeckForNewGame() {
        Deck deck = null;
        if (event instanceof QuestEventChallenge) {
            // Predefined HumanDeck
            deck = ((QuestEventChallenge) event).getHumanDeck();
        }
        if (deck == null) {
            // If no predefined Deck, use the Player's Deck
            deck = getCurrentDeck();
        }
        return deck;
    }


    //////////////////////////////////////////////////////////





    /** Duplicate in DeckEditorQuestMenu and
     * probably elsewhere...can streamline at some point
     * (probably shouldn't be here).
     *
     * @param in &emsp; {@link java.lang.String}
     * @return {@link java.lang.String}
     */
    public static String cleanString(final String in) {
        final StringBuilder out = new StringBuilder();
        final char[] c = in.toCharArray();

        for (final char aC : c) {
            if (Character.isLetterOrDigit(aC) || (aC == '-') || (aC == '_') || (aC == ' ')) {
                out.append(aC);
            }
        }

        return out.toString();
    }

    public static void buyQuestItem(final IQuestBazaarItem item) {
        final QuestAssets qA = FModel.getQuest().getAssets();
        final int cost = item.getBuyingPrice(qA);
        if (cost >= 0 && (qA.getCredits() - cost) >= 0) {
            qA.subtractCredits(cost);
            qA.addCredits(item.getSellingPrice(qA));
            item.onPurchase(qA);
            FModel.getQuest().save();
        }
    }

    ///////////////////////////////////////////
    /**
     * <p>
     * getComputerStartingCards.
     * </p>
     * Returns new card instances of extra AI cards in play at start of event.
     *
     * @param qe
     *            a {@link forge.gamemodes.quest.QuestEvent} object.
     * @return a {@link java.util.List} object.
     */
    public static List<IPaperCard> getComputerStartingCards(final QuestEvent qe) {
        final List<IPaperCard> list = new ArrayList<>();

        for (final String s : qe.getAiExtraCards()) {
            list.add(QuestUtil.readExtraCard(s));
        }

        return list;
    }

    /**
     * <p>
     * getHumanStartingCards.
     * </p>
     * Returns list of current plant/pet configuration only.
     * @param qc
     *            a {@link forge.gamemodes.quest.QuestController} object.
     * @return a {@link java.util.List} object.
     */
    public static List<IPaperCard> getHumanStartingCards(final QuestController qc) {
        final List<IPaperCard> list = new ArrayList<>();

        for (int iSlot = 0; iSlot < QuestController.MAX_PET_SLOTS; iSlot++) {
            final String petName = qc.getSelectedPet(iSlot);
            final QuestPetController pet = qc.getPetsStorage().getPet(petName);
            if (pet != null) {
                final IPaperCard c = pet.getPetCard(qc.getAssets());
                if (c != null) {
                    list.add(c);
                }
            }
        }

        return list;
    }

    /**
     * <p>
     * getHumanStartingCards.
     * </p>
     * Returns new card instances of extra human cards, including current
     * plant/pet configuration, and cards in play at start of quest.
     *
     * @param qc
     *            a {@link forge.gamemodes.quest.QuestController} object.
     * @param qe
     *            a {@link forge.gamemodes.quest.QuestEvent} object.
     * @return a {@link java.util.List} object.
     */
    public static List<IPaperCard> getHumanStartingCards(final QuestController qc, final QuestEvent qe) {
        final List<IPaperCard> list = QuestUtil.getHumanStartingCards(qc);
        for (final String s : qe.getHumanExtraCards()) {
            list.add(QuestUtil.readExtraCard(s));
        }
        return list;
    }

    /**
     * <p>
     * createToken.
     * </p>
     * Creates a card instance for token defined by property string.
     *
     * @param s
     *            Properties string of token
     *            (TOKEN;W;1;1;sheep;type;type;type...)
     * @return token Card
     */
    public static PaperToken createToken(final String s) {
        final String[] properties = s.split(";", 6);

        final List<String> script = new ArrayList<>();
        script.add("Name:" + properties[4]);
        script.add("Colors:" + properties[1]);
        script.add("PT:"+ properties[2] + "/" + properties[3]);
        script.add("Types:" + properties[5].replace(';', ' '));
        script.add("Oracle:"); // tokens don't have texts yet
        final String fileName = PaperToken.makeTokenFileName(properties[1], properties[2], properties[3], properties[4]);
        return new PaperToken(CardRules.fromScript(script), CardEdition.UNKNOWN, fileName);
    }

    /**
     * <p>
     * readExtraCard.
     * </p>
     * Creates single card for a string read from unique event properties.
     *
     * @param name
     *            the name
     * @return the card
     */
    public static IPaperCard readExtraCard(final String name) {
        // Token card creation
        IPaperCard tempcard;
        if (name.startsWith("TOKEN")) {
            tempcard = QuestUtil.createToken(name);
            return tempcard;
        }
        // Standard card creation
        return FModel.getMagicDb().getCommonCards().getCardFromEditions(name);
    }

    private static void updatePlantAndPetForView(final IVQuestStats view, final QuestController qCtrl) {
        final Localizer localizer = Localizer.getInstance();
        for (int iSlot = 0; iSlot < QuestController.MAX_PET_SLOTS; iSlot++) {
            final List<QuestPetController> petList = qCtrl.getPetsStorage().getAvaliablePets(iSlot, qCtrl.getAssets());
            final String currentPetName = qCtrl.getSelectedPet(iSlot);

            if (iSlot == 0) { // Plant visiblity
                if (petList.isEmpty()) {
                    view.getCbPlant().setVisible(false);
                }
                else {
                    view.getCbPlant().setVisible(true);
                    view.getCbPlant().setSelected(currentPetName != null);
                }
            }
            if (iSlot == 1) {
                view.getCbxPet().removeAllItems();

                // Pet list visibility
                if (!petList.isEmpty()) {
                    view.getCbxPet().setVisible(true);
                    view.getCbxPet().addItem(localizer.getMessage("lblDontSummonAPet"));

                    for (final QuestPetController pet : petList) {
                        final String name = localizer.getMessage("lblSummon").replace("%n","\"" + pet.getName() + "\"");
                        view.getCbxPet().addItem(name);
                        if (pet.getName().equals(currentPetName)) {
                            view.getCbxPet().setSelectedItem(name);
                        }
                    }
                } else {
                    view.getCbxPet().setVisible(false);
                }
            }
        }

        view.getCbxMatchLength().removeAllItems();
        boolean activeCharms = false;
        StringBuilder matchLength = new StringBuilder();
        matchLength.append(localizer.getMessage("lblMatchBestof")).append(" ").append(qCtrl.getMatchLength());
        if (qCtrl.getAssets().hasItem(QuestItemType.CHARM_VIM)) {
            view.getCbxMatchLength().addItem(localizer.getMessage("lblMatchBestOf1"));
            activeCharms = true;
        }
        view.getCbxMatchLength().addItem(localizer.getMessage("lblMatchBestOf3"));
        if (qCtrl.getAssets().hasItem(QuestItemType.CHARM)) {
            view.getCbxMatchLength().addItem(localizer.getMessage("lblMatchBestOf5"));
            activeCharms = true;
        }
        view.getCbxMatchLength().setSelectedItem(matchLength.toString());
        view.getCbxMatchLength().setVisible(activeCharms);

        if (view.isChallengesView()) {
            view.getLblZep().setVisible(qCtrl.getAssets().hasItem(QuestItemType.ZEPPELIN));
            if (qCtrl.getAssets().getItemLevel(QuestItemType.ZEPPELIN) == 2) {
                view.getLblZep().setEnabled(false);
                view.getLblZep().setTextColor(128, 128, 128);
            }
            else {
                view.getLblZep().setEnabled(true);
                view.getLblZep().setImage(FSkinProp.CLR_TEXT);
            }
        }
        else {
            view.getLblZep().setVisible(false);
        }
    }

    //////////////////////

    public static void setEvent(final QuestEvent event0) {
        event = event0;
    }
    public static void setDraftEvent(final QuestEventDraft event0) {
        draftEvent = event0;
    }
    public static QuestEventDraft getDraftEvent() {
        return draftEvent;
    }
    public static boolean checkActiveQuest(final String location) {
        final Localizer localizer = Localizer.getInstance();

        final QuestController qc = FModel.getQuest();
        if (qc == null || qc.getAssets() == null) {
            final String msg = localizer.getMessage("PleaseCreateAQuestBefore").replace("%n",location);
            SOptionPane.showErrorDialog(msg, localizer.getMessage("lblNoQuest"));
            System.out.println(msg);
            return false;
        }
        return true;
    }

    /** */
    public static void showSpellShop() {
        GuiBase.getInterface().showSpellShop();
    }

    /** */
    public static void showBazaar() {
        final Localizer localizer = Localizer.getInstance();
        if (!checkActiveQuest(localizer.getMessage("lblVisitTheBazaar"))) {
            return;
        }
        GuiBase.getInterface().showBazaar();
    }

    /** */
    public static void chooseAndUnlockEdition() {
        final Localizer localizer = Localizer.getInstance();
        if (!checkActiveQuest(localizer.getMessage("lblUnlockEditions"))) {
            return;
        }
        final QuestController qData = FModel.getQuest();
        final ImmutablePair<CardEdition, Integer> toUnlock = QuestUtilUnlockSets.chooseSetToUnlock(qData, false, null);
        if (toUnlock == null) {
            return;
        }

        final CardEdition unlocked = toUnlock.left;
        qData.getAssets().subtractCredits(toUnlock.right);
        SOptionPane.showMessageDialog(localizer.getMessage("lblUnlocked").replace("%n",unlocked.getName()),
                localizer.getMessage("titleUnlocked").replace("%n",unlocked.getName()), null);

        QuestUtilUnlockSets.doUnlock(qData, unlocked);
    }

    public static void updateQuestView(final IVQuestStats view0) {
        final QuestController qCtrl = FModel.getQuest();
        final QuestAchievements qA = qCtrl.getAchievements();
        final QuestAssets qS = qCtrl.getAssets();

        if (qA == null) { return; }

        // Fantasy UI display
        view0.getLblNextChallengeInWins().setVisible(true);
        view0.getBtnBazaar().setVisible(true);
        view0.getLblLife().setVisible(true);

        final Localizer localizer = Localizer.getInstance();

        // Stats panel
        view0.getLblCredits().setText(localizer.getMessage("lblCredits") + ": " + QuestUtil.formatCredits(qS.getCredits()));
        view0.getLblLife().setText(localizer.getMessage("lblLife") + ": " + qS.getLife(qCtrl.getMode()));
        view0.getLblWins().setText(localizer.getMessage("lblWins") + ": " + qA.getWin());
        view0.getLblLosses().setText(localizer.getMessage("lblLosses") + ": " + qA.getLost());
        view0.getLblWorld().setText(localizer.getMessage("lblWorld") +": "+ (qCtrl.getWorld() == null ? " (" + localizer.getMessage("lblNone") + ")" : qCtrl.getWorld()));

        // Show or hide the set unlocking button

        view0.getBtnUnlock().setVisible(qCtrl.getUnlocksTokens() > 0 && qCtrl.getWorldFormat() == null);

        // Challenge in wins


        if (view0.allowHtml()) {
            view0.getLblWinStreak().setText(
                    "<html>" + localizer.getMessage("lblWinStreak") + ": " + qA.getWinStreakCurrent()
                            + "<br>&nbsp; (" + localizer.getMessage("lblBest") + ": " + qA.getWinStreakBest() + ")</html>");
        }
        else {
            view0.getLblWinStreak().setText(
                    localizer.getMessage("lblWinStreak") +": " + qA.getWinStreakCurrent()
                            + " (" + localizer.getMessage("lblBest") + ": " + qA.getWinStreakBest() + ")");
        }

        // Current deck message
        final IButton lblCurrentDeck = view0.getLblCurrentDeck();
        if (getCurrentDeck() == null) {
            lblCurrentDeck.setTextColor(204, 0, 0);
            lblCurrentDeck.setText(localizer.getMessage("lblBuildAndSelectaDeck"));
        }
        else {
            lblCurrentDeck.setImage(FSkinProp.CLR_TEXT);
            lblCurrentDeck.setText(localizer.getMessage("lblCurrentDeck").replace("%n","\"" + getCurrentDeck().getName() + "\"."));
        }

        // Start panel: pet, plant, zep.
        if (qCtrl.getMode() == QuestMode.Fantasy) {
            updatePlantAndPetForView(view0, qCtrl);
        }
        else {
            // Classic mode display changes
            view0.getCbxPet().setVisible(false);
            view0.getCbPlant().setVisible(false);
            view0.getCbxMatchLength().setVisible(false);
            view0.getLblZep().setVisible(false);
            view0.getLblNextChallengeInWins().setVisible(false);
            view0.getBtnBazaar().setVisible(false);
            view0.getLblLife().setVisible(false);
        }
    }

} // QuestUtil
