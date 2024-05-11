package forge.screens.home.quest.thos;

import forge.gamemodes.quest.QuestMode;
import forge.gamemodes.quest.QuestUtil;
import forge.model.FModel;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

import java.io.File;
import java.util.ArrayList;

import static forge.localinstance.properties.ForgeConstants.VIDEO_DIR;
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




    //fns
    public static Location CURRENT_LOCATION = MAIN_MENU;
    public static Location PREVIOUS_LOCATION = MAIN_MENU;

    public static void travelTo(Location location)
    {
        PREVIOUS_LOCATION = Locations.CURRENT_LOCATION;
        Locations.CURRENT_LOCATION = location;

        Platform.runLater(() -> {
            Platform.runLater(() -> {
                MediaPlayer player = new MediaPlayer(new Media(new File(VIDEO_DIR , location.video()).toURI().toString()));
                player.setCycleCount(MediaPlayer.INDEFINITE);
                player.setAutoPlay(true);
                player.setOnReady(()-> {
                    PREVIOUS_LOCATION.fadeOut();
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
                        });
                        in.play();
                    });

                    out.play();
                });
            });
        });


    }





    public static void update_stats()
    {
        lbl_crystals.fLabel.setText(QuestUtil.formatCredits(FModel.getQuest().getAssets().getCredits()));
        lbl_life.fLabel.setText(FModel.getQuest().getAssets().getLife(QuestMode.Classic) + "/" + FModel.getQuest().getAssets().getLifeMax(QuestMode.Classic));
    }



}
