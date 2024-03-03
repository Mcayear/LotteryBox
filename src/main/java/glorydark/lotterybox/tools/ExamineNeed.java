package glorydark.lotterybox.tools;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import glorydark.lotterybox.MainClass;
import glorydark.lotterybox.adapter.CodeException;
import glorydark.lotterybox.adapter.Econ;
import glorydark.lotterybox.adapter.PointCoupon;
import glorydark.lotterybox.config.McrmbConfig;
import net.player.api.Point;

import java.util.ArrayList;
import java.util.List;


public class ExamineNeed {
    public static boolean examineNeed(String[] needArray, Player player) {
        return examineNeed(needArray, player, "LotteryBox");
    }
    public static boolean examineNeed(String[] needArray, Player player, String reason) {
        List<String> itemNeedList = new ArrayList<>();
        List<Item> itemList = new ArrayList<>();
        int needMoney = 0;
        int needRMB = 0;
        int needPoint = 0;
        for (int i = 0; i < needArray.length; i++) {
            String[] type = needArray[i].split("@");
            if (type[0].equals("money")) {
                needMoney += Integer.parseInt(type[1]);
                continue;
            } else if (type[0].equals("rmb")) {
                needRMB += Integer.parseInt(type[1]);
                continue;
            } else if (type[0].equals("point")) {
                needPoint += Integer.parseInt(type[1]);
                continue;
            }
            Item item = Utils.parseItemString(needArray[i], player.getLanguageCode());
            if (item == null) {
                MainClass.getInstance().getLogger().warning("配置文件中需求有误: " + String.join("||", needArray));
                return false;
            }
            if (player.getInventory().contains(item)) {
                itemList.add(item);
            } else {
                itemNeedList.add((item.getCustomName() != null ? item.getCustomName() : item.getName()) + " §r*" + item.getCount());
            }
        }
        if (!itemNeedList.isEmpty()) {
            player.sendMessage(MainClass.getI18n().tr(player.getLanguageCode(), "ninvshop.need_failed_msg", String.join("、", itemNeedList)));
            return false;
        }
        Econ EconAPI = new Econ(player);
        if (needMoney > 0) {
            if (EconAPI.getMoney() < needMoney) {
                player.sendMessage(MainClass.getI18n().tr(player.getLanguageCode(), "ninvshop.need_failed_msg", "Money *" + (needMoney - EconAPI.getMoney())));
                return false;
            }
            EconAPI.reduceMoney(needMoney);
        }
        if (needPoint > 0) {
            if (!Point.reducePoint(player, needPoint)) {
                player.sendMessage(MainClass.getI18n().tr(player.getLanguageCode(), "ninvshop.cannot.point", McrmbConfig.website).replace("{n}", "\n"));
                return false;
            }
        } else if (needRMB > 0) {
            boolean isPay;
            try {
                isPay = PointCoupon.toPay(player.getName().replace(" ", "_"), needRMB, reason);
            } catch (CodeException e) {
                player.sendMessage("出现了未知错误："+e);
                return false;
            }

            if (!isPay) {
                player.sendMessage(MainClass.getI18n().tr(player.getLanguageCode(), "ninvshop.cannot.point", McrmbConfig.website).replace("{n}", "\n"));
                return false;
            }
        }
        for (Item v : itemList) {
            player.getInventory().removeItem(v);
        }
        return true;
    }

}