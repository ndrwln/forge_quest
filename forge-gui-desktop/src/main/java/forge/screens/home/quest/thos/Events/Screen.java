package forge.screens.home.quest.thos.Events;

import forge.gui.UiCommand;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.HashMap;

@Getter @Setter @Accessors(fluent = true, chain = true) public class Screen {
    String id;
    String text;
    String image_key;
    UiCommand execute;
    final HashMap<String, UiCommand> options = new HashMap<String, UiCommand>();
    //option text -> command

    public void show() {
        if (execute != null) execute.run();

        final ScreenDialog dialog = new ScreenDialog();
        dialog.image_key(image_key);
        dialog.text(text);
        dialog.options(options);

        dialog.init();
        dialog.show(() -> {

        });
    }

    public Screen add_option(String s, UiCommand u)
    {
        options.put(s, u);
        return this;
    }

    public Screen insertInto(ArrayList<Screen> lst)
    {
        lst.add(this);
        return this;
    }
}
