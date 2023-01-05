package me.dyncake.civilization.command.subcommand.civilization

import me.dyncake.civilization.ComponentColor
import me.dyncake.civilization.ExtUtils.toBytes
import me.dyncake.civilization.ExtUtils.toUUID
import me.dyncake.civilization.command.ParentCommand
import me.dyncake.civilization.command.subcommand.SubCommand
import me.dyncake.civilization.command.subcommand.SubCommandExecutor
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.event.player.PlayerTeleportEvent
import java.util.*

class SubCmdCivTeleport(private val plugin: me.dyncake.civilization.CivMain) : SubCommandExecutor {
    override fun onCommand(
        sender: CommandSender,
        subCommand: SubCommand,
        parentCommand: ParentCommand,
        args: Array<out String>,
    ): Boolean {

        //switch player team
        //get name

        val connection = plugin.connection

        when (args[1]) {
            "player" -> {

                val player = Bukkit.getPlayer(args[2])
                if (player == null) {
                    sender.sendMessage(
                        Component.text(
                            "Cannot find player ${args[2]}",
                            ComponentColor.RED
                        )
                    )
                    return true
                }


                val psFindTeam = connection.prepareStatement(
                    """
                        SELECT team_uuid FROM ${plugin.databaseName}.player WHERE uuid=?
                    """.trimIndent()
                ).apply {
                    setBytes(1, player.uniqueId.toBytes())
                }

                val resFindTeam = psFindTeam.executeQuery()
                resFindTeam.next()

                val ps = connection.prepareStatement(
                    """
                        SELECT name FROM ${plugin.databaseName}.team WHERE uuid=?
                    """.trimIndent()
                ).apply {
                    setBytes(1, resFindTeam.getBytes("team_uuid"))
                }

                val res = ps.executeQuery()

                res.next()
                val teamName = res.getString("name")
                val section = plugin.config.getConfigurationSection("teams.${teamName}")

                if (section == null) {
                    sender.sendMessage(
                        Component.text(
                            "Cannot find team $teamName in the config.yml consider doing /civilizations reload",
                            ComponentColor.RED
                        )
                    )
                    return true
                }


                val loc = section.getLocation("spawn")
                if (loc == null) {
                    sender.sendMessage(
                        Component.text(
                            "Cannot find the location for the team $teamName",
                            ComponentColor.RED
                        )
                    )
                    return true
                }
                player.teleport(loc, PlayerTeleportEvent.TeleportCause.PLUGIN)

            }

            "team" -> {

                val psFind = connection.prepareStatement(
                    """
                        SELECT uuid FROM ${plugin.databaseName}.team WHERE `name`=?
                    """.trimIndent()
                ).apply {
                    setString(1, args[2])
                }
                val rsFind = psFind.executeQuery()

                if (!rsFind.isBeforeFirst) {
                    sender.sendMessage(
                        Component.text(
                            "The civilization ${args[2]} is not in the list of civilizations",
                            ComponentColor.RED
                        )
                    )
                    return true
                }

                rsFind.next()
                val teamUniqueIdBytes = rsFind.getBytes("uuid")


                val teamName = args[2]
                val section = plugin.config.getConfigurationSection("teams.${teamName}")

                if (section == null) {
                    sender.sendMessage(
                        Component.text(
                            "Cannot find team $teamName in the config.yml consider doing /civilizations reload",
                            ComponentColor.RED
                        )
                    )
                    return true
                }


                val loc = section.getLocation("spawn")
                if (loc == null) {
                    sender.sendMessage(
                        Component.text(
                            "Cannot find the location for the team $teamName",
                            ComponentColor.RED
                        )
                    )
                    return true
                }

                val ps = connection.prepareStatement(
                    """
                        SELECT uuid FROM ${plugin.databaseName}.player WHERE team_uuid=?
                    """.trimIndent()
                ).apply {
                    setBytes(1, teamUniqueIdBytes)
                }

                val res = ps.executeQuery()

                val targets = ArrayList<UUID>()
                while (res.next()) {
                    targets.add(res.getBytes("uuid").toUUID())
                }

                for (uuid in targets) {
                    val target = Bukkit.getPlayer(uuid) ?: continue
                    target.teleport(loc, PlayerTeleportEvent.TeleportCause.PLUGIN)
                }

            }

            else -> {
                sender.sendMessage(
                    Component.text(
                        "Please select an option between player and team",
                        ComponentColor.RED
                    )
                )
                return true
            }
        }

        return true
    }
}