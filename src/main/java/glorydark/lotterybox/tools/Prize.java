package glorydark.lotterybox.tools;

import cn.nukkit.item.Item;
import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;
import java.util.List;

@Getter
@Setter
public class Prize {
    private String name;
    private String description;
    private Item displayitem;
    private boolean broadcast;
    private Item[] items;
    private List<String> consolecommands;
    private int possibility;

    private boolean showOriginName;

    private String rarity;

    public Prize(String name, String description, Item displayItem, boolean broadcast, Item[] items, List<String> consolecommands, int possibility, boolean showOriginName, String rarity) {
        this.name = name;
        this.description = description
                .replace("{item.name}", displayItem.getCustomName() != null ? displayItem.getCustomName() : displayItem.getName())
                .replace("{item.lore}", String.join("\n", displayItem.getLore()));
        this.displayitem = displayItem;
        this.broadcast = broadcast;
        this.items = items;
        this.consolecommands = consolecommands;
        this.possibility = possibility;
        this.showOriginName = showOriginName;
        this.rarity = rarity;
    }

    @Override
    public String toString() {
        return "Prize{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", displayitem=" + displayitem +
                ", broadcast=" + broadcast +
                ", items=" + Arrays.toString(items) +
                ", consolecommands=" + consolecommands +
                ", possibility=" + possibility +
                '}';
    }
}
