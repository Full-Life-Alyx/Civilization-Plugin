package me.dyncake.civilization.command.subcommand.civilization

import com.opraion.civilization.ExtUtils.toBytes
import com.opraion.civilization.ExtUtils.toUUID
import net.luckperms.api.LuckPerms
import net.luckperms.api.node.types.InheritanceNode
import java.sql.Connection
import java.sql.PreparedStatement
import java.util.*

object Synchronizer {

    fun syncTeam(plugin: me.dyncake.civilization.CivMain, lp: LuckPerms, targetList: List<UUID>) {

        val connection: Connection = plugin.connection

        val targets = HashMap<UUID, String?>()
        targetList.forEach {
            targets[it] = null
        }

        val ps: PreparedStatement = if (targets.size <= 1) {
            connection.prepareStatement(
                """
                    SELECT 
                    	player.uuid,
                    	team.`name`
                    FROM ${plugin.databaseName}.player 
                    JOIN ${plugin.databaseName}.team 
                    ON 
                    	player.team_uuid = team.uuid
                    WHERE player.uuid=?
                """.trimIndent()
            ).apply {
                setBytes(
                    1,
                    targets.keys.first().toBytes()
                )
            }

        } else {
            connection.prepareStatement(
                """
                    SELECT 
                    	player.uuid,
                    	team.`name`
                    FROM ${plugin.databaseName}.player 
                    JOIN ${plugin.databaseName}.team 
                    ON
                    	player.team_uuid = team.uuid
                """.trimIndent()
            )
        }

        val res = ps.executeQuery()
        while (res.next()) {
            targets[res.getBytes("uuid").toUUID()] = res.getString("name")
        }

        val teamsPs = connection.prepareStatement(
            """
                SELECT name FROM ${plugin.databaseName}.team
            """.trimIndent()
        )
        val teamsRes = teamsPs.executeQuery()

        val teams = ArrayList<String>()
        while (teamsRes.next())
            teams.add(teamsRes.getString("name"))

        println(targets)


        for ((target, team) in targets) {
            val user = lp.userManager.getUser(target)!!

            teams.forEach {
                user.data().remove(InheritanceNode.builder(it).build())
            }


            if (team != null) {
                val node = InheritanceNode.builder(team).value(true).build()
                user.data().add(node)
                // Bukkit.getPlayer(target)!!.sendMessage(result.toString())
                lp.userManager.saveUser(user)

            }


        }
    }

    fun syncKing(plugin: me.dyncake.civilization.CivMain, connection: Connection) {

        val ps = connection.prepareStatement(
            """
                SELECT king FROM ${plugin.databaseName}.team
            """.trimIndent()
        )
        val res = ps.executeQuery()

        val kings = plugin.kings
        kings.clear()
        while (res.next()) {
            val king = res.getBytes("king")
            kings.add(
                king?.toUUID() ?: continue

            )
        }
    }
}