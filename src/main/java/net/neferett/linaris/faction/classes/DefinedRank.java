package net.neferett.linaris.faction.classes;

import lombok.Data;
import net.neferett.linaris.api.ranks.RankAPI;
import net.neferett.linaris.faction.handlers.kits.Kits;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

@Data
public class DefinedRank {

    private List<String> commands = new ArrayList<>();

    private List<String> spells = new ArrayList<>();

    private List<ItemStack> items = new ArrayList<>();

    private Long cooldown = (long)0;

    private final int linkedRank;

    public Kits getKit() {
        return new Kits(String.valueOf(linkedRank), this.cooldown, items.toArray(new ItemStack[items.size()]));
    }
}
