package dev.louiem.pandemicplayer.domain.entity.board

import dev.louiem.pandemicplayer.domain.entity.board.Location.Companion.MAXIMUM_CUBES
import dev.louiem.pandemicplayer.domain.valueobject.City
import dev.louiem.pandemicplayer.domain.valueobject.Colour
import dev.louiem.pandemicplayer.exception.GameException
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class LocationSpec {
    private var location = Location(City.Atlanta)

    @BeforeEach
    fun setup() {
        location = Location(City.Atlanta)
    }

    @Test
    fun `Infect increases the qty of cubes`() {
        Assertions.assertEquals(0, location.getQtyOfDiseaseCubes()[Colour.Red]!!)
        location.infect(Colour.Red, 1)
        Assertions.assertEquals(1, location.getQtyOfDiseaseCubes()[Colour.Red]!!)
    }

    @Test
    fun `Throws Game exception when putting more cubes than maximum`() {
        for(i in 0 until MAXIMUM_CUBES) {
            location.infect(Colour.Red, 1)
        }
        assertThrows<GameException> {
            location.infect(Colour.Red, 1)
        }
    }

    @Test
    fun `Throws Game exception when putting more cubes than maximum in one go`() {
        assertThrows<GameException> {
            location.infect(Colour.Red, 4)
        }
    }

    @Test
    fun `Can have cubes of multiple colours`() {
        for(colour in Colour.values()) {
            location.infect(colour, 2)
        }
        for(colour in Colour.values()) {
            Assertions.assertEquals(2, location.getQtyOfDiseaseCubes()[colour]!!)
        }
    }

    @Test
    fun `Treating disease removes cubes`() {
        for(colour in Colour.values()) {
            location.infect(colour, 2)
        }
        location.treat(Colour.Red, 1)

        Assertions.assertEquals(1, location.getQtyOfDiseaseCubes()[Colour.Red]!!)
        Assertions.assertEquals(2, location.getQtyOfDiseaseCubes()[Colour.Black]!!)
        Assertions.assertEquals(2, location.getQtyOfDiseaseCubes()[Colour.Yellow]!!)
        Assertions.assertEquals(2, location.getQtyOfDiseaseCubes()[Colour.Blue]!!)
    }

    @Test
    fun `Treating a location with no disease throws a GameException`() {
        assertThrows<GameException> { location.treat(Colour.Red, 1) }
    }

    @Test
    fun `Can add a research station`() {
        Assertions.assertEquals(false, location.hasResearchStation())
        location.addResearchStation()
        Assertions.assertEquals(true, location.hasResearchStation())
    }
}