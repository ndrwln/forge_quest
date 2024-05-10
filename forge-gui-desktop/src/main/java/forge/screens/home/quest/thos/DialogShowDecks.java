package forge.screens.home.quest.thos;

import forge.deck.DeckProxy;
import forge.game.GameType;
import forge.gamemodes.quest.QuestController;
import forge.gui.SOverlayUtils;
import forge.gui.UiCommand;
import forge.itemmanager.DeckManager;
import forge.itemmanager.ItemManagerConfig;
import forge.itemmanager.ItemManagerContainer;
import forge.localinstance.skin.FSkinProp;
import forge.model.FModel;
import forge.screens.deckeditor.CDeckEditorUI;
import forge.toolbox.FButton;
import forge.toolbox.FOverlay;
import forge.toolbox.FPanel;
import forge.toolbox.FSkin;
import forge.util.Localizer;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;

@SuppressWarnings("WeakerAccess")
public class DialogShowDecks {
	final Localizer localizer = Localizer.getInstance();
	private final FPanel pnl = new FPanel(new MigLayout("insets 0, gap 10px, fillx, wrap 2"));
	private final FButton btnOk = new FButton(localizer.getMessage("lblOK"));

	private final DeckManager lstDecks = new DeckManager(GameType.Quest, CDeckEditorUI.SINGLETON_INSTANCE.getCDetailPicture());


	private Runnable callback;

	@SuppressWarnings("serial")
	public DialogShowDecks() {
		pnl.setOpaque(false);
		pnl.setBackgroundTexture(FSkin.getIcon(FSkinProp.BG_TEXTURE));

		pnl.add(new ItemManagerContainer(lstDecks), "w 900px!, h 900px!, gap 0 10% 0 0, pushy, growy");



		btnOk.setCommand(new UiCommand() {
			@Override
			public void run() {
				SOverlayUtils.hideOverlay();
//				callback.run();
			}
		});

		FButton btnCancel = new FButton(localizer.getMessage("lblCancel"));
		btnCancel.setCommand(new UiCommand() {
			@Override
			public void run() {
				SOverlayUtils.hideOverlay();
			}
		});

		JPanel southPanel = new JPanel(new MigLayout("insets 10, gap 20"));
		southPanel.setOpaque(false);
		southPanel.add(btnOk, "w 150px!, h 30px!");
		southPanel.add(btnCancel, "w 150px!, h 30px!");

		pnl.add(southPanel, "dock north, gapBottom 10");

	}

	public void show(final Runnable callback) {

		this.callback = callback;

		final JPanel overlay = FOverlay.SINGLETON_INSTANCE.getPanel();
		overlay.setLayout(new MigLayout("insets 30, gap 15, wrap, ax center, ay center"));
		overlay.add(pnl);

		final QuestController qData = FModel.getQuest();
		final boolean hasQuest = qData.getAssets() != null;
		// Retrieve and set all decks
		getLstDecks().setPool(DeckProxy.getAllQuestDecks(hasQuest ? qData.getMyDecks() : null));
		getLstDecks().setup(ItemManagerConfig.QUEST_DECKS);

		// Look through list for preferred deck from prefs
		final DeckProxy deck = hasQuest ? getLstDecks().stringToItem(FModel.getQuest().getCurrentDeck()) : null;
		if (deck != null) {
			getLstDecks().setSelectedItem(deck);
		}

		pnl.getRootPane().setDefaultButton(btnOk);

		SOverlayUtils.showOverlay();

	}

	public DeckManager getLstDecks() {
		return this.lstDecks;
	}

}
