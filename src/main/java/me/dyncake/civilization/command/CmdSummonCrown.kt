package me.dyncake.civilization.command

import me.dyncake.civilization.ComponentColor
import me.dyncake.civilization.Items
import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.LightningStrike
import org.bukkit.entity.Player
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack

class CmdSummonCrown(private val plugin: me.dyncake.civilization.CivMain) : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {

        if (sender !is Player) {
            sender.sendMessage(
                Component.text(
                    "Only players can execute this command!",
                    ComponentColor.RED
                )
            )
            return true
        }

        if (!plugin.kings.contains(sender.uniqueId)) {
            sender.sendMessage(
                Component.text(
                    """
                        Only leaders can execute this command!
                        If you believe that you are a king, please tell staff the code 101
                    """.trimIndent(),
                    ComponentColor.RED
                )
            )
            return true
        }

        val inventory = sender.inventory
        if (inventory.itemInMainHand.type != Material.GOLDEN_HELMET) {
            sender.sendMessage(
                Component.text(
                    "You must be holding a golden helmet to create a crown!",
                    ComponentColor.RED
                )
            )
            return true
        }


        if (args.isNotEmpty()) {

            val crown: ItemStack = when (args[0]) {
                "king" -> {
                    Items.kingCrown
                }
                "queen" -> {
                    Items.queenCrown
                }
                "leader" -> {
                    Items.leaderCrown
                }
                else -> {
                    sender.sendMessage(
                        Component.text(
                            "Crown type not found!",
                            ComponentColor.RED
                        )
                    )
                    return true
                }
            }
            inventory.setItem(EquipmentSlot.HAND, crown)

            sender.world.spawn(sender.location, LightningStrike::class.java)

        } else {
            sender.sendMessage(
                Component.textOfChildren(
                    Component.text(
                        "Please specify a crown type!\n",
                        ComponentColor.RED
                    ),
                    Component.text(
                        """
                        To change the crown type, use one of the three arguments:
                            king, queen, leader
                        (When you summon your crown, lightning will strike, 
                        but you are immune to lightning so that is probably ok)
                        
                    """.trimIndent(),
                        ComponentColor.GOLD
                    ),
                    Component.text(
                        "Note that these are strictly cosmetic changes and bring no other benefits",
                        ComponentColor.DARK_GRAY
                    )
                )
            )
            return true
        }



        return true
    }
}