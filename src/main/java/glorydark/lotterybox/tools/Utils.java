package glorydark.lotterybox.tools;

import RcRPG.RPG.Armour;
import RcRPG.RPG.Ornament;
import RcRPG.RPG.Stone;
import RcRPG.RPG.Weapon;
import RcRPG.RcRPGMain;
import cn.ankele.plugin.MagicItem;
import cn.ankele.plugin.bean.ItemBean;
import cn.ankele.plugin.utils.Tools;
import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.item.Item;
import cn.nukkit.lang.LangCode;
import glorydark.lotterybox.MainClass;

import java.time.Instant;
import java.util.LinkedHashMap;

import static cn.ankele.plugin.utils.BaseCommand.createItem;
import static java.lang.Integer.parseInt;

public class Utils {
    public static long getNowTime() {
        Instant timestamp = Instant.now();
        long millis = timestamp.toEpochMilli();
        return millis;
    }

    public static Item parseItemString(String str, LangCode langCode) {
        String[] arr = str.split("@");
        if (arr[0].equals("mi")) {// mi@1 代金券
            if (Server.getInstance().getPluginManager().getPlugin("MagicItem") == null) {
                MainClass.getInstance().getLogger().warning("你没有使用 MagicItem 插件却在试图获取它的物品：" + str);
                return Item.AIR_ITEM;
            }
            LinkedHashMap<String, ItemBean> items = MagicItem.getItemsMap();
            LinkedHashMap<String, Object> otherItems = MagicItem.getOthers();
            String[] args = arr[1].split(" ");
            if (items.containsKey(args[1])) {
                ItemBean item = items.get(args[1]);
                Item back = createItem(item);
                back.setCount(parseInt(args[0]));
                return back;
            } else if (otherItems.containsKey(args[1])) {
                String[] otherItemArr = ((String) otherItems.get(args[1])).split(":");
                Item item = Item.get(parseInt(otherItemArr[0]), parseInt(otherItemArr[1]));
                item.setCount(parseInt(args[0]));
                item.setCompoundTag(Tools.hexStringToBytes(otherItemArr[3]));
                return item;
            } else {
                MainClass.getInstance().getLogger().warning("MagicItem物品不存在：" + args[1]);
            }
        } else if (arr[0].equals("item")) {
            String[] args = arr[1].split(" ");
            Item item = Item.fromString(args[0]);
            if (args.length == 2) {
                item.setCount(parseInt(args[1]));
            } else {
                item.setDamage(parseInt(args[1]));
                item.setCount(parseInt(args[2]));
            }
            return item;
        } else if (arr[0].equals("nweapon") || arr[0].equals("rcrpg")) {
            if (Server.getInstance().getPluginManager().getPlugin("RcRPG") == null) {
                MainClass.getInstance().getLogger().warning("你没有使用 RcRPG 插件却在试图获取它的物品：" + str);
                return Item.AIR_ITEM;
            }
            String[] args = arr[1].split(" ");//Main.loadWeapon
            String type = args[0];
            String itemName = args[1];
            int count = 1;

            if (args.length > 2) {
                count = parseInt(args[2]);
            }

            switch (type) {
                case "护甲", "防具", "armour", "armor" -> {
                    if (RcRPGMain.loadArmour.containsKey(itemName)) {
                        return Armour.getItem(itemName, count);
                    }
                }
                case "武器", "weapon" -> {
                    if (RcRPGMain.loadWeapon.containsKey(itemName)) {
                        return Weapon.getItem(itemName, count);
                    }
                }
                case "宝石", "stone", "gem" -> {
                    if (RcRPGMain.loadStone.containsKey(itemName)) {
                        return Stone.getItem(itemName, count);
                    }
                }
                case "饰品", "ornament", "jewelry" -> {
                    if (RcRPGMain.loadOrnament.containsKey(itemName)) {
                        return Ornament.getItem(itemName, count);
                    }
                }
                case "锻造图" -> {
                }
                case "宝石券", "精工石", "强化石", "锻造石" -> {
                }
            }
            return Item.AIR_ITEM;
            //return nWeapon.onlyNameGetItem(args[0], args[1], args[2], null);
        } else {
            MainClass.getInstance().getLogger().warning("物品配置有误：" + str);
        }
        return Item.AIR_ITEM;
    }
    public static Item parseItemString(String str) {
        return parseItemString(str, LangCode.zh_CN);
    }

    public static int defaultVaule(int value) {
        if (value == 0) {
            return 1;
        }
        return value;
    }

    /**
     * 向玩家背包添加物品
     *
     * @param player 要添加物品的玩家
     * @param item   要添加到玩家背包的物品
     */
    public static void addItemToPlayer(Player player, Item item) {
        if (player.getInventory().canAddItem(item)) {
            player.getInventory().addItem(item);
        } else {
            player.sendPopup(MainClass.getI18n().tr(player.getLanguageCode(), "MainClass.item.item_drop_tips", item.getName()));
            player.getLevel().dropItem(player, item);
        }
    }

}
