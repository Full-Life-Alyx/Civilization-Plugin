package me.dyncake.civilization.command.subcommand.civilization

import me.dyncake.civilization.ComponentColor
import me.dyncake.civilization.ExtUtils.toBytes
import me.dyncake.civilization.ExtUtils.toUUID
import me.dyncake.civilization.command.ParentCommand
import me.dyncake.civilization.command.subcommand.SubCommand
import me.dyncake.civilization.command.subcommand.SubCommandExecutor
import net.kyori.adventure.text.Component
import org.bukkit.command.CommandSender
import java.sql.ResultSet
import java.util.*

class SubCmdCivUpdate(private val plugin: me.dyncake.civilization.CivMain) : SubCommandExecutor {
    override fun onCommand(
        sender: CommandSender,
        subCommand: SubCommand,
        parentCommand: ParentCommand,
        args: Array<out String>,
    ): Boolean {

        val config = plugin.config
        val connection = plugin.connection

        val psFindTeams = connection.prepareStatement(
            """
                SELECT * FROM ${plugin.databaseName}.team
            """.trimIndent(),
            ResultSet.TYPE_SCROLL_SENSITIVE,
            ResultSet.CONCUR_UPDATABLE
        )
        val resFindTeams = psFindTeams.executeQuery()

        val teamUniqueIds = HashMap<String, UUID>()
        val original = ArrayList<String>()

        while (resFindTeams.next()) {
            val name = resFindTeams.getString("name")
            original.add(name)
            teamUniqueIds[name] = resFindTeams.getBytes("uuid").toUUID()
        }

        resFindTeams.first()

        val updated = config.getConfigurationSection("teams")!!.getKeys(false)

        //Config:
        //Database: Original

        //Boolean 1 = Original | Boolean 2 = New
        //Update: Config(has) Database(has) : true | true
        //Remove: Config(doesn't have) Database(has) : true | false
        //Add: Config(has) Database(doesn't have) : false | true
        //None: false | false (should be impossible)


        val map = HashMap<String, Pair<Boolean, Boolean>>()

        for (team in original) {
            map[team] = Pair(true, false)
        }
        for (team in updated) {
            if (map.contains(team)) {
                map[team] = Pair(map[team]?.first!!, true)
            } else {
                map[team] = Pair(false, true)
            }
        }


        val changes = ArrayList<Component>(map.size)
        for ((team, change) in map) {
            val name: Component = when (change) {

                Pair(true, true) -> {
                    continue
                }

                Pair(true, false) -> {
                    Component.text(
                        "Remove",
                        ComponentColor.RED
                    )
                }

                Pair(false, true) -> {
                    Component.text(
                        "Add",
                        ComponentColor.GREEN
                    )
                }

                else -> {
                    Component.text(
                        "Impossible"
                    )
                }
            }

            changes.add(
                Component.text(
                    "$team - "
                ).append(
                    name
                ).append(
                    Component.text("\n")
                )
            )
        }



        if (args.size > 1) {
            if (args[1] == "confirm") {

                for ((team, change) in map) {
                    when (change) {

                        //remove
                        Pair(true, false) -> {

                            connection.prepareStatement(
                                """
                                    DELETE FROM ${plugin.databaseName}.team uuid=?
                                """.trimIndent()
                            ).apply {
                                setBytes(1, teamUniqueIds[team]!!.toBytes())
                                execute()
                            }

                        }

                        //add
                        Pair(false, true) -> {

                            connection.prepareStatement(
                                """
                                    INSERT INTO ${plugin.databaseName}.team (uuid, name, king, friendly_fire) VALUES (?, ?, null, false)
                                """.trimIndent()
                            ).apply {
                                setBytes(1, UUID.randomUUID().toBytes())
                                setString(2, team)
                                execute()
                            }

                        }

                        //update
                        /*
                        Pair(true, true) -> {}
                        */
                    }

                    sender.sendMessage(
                        Component.text(
                            "Successfully updated the database!",
                            ComponentColor.GREEN
                        )
                    )
                }

            }

        }

        sender.sendMessage(
            //No idea what to do
            me.dyncake.civilization.Component.fromArray(changes).append(
                Component.text(
                    "Type \"/civilization update confirm\" to confirm these changes"
                )
            )
        )
        return true

    }
}

