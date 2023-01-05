package me.dyncake.civilization

import net.kyori.adventure.text.Component
import net.kyori.adventure.title.Title
import org.bukkit.ChatColor
import org.bukkit.configuration.ConfigurationSection
import java.time.Duration

class Config(plugin: CivMain) {

    private val plugin: CivMain
    private val validatedPaths: ArrayList<String>

    init {
        this.plugin = plugin

        this.validatedPaths = ArrayList()
        if (!validatePaths()) {
            plugin.pluginLoader.disablePlugin(plugin)
        }
    }

    fun reload() {
        plugin.reloadConfig()
        load()
    }
    fun load() {
        fetchPhases(plugin)
    }

    private fun isValid(path: String): Boolean {
        return plugin.config.get(path) != null
    }

    private fun validatePath(path: String): Boolean {
        val isValid = isValid(path)

        return if (isValid) {
            true
        } else {
            plugin.logger.severe("Configuration path $path has not been found!")
            false
        }
    }
     fun validatePaths(): Boolean {
        val paths = listOf(
            "databaseConnection.connectionString",
            "databaseConnection.username",
            "databaseConnection.password",
            "status.databaseInitialized",
            "constants.databaseInitCommand"
        )

        for (path in paths) {
            if (!validatePath(path)) return false
        }
        return true
    }

    private fun fetchPhases(plugin: CivMain) {
        val section = plugin.config.getConfigurationSection("phases.order")!!
        plugin.phases = section.getKeys(false)
    }

    companion object {
        fun ConfigurationSection.getTitle(path: String): Title {
            val section = this.getConfigurationSection(path)!!
            val top = section.getString("top")!!
            val bottom = section.getString("bottom")!!

            val fadeIn = section.getLong("fadeIn")
            val stay = section.getLong("stay")
            val fadeOut = section.getLong("fadeOut")

            return Title.title(
                Component.text(ChatColor.translateAlternateColorCodes('&', top)),
                Component.text(ChatColor.translateAlternateColorCodes('&', bottom)),
                Title.Times.times(
                    Duration.ofMillis(fadeIn),
                    Duration.ofMillis(stay),
                    Duration.ofMillis(fadeOut)
                )
            )
        }
    }
}