package forge.screens.home.quest.thos.Duels;

import forge.gamemodes.quest.QuestEventDuel;
import forge.gamemodes.quest.io.MainWorldDuelReader;
import forge.localinstance.properties.ForgeConstants;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.File;

import static forge.localinstance.properties.ForgeConstants.DEFAULT_DUELS_DIR;

@Getter @Setter @Accessors(fluent = true, chain = true) public class Duel {

    static MainWorldDuelReader reader = new MainWorldDuelReader(new File(ForgeConstants.DEFAULT_DUELS_DIR));

    QuestEventDuel duel;
    String fname;

    public Duel build()
    {
        duel = reader.read(new File(DEFAULT_DUELS_DIR , fname));
        return this;
    }


}
