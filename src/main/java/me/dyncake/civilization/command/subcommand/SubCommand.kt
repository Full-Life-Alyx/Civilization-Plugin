package me.dyncake.civilization.command.subcommand

import me.dyncake.civilization.ComponentColor
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextComponent
import org.bukkit.permissions.Permission

class SubCommand {

    lateinit var name: String
    val description: String
    val aliases: List<String>?
    val permission: Permission
    val permissionMessage: Component
    val usage: String
    val minArgs: Int
    val maxArgs: Int
    val notEnoughArgsMsg: TextComponent
    val tooManyArgsMsg: TextComponent

    companion object {
        private val noPermission = Component.text(
            "You do not have permission to execute this command",
            ComponentColor.RED
        )
        private val notEnoughArgsMsg: TextComponent = Component.text(
            "Not enough arguments!",
            ComponentColor.RED
        )
        private val tooManyArgsMsg: TextComponent = Component.text(
            "Too many arguments!",
            ComponentColor.RED
        )
    }

    constructor(
        name: String,
        description: String,
        aliases: List<String>?,
        permission: Permission,
        permissionMessage: Component,
        usage: String,
        minArgs: Int,
        maxArgs: Int,
        notEnoughArgsMsg: TextComponent,
        tooManyArgsMsg: TextComponent,
    ) {
        this.name = name
        this.description = description
        this.aliases = aliases
        this.permission = permission
        this.permissionMessage = permissionMessage
        this.usage = usage
        this.minArgs = minArgs
        this.maxArgs = maxArgs
        this.notEnoughArgsMsg = notEnoughArgsMsg
        this.tooManyArgsMsg = tooManyArgsMsg
    }

    constructor(
        description: String,
        aliases: List<String>?,
        permission: Permission,
        usage: String,
        minArgs: Int,
        maxArgs: Int,
    ) {
        this.description = description
        this.aliases = aliases
        this.permission = permission
        this.permissionMessage = noPermission
        this.usage = usage
        this.minArgs = minArgs
        this.maxArgs = maxArgs
        this.notEnoughArgsMsg = Companion.notEnoughArgsMsg
        this.tooManyArgsMsg = Companion.tooManyArgsMsg
    }

    constructor(
        description: String,
        permission: Permission,
        usage: String,
        minArgs: Int,
        maxArgs: Int,
    ) {
        this.description = description
        this.aliases = null
        this.permission = permission
        this.permissionMessage = noPermission
        this.usage = usage
        if (minArgs > maxArgs) {

            this.minArgs = -1
            this.maxArgs = -1

            throw IllegalArgumentException("minArgs cannot be bigger than maxArgs!")
        } else {
            this.minArgs = minArgs
            this.maxArgs = maxArgs
        }
        this.notEnoughArgsMsg = Companion.notEnoughArgsMsg
        this.tooManyArgsMsg = Companion.tooManyArgsMsg
    }

    constructor(
        description: String,
        permission: Permission,
        usage: String,
    ) {
        this.description = description
        this.aliases = null
        this.permission = permission
        this.permissionMessage = noPermission
        this.usage = usage
        this.minArgs = -1
        this.maxArgs = -1
        this.notEnoughArgsMsg = Companion.notEnoughArgsMsg
        this.tooManyArgsMsg = Companion.tooManyArgsMsg
    }
}
