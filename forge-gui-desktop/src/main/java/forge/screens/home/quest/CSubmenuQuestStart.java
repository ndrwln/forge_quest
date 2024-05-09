package forge.screens.home.quest;

import forge.deck.Deck;
import forge.game.GameFormat;
import forge.gamemodes.quest.QuestController;
import forge.gamemodes.quest.QuestMode;
import forge.gamemodes.quest.QuestUtil;
import forge.gamemodes.quest.StartingPoolPreferences;
import forge.gamemodes.quest.data.DeckConstructionRules;
import forge.gamemodes.quest.data.QuestData;
import forge.gamemodes.quest.data.QuestPreferences;
import forge.gui.UiCommand;
import forge.gui.framework.ICDoc;
import forge.model.FModel;
import forge.screens.home.CHomeUI;
import forge.toolbox.FOptionPane;
import forge.util.Localizer;

import java.util.*;

/**
 * Controls the quest data submenu in the home UI.
 *
 * <br><br><i>(C at beginning of class name denotes a control class.)</i>
 *
 */
@SuppressWarnings("serial")
public enum CSubmenuQuestStart implements ICDoc {
    SINGLETON_INSTANCE;

    private final Map<String, QuestData> arrQuests = new HashMap<>();

    private final VSubmenuQuestStart view = VSubmenuQuestStart.SINGLETON_INSTANCE;
    private final List<String> customFormatCodes = new ArrayList<>();
    private final List<String> customPrizeFormatCodes = new ArrayList<>();

    private List<Byte> preferredColors = new ArrayList<>();
    private StartingPoolPreferences.PoolType poolType = StartingPoolPreferences.PoolType.BALANCED;
    private boolean includeArtifacts = true;
    private int numberOfBoosters = 0;

    @Override
    public void register() {
    }

    /* (non-Javadoc)
     * @see forge.gui.control.home.IControlSubmenu#update()
     */
    @Override
    public void initialize() {
        view.getBtnEmbark().setCommand(
                new UiCommand() { @Override public void run() { newQuest(); } });

        // disable the very powerful sets -- they can be unlocked later for a high price
        final List<String> unselectableSets = new ArrayList<>();
        unselectableSets.add("LEA");
        unselectableSets.add("LEB");
        unselectableSets.add("MBP");
        unselectableSets.add("VAN");
        unselectableSets.add("ARC");
        unselectableSets.add("PC2");

        view.getBtnCustomFormat().setCommand(new UiCommand() {
            @Override
            public void run() {
                final DialogChooseSets dialog = new DialogChooseSets(customFormatCodes, unselectableSets, false);
                dialog.setOkCallback(new Runnable() {
                    @Override
                    public void run() {
                        customFormatCodes.clear();
                        customFormatCodes.addAll(dialog.getSelectedSets());
                    }
                });
            }
        });

        view.getBtnSelectFormat().setCommand(new UiCommand() {
            @Override
            public void run() {
                final DialogChooseFormats dialog = new DialogChooseFormats();
                dialog.setOkCallback(new Runnable() {
                    @Override
                    public void run() {
                        customFormatCodes.clear();
                        Set<String> sets = new HashSet<>();
                        for(GameFormat format:dialog.getSelectedFormats()){
                            sets.addAll(format.getAllowedSetCodes());
                        }
                        customFormatCodes.addAll(sets);
                    }
                });
            }
        });

        view.getBtnPrizeCustomFormat().setCommand(new UiCommand() {
            @Override
            public void run() {
                final DialogChooseSets dialog = new DialogChooseSets(customPrizeFormatCodes, unselectableSets, false);
                dialog.setOkCallback(new Runnable() {
                    @Override
                    public void run() {
                        customPrizeFormatCodes.clear();
                        customPrizeFormatCodes.addAll(dialog.getSelectedSets());
                    }
                });
            }
        });

        view.getBtnPrizeSelectFormat().setCommand(new UiCommand() {
            @Override
            public void run() {
                final DialogChooseFormats dialog = new DialogChooseFormats();
                dialog.setOkCallback(new Runnable() {
                    @Override
                    public void run() {
                        customPrizeFormatCodes.clear();
                        Set<String> sets = new HashSet<>();
                        for(GameFormat format:dialog.getSelectedFormats()){
                            sets.addAll(format.getAllowedSetCodes());
                        }
                        customPrizeFormatCodes.addAll(sets);
                    }
                });
            }
        });

        view.getBtnPreferredColors().setCommand(new UiCommand() {
            @Override
            public void run() {
                final DialogChoosePoolDistribution colorChooser = new DialogChoosePoolDistribution(preferredColors, poolType, includeArtifacts);
                colorChooser.show(new UiCommand() {
                    @Override
                    public void run() {
                        preferredColors = colorChooser.getPreferredColors();
                        poolType = colorChooser.getPoolType();
                        includeArtifacts = colorChooser.includeArtifacts();
                        numberOfBoosters = colorChooser.getNumberOfBoosters();
                    }
                });
            }
        });

    }

    /* (non-Javadoc)
     * @see forge.gui.control.home.IControlSubmenu#update()
     */
    @Override
    public void update() {
    }

    private void newQuest() {
        String questName = input_getQuestName();
        if (questName == null) return;

        //IStorage<PreconDeck> decks = QuestController.getPrecons();
        Deck dckStartPool = QuestController.getPrecons().get("Angelic Might").getDeck(); //start with a deck

        //IStorage<QuestWorld> w =  FModel.getWorlds(); //used to view worlds in debug
        //final QuestWorld startWorld = FModel.getWorlds().get("Main world"); //useful to get main world

        final StartingPoolPreferences userPrefs = new StartingPoolPreferences(StartingPoolPreferences.PoolType.BALANCED,
                preferredColors,
                false,
                false,
                false,
                0);

        //setup quest
        final QuestController qc = FModel.getQuest();
        qc.newGame(questName,
                3, //Very Hard
                QuestMode.Classic,
                null,
                true,
                dckStartPool,
                null,
                "Main world",
                userPrefs,
                DeckConstructionRules.Default);
        FModel.getQuest().save();
        FModel.getQuestPreferences().setPref(QuestPreferences.QPref.CURRENT_QUEST, questName + ".dat");
        FModel.getQuestPreferences().save();

        //TODO: change to main game screen
        //TODO: create event system and event popup. Display starting event

        // Change to QuestDecks screen
        CHomeUI.SINGLETON_INSTANCE.itemClick(VSubmenuQuestDecks.SINGLETON_INSTANCE.getDocumentID());
    }

    private String input_getQuestName()
    {
        final Localizer localizer = Localizer.getInstance();
        String questName = "";
        while (true) {
            questName = FOptionPane.showInputDialog(localizer.getMessage("MsgQuestNewName") + ":",  localizer.getMessage("TitQuestNewName"));
            if (questName == null) return null;
            questName = QuestUtil.cleanString(questName);
            if (questName.isEmpty()) {
                FOptionPane.showMessageDialog(localizer.getMessage("lblQuestNameEmpty"));
                continue;
            }
            if (getAllQuests().get(questName + ".dat") != null) {
                FOptionPane.showMessageDialog(localizer.getMessage("lblQuestExists"));
                continue;
            }
            break;
        }

        return questName;
    }

    private Map<String, QuestData> getAllQuests() {
        return arrQuests;
    }

}
