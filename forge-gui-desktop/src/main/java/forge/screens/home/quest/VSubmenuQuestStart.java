package forge.screens.home.quest;

import forge.gui.framework.DragCell;
import forge.gui.framework.DragTab;
import forge.gui.framework.EDocID;
import forge.screens.home.EMenuGroup;
import forge.screens.home.IVSubmenu;
import forge.screens.home.VHomeUI;
import forge.screens.home.quest.thos.Buttons;
import forge.screens.home.quest.thos.Location;
import forge.screens.home.quest.thos.Locations;
import forge.screens.home.quest.thos.SNode;
import forge.util.Localizer;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;

/**
 * Assembles Swing components of quest data submenu singleton.
 *
 * <br><br><i>(V at beginning of class name denotes a view class.)</i>
 */
public enum VSubmenuQuestStart implements IVSubmenu<CSubmenuQuestStart> {
    SINGLETON_INSTANCE;

    public static Buttons buttons = new Buttons();

    private final JPanel main_panel = new JPanel();
    public static JPanel MAIN_PANEL;
    public static final MediaView MEDIA_VIEW = new MediaView();

    @SuppressWarnings("unchecked") VSubmenuQuestStart() {
        main_panel.setOpaque(false);
        main_panel.setLayout(new MigLayout("insets 0, gap 10px, fillx, wrap 2"));
    }

    public static boolean is_playing_new_music = false;

    @Override public void populate() {
//        if (is_playing_new_music)
//        {
//            SoundSystem.instance.setBackgroundMusic(MusicPlaylist.MENUS);
//            is_playing_new_music = false;
//        }

        Platform.setImplicitExit(false);
        VHomeUI.SINGLETON_INSTANCE.getPnlDisplay().removeAll();
        VHomeUI.SINGLETON_INSTANCE.getPnlDisplay().setLayout(new MigLayout("insets 0, gap 0, wrap"));

        MAIN_PANEL = main_panel;
        VHomeUI.SINGLETON_INSTANCE.getPnlDisplay().add(main_panel, "w 98%!, growy, pushy, gap 1% 0 0 0");
        JFXPanel fxPanel = new JFXPanel();
        VHomeUI.SINGLETON_INSTANCE.getPnlDisplay().add(fxPanel, "pos 0 0");

        Location.init_videos();
        SNode.init_panels(main_panel);
        SNode.populate_nodes();

        Platform.setImplicitExit(false);
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                AnchorPane pane = new AnchorPane();
                pane.setStyle("-fx-background-color: #000000");

                Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
                Scene scene = new Scene(pane, screenSize.width, screenSize.height);

                fxPanel.setBackground(Color.BLACK);
                fxPanel.setScene(scene);

                MEDIA_VIEW.setMediaPlayer(Locations.CURRENT_LOCATION.player());
                MEDIA_VIEW.getMediaPlayer().setCycleCount(MediaPlayer.INDEFINITE);
                MEDIA_VIEW.getMediaPlayer().setAutoPlay(true);
                Locations.CURRENT_LOCATION.fadeIn();
                Locations.update_stats();
                pane.getChildren().add(MEDIA_VIEW);

            }
        });

        VHomeUI.SINGLETON_INSTANCE.getPnlDisplay().repaintSelf();
        VHomeUI.SINGLETON_INSTANCE.getPnlDisplay().revalidate();
    }



    //interface IVDoc
    private DragCell parentCell;
    private final DragTab tab = new DragTab(Localizer.getInstance().getMessage("lblStartanewQuest"));
    @Override public EMenuGroup getGroupEnum() {return EMenuGroup.QUEST;}
    @Override public String getMenuTitle() {return Localizer.getInstance().getMessage("lblStartanewQuest");}
    @Override public EDocID getItemEnum() {return getDocumentID();}
    @Override public EDocID getDocumentID() {return EDocID.HOME_QUESTSTART;}
    @Override public DragTab getTabLabel() {return tab;}
    @Override public CSubmenuQuestStart getLayoutControl() {return CSubmenuQuestStart.SINGLETON_INSTANCE;}
    @Override public void setParentCell(final DragCell cell0) {this.parentCell = cell0;}
    @Override public DragCell getParentCell() {return parentCell;}

}
