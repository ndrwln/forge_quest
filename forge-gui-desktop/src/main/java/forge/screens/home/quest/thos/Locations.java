package forge.screens.home.quest.thos;

import forge.gamemodes.quest.QuestMode;
import forge.gamemodes.quest.QuestUtil;
import forge.model.FModel;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

import java.util.ArrayList;

import static forge.screens.home.quest.VSubmenuQuestStart.MEDIA_VIEW;
import static forge.screens.home.quest.thos.Buttons.lbl_crystals;
import static forge.screens.home.quest.thos.Buttons.lbl_life;

public class Locations {

    public static final ArrayList<SNode> UI_MAIN = new ArrayList<>();
    public static final ArrayList<SNode> UI_INFO = new ArrayList<>();
    public static final ArrayList<SNode> UI_GENERAL = new ArrayList<>();
    public static final ArrayList<SNode> UI_MAP = new ArrayList<>();
    public static final ArrayList<SNode> UI_LEARN = new ArrayList<>();
    public static final ArrayList<SNode> UI_GOTOPLANE = new ArrayList<>();
    public static final ArrayList<SNode> UI_EXPLORE = new ArrayList<>();

    public static final Location MAIN_MENU = new Location()
            .video("main_menu.mp4")
            .add_action(UI_MAIN);

    public static final Location REST_AREA = new Location()
            .video("scene_rest.mp4")
            .add_action(UI_GENERAL)
            .add_action(UI_INFO);

    public static final Location MAP_AREA = new Location()
            .video("map.mp4")
            .add_action(UI_MAP);

    public static final Location GREEN_AREA_HUNTING = new Location()
            .video("green_hunting.mp4")
            .add_action(UI_GENERAL)
            .add_action(UI_INFO)
            .add_action(UI_EXPLORE);

    public static final Location GREEN_AREA = new Location()
            .video("green.mp4")
            .plane(GREEN_AREA_HUNTING)
            .add_action(UI_GENERAL)
            .add_action(UI_INFO)
            .add_action(UI_LEARN)
            .add_action(UI_GOTOPLANE);

    public static final Location RED_AREA = new Location()
            .video("red.mp4")
            .plane(GREEN_AREA_HUNTING)
            .add_action(UI_GENERAL)
            .add_action(UI_INFO)
            .add_action(UI_LEARN)
            .add_action(UI_GOTOPLANE);

    public static final Location BLUE_AREA = new Location()
            .video("blue.mp4")
            .plane(GREEN_AREA_HUNTING)
            .add_action(UI_GENERAL)
            .add_action(UI_INFO)
            .add_action(UI_LEARN)
            .add_action(UI_GOTOPLANE);

    public static final Location WHITE_AREA = new Location()
            .video("white.mp4")
            .plane(GREEN_AREA_HUNTING)
            .add_action(UI_GENERAL)
            .add_action(UI_INFO)
            .add_action(UI_LEARN)
            .add_action(UI_GOTOPLANE);

    public static final Location BLACK_AREA_HUNTING = new Location()
            .video("black_hunting.mp4")
            .add_action(UI_GENERAL)
            .add_action(UI_INFO)
            .add_action(UI_EXPLORE);

    public static final Location BLACK_AREA = new Location()
            .video("black.mp4")
            .plane(BLACK_AREA_HUNTING)
            .add_action(UI_GENERAL)
            .add_action(UI_INFO)
            .add_action(UI_LEARN)
            .add_action(UI_GOTOPLANE);






    //fns
    public static Location CURRENT_LOCATION = GREEN_AREA_HUNTING;
    public static Location PREVIOUS_LOCATION = GREEN_AREA_HUNTING;

    public static void travelToPlane(Location location)
    {
        if (location.plane != null) travelTo(location.plane);

    }

    public static void travelTo(Location location)
    {
        Platform.setImplicitExit(false);
        Platform.runLater(() -> {
            Platform.runLater(() -> {
                MediaPlayer player = location.player();
                player.setCycleCount(MediaPlayer.INDEFINITE);
                player.setAutoPlay(true);

                CURRENT_LOCATION.fadeOut();

                FadeTransition out = new FadeTransition();
                out.setDuration(Duration.millis(500));
                out.setNode(MEDIA_VIEW);
                out.setFromValue(1);
                out.setToValue(0);
                out.setOnFinished(event -> {
                    MEDIA_VIEW.setMediaPlayer(player);

                    FadeTransition in = new FadeTransition();
                    in.setDuration(Duration.millis(500));
                    in.setNode(MEDIA_VIEW);
                    in.setFromValue(0);
                    in.setToValue(1);
                    in.setOnFinished(event1 -> {
                        update_stats();
                        location.fadeIn();
                        PREVIOUS_LOCATION = Locations.CURRENT_LOCATION;
                        Locations.CURRENT_LOCATION = location;
                    });
                    in.play();
                });

                out.play();

            });
        });


    }





    public static void update_stats()
    {
        lbl_crystals.fLabel.setText(QuestUtil.formatCredits(FModel.getQuest().getAssets().getCredits()));
        lbl_life.fLabel.setText(FModel.getQuest().getAssets().getLife(QuestMode.Classic) + "/" + FModel.getQuest().getAssets().getLifeMax(QuestMode.Classic));
    }



}
