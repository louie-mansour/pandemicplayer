package dev.louiem.pandemicplayer.domain.entity.board

import dev.louiem.pandemicplayer.exception.GameOverException
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class OutbreakCounterSpec {
    private var outbreakCounter: OutbreakCounter = OutbreakCounter()

    @BeforeEach
    fun setup() {
        outbreakCounter = OutbreakCounter()
    }

    @Test
    fun `throws GameException on Max outbreak`() {
        for(i in 1 until OutbreakCounter.GAME_OVER_VALUE) {
            outbreakCounter.increment()
            assertEquals(i, outbreakCounter.get())
        }
        assertThrows<GameOverException> {
            outbreakCounter.increment()
        }
    }
}