package forge.screens.home.quest.thos.Events._Triggered;

import forge.screens.home.quest.thos.Events.Event;
import forge.screens.home.quest.thos.Events.Screen;

public class EventExplore extends Event {

    public EventExplore()
    {
        screens.add(new Screen()
                .id("A")
                .image_key("vampire_I_large.png")
                .text("This is a testThis is a testThis is a test\nThis is a testThis is a testThis is a test\n\nThis is a testThis is a testThis is a test/n")
                .add_option("Ok", this::close)
                .add_option("Next", () -> {
                    this.display("B");
                })
        );

        screens.add(new Screen()
                .id("B")
                .image_key("vampire_I_large.png")
                .text("2")
                .add_option("Ok", this::close)
                .add_option("Cancel", this::close)
                .add_option("C", this::close)
        );
    }


}
