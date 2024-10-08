package org.readutf.game.engine.event.impl

import org.readutf.game.engine.Game
import org.readutf.game.engine.event.Cancellable
import org.readutf.game.engine.event.GameEvent

class GameTeamAddEvent(
    game: Game<*>,
) : GameEvent(game),
    Cancellable {
    private var cancelled: Boolean = false

    override fun isCancelled(): Boolean = cancelled

    override fun setCancelled(cancelled: Boolean) {
        this.cancelled = cancelled
    }
}
