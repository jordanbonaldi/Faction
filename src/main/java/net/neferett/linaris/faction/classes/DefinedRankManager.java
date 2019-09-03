package net.neferett.linaris.faction.classes;

import lombok.Data;
import net.neferett.linaris.api.ranks.Kit;
import net.neferett.linaris.api.ranks.RankAPI;
import net.neferett.linaris.faction.Main;

import java.util.ArrayList;
import java.util.List;

@Data
public class DefinedRankManager {

    private List<DefinedRank> ranks = new ArrayList<>();

    public static DefinedRankManager getInstance() {
        return Main.getInstanceMain().getDefinedRank();
    }

    public DefinedRank getRank(int id) {
        return ranks.stream().filter(e -> e.getLinkedRank() == id).findFirst().orElse(null);
    }
}
