package me.dyncake.civilization.command.subcommand.civilization

import me.dyncake.civilization.command.ParentCommand
import me.dyncake.civilization.command.subcommand.SubCommand
import me.dyncake.civilization.command.subcommand.SubCommandExecutor
import org.bukkit.command.CommandSender

class SubCmdCivPhase(private val plugin: me.dyncake.civilization.CivMain) : SubCommandExecutor {
    override fun onCommand(
        sender: CommandSender,
        subCommand: SubCommand,
        parentCommand: ParentCommand,
        args: Array<out String>,
    ): Boolean {


        return true
    }
}