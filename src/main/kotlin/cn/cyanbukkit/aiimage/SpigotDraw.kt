package cn.cyanbukkit.aiimage

import cn.cyanbukkit.aiimage.listener.PlayerListener
import cn.cyanbukkit.aiimage.listener.PluginCommands
import org.black_ixx.playerpoints.PlayerPoints
import org.black_ixx.playerpoints.PlayerPointsAPI
import org.bukkit.command.Command
import org.bukkit.command.SimpleCommandMap
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.plugin.java.JavaPlugin
import java.io.File

class SpigotDraw : JavaPlugin() {

    companion object {
        lateinit var instance : SpigotDraw

        var apiKey = ""

        fun Command.register() {
            val pluginManagerClazz = SpigotDraw.instance.server.pluginManager.javaClass
            val field = pluginManagerClazz.getDeclaredField("commandMap")
            field.isAccessible = true
            val commandMap = field.get(SpigotDraw.instance.server.pluginManager) as SimpleCommandMap
            commandMap.register("CyanShop", this)
        }

        fun isSpigot() : Boolean {
            try {
                for (method in Class.forName("org.bukkit.entity.Player").declaredMethods) {
                    if (method.name.equals("Spigot", ignoreCase = true)) {
                        return true
                    }
                }
            } catch (e: Exception) {
                return false
            }
            return false
        }


        lateinit var playerDataFile : File
        lateinit var playerData : YamlConfiguration
        lateinit var imageFolder : File

    }


    lateinit var pointsAPI: PlayerPointsAPI


    override fun onEnable() {
        instance = this
        saveDefaultConfig()
        pointsAPI = PlayerPoints.getInstance().api
        logger.info("On Spigot Text generator Picture is enabled!")
        apiKey = config.getString("ApiKey")!!
        if (apiKey.isEmpty()) {
            logger.warning("ApiKey is empty!")
            return
        }
        playerDataFile = File(dataFolder, "PlayerData.yml")
        if (!playerDataFile.exists()) {
            playerDataFile.createNewFile()
        }
        playerData = YamlConfiguration.loadConfiguration(playerDataFile)
        imageFolder = File(dataFolder, "images")
        if (!imageFolder.exists()) {
            imageFolder.mkdirs()
        }
        PluginCommands().register()
        server.pluginManager.registerEvents(PlayerListener, this)
        logger.info("On Spigot Text generator Picture is enabled!")

    }


    override fun onDisable() {
        PlayerListener.playerOldItem.forEach() {
            it.key.inventory.setItem(it.key.inventory.heldItemSlot, it.value)//
        }
    }


}


