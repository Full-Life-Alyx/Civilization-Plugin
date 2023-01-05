package me.dyncake.civilization.command.subcommand.civilization

import com.google.common.base.Preconditions
import me.dyncake.civilization.ComponentColor
import me.dyncake.civilization.command.ParentCommand
import me.dyncake.civilization.command.subcommand.SubCommand
import me.dyncake.civilization.command.subcommand.SubCommandExecutor
import net.kyori.adventure.text.Component
import org.bukkit.command.CommandSender
import java.sql.ResultSet

class SubCmdCivFriendlyFire(private val plugin: me.dyncake.civilization.CivMain) : SubCommandExecutor {
    override fun onCommand(
        sender: CommandSender,
        subCommand: SubCommand,
        parentCommand: ParentCommand,
        args: Array<out String>,
    ): Boolean {

        val connection = plugin.connection

        val psFind = connection.prepareStatement(
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

        rsFind.next()
        val teamUniqueIdBytes = rsFind.getBytes("uuid")

        rsFind.last()
        val foundRows = rsFind.row

        Preconditions.checkArgument(
            foundRows <= 1,
            "Name is unique therefore making it impossible to have multiple results %s",
            foundRows > 1
        )

        val option = when (args[2]) {

            "on" -> {
                true
            }

            "off" -> {
                false
            }

            "toggle" -> {
                val ps = connection.prepareStatement(
                    """
                        SELECT friendly_fire FROM ${plugin.databaseName}.team WHERE uuid=?
                    """.trimIndent()
                ).apply {
                    setBytes(1, teamUniqueIdBytes)
                }
                val res = ps.executeQuery()
                res.next()

                !res.getBoolean("friendly_fire")
            }

            else -> {
                sender.sendMessage(
                    Component.text(
                        "Could not find option ${args[2]}, your options are: on, off and toggle",
                        ComponentColor.RED
                    )
                )
                return true
            }
        }


        connection.prepareStatement(
            """
                UPDATE ${plugin.databaseName}.team SET friendly_fire=? WHERE uuid=?
            """.trimIndent()
        ).apply {
            setBoolean(1, option)
            setBytes(2, teamUniqueIdBytes)
        }

        sender.sendMessage(
            Component.text(
                "Successfully updated database!",
                ComponentColor.GOLD
            )
        )

        return true

    }
}