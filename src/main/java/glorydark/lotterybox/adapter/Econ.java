package glorydark.lotterybox.adapter;
import cn.nukkit.Player;
import me.onebone.economyapi.EconomyAPI;

public class Econ {
    private final Player player;
    private final String currentPlugin;

    public Econ(Player player) {
        this.player = player;
        this.currentPlugin = "EconomyAPI";
    }
    public double getMoney() {
        return EconomyAPI.getInstance().myMoney(player);
    }
    public double reduceMoney(double amount) {
        return EconomyAPI.getInstance().reduceMoney(player, amount);
    }
}
