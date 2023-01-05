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

class SubCmdCivSetKing(private val plugin: me.dyncake.civilization.CivMain) : SubCommandExecutor {
    override fun onCommand(
        sender: CommandSender,
        subCommand: SubCommand,
        parentCommand: ParentCommand,
        args: Array<out String>,
    ): Boolean {

        val connection = plugin.connection

        val teams: ArrayList<String> = kotlin.run {
            val ps = connection.prepareStatement(
                """
                    SELECT name FROM ${plugin.databaseName}.team
                """.trimIndent()
            )
            val res = ps.executeQuery()

            val foundTeams = ArrayList<String>()

            while (res.next())
                foundTeams.add(res.getString("name"))

            foundTeams
        }

        if (!teams.contains(args[1])) {
            sender.sendMessage(
                Component.text(
                    "Team ${args[1]} not found!",
                    ComponentColor.RED
                )
            )
            return true
        }

        val teamUniqueId = kotlin.run {
            val ps = connection.prepareStatement(
                """
                    SELECT uuid FROM ${plugin.databaseName}.team WHERE name=?
                """.trimIndent()
            ).apply {
                setString(1, args[1])
            }
            val res = ps.executeQuery()
            res.next()
            res.getBytes("uuid").toUUID()
        }

        val target = Bukkit.getPlayer(args[2])
        if (args[2] == "~") {
            connection.prepareStatement(
                """
                    UPDATE ${plugin.databaseName}.team SET king=null WHERE uuid=?
                """.trimIndent()
            ).apply {
                setBytes(1, teamUniqueId.toBytes())
                execute()
            }

            sender.sendMessage(
                Component.text(
                    "Successfully updated the team ${args[1]} to have the king <NONE>",
                    ComponentColor.GREEN
                )
            )
            Synchronizer.syncKing(plugin, plugin.connection)
            return true
        } else if (target == null) {
            sender.sendMessage(
                Component.text(
                    "Player ${args[2]} cannot be found!",
                    ComponentColor.RED
                )
            )

            Synchronizer.syncKing(plugin, connection)
            return true
        }

        connection.prepareStatement(
            """
                UPDATE ${plugin.databaseName}.team SET king=? WHERE uuid=?
            """.trimIndent()
        ).apply {
            setBytes(1, target.uniqueId.toBytes())
            setBytes(2, teamUniqueId.toBytes())
            execute()
        }


        sender.sendMessage(
            Component.text(
                "Successfully updated the team ${args[1]} to have the king ${args[2]}",
                ComponentColor.GREEN
            )
        )

        Synchronizer.syncKing(plugin, connection)

        return true
    }
}