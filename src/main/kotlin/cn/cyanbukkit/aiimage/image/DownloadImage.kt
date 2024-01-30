package cn.cyanbukkit.aiimage.image

import cn.cyanbukkit.aiimage.SpigotDraw
import cn.cyanbukkit.aiimage.data.ImageStatus
import cn.cyanbukkit.aiimage.listener.PlayerListener
import cn.cyanbukkit.aiimage.onmap.RenderImageMaps
import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONObject
import com.comphenix.protocol.PacketType
import com.comphenix.protocol.ProtocolLibrary
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.map.MapCanvas
import org.bukkit.map.MapView
import java.awt.Image
import java.awt.image.BufferedImage
import java.io.File
import java.lang.reflect.Method
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.ThreadLocalRandom
import javax.imageio.ImageIO
import kotlin.math.ceil


object DownloadImage {

    fun String.connectHttp(method: String, data : JSONObject, id : String = ""): HttpURLConnection {
        val connection = URL(this).openConnection() as HttpURLConnection
        connection.setRequestProperty(
            "User-Agent",
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/117.0.0.0 Safari/537.36 Edg/117.0.2045.60"
        )
        connection.setRequestProperty(
            "ys-api-key",
            SpigotDraw.apiKey
            // 在header  ys-api-key	你的 APIKEY
        )
        connection.requestMethod = method// 设置http状态 POST
        // Query 参数 id  string   必需
        if (method == "POST") {
            // HTTP Header 的 Content-Type 需要设置为 application/json。
            connection.setRequestProperty("Content-Type", "application/json")
            // data的String 放在Body
            connection.doOutput = true
            connection.outputStream.write(data.toString().toByteArray())
        }

        connection.connect()
        return if (connection.getResponseCode() == HttpURLConnection.HTTP_MOVED_PERM ||
            connection.getResponseCode() == HttpURLConnection.HTTP_MOVED_TEMP
        ) {
            connection.getHeaderField("Location").connectHttp(method, data)
        } else connection
    }


    fun Player.getImageAndView(id : String) {
        val http = "https://ston.6pen.art/release/open-task?id=${id}".connectHttp("GET", JSONObject(), id)
        // 获取callback页面返回的Json
        if (http.responseCode != 200) {
            this.sendMessage("§cError与管理员反馈")
            return
        }
        val rawJson = String(http.inputStream.readBytes())
        SpigotDraw.instance.logger.info(rawJson)
        val date = JSON.parseObject(rawJson)
        if (date.getString("info") == "success") {
            val data = date.getJSONObject("data")
            val url = try {
                data.getString("gen_img")
            } catch (e: Exception) {
                this.sendMessage("""
                    §7§l图片现在的状态为${ImageStatus.valueOf(data.getString("state").uppercase()).chinese} §7§l-
                    理由为${data.getString("fail_reason")}
                    §b  -- 建议你重新生成，不要涉及国家不允许或违法的内容! --
                """.trimIndent())
                return
            }
            val file = File(SpigotDraw.imageFolder, "${id}.png")
            if (!file.exists())  {
                val http2 = url.connectHttp("GET", JSONObject(), id)
                val inputStream = http2.inputStream
                val bytes = inputStream.readBytes()
                file.writeBytes(bytes)
            }
            val pngNeed = SpigotDraw.playerData.getString("${this.uniqueId}.${id}")
            Bukkit.getScheduler().runTaskLaterAsynchronously(SpigotDraw.instance, Runnable {
                this.sendMessage("§a§l请稍等...正在为你加载图片到手持物品")
                val image = ImageIO.read(file)
                val png = BufferedImage(128, 128, image.type)  // 把原有的大小无损缩放到128*128
                val graphics = png.createGraphics() // draw the input image into the output image
                graphics.drawImage(image.getScaledInstance(128, 128, Image.SCALE_AREA_AVERAGING), 0, 0, null)
                graphics.dispose()
                val map = Bukkit.createMap(Bukkit.getWorlds()[0])
                map.renderers.forEach { map.removeRenderer(it) }
                map.addRenderer(RenderImageMaps(png))
                val item = ItemStack(358, 1, map.id.toShort())
                PlayerListener.playerOldItem[player] = player.inventory.itemInHand
                player.inventory.itemInHand = item
            }, 0)
        }
    }


    fun pLib(image: BufferedImage, player: Player) {
        val protocolManager = ProtocolLibrary.getProtocolManager()
        val packet = protocolManager.createPacket(PacketType.Play.Server.MAP)
        val mapWidth = ceil(image.width / 128.0).toInt()
        val mapHeight = ceil(image.height / 128.0).toInt()
        val mapId = ThreadLocalRandom.current().nextInt(Short.MAX_VALUE.toInt());
        val mapData = ByteArray(mapWidth * mapHeight * 128 * 128)
        for (x in 0 until image.width) {
            for (z in 0 until image.height) {
                val color = image.getRGB(x, z)
                val index = x / 128 * mapHeight * 128 + z / 128 * 128 + x % 128 + z % 128 * mapWidth
                mapData[index] = color.toByte()
            }
        }
        packet.integers.write(0, mapId);
        packet.byteArrays.write(0, mapData);
        protocolManager.sendServerPacket(player, packet)
        // 创建物品
        val item = ItemStack(358, 1, mapId.toShort())
        PlayerListener.playerOldItem[player] = player.inventory.itemInHand
        player.inventory.itemInHand = item
    }

/*
                val mapCanvasClass = Class.forName("org.bukkit.map.MapCanvas")
                val drawImageMethod: Method = mapCanvasClass.getMethod("drawImage", Int::class.javaPrimitiveType, Int::class.javaPrimitiveType, Image::class.java)
                val cx = Bukkit.createMap(Bukkit.getWorlds()[0])
                val xs = cx.renderers[0].javaClass

//                val mapRendererClass = Class.forName("org.bukkit.map.MapRenderer")
//                val renderMethod = mapRendererClass.getDeclaredMethod("render", cx.javaClass, MapCanvas::class.java, Player::class.java)
//                val paramTypes = renderMethod.parameterTypes
//                val canvasClass = paramTypes[1]
                SpigotDraw.instance.logger.info(xs.canonicalName); val view = map.renderers[0]
                val canvas = view.javaClass.getDeclaredField("canvas")
                canvas.isAccessible = true
                canvas.set(view, MapCanvas(map))
                view.render(map, canvas.get(view) as MapCanvas, this)
                val item = ItemStack(358, 1, map.id.toShort())
                this.inventory.addItem(item)
                this.sendMessage("§a§l加载完成，已经放入你的背包")*/


}