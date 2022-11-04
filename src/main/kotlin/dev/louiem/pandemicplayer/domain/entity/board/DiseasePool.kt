package dev.louiem.pandemicplayer.domain.entity.board

import dev.louiem.pandemicplayer.domain.valueobject.Colour
import dev.louiem.pandemicplayer.exception.GameException
import dev.louiem.pandemicplayer.exception.GameOverException

class DiseasePool {
    private var diseaseCubes = mutableMapOf(
            Colour.Blue to TOTAL_NUMBER_OF_CUBES,
            Colour.Yellow to TOTAL_NUMBER_OF_CUBES,
            Colour.Black to TOTAL_NUMBER_OF_CUBES,
            Colour.Red to TOTAL_NUMBER_OF_CUBES,
    )

    fun takeCubes(colour: Colour, qty: Int) {
        diseaseCubes[colour] = diseaseCubes[colour]!! - qty
        if(diseaseCubes[colour]!! < 0) {
            throw GameOverException("Ran our of $colour cubes")
        }
    }

    fun putCubesBack(colour: Colour, qty: Int) {
        diseaseCubes[colour] = diseaseCubes[colour]!! + qty
        if(diseaseCubes[colour]!! > TOTAL_NUMBER_OF_CUBES) {
            throw GameException("Too many cubes in pool")
        }
    }

    fun getCubes(): Map<Colour, Int> {
        return diseaseCubes.toMap()
    }

    companion object {
        const val TOTAL_NUMBER_OF_CUBES = 24
    }
}