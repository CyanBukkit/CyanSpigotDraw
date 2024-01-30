package cn.cyanbukkit.aiimage.onmap

import org.bukkit.entity.Player
import org.bukkit.map.MapCanvas
import org.bukkit.map.MapCursorCollection
import org.bukkit.map.MapRenderer
import org.bukkit.map.MapView
import java.awt.image.BufferedImage


class RenderImageMaps(val png: BufferedImage) : MapRenderer() {
    private var isRender = false

    override fun render(map: MapView, canvas: MapCanvas, player: Player) {
        if (isRender) return
        canvas.drawImage(0, 0, png)
        isRender = true
    }

}