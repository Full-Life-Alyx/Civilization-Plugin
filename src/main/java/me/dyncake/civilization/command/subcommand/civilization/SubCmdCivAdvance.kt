package me.dyncake.civilization.command.subcommand.civilization

import me.dyncake.civilization.ComponentColor
import me.dyncake.civilization.PhaseMoveEvent
import me.dyncake.civilization.command.ParentCommand
import me.dyncake.civilization.command.subcommand.SubCommand
import me.dyncake.civilization.command.subcommand.SubCommandExecutor
import net.kyori.adventure.text.Component
import org.bukkit.command.CommandSender

class SubCmdCivAdvance(private val plugin: me.dyncake.civilization.CivMain) : SubCommandExecutor {
    override fun onCommand(
        sender: CommandSender,
        subCommand: SubCommand,
        parentCommand: ParentCommand,
        args: Array<out String>,
    ): Boolean {

        PhaseMoveEvent(plugin.phases, plugin.phase, plugin.phase + 1).callEvent()
        plugin.phase += 1

        sender.sendMessage(
            Component.text(
                "Moved to next phase!",
                ComponentColor.GREEN
            )
        )

        return true
    }
}