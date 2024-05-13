package forge.screens.home.quest;

import forge.deck.Deck;
import forge.gamemodes.quest.QuestController;
import forge.gamemodes.quest.QuestMode;
import forge.gamemodes.quest.QuestUtil;
import forge.gamemodes.quest.StartingPoolPreferences;
import forge.gamemodes.quest.data.DeckConstructionRules;
import forge.gamemodes.quest.data.PreferencesResearch;
import forge.gamemodes.quest.data.QuestData;
import forge.gamemodes.quest.data.QuestPreferences;
import forge.gui.framework.ICDoc;
import forge.model.FModel;
import forge.screens.home.quest.thos.Locations;
import forge.toolbox.FOptionPane;
import forge.util.Localizer;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static forge.localinstance.properties.ForgeConstants.PATH_SEPARATOR;
import static forge.localinstance.properties.ForgeConstants.USER_PREFS_DIR;
import static forge.model.FModel.setResearchPreferences;
import static forge.screens.home.quest.thos.Locations.REST_AREA;

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
    public List<Byte> preferredColors = new ArrayList<>();
    public StartingPoolPreferences.PoolType poolType = StartingPoolPreferences.PoolType.BALANCED;

    @Override public void register() {}
    @Override public void initialize() {}
    @Override public void update() {}

    public void newQuest() {
        String questName = input_getQuestName();
        if (questName == null) return;

//        IStorage<PreconDeck> decks = QuestController.getPrecons();
        Deck dckStartPool = QuestController.getPrecons().get("Tales of Vampires I - Starter").getDeck(); //start with a deck

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

        String PATH = USER_PREFS_DIR  + questName + PATH_SEPARATOR + "research.preferences";
        new File(USER_PREFS_DIR  + questName).mkdirs();
        setResearchPreferences(new PreferencesResearch(PATH));
        FModel.getResearchPreferences().reset();
        FModel.getResearchPreferences().save();
        Locations.travelTo(REST_AREA);
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
