package me.dyncake.civilization.listener

import me.dyncake.civilization.ExtUtils.toBytes
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent

class OnFriendlyFire(private val plugin: me.dyncake.civilization.CivMain) : Listener {
    @EventHandler
    fun onHit(event: EntityDamageByEntityEvent) {

        val entity = event.entity
        val damager = event.damager

        if (entity !is Player) return
        if (damager !is Player) return

        if (entity == damager) return
        val connection = plugin.connection

        //FIXME fk catching
        val hitPs = connection.prepareStatement(
            """
                SELECT team_uuid FROM civilization.player WHERE uuid=?
            """.trimIndent()
        ).apply {
            setBytes(1, entity.uniqueId.toBytes())
        }

        val hitRes = hitPs.executeQuery()

        hitRes.next()




        val hitterPs = connection.prepareStatement(
            """
                SELECT team_uuid FROM civilization.player WHERE uuid=?
            """.trimIndent()
        ).apply {
            setBytes(1, entity.uniqueId.toBytes())
        }

        val hitterRes = hitterPs.executeQuery()
        hitterRes.next()

        val hitterTeamUniqueId = hitterRes.getBytes("team_uuid")
        val hitTeamUniqueId = hitRes.getBytes("team_uuid")

        if (hitterTeamUniqueId.contentEquals(hitTeamUniqueId)) {
            val ps = connection.prepareStatement(
                """
                    SELECT friendly_fire FROM civilization.team WHERE uuid=?
                """.trimIndent()
            ).apply {
                setBytes(1, hitterTeamUniqueId)
            }
            val res = ps.executeQuery()

            if (!res.getBoolean("friendly_fire")) {
                event.isCancelled = true
                return
            }
        }
    }
}