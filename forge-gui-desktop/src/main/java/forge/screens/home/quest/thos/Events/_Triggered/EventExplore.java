package forge.screens.home.quest.thos.Events._Triggered;

import forge.gamemodes.quest.QuestUtil;
import forge.gamemodes.quest.QuestUtilCards;
import forge.gamemodes.quest.QuestUtil_MatchData;
import forge.localinstance.skin.FSkinProp;
import forge.model.FModel;
import forge.screens.home.quest.thos.Events.Event;
import forge.screens.home.quest.thos.Events.EventManager;
import forge.screens.home.quest.thos.Events.Screen;
import forge.screens.home.quest.thos.Events._Interface.IMatchHandler;

public class EventExplore extends Event implements IMatchHandler {

    public static final int MAX_PROGRESS = 2;

    public EventExplore()
    {
//        screens.add(new Screen()
//                .id("A")
//                .image_key("vampire_I_large.png")
//                .text("This is a testThis is a testThis is a test\nThis is a testThis is a testThis is a test\n\nThis is a testThis is a testThis is a test/n")
//                .add_option("Ok", this::close)
//                .add_option("Next", () -> {
//                    this.display("B");
//                })
//        );

        ////////////////////

        long crystals = FModel.getQuest().getAssets().getCredits();
        Screen start = new Screen()
                .id("Start")
                .image_key("vampire_I_large.png")
                .text("The academy's colleges control secret planes where apprentices can scavenge for resources to " +
                        "exchange for crystals, and apprentices can sometimes derive lore from defeating supernatural creatures." +
                        "But one needs to pay to enter..." +
                        "\n\n You have " + crystals + " Crystals")
                ;


        if (crystals >= 10) start.add_option("Enter the Neophyte Zone (X Crystals)", this::fight);
        if (crystals >= 20) start.add_option("Enter the Adept Zone (X Crystals)", this::fight);
        if (crystals >= 50) start.add_option("Enter the Quasi-Wizard Zone (X Crystals)", this::fight);
        start.add_option("Leave", this::close);
        screens.add(start);

        ////////////////////

        //TODO: show match rewards
        screens.add(new Screen()
                .id("Continue?")
                .image_key("vampire_I_large.png")
                .text("")
                .preinit(() -> {
                    Screen s = EventManager.CURRENT_EVENT.getScreen("Continue?");
                    boolean is_winner = QuestUtil_MatchData.MATCH_RESULT == QuestUtil_MatchData.MatchResult.WIN;

                    if (is_winner)
                    {
                        if (QuestUtil_MatchData.NUM_PROGRESS <= 2)
                        {
                            s.text("A battle well fought. Do you continue exploring?");
                            s.add_option("Continue", this::fight);
                        }
                        else s.text("Your time in the secret plane is up. The protection array teleports you back. Hopefully the harvest was good...");
                    }
                    else
                    {
                        s.text("The school's protection array saves your life by teleporting you back outside the secret plane, but you are still injured. " +
                                "Passing out, the apprentices on duty cart you off to the infirmary");

                    }

                    s.add_option(is_winner ? "Leave" : "...", () ->
                    {
                        this.close();
                        this.close_match_screen();
                    });

                    s.show_extra(true);
                })
                .init(() -> {
                    Screen s = EventManager.CURRENT_EVENT.getScreen("Continue?");
                    if (QuestUtil_MatchData.STR_CRYSTALS != null) s.push_extra(QuestUtil_MatchData.STR_CRYSTALS, FSkinProp.ICO_QUEST_GOLD);
                    if (QuestUtil_MatchData.STR_CRYSTALS_LOSS != null) s.push_extra(QuestUtil_MatchData.STR_CRYSTALS_LOSS, FSkinProp.ICO_QUEST_GOLD);

                })
        );

    }

    public void fight()
    {
        QuestUtilCards.is_using_plane_mediaplayer = true;
        QuestUtil.notify_start_game();
    }


    @Override
    public void handle_postmatch() {
        EventManager.CURRENT_EVENT.display("Continue?");
    }
}
