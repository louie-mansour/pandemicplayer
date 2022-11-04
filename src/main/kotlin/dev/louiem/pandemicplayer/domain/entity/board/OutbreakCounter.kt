package dev.louiem.pandemicplayer.domain.entity.board

import dev.louiem.pandemicplayer.exception.GameOverException

class OutbreakCounter {
    private var count = 0

    fun increment() {
        count++
        if (count >= GAME_OVER_VALUE) {
            throw GameOverException("Too many outbreaks")
        }
    }

    fun get(): Int {
        return count
    }

    companion object {
        const val GAME_OVER_VALUE = 8
    }
}