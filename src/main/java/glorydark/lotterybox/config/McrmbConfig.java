package glorydark.lotterybox.config;

import cn.nukkit.utils.Config;

public class McrmbConfig {
    public static String website = "";
    public static String sid = "";
    public static String key = "";
    public static void init() {
        Config cfg = new Config("./NInvShop/mcrmbConfig.yml", Config.YAML);
        website = cfg.getString("website", "");
        sid = cfg.getString("sid", "");
        key = cfg.getString("key", "");
    }

}
