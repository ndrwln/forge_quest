package forge.screens.home.quest.thos;

import forge.gui.UiCommand;
import forge.toolbox.FLabel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.swing.*;
import java.util.ArrayList;

@Getter @Setter @Accessors(fluent = true, chain = true) public class SNode {
    public static ArrayList<SNode> sNodes = new ArrayList<>();


    FLabel fLabel;
    String constraints;
    ArrayList<SNode> ui;
    UiCommand fn;

    public SNode() { sNodes.add(this); }


//    Double x = 0.0;public SNode setX(double x) {this.x = x;return this;}
//    Double y = 0.0;public SNode setY(double y) {this.y = y;return this;}
//    Double x_step = 0.0;public SNode setX_step(double x_step) {this.x_step = x_step;return this;}
//    Double y_step = 0.0;public SNode setY_step(double y_step) {this.y_step = y_step;return this;}
//    boolean panelTransparent = false; public SNode setPanelTransparent() {panelTransparent = true; return this;}

    public static void init_buttons()
    {
        Buttons.init_buttons();
    }


    public static void init_panels(JPanel parent)
    {
        for (SNode s : sNodes)
        {
            parent.add(s.fLabel, s.constraints);
            if (s.fn != null) s.fLabel.setCommand(s.fn);
            s.fLabel.setOpacity(0.0F);
            s.fLabel.setEnabled(false);
        }
    }

    public static void populate_nodes()
    {
        for (SNode sNode : sNodes)
        {
           if (sNode.ui != null) sNode.ui.add(sNode);
        }
    }
}
