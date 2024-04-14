# LotteryBox 使用教程

## 指令介绍

| 指令                                 | 说明                                                |
|------------------------------------|-----------------------------------------------------|
| /lotterybox menu                   | 打开主界面                                          |
| /lotterybox menu <抽奖箱配置文件名>        | 打开对应的抽奖箱界面                                |
| /lotterybox give <玩家名> <抽奖券名> <数量> | 给予玩家对应的抽奖券，需要在config.yml添加          |
| /lotterybox saveitem <保存名>         | 将nbt物品转换至字符串，保存至saveitem.yml           |
| /lotterybox reload                 | 重载                                                |
| /lotterybox createbox <名称>         | 创建抽奖箱                                          |
| /lotterybox help                   | 获取帮助提示                                        |

## config.yml

```yaml
language: zh-cn
version: 2022082101
force_default_mode: true # 是否默认为 物品栏滚动 或 箱子抽奖
default_speed_ticks: 4 # 物品栏抽奖噔噔噔时，切换到下一个物品的间隔
chest_speed_ticks: 4 # 箱子抽奖噔噔噔时，切换到下一个物品的间隔
ban_worlds: [] # 禁止世界
ban_worlds_prefixs: [] # 禁止世界名的前缀
show_type: actionbar # 展示类型，可为popup，actionbar和tip
show_reward_window: true # 是否显示抽奖结算界面
inventory_cache_paths: # 适配小游戏，避免玩家进房间还在抽奖
  - "plugins/GunWar/PlayerInventory/%player%.json"
save_bag_enabled: true # 是否保存背包（一般小游戏服只要开这个就行）
registered_tickets: [] # 需加载的抽奖券，这个抽奖券在tickets文件夹下。
```

## 抽奖箱配置

```yaml
needs: [] # 需要物品
# 格式:
# mi@1 普通抽奖券
# rmb@100
displayName: 测试抽奖箱 # 展示名称
permanentLimit: 3000 # 限制总抽奖次数
spawnFirework: true # 是否结束放烟花
endParticle: hugeexplosion # 结束时的粒子，详见附表
weightEnabled: true # 是否支持权重，即将概率变为权重计算，但同时不支持箱子界面
sound: "random.orb" # 播放音效，详见附表
descriptions: # 介绍
  - '内含奖品:'
  - '[ 233 ] * 1天'
  - '[ 466 ] * 1天'
prizes: 
# 抽奖箱为箱子界面抽奖时，最多可存22个
  test1:
    displayitem: item@minecraft:stone 0 1 # 展示物品，格式为: item@id meta count
    possibility: 5000 # 默认为概率，可通过weightEnabled变为权重
    description: '233' # 介绍
    broadcast: true # 是否全服播报
    items: ## 奖品，格式为: item@id meta count
      - item@minecraft:stone 0 1
    showoriginname: true # 是否显示原物品名
    consolecommands: [] # 控制台执行指令，变量为%player%
    rarity: 平凡 # 品质，在rarity.yml进行注册
bonuses: # 累抽奖励
  test1:
    times: 1 # 次数
    items: ## 奖品，格式为: item@id meta count
      - item@minecraft:stone 0 1
    consolecommands: # 控制台执行指令，变量为%player%
      - say test
      - give %player% 1 1
```

## 附表1：粒子列表

| 物品/方块名称         |
|---------------------|
| spore               |
| portal              |
| flame               |
| lava                |
| reddust:存在时间    |
| snowballpoof        |
| slime               |
| itembreak:破坏物品id|
| terrain:方块id      |
| heart:大小          |
| InkParticle:大小    |
| droplet             |
| enchantmenttable    |
| happyvillager       |
| angryvillager       |
| forcefield          |
| iconcrack_物品id_meta |
| blockcrack_方块id_meta |
| blockdust_物品id_meta |


## 附表2：sound

点击如下链接查看BE允许音效（需要nk也同时注册了这些音效）：

[音乐 - Minecraft Wiki，最详细的我的世界百科](https://minecraft.fandom.com/zh/wiki/%E9%9F%B3%E4%B9%90#%E4%B8%BB%E4%B8%96%E7%95%8C%E9%9F%B3%E4%B9%90)


