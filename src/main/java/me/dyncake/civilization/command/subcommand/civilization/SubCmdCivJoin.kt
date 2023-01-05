package me.dyncake.civilization.command.subcommand.civilization

import com.google.common.base.Preconditions
import me.dyncake.civilization.ComponentColor
import me.dyncake.civilization.ExtUtils.toBytes
import me.dyncake.civilization.command.ParentCommand
import me.dyncake.civilization.command.subcommand.SubCommand
import me.dyncake.civilization.command.subcommand.SubCommandExecutor
import net.kyori.adventure.text.Component
import net.luckperms.api.LuckPerms
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.sql.ResultSet

class SubCmdCivJoin(private val plugin: me.dyncake.civilization.CivMain, private val lp: LuckPerms) :
    SubCommandExecutor {
    override fun onCommand(
        sender: CommandSender,
        subCommand: SubCommand,
        parentCommand: ParentCommand,
        args: Array<out String>,
    ): Boolean {


        val usage = Component.text("usage: /civilization join <team> <player>", ComponentColor.AQUA)

        /*
        //Should have checked the database instead because it contains updated information
        val teams = config.getConfigurationSection("teams")?.getKeys(false)!!
        if (!teams.contains(args[1])) {
            sender.sendMessage(
                Component.text(
                    "The civilization ${args[1]} is not in the list of civilizations",
                    ComponentColor.RED
                )
            )
            return true
        }
         */

        plugin.logger.info("${args.size == 2}, ${args.size}")
        val target: Player = if (args.size == 2) {
            if (sender is Player)
                sender
            else {
                sender.sendMessage(
                    Component.text(
                        "You must be a player to select yourself",
                        ComponentColor.RED
                    )
                )
                return true
            }

        } else {
            val foundPlayer = Bukkit.getPlayer(args[2])
            if (foundPlayer == null) {
                sender.sendMessage(
                    Component.text(
                        "Player ${args[2]} cannot be found!",
                        ComponentColor.RED
                    )
                )
                return true
            }
            println(foundPlayer.name)
            foundPlayer

        }


        val psFind = plugin.connection.prepareStatement(
            """
                SELECT uuid FROM ${plugin.databaseName}.team WHERE `name`=?
            """.trimIndent(),
            ResultSet.TYPE_SCROLL_SENSITIVE,
            ResultSet.CONCUR_UPDATABLE
        ).also {
            it.setString(1, args[1])
        }
        val rsFind = psFind.executeQuery()

        if (!rsFind.isBeforeFirst) {
            sender.sendMessage(
                Component.text(
                    "The civilization ${args[1]} is not in the list of civilizations",
                    ComponentColor.RED
                )
            )
            return true
        }

        rsFind.first()
        val teamUniqueIdBytes = rsFind.getBytes("uuid")

        rsFind.last()
        val foundRows = rsFind.row

        Preconditions.checkArgument(
            foundRows <= 1,
            "Name is unique therefore making it impossible to have multiple results %s",
            foundRows > 1
        )



        plugin.connection.prepareStatement(
            """
                UPDATE ${plugin.databaseName}.player SET team_uuid=? WHERE uuid=?
            """.trimIndent()
        ).also {
            it.setBytes(1, teamUniqueIdBytes)
            it.setBytes(2, target.uniqueId.toBytes())
            it.execute()
        }

        sender.sendMessage(
            Component.text(
                "Successfully updated database"
            )
        )


        Synchronizer.syncTeam(plugin, lp, listOf(target.uniqueId))

        return true
    }

}