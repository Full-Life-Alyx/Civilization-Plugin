package me.dyncake.civilization

import org.bukkit.event.Event
import org.bukkit.event.HandlerList

class PhaseMoveEvent(val events: Set<String>, val oldValue: Int, val newValue: Int) : Event() {

    val handlersList: HandlerList = HandlerList()

    override fun getHandlers(): HandlerList {
        return handlersList
    }

}