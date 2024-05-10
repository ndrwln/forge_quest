package forge.screens.home.quest.thos;

import forge.gui.UiCommand;
import forge.screens.home.quest.CSubmenuQuestStart;
import forge.screens.home.quest.DialogChoosePoolDistribution;
import forge.toolbox.FLabel;

import static forge.screens.home.quest.thos.Locations.REST_AREA;
import static forge.screens.home.quest.thos.Locations.UI_MAIN;

public class Buttons {

    public static SNode btn_new;
    public static SNode btn_continue;


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
                .constraints("w 300px!, h 30px!, ax center, span 2")
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

    }
}
