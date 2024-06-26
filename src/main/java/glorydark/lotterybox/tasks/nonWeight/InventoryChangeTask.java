package glorydark.lotterybox.tasks.nonWeight;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.block.BlockGlassStained;
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
import java.util.List;
import java.util.Map;
import java.util.Random;

public class InventoryChangeTask extends Task implements Runnable {

    private final Map<Integer, Item> inventory;
    private final List<Integer> maxIndex = new ArrayList<>();
    private final Player player;
    private final LotteryBox lotteryBox;
    private final int maxSpin;
    private final int maxCounts;
    private int index = 0;
    private int ticks;

    public InventoryChangeTask(Player player, LotteryBox box, int spins) {
        this.player = player;
        this.lotteryBox = box;
        this.maxSpin = spins;
        this.maxCounts = lotteryBox.getPrizes().size() * 2;
        for (int i = 0; i < spins; i++) {
            LotteryBoxAPI.changeLotteryPlayTimes(player.getName(), lotteryBox.getName(), 1);
            if (lotteryBox.getBonus(LotteryBoxAPI.getLotteryPlayTimes(player.getName(), lotteryBox.getName())) != null) {
                Bonus bonus = lotteryBox.getBonus(LotteryBoxAPI.getLotteryPlayTimes(player.getName(), lotteryBox.getName()));
                for (String s : bonus.getConsolecommands()) {
                    Server.getInstance().dispatchCommand(Server.getInstance().getConsoleSender(), s.replace("%player%", player.getName()));
                }
                player.getInventory().addItem(bonus.getItems());
                Server.getInstance().broadcastMessage(LotteryBoxMain.lang.getTranslation("Tips", "BonusBroadcast", player.getName(), lotteryBox.getName(), bonus.getNeedTimes(), bonus.getName()));
                LotteryBoxMain.log.info("玩家 {" + player.getName() + "} 在抽奖箱 {" + lotteryBox.getName() + "} 中抽奖达到 {" + bonus.getNeedTimes() + "} 次，获得物品 {" + bonus.getName() + "}!");}
            int get = getObtained();
            this.maxIndex.add(maxCounts + get);
            //player.sendMessage((get) + (getPrize(get)==null? "无奖": "有奖"));
        }
        this.inventory = player.getInventory().getContents();
        player.getInventory().clearAll();
        player.getInventory().setHeldItemIndex(4);
        player.getInventory().setItem(0, getDisplayItem(index - 4, false));
        player.getInventory().setItem(1, getDisplayItem(index - 3, false));
        player.getInventory().setItem(2, getDisplayItem(index - 2, false));
        player.getInventory().setItem(3, getDisplayItem(index - 1, false));
        player.getInventory().setItem(4, getDisplayItem(index, true));
        player.getInventory().setItem(5, getDisplayItem(index + 1, false));
        player.getInventory().setItem(6, getDisplayItem(index + 2, false));
        player.getInventory().setItem(7, getDisplayItem(index + 3, false));
        player.getInventory().setItem(8, getDisplayItem(index + 4, false));
        LotteryBoxMain.playingPlayers.add(player);
    }

    public int getObtained() {
        Random random = new Random();
        for (Prize prize : lotteryBox.getPrizes()) {
            List<Integer> integers = new ArrayList<>();
            for (int i = 0; i < prize.getPossibility(); i++) {
                integers.add(Math.abs(random.nextInt()) % 10000);
            }
            if (integers.contains(Math.abs(random.nextInt()) % 10000)) {
                return lotteryBox.getPrizes().indexOf(prize) * 2;
            }
        }
        return random.nextInt(lotteryBox.getPrizes().size()) * 2 + 1;
    }

    public Prize getPrize(int index) {
        if (index % 2 == 0) {
            return lotteryBox.getPrizes().get(index % maxCounts / 2);
        } else {
            return null;
        }
    }

    public Item getDisplayItem(int index, boolean isEnchanted) {
        index += maxCounts;
        if (index == (index / 2) * 2) {
            Prize prize = lotteryBox.getPrizes().get((index % maxCounts) / 2);
            Item item = prize.getDisplayitem().clone();
            if (prize.isShowOriginName()) {
                item.setCustomName(prize.getName());
            }
            if (isEnchanted) {
                item.addEnchantment(new EnchantmentProtectionAll());
            }
            return item;
        } else {
            Item item = new BlockGlassStained().toItem();
            item.setCustomName(LotteryBoxMain.lang.getTranslation("PlayLotteryWindow", "BlockAir"));
            if (isEnchanted) {
                item.addEnchantment(new EnchantmentProtectionAll());
            }
            return item;
        }
    }

    @Override
    public void onRun(int i) {
        if (player.isOnline() && !LotteryBoxMain.banWorlds.contains(player.getLevel().getName()) && LotteryBoxMain.isWorldAvailable(player.getLevel().getName()) && LotteryBoxMain.playingPlayers.contains(player)) {
            int thisMaxIndex = maxIndex.get(0);
            ticks += 1;
            if (maxCounts > 10) {
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
                Prize prize = getPrize(index);
                if (prize != null) {
                    lotteryBox.addBlockParticle(player, getPrize(index));
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
                    if (getPrize(index) != null) {
                        count += 1;
                    }
                }
                StringBuilder content = new StringBuilder(LotteryBoxMain.lang.getTranslation("RewardWindow", "StartText", count));
                for (int index : maxIndex) {
                    Prize prize = getPrize(index);
                    lotteryBox.showEndParticle(player);
                    if (lotteryBox.isSpawnFirework()) {
                        CreateFireworkApi.spawnFirework(player.getPosition(), DyeColor.YELLOW, ItemFirework.FireworkExplosion.ExplosionType.BURST);
                    }
                    if (prize != null) {
                        content.append("\n").append(LotteryBoxMain.lang.getTranslation("RewardWindow", "PrizeText", prize.getRarity(), prize.getName(), prize.getDescription()));
                        player.getInventory().addItem(prize.getItems());
                        player.sendMessage(LotteryBoxMain.lang.getTranslation("Tips", "DrawEndWithPrize", prize.getName()));
                        for (String s : prize.getConsolecommands()) {
                            Server.getInstance().dispatchCommand(Server.getInstance().getConsoleSender(), s.replace("%player%", player.getName()));
                        }
                        if (prize.isBroadcast()) {
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
            for (int index : maxIndex) {
                Prize prize = getPrize(index);
                if (prize != null) {
                    if (player.isOnline() && LotteryBoxMain.playingPlayers.contains(player)) {
                        player.sendMessage(LotteryBoxMain.lang.getTranslation("Tips", "DrawEndWithPrize", prize.getName()));
                        player.getInventory().addItem(prize.getItems());
                    } else {
                        saveMessage(LotteryBoxMain.lang.getTranslation("Tips", "DrawEndWithPrize", prize.getName()));
                        saveItem(prize.getItems());
                    }
                    for (String s : prize.getConsolecommands()) {
                        saveCommand(s);
                    }
                    if (prize.isBroadcast()) {
                        Server.getInstance().broadcastMessage(LotteryBoxMain.lang.getTranslation("Tips", "PrizeBroadcast", player.getName(), prize.getName()));
                    }
                    LotteryBoxMain.log.info("玩家 {" + player.getName() + "} 在抽奖箱 {" + lotteryBox.getName() + "} 中抽到物品 {" + prize.getName() + "}!");
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
