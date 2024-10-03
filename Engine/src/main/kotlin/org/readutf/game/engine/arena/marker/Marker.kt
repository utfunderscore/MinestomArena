package org.readutf.game.engine.arena.marker

import net.minestom.server.coordinate.Vec

data class Marker(
    val position: Vec,
    val originalPosition: Vec,
    val markerLines: Array<String> = Array(4) { "" },
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Marker

        if (position != other.position) return false
        if (originalPosition != other.originalPosition) return false
        if (!markerLines.contentEquals(other.markerLines)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = position.hashCode()
        result = 31 * result + originalPosition.hashCode()
        result = 31 * result + markerLines.contentHashCode()
        return result
    }
}
