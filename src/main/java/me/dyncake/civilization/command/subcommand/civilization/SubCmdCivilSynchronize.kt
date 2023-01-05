package me.dyncake.civilization.command.subcommand.civilization

import me.dyncake.civilization.ComponentColor
import me.dyncake.civilization.command.ParentCommand
import me.dyncake.civilization.command.subcommand.SubCommand
import me.dyncake.civilization.command.subcommand.SubCommandExecutor
import net.kyori.adventure.text.Component
import net.luckperms.api.LuckPerms
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.util.*


class SubCmdCivilSynchronize(private val plugin: me.dyncake.civilization.CivMain, private val lp: LuckPerms) :
    SubCommandExecutor {
    override fun onCommand(
        sender: CommandSender,
        subCommand: SubCommand,
        parentCommand: ParentCommand,
        args: Array<out String>,
    ): Boolean {

        val connection = plugin.connection

        val targets = ArrayList<UUID>()

        when (args.size) {
            1 -> {
                sender.sendMessage(
                    Component.text(
                        "Too little arguments!",
                        ComponentColor.RED
                    )
                )
                return true
            }

            2 -> {
                if (sender is Player) {
                    targets.add(sender.uniqueId)
                } else {
                    sender.sendMessage(
                        Component.text(
                            "You must be a player to target yourself!",
                            ComponentColor.RED
                        )
                    )
                    return true
                }
            }

            3 -> {
                if (args[2] == "*") {
                    Bukkit.getOnlinePlayers().forEach {

                        targets.add(it.uniqueId)
                    }
                } else if (args[2] == "**") {

                    if (sender is Player) {
                        Bukkit.getOnlinePlayers().forEach {
                            if (it != sender)
                                targets.add(it.uniqueId)
                        }
                    } else {
                        sender.sendMessage(
                            Component.text(
                                "You must be a player to exclude yourself!",
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
                                "Player ${args[2]} not found!",
                                ComponentColor.RED
                            )
                        )
                        return true
                    }
                    targets.add(foundPlayer.uniqueId)
                }
            }
        }

        when (args[1]) {
            "team" -> {
                Synchronizer.syncTeam(plugin, lp, targets)
                sender.sendMessage(
                    Component.text(
                        "Successfully updated the teams!",
                        ComponentColor.GOLD
                    )
                )
            }

            "king" -> {
                Synchronizer.syncKing(plugin, connection)
                sender.sendMessage(
                    Component.text(
                        "Successfully updated the kings!",
                        ComponentColor.GOLD
                    )
                )
            }

            "all" -> {
                Synchronizer.syncTeam(plugin, lp, targets)
                Synchronizer.syncKing(plugin, connection)
                sender.sendMessage(
                    Component.text(
                        "Successfully updated everything!",
                        ComponentColor.GOLD
                    )
                )
            }
        }
        return true

        /*
        if (team == null) {
                val dmr = user.setPrimaryGroup("default").toString()
                lp.userManager.saveUser(user)
                Bukkit.getPlayer(target)!!.sendMessage("default", dmr)
            } else {
                val dmr = user.setPrimaryGroup(team).toString()
                lp.userManager.saveUser(user)
                Bukkit.getPlayer(target)!!.sendMessage(team, dmr)
            }
         */
    }


}