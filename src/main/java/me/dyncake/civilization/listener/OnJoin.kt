package me.dyncake.civilization.listener

import me.dyncake.civilization.ComponentColor
import me.dyncake.civilization.ExtUtils.toBytes
import me.dyncake.civilization.command.subcommand.civilization.Synchronizer
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextDecoration
import net.luckperms.api.LuckPerms
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.scoreboard.Criteria

class OnJoin(private val plugin: me.dyncake.civilization.CivMain, private val lp: LuckPerms) : Listener {

    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        val player = event.player
        plugin.connection.prepareStatement(
            """
                INSERT IGNORE INTO civilization.player (uuid, team_uuid) VALUES (?, null);
            """.trimIndent()
        ).also {
            it.setBytes(1, player.uniqueId.toBytes())
            it.execute()

        }

        Synchronizer.syncTeam(plugin, lp, listOf(player.uniqueId))


        //TODO Find how to scoreboard
        player.addScoreboardTag("Cheese")

        player.scoreboard.registerNewObjective(
            "yes",
            Criteria.DUMMY,
            Component.text("Civilizations", ComponentColor.GOLD).decorate(TextDecoration.BOLD)
        )

    }

}