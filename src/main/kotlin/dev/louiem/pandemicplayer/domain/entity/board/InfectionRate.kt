package dev.louiem.pandemicplayer.domain.entity.board

import dev.louiem.pandemicplayer.exception.GameException

class InfectionRate {
    private var numberOfEpidemics = 0

    fun increase() {
        numberOfEpidemics++
    }

    fun get(): Int {
        return RATES.getOrNull(numberOfEpidemics)?: throw GameException("Unexpected infection rate $numberOfEpidemics index of $RATES")
    }

    companion object {
        val RATES = listOf(2, 2, 2, 3, 3, 4, 4)
    }
}