package forge.screens.home.quest.thos;

import forge.gui.UiCommand;
import forge.toolbox.FLabel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

@Getter @Setter @Accessors(fluent = true, chain = true) public class SNode {
    public static ArrayList<SNode> sNodes = new ArrayList<>();


    FLabel fLabel;
    String constraints;
    ArrayList<SNode> ui;
    UiCommand fn;
    boolean is_transparent = false;

    public SNode() { sNodes.add(this); }

    public static void init_panels(JPanel parent)
    {
        for (SNode s : sNodes)
        {
            if (s.fn != null) s.fLabel.setCommand(s.fn);
            s.fLabel.setOpacity(0.0F);
            s.fLabel.setEnabled(false);
            if (s.is_transparent) s.fLabel.setBackground(new Color(0,0,0,0));
        }
    }

    public static void populate_nodes()
    {
        for (SNode sNode : sNodes)
        {
           if (sNode.ui != null) sNode.ui.add(sNode);
        }
    }

    public static void add_to_view(JPanel parent, SNode s)
    {
        parent.add(s.fLabel, s.constraints);
    }

    public static void remove_from_view(JPanel parent, SNode s)
    {
        parent.remove(s.fLabel);
    }
}
