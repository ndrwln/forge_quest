package forge.screens.home.quest.thos;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.pushingpixels.radiance.animation.api.Timeline;
import org.pushingpixels.radiance.animation.api.callback.TimelineCallback;

import javax.swing.*;
import java.util.ArrayList;

@Getter @Setter @Accessors(fluent = true, chain = true) public class Location {

    ArrayList<ArrayList<SNode>> actions = new ArrayList<>();
    String video;

    public Location() {}
    public Location add_action(ArrayList<SNode> action) { actions.add(action); return this; }


    public void fadeOut() {fadeOut(500);}
    public void fadeOut(int ms)
    {
        SwingUtilities.invokeLater(() -> {
            for (ArrayList<SNode> a : actions) for (SNode node : a)
            {
                node.fLabel.setEnabled(false);
                Timeline.builder(node.fLabel())
                        .addPropertyToInterpolate("opacity", node.fLabel.getStartingAlpha(), 0.0F)
                        .setDuration(ms)
                        .play();
            }
        });
    }

    public void fadeIn() {fadeIn(500);}
    public void fadeIn(int ms)
    {
        SwingUtilities.invokeLater(() -> {
            for (ArrayList<SNode> a : actions) for (SNode node : a)
            {
                Timeline.builder(node.fLabel())
                        .addPropertyToInterpolate("opacity", 0.0F, node.fLabel.getStartingAlpha())
                        .setDuration(ms)
                        .addCallback(new TimelineCallback() {
                            @Override public void onTimelineStateChanged(Timeline.TimelineState timelineState, Timeline.TimelineState timelineState1, float v, float v1) {
                                if (timelineState == Timeline.TimelineState.DONE) node.fLabel.setEnabled(true);
                            }
                            @Override public void onTimelinePulse(float v, float v1) {}
                        } )
                        .play();
            }
        });
    }




}
