package dev.louiem.pandemicplayer.domain.entity.board

import dev.louiem.pandemicplayer.domain.valueobject.Colour
import dev.louiem.pandemicplayer.exception.GameException
import dev.louiem.pandemicplayer.exception.GameOverException
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class DiseasePoolSpec {
    private var diseasePool: DiseasePool = DiseasePool()

    @BeforeEach
    fun setup() {
        diseasePool = DiseasePool()
    }

    @Test
    fun `throws GameOverException when cubes of a colour run out`() {
        for(i in 0 until DiseasePool.TOTAL_NUMBER_OF_CUBES) {
            Assertions.assertEquals(DiseasePool.TOTAL_NUMBER_OF_CUBES - i, diseasePool.getCubes()[Colour.Red])
            diseasePool.takeCubes(Colour.Red, 1)
        }

        assertThrows<GameOverException> {
            diseasePool.takeCubes(Colour.Red, 1)
        }
    }

    @Test
    fun `Can remove all cubes of all colours`() {
        for(colour in Colour.values()) {
            for (i in 0 until DiseasePool.TOTAL_NUMBER_OF_CUBES) {
                Assertions.assertEquals(DiseasePool.TOTAL_NUMBER_OF_CUBES - i, diseasePool.getCubes()[colour])
                diseasePool.takeCubes(colour, 1)
            }
        }
    }

    @Test
    fun `Can take multiple cubes at once`() {
        diseasePool.takeCubes(Colour.Red, 3)
        Assertions.assertEquals(DiseasePool.TOTAL_NUMBER_OF_CUBES - 3, diseasePool.getCubes()[Colour.Red])
    }

    @Test
    fun `Can put cubes back`() {
        diseasePool.takeCubes(Colour.Red, 3)
        diseasePool.putCubesBack(Colour.Red, 1)
        Assertions.assertEquals(DiseasePool.TOTAL_NUMBER_OF_CUBES - 2, diseasePool.getCubes()[Colour.Red])
    }

    @Test
    fun `Throws GameException when exceeding total number of cubes`() {
        assertThrows<GameException> {
            diseasePool.putCubesBack(Colour.Red, 1)
        }
    }
}