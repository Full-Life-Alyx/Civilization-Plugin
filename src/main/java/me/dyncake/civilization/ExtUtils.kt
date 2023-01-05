package me.dyncake.civilization

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextComponent
import java.nio.ByteBuffer
import java.util.*

object ExtUtils {
    fun UUID.toBytes(): ByteArray {
        val bb = ByteBuffer.wrap(ByteArray(16))
        bb.putLong(this.mostSignificantBits)
        bb.putLong(this.leastSignificantBits)
        return bb.array()
    }

    fun ByteArray.toUUID(): UUID {
        val bb = ByteBuffer.wrap(this)
        val high = bb.long
        val low = bb.long
        return UUID(high, low)
    }
    //find a better way to do this



}
object Component {
    fun fromArray(arr: ArrayList<Component>): TextComponent {
        var outComponent = Component.empty()
        for (component in arr) {
            outComponent = outComponent.append(
                component
            )
        }
        return outComponent
    }
}
