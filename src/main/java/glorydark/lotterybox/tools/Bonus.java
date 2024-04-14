package glorydark.lotterybox.tools;

import cn.nukkit.item.Item;

import java.util.List;

public class Bonus {
    private final String name;
    private final Item[] items;
    private final List<String> consolecommands;
    private final int needTimes;

    public Bonus(String prizeName, Item[] items, List<String> consolecommands, int needTimes) {
        this.name = prizeName;
        this.items = items;
        this.consolecommands = consolecommands;
        this.needTimes = needTimes;
    }

    public String getName() {
        return name;
    }

    public int getNeedTimes() {
        return needTimes;
    }

    public List<String> getConsolecommands() {
        return consolecommands;
    }

    public Item[] getItems() {
        return items;
    }
}
