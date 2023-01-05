package me.dyncake.civilization.command

import me.dyncake.civilization.ComponentColor
import me.dyncake.civilization.command.subcommand.SubCommand
import me.dyncake.civilization.command.subcommand.SubCommandExecutor
import net.kyori.adventure.text.Component
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.permissions.Permission
import org.bukkit.plugin.java.JavaPlugin

open class ParentCommand : CommandExecutor {

    var commands: HashMap<String, Pair<SubCommandExecutor, SubCommand>> = HashMap()
    lateinit var name: String
    var commandAliases: HashMap<String, String> = HashMap()
    lateinit var defaultCommand: Pair<SubCommandExecutor, SubCommand>
    lateinit var usageCommand: Permission

    fun register(name: String, plugin: JavaPlugin): ParentCommand {
        plugin.getCommand(name)?.setExecutor(this)
        this.name = name
        return this
    }

    fun registerSubCommand(name: String, subCommand: SubCommandExecutor, subCommandProperties: SubCommand): ParentCommand {
        subCommandProperties.name = name
        commands[name] = Pair(subCommand, subCommandProperties)
        if (subCommandProperties.aliases != null) {
            for (alias in subCommandProperties.aliases) {
                commandAliases[name] = alias
            }
        }
        return this
    }

    fun setDefaultCommand(subCommand: SubCommandExecutor, subCommandProperties: SubCommand): ParentCommand {
        this.defaultCommand = Pair(subCommand, subCommandProperties)
        return this
    }


    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (args.isEmpty()) {
            return if (sender.hasPermission(defaultCommand.second.permission)) {
                defaultCommand.first
                    .onCommand(sender, defaultCommand.second, this, args)
            } else {
                sender.sendMessage(
                    defaultCommand.second.permissionMessage
                )
                false
            }
        }


        if (!commands.containsKey(args[0].lowercase())) {
            if (commandAliases.containsValue(args[0].lowercase())) {

                val aliasCommand = commands[
                    commandAliases.filterValues {
                        it == args[0].lowercase()
                    }.keys.first()
                ]!!
                aliasCommand.first
                    .run(sender, aliasCommand.second, this, args)
                return true
            } else {
                return if (sender.hasPermission(defaultCommand.second.permission)) {
                    defaultCommand.first
                        .run(sender, defaultCommand.second, this, args)
                } else {
                    sender.sendMessage(
                        defaultCommand.second.permissionMessage
                    )
                    false
                }
            }


        }
        val subCommand: Pair<SubCommandExecutor, SubCommand> = commands[args[0]]!!

        return if (sender.hasPermission(subCommand.second.permission)) {
            subCommand.first
                .run(sender, subCommand.second, this, args)
        } else {
            sender.sendMessage(
                subCommand.second.permissionMessage
            )
            false
        }
    }

    companion object {
        private fun SubCommandExecutor.run(sender: CommandSender, subCommand: SubCommand, parentCommand: ParentCommand, args: Array<out String>): Boolean {

            val usageMsg = Component.text(
                "Correct Usage:\n",
                ComponentColor.WHITE
            ).append(
                Component.text(
                    subCommand.usage
                        .replace(
                            "%s", "/${parentCommand.name} ${subCommand.name}"
                        ),
                    ComponentColor.GOLD
                )
            )

            if (args.size < subCommand.minArgs) {
                sender.sendMessage(
                    subCommand.notEnoughArgsMsg
                        .append(Component.newline())
                        .append(usageMsg)
                )
                return true
            }
            if (args.size > subCommand.maxArgs) {
                sender.sendMessage(
                    subCommand.tooManyArgsMsg
                        .append(Component.newline())
                        .append(usageMsg)
                )
                return true
            }

            return this.onCommand(sender, subCommand, parentCommand, args)
        }
    }
}

