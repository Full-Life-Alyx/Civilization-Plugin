package me.dyncake.civilization

import com.google.common.base.Preconditions
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.attribute.Attribute
import org.bukkit.attribute.AttributeModifier
import org.bukkit.attribute.AttributeModifier.Operation
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.persistence.PersistentDataType
import java.util.*

object Items {

    val kingCrown: ItemStack = ItemStack(Material.GOLDEN_HELMET)
    val queenCrown: ItemStack = ItemStack(Material.GOLDEN_HELMET)
    val leaderCrown: ItemStack = ItemStack(Material.GOLDEN_HELMET)

    fun init(plugin: CivMain) {
        initLeaderCrown(plugin)
        initKingCrown(plugin)
        initQueenCrown(plugin)
    }

    private fun addCrownMeta(itemMeta: ItemMeta, plugin: CivMain) {
        itemMeta.persistentDataContainer[
            NamespacedKey(plugin, "item"),
            PersistentDataType.STRING
        ] = "crown"

        itemMeta.lore(
            listOf(
                Component.text(
                    "Legend says that only true rulers of civilizations can handle the magical properties from it",
                    ComponentColor.DARK_GRAY
                )
            )
        )

        //TODO figure out modifiers
        itemMeta.addAttributeModifier(Attribute.GENERIC_ARMOR, AttributeModifier(
            UUID.randomUUID(), "resistance", 3.0, Operation.ADD_NUMBER, EquipmentSlot.HEAD
        ))
        itemMeta.addAttributeModifier(Attribute.GENERIC_ARMOR_TOUGHNESS, AttributeModifier(
            UUID.randomUUID(),  "other_resistance", 2.0, Operation.ADD_NUMBER, EquipmentSlot.HEAD
        ))
        itemMeta.addAttributeModifier(Attribute.GENERIC_MAX_HEALTH, AttributeModifier(
            UUID.randomUUID(), "heath", 20.0, Operation.ADD_NUMBER, EquipmentSlot.HEAD
        ))

        itemMeta.isUnbreakable = true
    }

    private fun initKingCrown(plugin: CivMain) {
        val itemMeta = kingCrown.itemMeta
        Preconditions.checkNotNull(leaderCrown)

        addCrownMeta(itemMeta, plugin)

        itemMeta.displayName(
            Component.text(
                "King's Crown",
                ComponentColor.GOLD,
            )
                .decoration(TextDecoration.BOLD, true)
                .decoration(TextDecoration.ITALIC, false)
        )



        kingCrown.itemMeta = itemMeta
    }

    private fun initQueenCrown(plugin: CivMain) {
        val itemMeta = queenCrown.itemMeta
        Preconditions.checkNotNull(leaderCrown)

        addCrownMeta(itemMeta, plugin)


        itemMeta.displayName(
            Component.text(
                "Queen's Crown",
                ComponentColor.GOLD,
            )
                .decoration(TextDecoration.BOLD, true)
                .decoration(TextDecoration.ITALIC, false)
        )


        queenCrown.itemMeta = itemMeta
    }

    private fun initLeaderCrown(plugin: CivMain) {
        val itemMeta = leaderCrown.itemMeta
        Preconditions.checkNotNull(leaderCrown)

        addCrownMeta(itemMeta, plugin)

        itemMeta.displayName(
            Component.text(
                "Leader's Crown",
                ComponentColor.GOLD,
            )
                .decoration(TextDecoration.BOLD, true)
                .decoration(TextDecoration.ITALIC, false)
        )

        leaderCrown.itemMeta = itemMeta
    }


}