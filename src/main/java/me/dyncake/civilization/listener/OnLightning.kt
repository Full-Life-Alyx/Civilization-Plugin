package me.dyncake.civilization.listener

import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent

class OnLightning(private val plugin: me.dyncake.civilization.CivMain) : Listener {
    @EventHandler
    fun onHit(event: EntityDamageByEntityEvent) {
        if (event.damager.type != EntityType.LIGHTNING) return
        val player = event.entity
        if (player !is Player) return

        if (!plugin.kings.contains(player.uniqueId)) return

        event.isCancelled = true
    }
}