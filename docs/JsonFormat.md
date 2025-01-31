# Json格式文档
```json
{
    "name": "default",
    "version": "1.0.0",
    "info": "default random events",
    "random_events": [
        {
            "id": 1,
            "drop_items": [
                {
                    "id": "minecraft:apple",
                    "use_random": true,
                    "random_num": {
                        "min": 1,
                        "max": 5
                    },
                    "pos_src": 0
                }
            ],
            "place_blocks": [
                {
                    "id": "minecraft:stone",
                    "pos_src": 0
                }
            ]
        },
        {
            "id": 2,
            "fall_blocks": [
                {
                    "id": "minecraft:tnt",
                    "pos_src": 1,
                    "offset": {
                        "x": 0,
                        "y": 8,
                        "z": 0
                    }
                }
            ]
        }
    ]
}
```
## 文件头部
 - `name` [字符串]文件名
 - `version` [字符串]版本号
 - `info` [字符串]信息
 - `random_events` [对象]随机事件
## 随机事件randomEvents
### 目前开放的功能
- `drop_items` 掉落物品
- `place_blocks` 放置方块
- `fall_blocks` 掉落方块
- `create_explosions` 创建爆炸
- `spawn_mobs` 生成生物
- `give_potion_effects` 给予药水效果
- `send_messages` 发送消息
- `add_particles` 生成粒子
- `load_structures` 加载结构
- `execute_commands` 执行命令
### 掉落物品drop_items
- `id` [字符串]物品id
- `use_random` [可选][布尔值]是否使用随机数量(默认为`false`)
- `random_num` [可选][对象]随机数量 [go](#随机数量random_num)
- `num` [可选][整数]数量(默认为`1`)
- `pos_src` [可选][整数]位置来源
- `offset` [可选][对象]偏移量 [go](#偏移量offset)
### 放置方块place_blocks
- `id` [字符串]方块id
- `pos_src` [可选][整数]位置来源 [go](#位置来源pos_src)
- `offset` [可选][对象]偏移量 [go](#偏移量offset)
### 放置箱子place_chests
- `id` [字符串]战利品表id(默认为`empty`)
- `type` [可选][整数]箱子类型(默认为`0`,即箱子)
- `pos_src` [可选][整数]位置来源 [go](#位置来源pos_src)
- `offset` [可选][对象]偏移量 [go](#偏移量offset)
### 掉落方块fall_blocks
- `id` [字符串]方块id
- `pos_src` [可选][整数]位置来源 [go](#位置来源pos_src)
- `offset` [可选][对象]偏移量 [go](#偏移量offset)
### 创建爆炸create_explosions
- `power` [整数]爆炸强度(默认为`1`)
- `pos_src` [可选][整数]位置来源 [go](#位置来源pos_src)
- `offset` [可选][对象]偏移量 [go](#偏移量offset)
### 生成生物spawn_mobs
- `id` [字符串]生物id
- `pos_src` [可选][整数]位置来源 [go](#位置来源pos_src)
- `offset` [可选][对象]偏移量 [go](#偏移量offset)
- `name` [可选][字符串]生物名称(默认为`null`)
- `name_visible` [可选][布尔值]生物名称是否始终可见(默认为`false`)
- `is_baby` [可选][布尔值]生物是否为幼体(默认为`false`)
- `use_random` [可选][布尔值]是否使用随机数量(默认为`false`) [go](#随机数量random_num)
- `random_num` [可选][对象]随机数量
- `num` [可选][整数]数量(默认为`1`)
- `nbt` [可选][字符串]NBT数据(默认为`null`)
### 给予药水效果give_potion_effects
- `id` [字符串]药水效果id`
- `amplifier` [整数]药水效果等级(默认为`0`)
- `duration` [整数]持续时间(默认为`1`)
### 发送消息send_messages
- `msg` [字符串]消息内容
- `receiver` [可选][字符串]显示位置(默认为`0`) [go](#显示位置receiver)
### 生成粒子add_particles
- `id` [字符串]粒子id
- `count` [可选][整数]粒子数量(默认为`1`)
- `speed` [可选][浮点数]粒子速度1(默认为`0`)
- `velocity` [可选][对象]粒子速度2(默认为`{ x: 1, y: 1, z: 1 }`)
- `pos_src` [可选][整数]位置来源 [go](#位置来源pos_src)
- `offset` [可选][对象]偏移量 [go](#偏移量offset)
### 加载结构load_structures
- `id` [字符串]结构id
- `pos_src` [可选][整数]位置来源 [go](#位置来源pos_src)
- `offset` [可选][对象]偏移量 [go](#偏移量offset)
### 执行命令execute_commands
- `command` [字符串]结构id
- `pos_src` [可选][整数]位置来源 [go](#位置来源pos_src)
- `offset` [可选][对象]偏移量 [go](#偏移量offset)
## 特殊
### 位置来源pos_src
默认为`0`
 - `0` 方块
 - `1` 玩家
### 显示位置receiver
默认为`0`
 - `0` 物品栏
 - `1` 聊天栏
### 偏移量offset
默认为`{ x: 0, y: 0, z: 0 }`
 - `x` [浮点数]x轴偏移量(默认为`0`)
 - `y` [浮点数]y轴偏移量(默认为`0`)
 - `z` [浮点数]z轴偏移量(默认为`0`)
### 随机数量random_num
`use_random`为`true`时启用，否则输入num。
 - `min` [整数]最小数量(默认为`0`)
 - `max` [整数]最大数量(默认为`1`)
### NBT数据
默认为`null`，例如
```
"nbt": "{Glowing:1b,HandItems:[{id:\"minecraft:tnt\",Count:12b},{}],ArmorItems:[{},{},{},{id:\"minecraft:tnt\",Count:12b}]}"
```