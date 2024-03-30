package glorydark.lotterybox.tasks.weight;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemFirework;
import cn.nukkit.item.enchantment.protection.EnchantmentProtectionAll;
import cn.nukkit.scheduler.Task;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.DyeColor;
import glorydark.lotterybox.LotteryBoxMain;
import glorydark.lotterybox.api.LotteryBoxAPI;
import glorydark.lotterybox.api.CreateFireworkApi;
import glorydark.lotterybox.forms.FormFactory;
import glorydark.lotterybox.tools.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class InventoryChangeTaskV2 extends Task implements Runnable {

    private final Map<Integer, Item> inventory;
    private final List<Integer> maxIndex = new ArrayList<>();
    private final Player player;
    private final LotteryBox lotteryBox;
    private final int maxSpin;
    private final int maxCounts;
    private final List<Prize> suff;
    private Integer index = 0;
    private int ticks;

    public InventoryChangeTaskV2(Player player, LotteryBox box, Integer spins) {
        this.player = player;
        this.lotteryBox = box;
        this.maxSpin = spins;
        this.suff = new ArrayList<>(lotteryBox.getPrizes());
        Collections.shuffle(this.suff);
        this.maxCounts = this.suff.size();
        int maxWeight = 0;
        for (Prize prize : this.suff) {
            maxWeight += prize.getPossibility();
        }
        for (int i = 0; i < spins; i++) {
            ThreadLocalRandom random = ThreadLocalRandom.current();
            int get = getFinalPrize(random.nextInt(1, maxWeight));
            this.maxIndex.add(maxCounts * 2 + get);
            //player.sendMessage((get) + (getPrize(get)==null? "无奖": "有奖"));
            LotteryBoxAPI.changeLotteryPlayTimes(player.getName(), lotteryBox.getName(), 1);
            int times = LotteryBoxAPI.getLotteryPlayTimes(player.getName(), lotteryBox.getName());
            if (lotteryBox.getBonus(times) != null) {
                Bonus bonus = lotteryBox.getBonus(times);
                for (String s : bonus.getConsolecommands()) {
                    Server.getInstance().dispatchCommand(Server.getInstance().getConsoleSender(), s.replace("%player%", player.getName()));
                }
                player.getInventory().addItem(bonus.getItems());
                Server.getInstance().broadcastMessage(LotteryBoxMain.lang.getTranslation("Tips", "BonusBroadcast", player.getName(), lotteryBox.getName(), bonus.getNeedTimes(), bonus.getName()));
                LotteryBoxMain.log.info("玩家 {" + player.getName() + "} 在抽奖箱 {" + lotteryBox.getName() + "} 中抽奖达到 {" + bonus.getNeedTimes() + "} 次，获得物品 {" + bonus.getName() + "}!");
            }
        }
        this.inventory = player.getInventory().getContents();
        player.getInventory().clearAll();
        player.getInventory().setItem(0, getDisplayItem(index - 4, false));
        player.getInventory().setItem(1, getDisplayItem(index - 3, false));
        player.getInventory().setItem(2, getDisplayItem(index - 2, false));
        player.getInventory().setItem(3, getDisplayItem(index - 1, false));
        player.getInventory().setItem(4, getDisplayItem(index, true));
        player.getInventory().setItem(5, getDisplayItem(index + 1, false));
        player.getInventory().setItem(6, getDisplayItem(index + 2, false));
        player.getInventory().setItem(7, getDisplayItem(index + 3, false));
        player.getInventory().setItem(8, getDisplayItem(index + 4, false));
        player.getInventory().setHeldItemIndex(4);
        player.getInventory().sendHeldItem(player);
        LotteryBoxMain.playingPlayers.add(player);
    }

    public Integer getFinalPrize(int prizeIndex) {
        int weight = 0;
        for (Prize prize : this.suff) {
            weight += prize.getPossibility();
            if (weight >= prizeIndex) {
                return this.suff.indexOf(prize);
            }
        }
        return 0;
    }

    public Prize getPrizeByIndex(int index) { //改
        return this.suff.get((index + maxCounts * 2) % maxCounts);
    }

    public Item getDisplayItem(Integer index, boolean isEnchanted) {
        int realIndex = (index + maxCounts * 2) % maxCounts;
        Item item = this.suff.get(realIndex).getDisplayitem().clone();
        if (isEnchanted) {
            item.addEnchantment(new EnchantmentProtectionAll());
        }
        return item;
    }

    @Override
    public void onRun(int i) {
        if (player.isOnline() && !LotteryBoxMain.banWorlds.contains(player.getLevel().getName()) && LotteryBoxMain.isWorldAvailable(player.getLevel().getName()) && LotteryBoxMain.playingPlayers.contains(player)) {
            Integer thisMaxIndex = maxIndex.get(0);
            ticks += 1;
            if (thisMaxIndex > 10) {
                if (index < 4) {
                    if (ticks % 4 != 0) {
                        return;
                    }
                }
                if (index + 4 < thisMaxIndex) {
                    if (ticks % 2 != 0) {
                        return;
                    }
                }
            }
            if (index <= thisMaxIndex) {
                player.getInventory().clearAll();
                player.getInventory().setItem(0, getDisplayItem(index - 4, false));
                player.getInventory().setItem(1, getDisplayItem(index - 3, false));
                player.getInventory().setItem(2, getDisplayItem(index - 2, false));
                player.getInventory().setItem(3, getDisplayItem(index - 1, false));
                player.getInventory().setItem(4, getDisplayItem(index, true));
                player.getInventory().setItem(5, getDisplayItem(index + 1, false));
                player.getInventory().setItem(6, getDisplayItem(index + 2, false));
                player.getInventory().setItem(7, getDisplayItem(index + 3, false));
                player.getInventory().setItem(8, getDisplayItem(index + 4, false));
                player.getInventory().setHeldItemIndex(4);
                player.getInventory().sendHeldItem(player);
                Prize prize = getPrizeByIndex(index);
                if (prize != null) {
                    lotteryBox.addBlockParticle(player, getPrizeByIndex(index));
                    switch (LotteryBoxMain.showType) {
                        case "actionbar":
                            player.sendActionBar(LotteryBoxMain.lang.getTranslation("Tips", "PrizeShow", prize.getName()));
                            break;
                        case "tip":
                            player.sendTip(LotteryBoxMain.lang.getTranslation("Tips", "PrizeShow", prize.getName()));
                            break;
                        case "popup":
                            player.sendPopup(LotteryBoxMain.lang.getTranslation("Tips", "PrizeShow", prize.getName()));
                            break;
                    }
                } else {
                    switch (LotteryBoxMain.showType) {
                        case "actionbar":
                            player.sendActionBar(LotteryBoxMain.lang.getTranslation("Tips", "PrizeShow", LotteryBoxMain.lang.getTranslation("PlayLotteryWindow", "BlockAir")));
                            break;
                        case "tip":
                            player.sendTip(LotteryBoxMain.lang.getTranslation("Tips", "PrizeShow", LotteryBoxMain.lang.getTranslation("PlayLotteryWindow", "BlockAir")));
                            break;
                        case "popup":
                            player.sendPopup(LotteryBoxMain.lang.getTranslation("Tips", "PrizeShow", LotteryBoxMain.lang.getTranslation("PlayLotteryWindow", "BlockAir")));
                            break;
                    }
                }
                player.getLevel().addSound(player.getPosition(), lotteryBox.getSound());
                index++;
            } else {
                player.getInventory().clearAll();
                player.getInventory().setContents(inventory);
                int count = 0;
                for (int index : maxIndex) {
                    if (getPrizeByIndex(index) != null) {
                        count += 1;
                    }
                }
                StringBuilder content = new StringBuilder(LotteryBoxMain.lang.getTranslation("RewardWindow", "StartText", count));
                for (int index : maxIndex) {
                    Prize prize = getPrizeByIndex(index);
                    lotteryBox.showEndParticle(player);
                    if (lotteryBox.getSpawnFirework()) {
                        CreateFireworkApi.spawnFirework(player.getPosition(), DyeColor.YELLOW, ItemFirework.FireworkExplosion.ExplosionType.BURST);
                    }
                    if (prize != null) {
                        content.append("\n").append(LotteryBoxMain.lang.getTranslation("RewardWindow", "PrizeText", prize.getRarity(), prize.getName(), prize.getDescription()));
                        player.getInventory().addItem(prize.getItems());
                        player.sendMessage(LotteryBoxMain.lang.getTranslation("Tips", "DrawEndWithPrize", prize.getName()));
                        for (String s : prize.getConsolecommands()) {
                            Server.getInstance().dispatchCommand(Server.getInstance().getConsoleSender(), s.replace("%player%", player.getName()));
                        }
                        if (prize.getBroadcast()) {
                            Server.getInstance().broadcastMessage(LotteryBoxMain.lang.getTranslation("Tips", "PrizeBroadcast", player.getName(), prize.getName()));
                        }
                        LotteryBoxMain.log.info("玩家 {" + player.getName() + "} 在抽奖箱 {" + lotteryBox.getName() + "} 中抽到物品 {" + prize.getName() + "}!");
                    } else {
                        if (maxSpin == 1) {
                            player.sendMessage(LotteryBoxMain.lang.getTranslation("Tips", "DrawEndWithoutPrize"));
                        }
                    }
                }
                if (content.toString().equals(LotteryBoxMain.lang.getTranslation("RewardWindow", "StartText", count))) {
                    FormFactory.showRewardWindow(player, LotteryBoxMain.lang.getTranslation("RewardWindow", "PrizeNone"));
                } else {
                    FormFactory.showRewardWindow(player, content.toString());
                }
                LotteryBoxMain.playingPlayers.remove(player);
                LotteryBoxMain.playerLotteryBoxes.remove(player);
                this.cancel();
            }
        } else {
            for (Integer index : maxIndex) {
                Prize prize = getPrizeByIndex(index);
                if (prize != null) {
                    if (player.isOnline() && LotteryBoxMain.playingPlayers.contains(player)) {
                        player.sendMessage(LotteryBoxMain.lang.getTranslation("Tips", "DrawEndWithPrize", prize.getName()));
                        player.getInventory().addItem(prize.getItems());
                        LotteryBoxMain.log.info("玩家 {" + player.getName() + "} 在抽奖箱 {" + lotteryBox.getName() + "} 中抽到物品 {" + prize.getName() + "}!");
                    } else {
                        saveMessage(LotteryBoxMain.lang.getTranslation("Tips", "DrawEndWithPrize", prize.getName()));
                        saveItem(prize.getItems());
                    }
                    for (String s : prize.getConsolecommands()) {
                        saveCommand(s);
                    }
                    if (prize.getBroadcast()) {
                        Server.getInstance().broadcastMessage(LotteryBoxMain.lang.getTranslation("Tips", "PrizeBroadcast", player.getName(), prize.getName()));
                    }
                }
                saveItem(inventory.values().toArray(new Item[0]));
            }
            LotteryBoxMain.playerLotteryBoxes.remove(player);
            LotteryBoxMain.playingPlayers.remove(player);
            LotteryBoxMain.instance.getLogger().warning("Detect [" + player.getName() + "] exit the server, server will retry to give it in his or her next join");
            this.cancel();
        }
    }

    private void saveItem(Item[] items) {
        if (!LotteryBoxMain.save_bag_enabled) {
            return;
        }
        Config config = new Config(LotteryBoxMain.path + "/cache.yml", Config.YAML);

        List<String> stringList = new ArrayList<>(config.getStringList(player.getName() + ".items"));
        for (Item item : items) {
            stringList.add(Inventory.saveItemToString(item));
        }
        config.set(player.getName() + ".items", stringList);
        config.save();
    }

    private void saveCommand(String command) {
        Config config = new Config(LotteryBoxMain.path + "/cache.yml", Config.YAML);

        List<String> stringList = new ArrayList<>(config.getStringList(player.getName() + ".commands"));
        stringList.add(command);
        config.set(player.getName() + ".commands", stringList);
        config.save();
    }

    private void saveMessage(String message) {
        Config config = new Config(LotteryBoxMain.path + "/cache.yml", Config.YAML);
        List<String> stringList = new ArrayList<>(config.getStringList(player.getName() + ".messages"));
        stringList.add(message);
        config.set(player.getName() + ".messages", stringList);
        config.save();
    }
}
