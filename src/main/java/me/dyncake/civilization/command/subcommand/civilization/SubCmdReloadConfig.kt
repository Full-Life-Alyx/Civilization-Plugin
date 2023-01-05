package me.dyncake.civilization.command.subcommand.civilization

import me.dyncake.civilization.ComponentColor
import me.dyncake.civilization.command.ParentCommand
import me.dyncake.civilization.command.subcommand.SubCommand
import me.dyncake.civilization.command.subcommand.SubCommandExecutor
import net.kyori.adventure.text.Component
import org.bukkit.command.CommandSender

class SubCmdReloadConfig(private val plugin: me.dyncake.civilization.CivMain) : SubCommandExecutor {

    override fun onCommand(
        sender: CommandSender,
        subCommand: SubCommand,
        parentCommand: ParentCommand,
        args: Array<out String>,
    ): Boolean {
        plugin.reloadConfig()
        plugin.configWrapper.validatePaths()

        sender.sendMessage(
            Component.text(
                "Successfully reloaded the configuration file!",
                ComponentColor.GREEN
            )
        )

        return true
    }
}