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


        if (crystals >= 10) start.option_add("Enter the Neophyte Zone (X Crystals)", this::fight);
        if (crystals >= 20) start.option_add("Enter the Adept Zone (X Crystals)", this::fight);
        if (crystals >= 50) start.option_add("Enter the Quasi-Wizard Zone (X Crystals)", this::fight);
        start.option_add("Leave", this::close);
        screens.add(start);

        ////////////////////

        //TODO: show match rewards
        screens.add(new Screen()
                .id("Continue?")
                .image_key("vampire_I_large.png")
                .text("")
                .preinit(() -> {
                    Screen s = EventManager.CURRENT_EVENT.getScreen("Continue?");
                    s.option_clear();
                    boolean is_winner = QuestUtil_MatchData.MATCH_RESULT == QuestUtil_MatchData.MatchResult.WIN;

                    if (is_winner)
                    {
                        if (QuestUtil_MatchData.NUM_PROGRESS <= 2)
                        {
                            s.text("Well fought. Do you wish to continue exploring?"+ "\n\nYou have " + (3 - QuestUtil_MatchData.NUM_PROGRESS) + "/3 times left to explore the secret realm");
                            s.option_add("Continue", this::fight);
                        }
                        else s.text("Time is up, you feel a jerking sensation as the protection array teleports you back to the academy. \n\nA good harvest hopefully?...");
                    }
                    else
                    {
                        s.text("You were severely injured. Sensing the danger to your life, you were teleported back in time. As you pass out, you feel the apprentices pick you up.");
                    }

                    s.option_add(is_winner ? "Leave" : "...", () ->
                    {
                        this.close();
                        this.close_match_screen();
                        QuestUtil_MatchData.flush();
                    });

                    s.show_extra(true);
                })
                .init(() -> {
                    Screen s = EventManager.CURRENT_EVENT.getScreen("Continue?");
                    if (QuestUtil_MatchData.STR_CRYSTALS != null) s.push_extra(QuestUtil_MatchData.STR_CRYSTALS, FSkinProp.ICO_QUEST_GOLD);
                    if (QuestUtil_MatchData.STR_CRYSTALS_LOSS != null) s.push_extra(QuestUtil_MatchData.STR_CRYSTALS_LOSS, FSkinProp.ICO_QUEST_GOLD);
                    if (QuestUtil_MatchData.CARDS != null) s.push_cards(QuestUtil_MatchData.CARDS);

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
