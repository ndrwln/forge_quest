package forge.screens.home.quest;

import forge.gamemodes.quest.QuestController;
import forge.gamemodes.quest.QuestUtil;
import forge.gamemodes.quest.bazaar.QuestPetController;
import forge.gui.UiCommand;
import forge.gui.framework.ICDoc;
import forge.model.FModel;

import java.awt.event.*;
import java.util.List;

/**
 * Controls the quest duels submenu in the home UI.
 * <p>
 * <br><br><i>(C at beginning of class name denotes a control class.)</i>
 */
public enum CSubmenuDuels implements ICDoc {

	SINGLETON_INSTANCE;

	@Override
	public void register() {
	}

	/* (non-Javadoc)
	 * @see forge.gui.control.home.IControlSubmenu#initialize()
	 */
	@SuppressWarnings("serial")
	@Override
	public void initialize() {
		final VSubmenuDuels view = VSubmenuDuels.SINGLETON_INSTANCE;

		view.getBtnSpellShop().setCommand(
				new UiCommand() {
					@Override
					public void run() {
						QuestUtil.showSpellShop();
					}
				});

		view.getBtnBazaar().setCommand(
				new UiCommand() {
					@Override
					public void run() {
						QuestUtil.showBazaar();
					}
				});



		view.getBtnUnlock().setCommand(
				new UiCommand() {
					@Override
					public void run() {
						QuestUtil.chooseAndUnlockEdition();
						CSubmenuDuels.this.update();
					}
				});

		view.getBtnStart().addActionListener(
				new ActionListener() {
					@Override
					public void actionPerformed(final ActionEvent e) {
						QuestUtil.notify_start_game();
					}
				});

		final QuestController quest = FModel.getQuest();
		view.getCbPlant().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent arg0) {
				// This can't be translated. As the English string "Plant" is used to find the Plant pet.
				quest.selectPet(0, view.getCbPlant().isSelected() ? "Plant" : null);
				quest.save();
			}
		});

		view.getCbxMatchLength().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent arg0) {
				String match = view.getCbxMatchLength().getSelectedItem();
				if (match != null) {
					quest.setMatchLength(match.substring(match.length() - 1));
					quest.save();
				}
			}
		});

		view.getCbxPet().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent arg0) {
				final int slot = 1;
				final int index = view.getCbxPet().getSelectedIndex();
				final List<QuestPetController> pets = quest.getPetsStorage().getAvaliablePets(slot, quest.getAssets());
				final String petName = index <= 0 || index > pets.size() ? null : pets.get(index - 1).getName();
				quest.selectPet(slot, petName);
				quest.save();
			}
		});

	}

	private final KeyAdapter startOnEnter = new KeyAdapter() {
		@Override
		public void keyPressed(final KeyEvent e) {
			if (e.getKeyChar() == KeyEvent.VK_ENTER) {
				VSubmenuDuels.SINGLETON_INSTANCE.getBtnStart().doClick();
			}
		}
	};

	private final MouseAdapter mouseClickListener = new MouseAdapter() {

		@Override
		public void mouseEntered(final MouseEvent e) {
			if (e.getComponent() instanceof PnlEvent) {
				((PnlEvent) e.getComponent()).getRad().getModel().setRollover(true);
			} else {
				((PnlEvent) e.getComponent().getParent()).getRad().getModel().setRollover(true);
			}
		}

		@Override
		public void mouseExited(final MouseEvent e) {
			if (e.getComponent() instanceof PnlEvent) {
				((PnlEvent) e.getComponent()).getRad().getModel().setRollover(false);
			} else {
				((PnlEvent) e.getComponent().getParent()).getRad().getModel().setRollover(false);
			}
		}

		@Override
		public void mouseDragged(final MouseEvent e) {
			mousePressed(e);
		}

		@Override
		public void mousePressed(final MouseEvent e) {
			if (e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() == 2) {
				VSubmenuDuels.SINGLETON_INSTANCE.getBtnStart().doClick();
			} else if (e.getButton() == MouseEvent.BUTTON1) {
				if (e.getComponent() instanceof PnlEvent) {
					((PnlEvent) e.getComponent()).getRad().setSelected(true);
				} else {
					((PnlEvent) e.getComponent().getParent()).getRad().setSelected(true);
				}
			}
		}
	};

	/* (non-Javadoc)
	 * @see forge.gui.control.home.IControlSubmenu#update()
	 */
	@Override
	public void update() {
		QuestUtil.updateQuestView(VSubmenuDuels.SINGLETON_INSTANCE);
	}

}
