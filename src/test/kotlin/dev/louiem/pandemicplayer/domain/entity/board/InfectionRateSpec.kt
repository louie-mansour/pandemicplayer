package dev.louiem.pandemicplayer.domain.entity.board

import dev.louiem.pandemicplayer.exception.GameException
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class InfectionRateSpec {
    private var infectionRate: InfectionRate = InfectionRate()

    @BeforeEach
    fun setup() {
        infectionRate = InfectionRate()
    }

    @Test
    fun `throws GameException incrementing beyond max`() {
        for(i in 0..InfectionRate.RATES.size - 2) {
            assertEquals(InfectionRate.RATES[i],  infectionRate.get())
            infectionRate.increase()
        }

        assertThrows<GameException> {
            infectionRate.increase()
            infectionRate.get()
        }
    }
}