package forge.screens.home.quest.thos.Events;

import forge.gamemodes.quest.QuestUtil_MatchData;
import forge.gui.SOverlayUtils;
import forge.screens.match.QuestWinLose;

import java.util.ArrayList;
import java.util.List;

//The parent class to implement an event text, dialogue, options
public class Event {
    protected final List<Screen> screens = new ArrayList<Screen>();

    /**
     * display the first screen
     */
    public void display(){
        display(screens.get(0));
    }

    public void display(String id){
        SOverlayUtils.hideOverlay();
        Screen to_display = null;
        for (Screen screen : screens)
        {
            if (!screen.id().equals(id)) continue;
            to_display = screen;
            break;
        }
        display(to_display);
    }

    private void display(Screen screen)
    {
        EventManager.CURRENT_EVENT = this;
        screen.show();
    }

    public Screen getScreen(String id){
        Screen to_display = null;
        for (Screen screen : screens)
        {
            if (!screen.id().equals(id)) continue;
            to_display = screen;
            break;
        }
        return to_display;
    }

    //Helper

    public void close() {
        SOverlayUtils.hideOverlay();
    }

    public void close_match_screen()
    {
        QuestWinLose.INSTANCE.actionOnQuit_real();
    }

    public void onClose() {

    }

    /**
     * used by the event manager to add the event to possible roll, if conditions are met.
     * Functional events like the enemy encounter event is always false to be out of the pool
     */
    public boolean isValid() {return false;}




}
