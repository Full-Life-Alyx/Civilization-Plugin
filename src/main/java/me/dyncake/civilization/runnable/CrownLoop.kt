package me.dyncake.civilization.runnable

import me.dyncake.civilization.ComponentColor
import net.kyori.adventure.text.Component
import net.kyori.adventure.title.Title
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.entity.LightningStrike
import org.bukkit.persistence.PersistentDataType
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.bukkit.scheduler.BukkitRunnable
import java.time.Duration
import kotlin.math.max

class CrownLoop(private val plugin: me.dyncake.civilization.CivMain) : BukkitRunnable() {
    override fun run() {
        val kings = plugin.kings
        for (player in Bukkit.getOnlinePlayers()) {
            if (kings.contains(player.uniqueId)) continue

            val helmet = player.inventory.helmet ?: return
            if (helmet.type != Material.GOLDEN_HELMET) return

            val itemMeta = helmet.itemMeta ?: return


            val itemType =
                itemMeta.persistentDataContainer[
                    NamespacedKey(plugin, "item"), PersistentDataType.STRING
                ] ?: return

            if (itemType == "crown" && !player.isDead) {
                //payload

                player.world.spawn(player.location, LightningStrike::class.java)
                player.health = max(player.health - 2, 0.1)

                player.addPotionEffects(
                    listOf(
                        PotionEffect(PotionEffectType.BLINDNESS, 20, 68, false, false, false),
                        PotionEffect(PotionEffectType.LEVITATION, 20, 1, false, false, false),
                        PotionEffect(PotionEffectType.WEAKNESS, 1200, 1, false, false, false)
                    )
                )

                player.showTitle(
                    Title.title(
                        Component.text("Take off the crown", ComponentColor.DARK_RED),
                        Component.text("You are not worthy", ComponentColor.GRAY),
                        Title.Times.times(Duration.ZERO, Duration.ofMillis(700), Duration.ofMillis(300)
                        )
                    )
                )

            }
        }
    }
}