package forge.screens.home.quest.thos;

import forge.gamemodes.quest.DuelBucket;
import forge.screens.home.VHomeUI;
import forge.screens.home.quest.VSubmenuQuestStart;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.pushingpixels.radiance.animation.api.Timeline;
import org.pushingpixels.radiance.animation.api.callback.TimelineCallback;

import javax.swing.*;
import java.io.File;
import java.util.ArrayList;

import static forge.localinstance.properties.ForgeConstants.VIDEO_DIR;

@Getter @Setter @Accessors(fluent = true, chain = true) public class Location {
    public static final ArrayList<Location> locations = new ArrayList<>();

    public static void init_videos()
    {
        for (Location location : locations)
        {
            if (location.player != null) return;
            location.player = new MediaPlayer(new Media(new File(VIDEO_DIR , location.video).toURI().toString()));
        }
    }

    MediaPlayer player;
    String video;
    ArrayList<ArrayList<SNode>> actions = new ArrayList<>();
    DuelBucket I;
    DuelBucket II;
    DuelBucket III;
    String lesson_key;


    ArrayList<ArrayList<SNode>> event_hunting = new ArrayList<>();
    ArrayList<ArrayList<SNode>> event_hunting_hard = new ArrayList<>();
    ArrayList<ArrayList<SNode>> event_explore = new ArrayList<>();
    Location plane = null;


    public Location() {locations.add(this);}
    public Location add_action(ArrayList<SNode> action) { actions.add(action); return this; }


    public void fadeOut() {fadeOut(500);}
    public void fadeOut(int ms)
    {
        for (SNode node : SNode.sNodes)
        {
            node.fLabel.setOpacity(0);
            node.fLabel.setEnabled(false);
            SNode.remove_from_view(VSubmenuQuestStart.MAIN_PANEL, node);
        }
        VHomeUI.SINGLETON_INSTANCE.getPnlDisplay().repaintSelf();
        VHomeUI.SINGLETON_INSTANCE.getPnlDisplay().revalidate();


    }

    public void fadeIn_long() {fadeIn(1000);}
    public void fadeIn() {fadeIn(500);}
    public void fadeIn(int ms)
    {
        SwingUtilities.invokeLater(() -> {
            for (ArrayList<SNode> a : actions) for (SNode node : a)
            {
                SNode.add_to_view(VSubmenuQuestStart.MAIN_PANEL, node);

                Timeline.builder(node.fLabel())
                        .addPropertyToInterpolate("opacity", 0.0F, node.fLabel.getStartingAlpha())
                        .setDuration(ms)
                        .addCallback(new TimelineCallback() {
                            @Override public void onTimelineStateChanged(Timeline.TimelineState timelineState, Timeline.TimelineState timelineState1, float v, float v1) {
                                if (timelineState == Timeline.TimelineState.DONE)
                                {
                                    node.fLabel.setEnabled(true);
                                }
                            }
                            @Override public void onTimelinePulse(float v, float v1) {}
                        } )
                        .play();
            }
            VHomeUI.SINGLETON_INSTANCE.getPnlDisplay().repaintSelf();
            VHomeUI.SINGLETON_INSTANCE.getPnlDisplay().revalidate();
        });
    }




}
