package forge.gamemodes.quest;

import java.util.List;

public interface QuestEventDuelManagerInterface {
    Iterable<QuestEventDuel> getAllDuels();
    Iterable<QuestEventDuel> getDuels(DuelBucket difficulty);
    List<QuestEventDuel> generateDuels();
    void randomizeOpponents();
}
