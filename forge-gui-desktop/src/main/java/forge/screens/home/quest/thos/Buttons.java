package forge.screens.home.quest.thos;

import forge.gamemodes.quest.QuestUtil;
import forge.gui.UiCommand;
import forge.localinstance.skin.FSkinProp;
import forge.model.FModel;
import forge.screens.home.quest.CSubmenuQuestStart;
import forge.screens.home.quest.DialogChoosePoolDistribution;
import forge.screens.home.quest.thos.Events._Triggered.EventExplore;
import forge.toolbox.FLabel;
import forge.toolbox.FSkin;

import static forge.screens.home.quest.thos.Locations.*;

public class Buttons {

    public static SNode btn_new;
    public static SNode btn_continue;
    public static SNode btn_map;
    public static SNode btn_decks;
    public static SNode btn_inventory;
    public static SNode btn_learn;
    public static SNode btn_explore;
    public static SNode btn_gotoplane;


    public static SNode lbl_life;
    public static SNode lbl_crystals;

    public static SNode btn_map_green;
    public static SNode btn_map_black;
    public static SNode btn_map_white;
    public static SNode btn_map_red;
    public static SNode btn_map_blue;
    public static SNode btn_map_rest;


    public Buttons()
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
                    if (FModel.getQuest().is_quest_existing())
                    {
                        Locations.travelTo(REST_AREA);
                    }

                });

        //UI Info
        lbl_life = new SNode()
                .fLabel(new FLabel.Builder()
                        .icon(FSkin.getIcon(FSkinProp.ICO_QUEST_LIFE))
                        .fontSize(15).build())
                .constraints("w 300px!, h 30px!, pos 80% 1% n n")
                .ui(UI_INFO);

        lbl_crystals = new SNode()
                .fLabel(new FLabel.Builder()
                        .icon(FSkin.getIcon(FSkinProp.ICO_QUEST_BIG_BOOTS))
                        .fontSize(15).build())
                .constraints("w 300px!, h 30px!, pos 84.5% 1% n n")
                .ui(UI_INFO);



        //General Buttons - Side
        btn_map = new SNode()
                .fLabel(new FLabel.Builder()
                        .icon(FSkin.getIcon(FSkinProp.ICO_QUEST_BIG_MAP))
                        .opaque(true)
                        .hoverable(true)
                        .fontSize(15)
                        .build())
                .constraints("w 75px!, h 75px!, pos 0% 28% n n")
                .ui(UI_GENERAL)
                .fn((UiCommand) () -> {
                    Locations.travelTo(MAP_AREA);
                });

        btn_decks = new SNode()
                .fLabel(new FLabel.Builder()
                        .icon(FSkin.getIcon(FSkinProp.ICO_QUEST_BIG_BOOK))
                        .opaque(true)
                        .hoverable(true)
                        .fontSize(15)
                        .build())
                .constraints("w 75px!, h 75px!, pos 0% 36% n n")
                .ui(UI_GENERAL)
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
                .ui(UI_GENERAL)
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
                .ui(UI_LEARN)
                .fn((UiCommand) QuestUtil::showSpellShop);

        btn_explore = new SNode()
                .fLabel(new FLabel.Builder()
                        .icon(FSkin.getIcon(FSkinProp.ICO_QUEST_BIG_SWORD))
                        .opaque(true)
                        .hoverable(true)
                        .fontSize(15)
                        .build())
                .constraints("w 75px!, h 75px!, pos 0% 60% n n")
                .ui(UI_EXPLORE)
                .fn((UiCommand) () -> {

                    EventExplore e = new EventExplore();
                    e.display();
//                    QuestUtilCards.is_plane = true;
//                    QuestUtil.notify_start_game();
//                    VSubmenuQuestStart.is_playing_new_music = true;

                });

        btn_gotoplane = new SNode()
                .fLabel(new FLabel.Builder()
                        .icon(FSkin.getIcon(FSkinProp.ICO_QUEST_BIG_SWORD))
                        .opaque(true)
                        .hoverable(true)
                        .fontSize(15)
                        .build())
                .constraints("w 75px!, h 75px!, pos 0% 60% n n")
                .ui(UI_GOTOPLANE)
                .fn((UiCommand) () -> {
                    Locations.travelToPlane(CURRENT_LOCATION);
                });


        //MAP
        //UI - MAP
        btn_map_green = new SNode()
                .fLabel(new FLabel.Builder()
                        .opaque(true)
                        .fontSize(16)
                        .hoverable(true)
                        .text("Green Sage's Hut")
                        .build())
                .constraints("w 300px!, h 30px!, pos 42% 77% n n")
                .ui(UI_MAP)
                .fn((UiCommand) () -> {
                    travelTo(GREEN_AREA);
                });

        btn_map_white = new SNode()
                .fLabel(new FLabel.Builder()
                        .opaque(true)
                        .fontSize(16)
                        .hoverable(true)
                        .text("Heavenly Court")
                        .build())
                .constraints("w 300px!, h 30px!, pos 30% 16% n n")
                .ui(UI_MAP)
                .fn((UiCommand) () -> {
                    travelTo(WHITE_AREA);
                });

        btn_map_red = new SNode()
                .fLabel(new FLabel.Builder()
                        .opaque(true)
                        .fontSize(16)
                        .hoverable(true)
                        .text("Inner Flame Peak")
                        .build())
                .constraints("w 300px!, h 30px!, pos 84% 46% n n")
                .ui(UI_MAP)
                .fn((UiCommand) () -> {
                    travelTo(RED_AREA);
                });

        btn_map_blue = new SNode()
                .fLabel(new FLabel.Builder()
                        .opaque(true)
                        .fontSize(16)
                        .hoverable(true)
                        .text("Blue Tower")
                        .build())
                .constraints("w 300px!, h 30px!, pos 35% 42% n n")
                .ui(UI_MAP)
                .fn((UiCommand) () -> {
                    travelTo(BLUE_AREA);
                });

        btn_map_black = new SNode()
                .fLabel(new FLabel.Builder()
                        .opaque(true)
                        .fontSize(16)
                        .hoverable(true)
                        .text("Abyssal Bone Forest")
                        .build())
                .constraints("w 300px!, h 30px!, pos 1% 18% n n")
                .ui(UI_MAP)
                .fn((UiCommand) () -> {
                    travelTo(ABYSSAL_BONE_FOREST);
                });

        btn_map_rest = new SNode()
                .fLabel(new FLabel.Builder()
                        .opaque(true)
                        .fontSize(16)
                        .hoverable(true)
                        .text("Illusion Dormitories")
                        .build())
                .constraints("w 300px!, h 30px!, pos 1% 86% n n")
                .ui(UI_MAP)
                .fn((UiCommand) () -> {
                    Locations.travelTo(REST_AREA);
                });


    }
}
