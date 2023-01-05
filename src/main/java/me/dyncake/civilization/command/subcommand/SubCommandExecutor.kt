package me.dyncake.civilization.command.subcommand

import me.dyncake.civilization.command.ParentCommand
import org.bukkit.command.CommandSender

interface SubCommandExecutor {
    fun onCommand(
        sender: CommandSender,
        subCommand: SubCommand,
        parentCommand: ParentCommand,
        args: Array<out String>,
    ): Boolean

}
