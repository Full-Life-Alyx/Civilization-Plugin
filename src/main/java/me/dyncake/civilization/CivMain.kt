package me.dyncake.civilization

import me.dyncake.civilization.command.CmdCivilization
import me.dyncake.civilization.command.CmdSummonCrown
import me.dyncake.civilization.command.subcommand.SubCommand
import me.dyncake.civilization.command.subcommand.civilization.*
import me.dyncake.civilization.listener.OnFriendlyFire
import me.dyncake.civilization.listener.OnJoin
import me.dyncake.civilization.listener.OnLightning
import me.dyncake.civilization.runnable.CrownLoop
import net.kyori.adventure.text.Component
import net.luckperms.api.LuckPerms
import org.bukkit.Bukkit
import org.bukkit.permissions.Permission
import org.bukkit.plugin.java.JavaPlugin
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException
import java.util.*

class CivMain : JavaPlugin() {

    lateinit var connection: Connection
    lateinit var configWrapper: Config
    val databaseName = "civilization"

    lateinit var phases: Set<String>
    var phase: Int = 0

    val kings = ArrayList<UUID>()

    override fun onEnable() {
        configWrapper = Config(this)
        saveDefaultConfig()

        configWrapper.load()

        initDatabase()
        Items.init(this)
        Synchronizer.syncKing(this, connection)

        val provider = Bukkit.getServicesManager().getRegistration(LuckPerms::class.java)
        val pluginManager = server.pluginManager
        if (provider == null) {
            logger.severe("Luckperms not found! This is a requirement to this plugin!")
            pluginManager.disablePlugin(this)
            return
        }

        val lp = provider.provider

        /*getCommand("addself")?.setExecutor(CmdAddSelf(this))
        getCommand("printdb")?.setExecutor(CmdPrintDatabase(this))*/

        getCommand("summoncrown")?.setExecutor(CmdSummonCrown(this))

        pluginManager.registerEvents(OnJoin(this, lp), this)
        pluginManager.registerEvents(OnLightning(this), this)
        pluginManager.registerEvents(OnFriendlyFire(this), this)

        CmdCivilization().register("civilization", this)
            .also {
                it.setDefaultCommand(
                    SubCmdCivHelp(),
                    SubCommand(
                        "The default help command",
                        Permission("civ.cmd.civ.help"),
                        "%s",
                    )
                ).registerSubCommand("help", SubCmdCivHelp(),
                    SubCommand(
                        "The help command",
                        Permission("civ.cmd.civ.help"),
                        "%s help",
                        1, 1
                    )
                ).registerSubCommand("join", SubCmdCivJoin(this, lp),
                    SubCommand(
                        "Makes a player or yourself join a team",
                        Permission("civ.cmd.civ.join"),
                        "%s join <team> [player]",
                        2, 3
                    )
                ).registerSubCommand("update", SubCmdCivUpdate(this),
                    SubCommand(
                        "Updates the teams with a database (call \"/civ sync *\" to update everyones permissions)",
                        Permission("civ.cmd.civ.update"),
                        "%s update",
                        1, 2
                    )
                ).registerSubCommand("reload", SubCmdReloadConfig(this),
                    SubCommand(
                        "Reloads the configuration file",
                        Permission("civ.cmd.civ.reload"),
                        "%s reload",
                        1, 1
                    )
                ).registerSubCommand("synchronize", SubCmdCivilSynchronize(this, lp),
                    SubCommand(
                        "Synchronizes the permissions and database, this might slow down the server",
                        listOf("sync"),
                        Permission("civ.cmd.civ.synchronize"),
                        "%s synchronize <team|king|all> [ Target Name | '*'(everyone) | '**'(everyone but you) ]",
                        1, 2
                    )
                ).registerSubCommand("setking", SubCmdCivSetKing(this),
                    SubCommand(
                        "Sets a team's king",
                        Permission("civ.cmd.civ.setking"),
                        "%s setking <Civilization> <Target Name>",
                        3, 3
                    )

                ).registerSubCommand("teleport", SubCmdCivTeleport(this),
                    SubCommand(
                        "Teleports the targets to their designated spawn",
                        listOf("tp"),
                        Permission("civ.cmd.civ.teleport"),
                        "%s <player|team> <name>",
                        3, 3
                    )
                ).registerSubCommand("pvp", SubCmdCivFriendlyFire(this),
                    SubCommand(
                        "Toggles civilization's friendly fire",
                        Permission("civ.cmd.civ.pvp"),
                        "%s <team> <on|off|toggle>",
                        3, 3
                    )

                ).registerSubCommand("phase", SubCmdCivPhase(this),
                    SubCommand(
                        "Changes the phase",
                        Permission("civ.cmd.civ.phase"),
                        "%s <set|prev|next> <phase name (if argument 1 is set) | amount (if arguement 1 is prev or next)>",
                        2, 3
                    )
                ).registerSubCommand("advance", SubCmdCivAdvance(this),
                    SubCommand(
                        "Switches to the next phase",
                        listOf("adv", "next"),
                        Permission("civ.cmd.civ.advance"),
                        "%s advance",
                        1, 1
                    )
                )
        }

        CrownLoop(this)
            .runTaskTimer(this, 0, 10)

    }

    private fun initDatabase() {
        try {
            connection = DriverManager.getConnection(
                config.getString("databaseConnection.connectionString"),
                config.getString("databaseConnection.username"),
                config.getString("databaseConnection.password")
            )
            server.consoleSender.sendMessage(
                Component.text("Connected to database!", me.dyncake.civilization.ComponentColor.GREEN)
            )
        } catch (ex: SQLException) {
            logger.severe("Database error! Please check config.yml (Stack trace printed below for debugging)")
            ex.printStackTrace()
            pluginLoader.disablePlugin(this)
            return
        }

        if (!config.getBoolean("status.databaseInitialized")) {
            connection.prepareStatement(config.getString("constants.databaseInitCommand")).execute()
            config.set("status.databaseInitialized", true)

        }
    }

    override fun onDisable() {

    }
}