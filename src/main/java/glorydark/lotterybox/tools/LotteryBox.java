package glorydark.lotterybox.tools;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.form.element.ElementButtonImageData;
import cn.nukkit.item.Item;
import cn.nukkit.level.Sound;
import cn.nukkit.level.particle.*;
import cn.nukkit.math.Vector3;
import glorydark.lotterybox.LotteryBoxMain;
import glorydark.lotterybox.api.LotteryBoxAPI;
import glorydark.lotterybox.forms.FormFactory;
import lombok.Data;
import me.onebone.economyapi.EconomyAPI;

import java.util.*;

@Data
public class LotteryBox {

    private final String name;
    private final List<String> needs;
    private final List<String> description;
    private final List<Prize> prizes;
    private final List<Bonus> bonuses;

    private final String displayName;

    private final int limit;

    private final boolean spawnFirework;

    private final String endParticle;

    private final Sound sound;

    private final boolean weightEnabled;
    private final int priority;
    private final int maxDrawPerTime;
    private final ElementButtonImageData elementButtonImageData;

    public LotteryBox(String name, int priority, String displayName, List<String> needs, List<String> description, List<Prize> prizes, List<Bonus> bonuses, Integer limit, boolean spawnFirework, String endParticle, String sound, boolean weightEnabled, int maxDrawPerTime, ElementButtonImageData elementButtonImageData) {
        this.name = name;
        this.priority = priority;
        this.needs = needs;
        this.description = description;
        this.prizes = prizes;
        this.bonuses = bonuses;
        this.displayName = displayName;
        this.limit = limit;
        this.spawnFirework = spawnFirework;
        this.endParticle = endParticle;
        Optional<Sound> got = Arrays.stream(Sound.values()).filter(get -> get.getSound().equals(sound)).findFirst();
        this.sound = got.orElse(Sound.RANDOM_ORB);
        this.weightEnabled = weightEnabled;
        this.maxDrawPerTime = maxDrawPerTime;
        this.elementButtonImageData = elementButtonImageData;
    }

    public void showEndParticle(Player player) {
        if (endParticle.equals("null")) {
            return;
        }
        String[] split = endParticle.split(":");
        if (split.length > 1) {
            player.getLevel().addParticle(getParticle(split[0], player.getPosition(), Integer.parseInt(split[1])));
        } else {
            player.getLevel().addParticle(getParticle(split[0], player.getPosition(), 0));
        }
    }

    public boolean checkLimit(String player, int spins) {
        return LotteryBoxAPI.getLotteryPlayTimes(player, name) + spins <= limit;
    }

    /**
     * 扣除需求
     * @param player 玩家对象
     * @param spins 数量
     * @return
     */
    public boolean deductNeeds(Player player, int spins) {
        return ExamineNeed.examineNeed(needs.toArray(String[]::new), player, spins);
    }

    public Bonus getBonus(int integer) {
        for (Bonus key : bonuses) {
            if (Objects.equals(key.getNeedTimes(), integer)) {
                return key;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return "LotteryBox{" +
                "name='" + name + '\'' +
                ", needs=" + needs +
                ", description=" + description +
                ", prizes=" + prizes +
                ", bonuses=" + bonuses +
                '}';
    }

    public void addBlockParticle(Player player, Prize prize) {
        Rarity rarity = LotteryBoxMain.rarities.getOrDefault(prize.getRarity(), null);
        if (rarity == null) {
            return;
        }
        rarity.addRarityParticle(player);
    }

    private Particle getParticle(String name, Vector3 pos, int data) {
        switch (name) {
            case "explode":
                return new ExplodeParticle(pos);
            case "hugeexplosion":
                return new HugeExplodeParticle(pos);
            case "hugeexplosionseed":
                return new HugeExplodeSeedParticle(pos);
            case "bubble":
                return new BubbleParticle(pos);
            case "splash":
                return new SplashParticle(pos);
            case "wake":
            case "water":
                return new WaterParticle(pos);
            case "crit":
                return new CriticalParticle(pos);
            case "smoke":
                return new SmokeParticle(pos, data != -1 ? data : 0);
            case "spell":
                return new EnchantParticle(pos);
            case "instantspell":
                return new InstantEnchantParticle(pos);
            case "dripwater":
                return new WaterDripParticle(pos);
            case "driplava":
                return new LavaDripParticle(pos);
            case "townaura":
            case "spore":
                return new SporeParticle(pos);
            case "portal":
                return new PortalParticle(pos);
            case "flame":
                return new FlameParticle(pos);
            case "lava":
                return new LavaParticle(pos);
            case "reddust":
                return new RedstoneParticle(pos, data != -1 ? data : 1);
            case "snowballpoof":
                return new ItemBreakParticle(pos, Item.get(332));
            case "slime":
                return new ItemBreakParticle(pos, Item.get(341));
            case "itembreak":
                if (data != -1 && data != 0) {
                    return new ItemBreakParticle(pos, Item.get(data));
                }
                break;
            case "terrain":
                if (data != -1 && data != 0) {
                    return new TerrainParticle(pos, Block.get(data));
                }
                break;
            case "heart":
                return new HeartParticle(pos, data != -1 ? data : 0);
            case "ink":
                return new InkParticle(pos, data != -1 ? data : 0);
            case "droplet":
                return new RainSplashParticle(pos);
            case "enchantmenttable":
                return new EnchantmentTableParticle(pos);
            case "happyvillager":
                return new HappyVillagerParticle(pos);
            case "angryvillager":
                return new AngryVillagerParticle(pos);
            case "forcefield":
                return new BlockForceFieldParticle(pos);
        }

        String[] d;
        if (name.startsWith("iconcrack_")) {
            d = name.split("_");
            if (d.length == 3) {
                return new ItemBreakParticle(pos, Item.get(Integer.parseInt(d[1]), Integer.valueOf(d[2])));
            }
        } else if (name.startsWith("blockcrack_")) {
            d = name.split("_");
            if (d.length == 2) {
                return new TerrainParticle(pos, Block.get(Integer.parseInt(d[1]) & 255, Integer.parseInt(d[1]) >> 12));
            }
        } else if (name.startsWith("blockdust_")) {
            d = name.split("_");
            if (d.length >= 4) {
                return new DustParticle(pos, Integer.parseInt(d[1]) & 255, Integer.parseInt(d[2]) & 255, Integer.parseInt(d[3]) & 255, d.length >= 5 ? Integer.parseInt(d[4]) & 255 : 255);
            }
        }

        return null;
    }
}
