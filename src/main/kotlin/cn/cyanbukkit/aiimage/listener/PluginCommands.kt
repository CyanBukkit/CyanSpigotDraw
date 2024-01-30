package cn.cyanbukkit.aiimage.listener

import cn.cyanbukkit.aiimage.SpigotDraw
import cn.cyanbukkit.aiimage.data.ImageStatus
import cn.cyanbukkit.aiimage.image.DownloadImage.connectHttp
import cn.cyanbukkit.aiimage.image.DownloadImage.getImageAndView
import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONObject
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.util.*

class PluginCommands : Command(
    "spigotdraw",
    "6pen ai绘图 MC版",
    "/spigotdraw help",
    listOf("sd")
) {

    init {
        permission = "spigotdraw.use"
        permissionMessage = "§c你还没有领取积分获得" +
                "§bAI艺术§c的使用权限你可以到淘宝店铺-青桶我的世界购买领取哦@"
    }

    override fun execute(sender: CommandSender, commandLabel: String, args: Array<String>): Boolean {
        if (args.isEmpty()) {
            sender.sendMessage("§c参数错误/spigotdraw help")
            return true
        }
        if (sender !is Player) {
            sender.sendMessage("§c控制台无法使用此指令")
            return true
        }

        when (args[0]) {
            "help"-> {
                sender.sendMessage("""
                    §6/spigotdraw help §f帮助指令
                    §6/spigotdraw me §f查看自己的申请的图片与进度
                    §6/spigotdraw start §f开始异想天开
                    §6/spigotdraw view <ID> §f 
                """.trimIndent())
            }
            "start" -> {
                val need = SpigotDraw.instance.config.getInt("NeedPoints")
                val now = SpigotDraw.instance.pointsAPI.look(sender.uniqueId)
                if (now < need) {
                    sender.sendMessage("§c你的积分不足需要${need}你现在只有${now}")
                    return true
                }
                sender.sendMessage("""
                    §a§l在聊天框输入画面中的文字时，请务必注意描述方式、顺序和措辞等，以便更清晰地展现画面效果
                    §a§l同时，请遵守法律法规，避免涉及违法违规信息导致图片生成失败或被ban!
                    §a§l输入“cancel”取消
                """.trimIndent())
                if (!PlayerListener.start.containsKey(sender)) {
                    PlayerListener.start[sender] = true
                } else {
                    sender.sendMessage("§c你已经开始了")
                }
            }
            "view" -> {
                if (args.size != 2) {
                    sender.sendMessage("§c参数错误/spigotdraw view <ID>")
                    return true
                }
                val id = args[1]
                if (!SpigotDraw.playerData.contains(sender.uniqueId.toString())) {
                    sender.sendMessage("§c你没有申请过图片")
                    return true
                }
                if (!SpigotDraw.playerData.contains(sender.uniqueId.toString() + "." + id)) {
                    sender.sendMessage("§c你没有申请过这个ID的图片")
                    return true
                }
                sender.getImageAndView(id)
            }
            "me" -> {
                if (!SpigotDraw.playerData.contains(sender.uniqueId.toString())) {
                    sender.sendMessage("§c你没有申请过图片")
                    return true
                }

                sender.sendMessage("§a§l你申请过的图片:")
                for (id in SpigotDraw.playerData.getConfigurationSection(sender.uniqueId.toString())!!.getKeys(false)) {
                    val need = SpigotDraw.playerData.getString(sender.uniqueId.toString() + "." + id) // 需求
                    val http = "https://ston.6pen.art/release/open-task?id=${id}".connectHttp("GET", JSONObject(), id) // http
                    // 获取callback页面返回的Json
                    if (http.responseCode != 200) {
                        continue
                    }
                    val rawJson = String(http.inputStream.readBytes())
                    val date = JSON.parseObject(rawJson)
                    if (date.getString("info") == "success") {
                        val data = date.getJSONObject("data")
                        val status = ImageStatus.valueOf(data.getString("state").uppercase()).chinese
                        val time = data.getString("create_at")
                        sender.sendMessage("§a§lID: $id - 状态: $status §a§l- 时间: $time -需求: $need")
                    }
                }
            }

        }


        return true
    }


    override fun tabComplete(sender: CommandSender, alias: String, args: Array<out String>): MutableList<String> {
        if (args.size == 1) {
            return mutableListOf("help", "me", "start", "view")
        }
        if (args[0] == "view" && sender is Player) {
            val list = mutableListOf<String>()
            if (SpigotDraw.playerData.contains(sender.uniqueId.toString())) {
                SpigotDraw.playerData.getConfigurationSection(sender.uniqueId.toString())!!.getKeys(false).forEach {
                    list.add(it)
                }
            }
            // 根据已经输入的内容进行补全
            if (args.size == 2) {
                val newList = mutableListOf<String>()
                val search = args[1].lowercase(Locale.ROOT)
                for ( s in list) {
                    if (!s.lowercase(Locale.ROOT).startsWith(search)) continue
                    newList.add(s)
                }
                return newList
            }
        }
        return mutableListOf()
    }
}



//                val data = SpigotDraw.playerData.getConfigurationSection(sender.uniqueId.toString() + "." + id)
//                val status = data!!.getString("status")
//                val time = data.getLong("time")
//                val text = data.getString("text")
//                val image = data.getString("image")
//                val point = data.getString("point")
//                val pointList = mutableListOf<Point>()
//                point!!.split(";").forEach {
//                    val x = it.split(",")[0].toInt()
//                    val y = it.split(",")[1].toInt()
//                    pointList.add(Point(x, y))
//                }
//                sender.sendMessage("§a§lID: $id")
//                sender.sendMessage("§a§l状态: $status")
//                sender.sendMessage("§a§l时间: ${time.sdf()}")
//                sender.sendMessage("§a§l文字: $text")
//                sender.sendMessage("§a§l图片: $image")
//                sender.sendMessage("§a§l坐标: $point")
//                sender.sendMessage("§a§l坐标: $pointList")