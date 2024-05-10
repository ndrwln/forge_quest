package forge.screens.home.quest.thos;

import forge.gamemodes.quest.QuestUtil;
import forge.gui.UiCommand;
import forge.localinstance.skin.FSkinProp;
import forge.screens.home.quest.CSubmenuQuestStart;
import forge.screens.home.quest.DialogChoosePoolDistribution;
import forge.toolbox.FLabel;
import forge.toolbox.FSkin;

import static forge.screens.home.quest.thos.Locations.REST_AREA;
import static forge.screens.home.quest.thos.Locations.UI_MAIN;

public class Buttons {

    public static SNode btn_new;
    public static SNode btn_continue;
    public static SNode btn_map;
    public static SNode btn_decks;
    public static SNode btn_inventory;
    public static SNode btn_learn;
    public static SNode btn_explore;


    public static void init_buttons()
    {
        if (btn_new != null) return;

        btn_new = new SNode()
                .fLabel(new FLabel.Builder()
                        .opaque(true)
                        .fontSize(16)
                        .hoverable(true)
                        .text("New")
                        .build())
                .constraints("w 300px!, h 30px!, ax center, gapy 40%, span 2")
                .ui(UI_MAIN)
                .fn((UiCommand) () -> {
                    final DialogChoosePoolDistribution colorChooser = new DialogChoosePoolDistribution();
                    colorChooser.show((UiCommand) () -> {
                        CSubmenuQuestStart.SINGLETON_INSTANCE.preferredColors = colorChooser.getPreferredColors();
                        CSubmenuQuestStart.SINGLETON_INSTANCE.poolType = colorChooser.getPoolType();
                        CSubmenuQuestStart.SINGLETON_INSTANCE.newQuest();
                    });
                });

        btn_continue = new SNode()
                .fLabel(new FLabel.Builder()
                        .opaque(true)
                        .fontSize(16)
                        .hoverable(true)
                        .text("Continue")
                        .build())
                .constraints("w 300px!, h 30px!, ax center, span 2")
                .ui(UI_MAIN)
                .fn((UiCommand) () -> {

                    Locations.travelTo(REST_AREA);
                });

        //General Buttons - Side
        btn_map = new SNode()
                .fLabel(new FLabel.Builder()
                        .icon(FSkin.getIcon(FSkinProp.ICO_QUEST_BIG_MAP))
                        .opaque(true)
                        .hoverable(true)
                        .fontSize(15)
                        .build())
                .constraints("w 75px!, h 75px!, pos 0% 28% n n")
                .ui(UI_MAIN);

        btn_decks = new SNode()
                .fLabel(new FLabel.Builder()
                        .icon(FSkin.getIcon(FSkinProp.ICO_QUEST_BIG_BOOK))
                        .opaque(true)
                        .hoverable(true)
                        .fontSize(15)
                        .build())
                .constraints("w 75px!, h 75px!, pos 0% 36% n n")
                .ui(UI_MAIN)
                .fn((UiCommand) () -> {
                    final DialogShowDecks decklist = new DialogShowDecks();
                    decklist.show((UiCommand) () -> {

                    });
                });

        btn_inventory = new SNode()
                .fLabel(new FLabel.Builder()
                        .icon(FSkin.getIcon(FSkinProp.ICO_QUEST_BIG_HOUSE))
                        .opaque(true)
                        .hoverable(true)
                        .fontSize(15)
                        .build())
                .constraints("w 75px!, h 75px!, pos 0% 44% n n")
                .ui(UI_MAIN)
                .fn((UiCommand) () -> {

                });

        btn_learn = new SNode()
                .fLabel(new FLabel.Builder()
                        .icon(FSkin.getIcon(FSkinProp.ICO_QUEST_BIG_STAKES))
                        .opaque(true)
                        .hoverable(true)
                        .fontSize(15)
                        .build())
                .constraints("w 75px!, h 75px!, pos 0% 52% n n")
                .ui(UI_MAIN)
                .fn((UiCommand) QuestUtil::showSpellShop);

        btn_explore = new SNode()
                .fLabel(new FLabel.Builder()
                        .icon(FSkin.getIcon(FSkinProp.ICO_QUEST_BIG_SWORD))
                        .opaque(true)
                        .hoverable(true)
                        .fontSize(15)
                        .build())
                .constraints("w 75px!, h 75px!, pos 0% 60% n n")
                .ui(UI_MAIN)
                .fn((UiCommand) () -> {
                    QuestUtil.notify_start_game();

                });


    }
}
