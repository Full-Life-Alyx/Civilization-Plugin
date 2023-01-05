package me.dyncake.civilization.command.subcommand.civilization

import me.dyncake.civilization.ComponentColor
import me.dyncake.civilization.command.ParentCommand
import me.dyncake.civilization.command.subcommand.SubCommand
import me.dyncake.civilization.command.subcommand.SubCommandExecutor
import net.kyori.adventure.text.Component
import org.bukkit.command.CommandSender

class SubCmdCivHelp : SubCommandExecutor {
    override fun onCommand(
        sender: CommandSender,
        subCommand: SubCommand,
        parentCommand: ParentCommand,
        args: Array<out String>,
    ): Boolean {


        var message = Component.text(
            "--- Help menu ---\n",
            ComponentColor.DARK_PURPLE
        )

        for ((cmdName, command) in parentCommand.commands) {
            //reassign
            message = message.append(
                Component.textOfChildren(
                    Component.text(cmdName, ComponentColor.GOLD),
                    Component.text(" - ", ComponentColor.WHITE),
                    Component.text(command.second.description, ComponentColor.GOLD),
                    Component.newline(),

                    Component.text("   "),
                    Component.text(
                        command.second.usage
                            .replace(
                                "%s", "/${parentCommand.name}"
                            )
                    ),
                    Component.text(" - ", ComponentColor.WHITE),
                    Component.text(
                        if (command.second.aliases == null)
                            "None"
                        else
                            command.second.aliases.toString()
                    ),
                    Component.newline()
                )
            )

        }

        sender.sendMessage(
            message.append(
                Component.text(
                    "--- Help menu ---",
                    ComponentColor.DARK_PURPLE
                )
            )
        )
        return true
    }
}