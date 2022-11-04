package dev.louiem.pandemicplayer.domain.entity.player

import dev.louiem.pandemicplayer.domain.entity.Game
import dev.louiem.pandemicplayer.domain.entity.TheIntelligence
import dev.louiem.pandemicplayer.domain.entity.board.DiseasePool
import dev.louiem.pandemicplayer.domain.entity.board.Location
import dev.louiem.pandemicplayer.domain.entity.board.ResearchStationPool
import dev.louiem.pandemicplayer.domain.entity.player.playerdeck.CityCard
import dev.louiem.pandemicplayer.domain.entity.player.playerdeck.EpidemicCard
import dev.louiem.pandemicplayer.domain.entity.player.playerdeck.PlayerDeck
import dev.louiem.pandemicplayer.domain.valueobject.City
import dev.louiem.pandemicplayer.domain.valueobject.Colour
import dev.louiem.pandemicplayer.exception.EpidemicException
import dev.louiem.pandemicplayer.exception.GameException
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class PawnSpec {
    private lateinit var location: Location
    private lateinit var playerDeck: PlayerDeck
    private var startingHandSize = 4
    private lateinit var diseasePool: DiseasePool
    private lateinit var researchStationPool: ResearchStationPool
    private lateinit var theIntelligence: TheIntelligence
    private lateinit var pawn: Pawn

    @BeforeEach
    fun setup() {
        location = mockk()
        playerDeck = mockk()
        startingHandSize = 4
        diseasePool = mockk()
        researchStationPool = mockk()
        theIntelligence = mockk()
        pawn = Pawn(location, playerDeck, startingHandSize, diseasePool, researchStationPool, theIntelligence)
        Colour.Black.isCured = false
        Colour.Black.isErradicated = false
        Colour.Blue.isCured = false
        Colour.Blue.isErradicated = false
        Colour.Red.isCured = false
        Colour.Red.isErradicated = false
        Colour.Yellow.isCured = false
        Colour.Yellow.isErradicated = false
    }

    @Test
    fun `dealHand adds card to the player's hand`() {
        every { playerDeck.drawCard() } returns
                CityCard(City.HoChiMinhCity) andThen CityCard(City.Algiers) andThen
                CityCard(City.Shanghai) andThen CityCard(City.Kolkata)

        pawn.dealHand()

        Assertions.assertEquals(4, pawn.getHand().size)
    }

    @Test
    fun `dealHand throws exception is an epidemic card is dealt`() {
        every { playerDeck.drawCard() } returns
                CityCard(City.HoChiMinhCity) andThen CityCard(City.Algiers)andThen
                CityCard(City.Shanghai) andThen EpidemicCard()

        assertThrows<GameException> { pawn.dealHand() }
    }

    @Test
    fun `turnEnd the player draws 2 cards and adds them to their hand`() {
        every { playerDeck.drawCard() } returns
                CityCard(City.HoChiMinhCity) andThen CityCard(City.Algiers) andThen
                CityCard(City.Shanghai) andThen CityCard(City.Kolkata) andThen
                CityCard(City.BuenosAires) andThen CityCard(City.Sydney)

        pawn.turnEnd()

        Assertions.assertEquals(2, pawn.getHand().size)
        verify(exactly = 2) { playerDeck.drawCard() }
    }

    @Test
    fun `turnEnd player discards cards when more than 7 cards at turn end`() {
        val hoChiMinCard = CityCard(City.HoChiMinhCity)
        every { theIntelligence.whichCardsToDiscard(any()) } returns listOf(hoChiMinCard)
        every { playerDeck.discard(any()) } returns Unit
        every { playerDeck.drawCard() } returns
                hoChiMinCard andThen CityCard(City.Algiers) andThen
                CityCard(City.Shanghai) andThen CityCard(City.Kolkata) andThen
                CityCard(City.BuenosAires) andThen CityCard(City.Sydney) andThen
                CityCard(City.Istanbul) andThen CityCard(City.MexicoCity)

        pawn.turnEnd()
        pawn.turnEnd()
        pawn.turnEnd()
        pawn.turnEnd()

        Assertions.assertEquals(7, pawn.getHand().size)
        verify(exactly = 8) { playerDeck.drawCard() }
        verify(exactly = 1) { theIntelligence.whichCardsToDiscard(any()) }
        verify(exactly = 1) { playerDeck.discard(any()) }
    }

    @Test
    fun `turnEnd throws EpidemicException when an epidemic is encountered`() {
        every { playerDeck.discard(any()) } returns Unit
        every { playerDeck.drawCard() } returns
                EpidemicCard() andThen CityCard(City.HoChiMinhCity)

        val epidemic = assertThrows<EpidemicException> { pawn.turnEnd() }
        Assertions.assertEquals(1, epidemic.qty)
        verify(exactly = 2) { playerDeck.drawCard() }
        verify(exactly = 1) { playerDeck.discard(any()) }
    }

    @Test
    fun `turnEnd throws EpidemicException when two epidemics are encountered`() {
        every { playerDeck.discard(any()) } returns Unit
        every { playerDeck.drawCard() } returns
                EpidemicCard() andThen EpidemicCard()

        val epidemic = assertThrows<EpidemicException> { pawn.turnEnd() }
        Assertions.assertEquals(2, epidemic.qty)
        verify(exactly = 2) { playerDeck.drawCard() }
        verify(exactly = 2) { playerDeck.discard(any()) }
    }

    @Test
    fun `driveFerry action success`() {
        val algiers = Location(City.Algiers)
        every { location.getConnectedLocations() } returns setOf(algiers)
        val action = DriveFerry(algiers)

        pawn.executeActions(listOf(action))

        Assertions.assertEquals(algiers, pawn.getLocation())
    }

    @Test
    fun `driveFerry to a non-connected location`() {
        val algiers = Location(City.Algiers)
        val kolkata = Location(City.Kolkata)
        every { location.getConnectedLocations() } returns setOf(algiers)
        val action = DriveFerry(kolkata)

        assertThrows<GameException> { pawn.executeActions(listOf(action)) }
    }

    @Test
    fun `directFlight action success`() {
        every { playerDeck.discard(any())} returns Unit
        val algiersCard = CityCard(City.Algiers)
        every { playerDeck.drawCard() } returns
                CityCard(City.HoChiMinhCity) andThen algiersCard andThen
                CityCard(City.Shanghai) andThen CityCard(City.Kolkata)
        val algiersLocation = Location(City.Algiers)
        pawn.dealHand()

        pawn.executeActions(listOf(DirectFlight(algiersLocation, algiersCard)))

        Assertions.assertEquals(algiersLocation, pawn.getLocation())
    }

    @Test
    fun `directFlight suggestion with the wrong card`() {
        every { playerDeck.discard(any())} returns Unit
        val algiersCard = CityCard(City.Algiers)
        val kolkataCard = CityCard(City.Kolkata)
        every { playerDeck.drawCard() } returns
                CityCard(City.HoChiMinhCity) andThen algiersCard andThen
                CityCard(City.Shanghai) andThen CityCard(City.Kolkata)
        val algiersLocation = Location(City.Algiers)
        pawn.dealHand()

        assertThrows<GameException> { pawn.executeActions(listOf(DirectFlight(algiersLocation, kolkataCard))) }
    }

    @Test
    fun `directFlight suggestion without the card in hand`() {
        every { playerDeck.discard(any())} returns Unit
        val algiersCard = CityCard(City.Algiers)
        every { playerDeck.drawCard() } returns
                CityCard(City.HoChiMinhCity) andThen algiersCard andThen
                CityCard(City.Shanghai) andThen CityCard(City.Kolkata)
        pawn.dealHand()

        assertThrows<GameException> { pawn.executeActions(listOf(DirectFlight(Location(City.London), CityCard(City.London)))) }
    }

    @Test
    fun `charteredFlight action success`() {
        every { playerDeck.discard(any())} returns Unit
        val algiersCard = CityCard(City.Algiers)
        every { playerDeck.drawCard() } returns
                CityCard(City.HoChiMinhCity) andThen algiersCard andThen
                CityCard(City.Shanghai) andThen CityCard(City.Kolkata)
        every { location.city } returns City.Algiers
        val jakartaLocation = Location(City.Jakarta)
        pawn.dealHand()
        pawn.executeActions(listOf(CharteredFlight(jakartaLocation, algiersCard)))

        Assertions.assertEquals(jakartaLocation, pawn.getLocation())
    }

    @Test
    fun `charteredFlight with wrong card`() {
        every { playerDeck.discard(any())} returns Unit
        val algiersCard = CityCard(City.Algiers)
        every { playerDeck.drawCard() } returns
                CityCard(City.HoChiMinhCity) andThen algiersCard andThen
                CityCard(City.Shanghai) andThen CityCard(City.Kolkata)
        every { location.city } returns City.Algiers
        val jakartaLocation = Location(City.Jakarta)
        pawn.dealHand()

        assertThrows<GameException> { pawn.executeActions(listOf(CharteredFlight(jakartaLocation, CityCard(City.Shanghai)))) }
    }

    @Test
    fun `charteredFlight without card in hand`() {
        every { playerDeck.discard(any())} returns Unit
        val algiersCard = CityCard(City.Algiers)
        every { playerDeck.drawCard() } returns
                CityCard(City.HoChiMinhCity) andThen algiersCard andThen
                CityCard(City.Shanghai) andThen CityCard(City.Kolkata)
        every { location.city } returns City.Algiers
        pawn.dealHand()

        assertThrows<GameException> { pawn.executeActions(listOf(CharteredFlight(Location(City.Bogota), CityCard(City.Bogota)))) }
    }

    @Test
    fun `shuttleFlight action success`() {
        every { playerDeck.discard(any())} returns Unit
        val algiersCard = CityCard(City.Algiers)
        every { playerDeck.drawCard() } returns
                CityCard(City.HoChiMinhCity) andThen algiersCard andThen
                CityCard(City.Shanghai) andThen CityCard(City.Kolkata)
        every { location.city } returns City.Algiers
        every { location.hasResearchStation() } returns true
        val jakartaLocation = Location(City.Jakarta)
        jakartaLocation.addResearchStation()

        pawn.dealHand()
        pawn.executeActions(listOf(ShuttleFlight(jakartaLocation)))

        Assertions.assertEquals(jakartaLocation, pawn.getLocation())
    }

    @Test
    fun `shuttleFlight suggestion when starting location has no research station`() {
        every { playerDeck.discard(any())} returns Unit
        val algiersCard = CityCard(City.Algiers)
        every { playerDeck.drawCard() } returns
                CityCard(City.HoChiMinhCity) andThen algiersCard andThen
                CityCard(City.Shanghai) andThen CityCard(City.Kolkata)
        every { location.city } returns City.Algiers
        every { location.hasResearchStation() } returns false
        val jakartaLocation = Location(City.Jakarta)
        jakartaLocation.addResearchStation()

        pawn.dealHand()
        assertThrows<GameException> { pawn.executeActions(listOf(ShuttleFlight(jakartaLocation))) }
    }

    @Test
    fun `shuttleFlight suggestion when target location has no research station`() {
        every { playerDeck.discard(any())} returns Unit
        val algiersCard = CityCard(City.Algiers)
        every { playerDeck.drawCard() } returns
                CityCard(City.HoChiMinhCity) andThen algiersCard andThen
                CityCard(City.Shanghai) andThen CityCard(City.Kolkata)
        every { location.city } returns City.Algiers
        every { location.hasResearchStation() } returns true
        val jakartaLocation = Location(City.Jakarta)

        pawn.dealHand()
        assertThrows<GameException> { pawn.executeActions(listOf(ShuttleFlight(jakartaLocation))) }
    }

    @Test
    fun `buildResearchStation action success`() {
        every { playerDeck.discard(any())} returns Unit
        val algiersCard = CityCard(City.Algiers)
        every { playerDeck.drawCard() } returns
                CityCard(City.HoChiMinhCity) andThen algiersCard andThen
                CityCard(City.Shanghai) andThen CityCard(City.Kolkata)
        every { location.city } returns City.Algiers
        every { researchStationPool.takeResearchStation() } returns Unit
        every { location.hasResearchStation() } returns false
        every { location.addResearchStation() } returns Unit

        pawn.dealHand()
        pawn.executeActions(listOf(BuildResearchStation(algiersCard)))

        verify(exactly = 1) { location.addResearchStation() }
        verify(exactly = 1) { researchStationPool.takeResearchStation() }
    }

    @Test
    fun `buildResearchStation when location already has a research station`() {
        every { playerDeck.discard(any())} returns Unit
        val algiersCard = CityCard(City.Algiers)
        every { playerDeck.drawCard() } returns
                CityCard(City.HoChiMinhCity) andThen algiersCard andThen
                CityCard(City.Shanghai) andThen CityCard(City.Kolkata)
        every { location.city } returns City.Algiers
        every { researchStationPool.takeResearchStation() } returns Unit
        every { location.hasResearchStation() } returns true
        every { location.addResearchStation() } returns Unit

        pawn.dealHand()
        assertThrows<GameException> { pawn.executeActions(listOf(BuildResearchStation(algiersCard))) }
    }

    @Test
    fun `buildResearchStation suggestion with wrong card`() {
        every { playerDeck.discard(any())} returns Unit
        val algiersCard = CityCard(City.Algiers)
        every { playerDeck.drawCard() } returns
                CityCard(City.HoChiMinhCity) andThen algiersCard andThen
                CityCard(City.Shanghai) andThen CityCard(City.Kolkata)
        every { location.city } returns City.Algiers
        every { researchStationPool.takeResearchStation() } returns Unit
        every { location.hasResearchStation() } returns true
        every { location.addResearchStation() } returns Unit

        pawn.dealHand()
        assertThrows<GameException> { pawn.executeActions(listOf(BuildResearchStation(CityCard(City.HoChiMinhCity)))) }
    }

    @Test
    fun `buildResearchStation suggestion when the card isn't in the player's hand`() {
        every { playerDeck.discard(any())} returns Unit
        val algiersCard = CityCard(City.Algiers)
        every { playerDeck.drawCard() } returns
                CityCard(City.HoChiMinhCity) andThen algiersCard andThen
                CityCard(City.Shanghai) andThen CityCard(City.Kolkata)
        every { location.city } returns City.Algiers
        every { researchStationPool.takeResearchStation() } returns Unit
        every { location.hasResearchStation() } returns true
        every { location.addResearchStation() } returns Unit

        pawn.dealHand()
        assertThrows<GameException> { pawn.executeActions(listOf(BuildResearchStation(CityCard(City.Paris)))) }
    }

    @Test
    fun `treatDisease action success`() {
        every { playerDeck.discard(any())} returns Unit
        val algiersCard = CityCard(City.Algiers)
        every { playerDeck.drawCard() } returns
                CityCard(City.HoChiMinhCity) andThen algiersCard andThen
                CityCard(City.Shanghai) andThen CityCard(City.Kolkata)
        every { location.city } returns City.Algiers
        every { location.getQtyOfDiseaseCubes() } returns mapOf(Colour.Black to 2)
        every { location.treat(any(), any()) } returns Unit
        every { diseasePool.putCubesBack(any(), any()) } returns Unit

        pawn.dealHand()
        pawn.executeActions(listOf(TreatDisease(Colour.Black)))

        verify(exactly = 1) { location.treat(Colour.Black, 1) }
        verify(exactly = 1) { diseasePool.putCubesBack(Colour.Black, 1) }
    }

    @Test
    fun `treatDisease action success for cured disease`() {
        every { playerDeck.discard(any())} returns Unit
        val algiersCard = CityCard(City.Algiers)
        every { playerDeck.drawCard() } returns
                CityCard(City.HoChiMinhCity) andThen algiersCard andThen
                CityCard(City.Shanghai) andThen CityCard(City.Kolkata)
        every { location.city } returns City.Algiers
        every { location.city } returns City.Algiers
        every { location.getQtyOfDiseaseCubes() } returns mapOf(Colour.Black to 2)
        every { location.treat(any(), any()) } returns Unit
        every { diseasePool.putCubesBack(any(), any()) } returns Unit
        Colour.Black.isCured = true


        pawn.dealHand()
        pawn.executeActions(listOf(TreatDisease(Colour.Black)))

        verify(exactly = 1) { location.treat(Colour.Black, 2) }
        verify(exactly = 1) { diseasePool.putCubesBack(Colour.Black, 2) }
    }

    @Test
    fun `treatDisease when there are no cubes to treat`() {
        every { playerDeck.discard(any())} returns Unit
        val algiersCard = CityCard(City.Algiers)
        every { playerDeck.drawCard() } returns
                CityCard(City.HoChiMinhCity) andThen algiersCard andThen
                CityCard(City.Shanghai) andThen CityCard(City.Kolkata)
        every { location.city } returns City.Algiers
        every { location.getQtyOfDiseaseCubes() } returns mapOf(Colour.Black to 0)
        every { location.treat(any(), any()) } returns Unit
        every { diseasePool.putCubesBack(any(), any()) } returns Unit

        pawn.dealHand()
        assertThrows<GameException> { pawn.executeActions(listOf(TreatDisease(Colour.Black))) }
    }

    @Test
    fun `discoverCure action success`() {
        every { playerDeck.discard(any())} returns Unit
        val kolkataCard = CityCard(City.Kolkata)
        val algiersCard = CityCard(City.Algiers)
        val cairoCard = CityCard(City.Cairo)
        val riyadhCard = CityCard(City.Riyadh)
        val istanbulCard = CityCard(City.Istanbul)
        every { playerDeck.drawCard() } returns
                kolkataCard andThen algiersCard andThen
                cairoCard andThen riyadhCard andThen
                istanbulCard andThen CityCard(City.Mumbai)

        every { location.city } returns City.Algiers
        every { location.hasResearchStation() } returns true

        pawn.dealHand()
        pawn.turnEnd()
        pawn.executeActions(listOf(DiscoverCure(Colour.Black, setOf(kolkataCard, algiersCard, cairoCard, riyadhCard, istanbulCard))))

        verify(exactly = 1) { playerDeck.discard(kolkataCard) }
        verify(exactly = 1) { playerDeck.discard(algiersCard) }
        verify(exactly = 1) { playerDeck.discard(cairoCard) }
        verify(exactly = 1) { playerDeck.discard(riyadhCard) }
        verify(exactly = 1) { playerDeck.discard(istanbulCard) }
        Assertions.assertTrue(Colour.Black.isCured)
    }

    @Test
    fun `discoverCure with the wrong number of cards`() {
        every { playerDeck.discard(any())} returns Unit
        val kolkataCard = CityCard(City.Kolkata)
        val algiersCard = CityCard(City.Algiers)
        val cairoCard = CityCard(City.Cairo)
        val riyadhCard = CityCard(City.Riyadh)
        val istanbulCard = CityCard(City.Istanbul)
        every { playerDeck.drawCard() } returns
                kolkataCard andThen algiersCard andThen
                cairoCard andThen riyadhCard andThen
                istanbulCard andThen CityCard(City.Mumbai)

        every { location.city } returns City.Algiers
        every { location.hasResearchStation() } returns true

        pawn.dealHand()
        pawn.turnEnd()

        assertThrows<GameException> { pawn.executeActions(listOf(DiscoverCure(Colour.Black, setOf(kolkataCard, algiersCard, cairoCard, riyadhCard)))) }
    }

    @Test
    fun `discoverCure while not at a research station`() {
        every { playerDeck.discard(any())} returns Unit
        val kolkataCard = CityCard(City.Kolkata)
        val algiersCard = CityCard(City.Algiers)
        val cairoCard = CityCard(City.Cairo)
        val riyadhCard = CityCard(City.Riyadh)
        val istanbulCard = CityCard(City.Istanbul)
        every { playerDeck.drawCard() } returns
                kolkataCard andThen algiersCard andThen
                cairoCard andThen riyadhCard andThen
                istanbulCard andThen CityCard(City.Mumbai)

        every { location.city } returns City.Algiers
        every { location.hasResearchStation() } returns false

        pawn.dealHand()
        pawn.turnEnd()

        assertThrows<GameException> { pawn.executeActions(listOf(DiscoverCure(Colour.Black, setOf(kolkataCard, algiersCard, cairoCard, riyadhCard, istanbulCard)))) }
    }

    @Test
    fun `discoverCure suggestion when the cards aren't in the player's hand`() {
        every { playerDeck.discard(any())} returns Unit
        val kolkataCard = CityCard(City.Kolkata)
        val algiersCard = CityCard(City.Algiers)
        val cairoCard = CityCard(City.Cairo)
        val riyadhCard = CityCard(City.Riyadh)
        val istanbulCard = CityCard(City.Istanbul)
        every { playerDeck.drawCard() } returns
                kolkataCard andThen algiersCard andThen
                cairoCard andThen riyadhCard andThen
                istanbulCard andThen CityCard(City.Mumbai)

        every { location.city } returns City.Algiers
        every { location.hasResearchStation() } returns true

        pawn.dealHand()
        pawn.turnEnd()

        assertThrows<GameException> { pawn.executeActions(listOf(DiscoverCure(Colour.Black, setOf(kolkataCard, algiersCard, cairoCard, riyadhCard, CityCard(City.Moscow))))) }
    }

    @Test
    fun `shareKnowledgeTake action success`() {
    }

    @Test
    fun `shareKnowledgeTake while not in the same city as the pawn you are sharing with`() {
    }

    @Test
    fun `shareKnowledgeTake while not sharing a card that matches your city`() {
    }

    @Test
    fun `shareKnowledgeTake while other player doesn't have the card`() {
    }

    @Test
    fun `shareKnowledgeGive action success`() {
    }

    @Test
    fun `shareKnowledgeGive while not in the same city as the pawn you are sharing with`() {
    }

    @Test
    fun `shareKnowledgeGive while not sharing a card that matches your city`() {
    }

    @Test
    fun `shareKnowledgeGive while other player doesn't have the card`() {
    }
}