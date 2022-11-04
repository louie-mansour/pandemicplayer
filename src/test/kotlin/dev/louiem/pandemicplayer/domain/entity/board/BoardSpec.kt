package dev.louiem.pandemicplayer.domain.entity.board

import dev.louiem.pandemicplayer.domain.entity.board.diseasedeck.DiseaseCard
import dev.louiem.pandemicplayer.domain.entity.board.diseasedeck.DiseaseDeck
import dev.louiem.pandemicplayer.domain.valueobject.City
import dev.louiem.pandemicplayer.domain.valueobject.Colour
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class BoardSpec {
    private lateinit var locationMap: Map<City, Location>
    private lateinit var diseaseDeck: DiseaseDeck
    private lateinit var infectionRate: InfectionRate
    private lateinit var outbreakCounter: OutbreakCounter
    private lateinit var diseasePool: DiseasePool
    private lateinit var board: Board

    @BeforeEach
    fun setup() {
        locationMap = Location.locationMapSingleton().toMutableMap()
        diseaseDeck = mockk()
        infectionRate = InfectionRate()
        outbreakCounter = OutbreakCounter()
        diseasePool = DiseasePool()
    }

    @Test
    fun `Setting up the board infects cities`() {
        val hoChiMinhCity = City.HoChiMinhCity
        val manila = City.Manila
        val atlanta = City.Atlanta
        val tehran = City.Tehran
        val moscow = City.Moscow
        val baghdad = City.Baghdad
        val buenosAires = City.BuenosAires
        val mumbai = City.Mumbai
        val paris = City.Paris

        locationMap = locationMap
                .map { it.key to mockk<Location>() }
                .toMap()

        locationMap.values.forEach {
            every { it.getQtyOfDiseaseCubes() } answers { mapOf(Colour.Red to 0, Colour.Black to 0, Colour.Yellow to 0, Colour.Blue to 0) }
            every { it.infect(any(), any()) } returns Unit
        }
        locationMap.values.forEach { every { it.getQtyOfDiseaseCubes() } answers { mapOf(Colour.Red to 0, Colour.Black to 0, Colour.Yellow to 0, Colour.Blue to 0) } }

        board = Board(locationMap, diseaseDeck, infectionRate, outbreakCounter, diseasePool)
        every { diseaseDeck.getTop() } returns
                DiseaseCard(hoChiMinhCity) andThen
                DiseaseCard(manila) andThen
                DiseaseCard(atlanta) andThen

                DiseaseCard(tehran) andThen
                DiseaseCard(moscow) andThen
                DiseaseCard(baghdad) andThen

                DiseaseCard(buenosAires) andThen
                DiseaseCard(mumbai) andThen
                DiseaseCard(paris)

        board.setup()

        verify { locationMap[hoChiMinhCity]!!.infect(hoChiMinhCity.colour, 3) }
        verify { locationMap[manila]!!.infect(manila.colour, 3) }
        verify { locationMap[atlanta]!!.infect(atlanta.colour, 3) }
        verify { locationMap[tehran]!!.infect(tehran.colour, 2) }
        verify { locationMap[moscow]!!.infect(moscow.colour, 2) }
        verify { locationMap[baghdad]!!.infect(baghdad.colour, 2) }
        verify { locationMap[buenosAires]!!.infect(buenosAires.colour, 1) }
        verify { locationMap[mumbai]!!.infect(mumbai.colour, 1) }
        verify { locationMap[paris]!!.infect(paris.colour, 1) }
    }
}