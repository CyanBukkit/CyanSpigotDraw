package cn.cyanbukkit.aiimage.image

import cn.cyanbukkit.aiimage.SpigotDraw
import cn.cyanbukkit.aiimage.image.DownloadImage.connectHttp
import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONObject
import org.bukkit.entity.Player
import java.util.*


class ImageStart(
    val player : Player
) {


    fun start(text : String) {
        val callback = "https://ston.6pen.art/release/open-task".connectHttp(
            "POST", convertJson(text)
        )
        // 获取callback页面返回的Json
        if (callback.responseCode != 200) {
            player.sendMessage("§cError与管理员反馈，点券已返还")
            val need = SpigotDraw.instance.config.getInt("NeedPoints")
            SpigotDraw.instance.pointsAPI.give(player.uniqueId, need)
            return
        }
        val rawJson = String(callback.inputStream.readBytes())
        val date = JSON.parseObject(rawJson)
        if (date.getString("info") == "success") {
            // 成功 创建进程跟踪 生成状态
            // 并占用排队列表
            val id = date.getJSONObject("data").getString("id")
            SpigotDraw.playerData.set("${player.uniqueId}.$id", text)
            SpigotDraw.playerData.save(SpigotDraw.playerDataFile)
            player.sendMessage("§a§l申请成功，ID为${id}，请等待合成完成后输入/spigotdraw view ${id}即可查看")
            SpigotDraw.instance.logger.info("${player.name}申请了图片，ID为${date.getJSONObject("data").getString("id")}")
        } else {
            player.sendMessage("§cError与管理员反馈，点券已返还")
            val need = SpigotDraw.instance.config.getInt("NeedPoints")
            SpigotDraw.instance.pointsAPI.give(player.uniqueId, need)
            return
        }

    }

    private fun convertJson(
        text : String
    ) : JSONObject {
        val new = JSONObject()
        new["prompt"] = text
        new["width"] = 512
        new["height"] = 512
        new["fill_prompt"] = 0
        val add = JSONObject()
        add["cfg_scale"] = 7
        add["negative_prompt"] = "minim aliqua qui in sed"
        new["addition"] = add
        return new
    }




    private fun generateRandomString(length: Int, chars: String): String {
        val random = Random()
        var result = ""
        for (i in 0 until length) {
            val randomIndex = random.nextInt(chars.length)
            result += chars[randomIndex]
        }
        return result
    }



}
//{
//  "code": 200,
//  "info": "success",
//  "data": {
//    "ids": [
//      "f7c8fb8231e8fb8e"
//    ],
//    "id": "f7c8fb8231e8fb8e",
//    "estimates": [
//      13
//    ],
//    "estimate": 13
//  }
//}