/** Forge: Play Magic: the Gathering.
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
package forge.screens.match;

import forge.game.GameView;
import forge.gamemodes.quest.QuestUtil_MatchData;
import forge.gamemodes.quest.QuestWinLoseController;
import forge.screens.home.quest.thos.Events.EventManager;
import forge.screens.home.quest.thos.Events._Interface.IMatchHandler;

import static forge.gui.SOverlayUtils.hideOverlay;

/**
 * <p>
 * QuestWinLose.
 * </p>
 * Processes win/lose presentation for Quest events. This presentation is
 * displayed by WinLoseFrame. Components to be added to pnlCustom in
 * WinLoseFrame should use MigLayout.
 *
 */
public class QuestWinLose extends ControlWinLose {
    public static QuestWinLose INSTANCE;
    public final QuestWinLoseController controller;

    /**
     * Instantiates a new quest win lose handler.
     *
     * @param view0 ViewWinLose object
     */
    public QuestWinLose(final ViewWinLose view0, final GameView game0, final CMatchUI matchUI) {
        super(view0, game0, matchUI);
        controller = new QuestWinLoseController(game0, view0);
        INSTANCE = this;
    }


    /**
     * <p>
     * populateCustomPanel.
     * </p>
     * Checks conditions of win and fires various reward display methods
     * accordingly.
     *
     * @return true, if successful
     */
    @Override
    public final boolean populateCustomPanel() {
        controller.showRewards();
        return true;
    }

    /**
     * <p>
     * actionOnQuit.
     * </p>
     * When "quit" button is pressed, this method adjusts quest data as
     * appropriate and saves.
     *
     */
    @Override
    public final void actionOnQuit() {
        controller.actionOnQuit();
//        super.actionOnQuit();
        if (EventManager.CURRENT_EVENT instanceof IMatchHandler)
        {
            hideOverlay();

            //TODO: revert normal logic
            //Normal Logic
//            final int x = FModel.getQuestPreferences().getPrefInt(QuestPreferences.QPref.PENALTY_LOSS);
//            QuestUtil_MatchData.STR_CRYSTALS_LOSS = Localizer.getInstance().getMessage("lblYouHaveLostNCredits", String.valueOf(x));
//            ((IMatchHandler) EventManager.CURRENT_EVENT).handle_postmatch();

            //Invert Logic to test victory
            QuestUtil_MatchData.MATCH_RESULT = QuestUtil_MatchData.MatchResult.WIN;
            QuestUtil_MatchData.NUM_PROGRESS += 1;
            controller.awardEventCredits();
            controller.awardBooster();
            ((IMatchHandler) EventManager.CURRENT_EVENT).handle_postmatch();




        }
    }

}
