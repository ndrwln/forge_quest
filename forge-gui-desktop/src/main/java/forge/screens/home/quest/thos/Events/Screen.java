package forge.screens.home.quest.thos.Events;

import forge.gui.UiCommand;
import forge.item.PaperCard;
import forge.localinstance.skin.FSkinProp;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

@Getter @Setter @Accessors(fluent = true, chain = true) public class Screen {
    String id;
    String text;
    String image_key;
    UiCommand preinit;
    UiCommand init;
    private ScreenDialog dialog;

    boolean show_extra = false;

    final LinkedHashMap<String, UiCommand> options = new LinkedHashMap<String, UiCommand>();
    //option text -> command

    public void show() {
        if (preinit != null) preinit.run();

        dialog = new ScreenDialog();
        dialog.image_key(image_key);
        dialog.text(text);
        dialog.options(options);
        dialog.show_extra(show_extra);

        dialog.init();
        if (init != null) init.run();

        dialog.show(() -> {

        });
    }

    public void push_extra(String msg, final FSkinProp icon)
    {
        dialog.push_extra(msg, icon);
    }


    public void push_cards(final List<PaperCard> cards)
    {
        dialog.push_cards(cards);
    }

    public Screen option_add(String s, UiCommand u)
    {
        options.put(s, u);
        return this;
    }

    public void option_clear()
    {
        options.clear();
    }

    public Screen insertInto(ArrayList<Screen> lst)
    {
        lst.add(this);
        return this;
    }
}
