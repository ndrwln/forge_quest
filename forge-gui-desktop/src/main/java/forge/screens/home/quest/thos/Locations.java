package forge.screens.home.quest.thos;

import forge.gamemodes.quest.*;
import forge.gamemodes.quest._thos.Boosters;
import forge.model.FModel;
import forge.screens.home.quest.VSubmenuQuestStart;
import forge.sound.MusicPlaylist;
import forge.sound.SoundSystem;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

import javax.swing.*;
import java.util.ArrayList;

import static forge.screens.home.quest.VSubmenuQuestStart.MEDIA_VIEW;
import static forge.screens.home.quest.VSubmenuQuestStart.jfxPanel;
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



    public static final Location ABYSSAL_BONE_FOREST_PLANE = new Location()
            .video("black_hunting.mp4")
            ;

    public static final Location ABYSSAL_BONE_FOREST = new Location()
            .video("black.mp4")
            .plane(ABYSSAL_BONE_FOREST_PLANE)

            .add_action(UI_GENERAL)
            .add_action(UI_INFO)
            .add_action(UI_LEARN)
            .add_action(UI_EXPLORE)

            .I(DuelBucket.ABYSSAL_BONE_FOREST_I)
            .II(DuelBucket.ABYSSAL_BONE_FOREST_II)
            .III(DuelBucket.ABYSSAL_BONE_FOREST_III)

            ;



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

    public static final Location BLUE_AREA_HUNTING = new Location()
            .video("blue_hunting.mp4")
            .add_action(UI_GENERAL)
            .add_action(UI_INFO)
            .add_action(UI_EXPLORE);

    public static final Location BLUE_AREA = new Location()
            .video("blue.mp4")
            .plane(BLUE_AREA_HUNTING)
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








    //fns
    public static Location CURRENT_LOCATION = MAIN_MENU;
    public static Location PREVIOUS_LOCATION = MAIN_MENU;



    public static void travelToPlane(Location location)
    {
        if (location.plane != null) travelTo(location.plane);

    }

    public static void set_CURRENT_LOCATION(Location location)
    {
        PREVIOUS_LOCATION = Locations.CURRENT_LOCATION;
        Locations.CURRENT_LOCATION = location;
        QuestUtilCards.I = Locations.CURRENT_LOCATION.I;
        QuestUtilCards.II = Locations.CURRENT_LOCATION.II;
        QuestUtilCards.III = Locations.CURRENT_LOCATION.III;

    }

    public static boolean is_doing_init = true;

    public static void travelTo(Location location)
    {
        if (CURRENT_LOCATION == MAIN_MENU) SoundSystem.instance.setBackgroundMusic(MusicPlaylist.MENUS);

        if (is_doing_init)
        {
            VSubmenuQuestStart.boosters = new Boosters();
        }

        Platform.setImplicitExit(false);
        Platform.runLater(() -> {
            MediaPlayer player = location.player();
            player.setCycleCount(MediaPlayer.INDEFINITE);
            player.setAutoPlay(true);
            player.setMute(true);

            CURRENT_LOCATION.fadeOut();

            FadeTransition in = new FadeTransition();
            in.setDuration(Duration.millis(500));
            in.setNode(jfxPanel.getScene().getRoot());
            in.setFromValue(0);
            in.setToValue(1);
            in.setOnFinished(event1 -> {
                update_stats();
                location.fadeIn();
                set_CURRENT_LOCATION(location);
            });


            FadeTransition out = new FadeTransition();
            out.setDuration(Duration.millis(250));
            out.setNode(jfxPanel.getScene().getRoot());
            out.setFromValue(1);
            out.setToValue(0);
            out.setOnFinished(event -> {
                MEDIA_VIEW.getMediaPlayer().pause();
                MEDIA_VIEW.setMediaPlayer(player);
                MEDIA_VIEW.getMediaPlayer().play();

                in.play();
            });
            out.play();
        });


    }





    public static void update_stats()
    {
        SwingUtilities.invokeLater(() -> {
            try {
                lbl_crystals.fLabel.setText(QuestUtil.formatCredits(FModel.getQuest().getAssets().getCredits()));
                lbl_life.fLabel.setText(FModel.getQuest().getAssets().getLife(QuestMode.Classic) + "/" + FModel.getQuest().getAssets().getLifeMax(QuestMode.Classic));
            }
            catch (Exception e) {}

        });
    }



}
