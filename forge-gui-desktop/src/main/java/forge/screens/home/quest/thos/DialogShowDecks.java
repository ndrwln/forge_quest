package forge.screens.home.quest.thos;

import forge.Singletons;
import forge.deck.DeckProxy;
import forge.game.GameType;
import forge.gamemodes.quest.QuestController;
import forge.gamemodes.quest.QuestUtil;
import forge.gamemodes.quest.data.QuestPreferences;
import forge.gui.SOverlayUtils;
import forge.gui.UiCommand;
import forge.gui.framework.FScreen;
import forge.itemmanager.DeckManager;
import forge.itemmanager.ItemManagerConfig;
import forge.itemmanager.ItemManagerContainer;
import forge.model.FModel;
import forge.screens.deckeditor.CDeckEditorUI;
import forge.screens.deckeditor.controllers.CEditorQuest;
import forge.toolbox.FLabel;
import forge.toolbox.FOverlay;
import forge.toolbox.FPanel;
import forge.util.Localizer;
import lombok.Getter;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;

@SuppressWarnings("WeakerAccess")
public class DialogShowDecks {
	final Localizer localizer = Localizer.getInstance();

	private final FPanel pnl = new FPanel(new MigLayout("w 30sp:30sp, h 83sp:83sp, insets 0, gap 10px, wrap 2"));

	FLabel btn_exit = new FLabel.Builder()
			.fontSize(30)
			.text("×")
			.opaque(false)
			.hoverable(true)
			.build();

	FLabel btn_new_deck = new FLabel
			.ButtonBuilder()
			.text("+")
			.fontSize(30)
			.build();

	@Getter private final DeckManager lstDecks = new DeckManager(GameType.Quest, CDeckEditorUI.SINGLETON_INSTANCE.getCDetailPicture());


    @SuppressWarnings("serial") public DialogShowDecks() {
		pnl.setOpaque(false);
//		pnl.setBackgroundTexture(FSkin.getIcon(FSkinProp.BG_TEXTURE));

		ItemManagerContainer comp = new ItemManagerContainer(lstDecks);
		lstDecks.quest_hidestuff();
		update();



		JPanel bar = new JPanel(new MigLayout("insets 10, gap 20"));
		bar.setOpaque(false);
		bar.add(btn_exit, "w 20px!, h 20px!, gapleft 93%");
		btn_exit.setCommand(new UiCommand() {
			@Override
			public void run() {
				final DeckProxy deck = getLstDecks().getSelectedItem();
				FModel.getQuest().setCurrentDeck(deck.toString());
				FModel.getQuest().save();

				SOverlayUtils.hideOverlay();
			}
		});
		pnl.add(bar, "w 100%:100%, dock north, gapBottom 5px");

		pnl.add(btn_new_deck, "w 95%, h 30px!, dock north, ax center, gapleft 3%, gap right 3%");
		btn_new_deck.setCommand((UiCommand) () -> {
            if (!QuestUtil.checkActiveQuest(localizer.getMessage("lblCreateaDeck"))) return;
            Singletons.getControl().setCurrentScreen(FScreen.DECK_EDITOR_QUEST);

			CEditorQuest childController0 = new CEditorQuest(FModel.getQuest(), CDeckEditorUI.SINGLETON_INSTANCE.getCDetailPicture());
			CDeckEditorUI.SINGLETON_INSTANCE.setEditorController(childController0);
        });



		pnl.add(comp, "w 95%, h 95%, gapleft 3%, gap right 3%, gapbottom 2%");
	}

	///////////////

	public void show(final Runnable callback) {
        final JPanel overlay = FOverlay.SINGLETON_INSTANCE.getPanel();
		overlay.setLayout(new MigLayout("insets 30, gap 15, wrap, ax center, ay center"));
		overlay.add(pnl);
		update();

		SOverlayUtils.showOverlay();

	}

	public void update() {
		getLstDecks().setSelectCommand(null); //set to null temporarily
		getLstDecks().setDeleteCommand(null);

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

		getLstDecks().setSelectCommand(cmdDeckSelect);
		getLstDecks().setDeleteCommand(cmdDeckDelete);
	}

	//////////////////////////

	private final UiCommand cmdDeckSelect = () -> {
		final DeckProxy deck = getLstDecks().getSelectedItem();
		if (deck != null) FModel.getQuest().setCurrentDeck(deck.toString());else FModel.getQuest().setCurrentDeck(QuestPreferences.QPref.CURRENT_DECK.getDefault());
		FModel.getQuest().save();
	};

	private final UiCommand cmdDeckDelete = () -> update();

}
