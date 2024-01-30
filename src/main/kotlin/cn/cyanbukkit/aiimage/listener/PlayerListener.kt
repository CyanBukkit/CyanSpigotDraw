package cn.cyanbukkit.aiimage.listener

import cn.cyanbukkit.aiimage.SpigotDraw
import cn.cyanbukkit.aiimage.image.ImageStart
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.player.*
import org.bukkit.inventory.ItemStack

object PlayerListener :  org.bukkit.event.Listener {

    val playerOldItem = mutableMapOf<Player, ItemStack>()
    val start = mutableMapOf<Player, Boolean>()


    @EventHandler
    fun onPlayerSendText(e : AsyncPlayerChatEvent) {
        if (start.contains(e.player)) {
            e.isCancelled = true
            val p = e.player
            if (e.message == "cancel") {
                // 删除start里 e.player的所有元素
                p.sendMessage("§c已取消")
                start.remove(e.player)
                return
            }
            val need = SpigotDraw.instance.config.getInt("NeedPoints")
            if (SpigotDraw.instance.pointsAPI.take(p.uniqueId, need)) {
                ImageStart(e.player).start(e.message)
                e.player.sendMessage("§a§l已开始制作图片....")
                start.remove(e.player)
            } else {
                p.sendMessage("§c支付失败 你的积分不足需要${need}你现在只有${SpigotDraw.instance.pointsAPI.look(p.uniqueId)}")
                start.remove(e.player)
            }
        }
    }

    @EventHandler
    fun onClose1(e: PlayerDropItemEvent) { // 丢弃物品
        if (playerOldItem.containsKey(e.player)) {
            e.isCancelled = true
            e.player.sendMessage("§c§l图片预览已关闭")
            e.player.inventory.remove(e.player.inventory.itemInHand)
            e.player.inventory.itemInHand = playerOldItem[e.player]
            playerOldItem.remove(e.player)
        }
    }


//    @EventHandler
//    fun changeItem(e: PlayerSwapHandItemsEvent) { // 切换物品栏物品的事件
//        if (playerOldItem.containsKey(e.player)) {
//            e.player.sendMessage("§c§l图片预览已关闭")
//            e.isCancelled = true
//            e.player.inventory.itemInHand = playerOldItem[e.player]
//            playerOldItem.remove(e.player)
//        }
//    }


//    @EventHandler
//    fun onChanger(e: PlayerItemHeldEvent) { // 切换物品栏物品的事件
//        if (playerOldItem.containsKey(e.player)) {
//            e.player.sendMessage("§c§l图片预览已关闭")
//            e.isCancelled = true
//            e.player.inventory.itemInHand = playerOldItem[e.player]
//            playerOldItem.remove(e.player)
//        }
//    }

    @EventHandler
    fun onQuit(e: PlayerQuitEvent) { // 退出事件
        if (playerOldItem.containsKey(e.player)) {
            e.player.sendMessage("§c§l图片预览已关闭")
            e.player.inventory.itemInHand = ItemStack(Material.AIR)
            e.player.inventory.itemInHand = playerOldItem[e.player]
            playerOldItem.remove(e.player)
        }
    }

    @EventHandler
    fun onTeleport(e: PlayerTeleportEvent) { // 传送事件
        if (playerOldItem.containsKey(e.player)) {
            e.player.sendMessage("§c§l图片预览已关闭")
            e.isCancelled = true
            e.player.inventory.itemInHand = ItemStack(Material.AIR)
            e.player.inventory.itemInHand = playerOldItem[e.player]
            playerOldItem.remove(e.player)
        }
    }

    @EventHandler
    fun on(e: PlayerToggleSneakEvent) { // 蹲下事件
        if (playerOldItem.containsKey(e.player)) {
            e.player.sendMessage("§c§l图片预览已关闭")
            e.isCancelled = true
            e.player.inventory.itemInHand = ItemStack(Material.AIR)
            e.player.inventory.itemInHand = playerOldItem[e.player]
            playerOldItem.remove(e.player)
        }
    }

}