package dev.louiem.pandemicplayer.domain.entity.player

import dev.louiem.pandemicplayer.domain.entity.TheIntelligence
import dev.louiem.pandemicplayer.domain.entity.board.DiseasePool
import dev.louiem.pandemicplayer.domain.entity.board.Location
import dev.louiem.pandemicplayer.domain.entity.board.ResearchStationPool
import dev.louiem.pandemicplayer.domain.entity.player.playerdeck.*
import dev.louiem.pandemicplayer.domain.valueobject.Colour
import dev.louiem.pandemicplayer.exception.EpidemicException
import dev.louiem.pandemicplayer.exception.GameException

class Pawn(
        private var location: Location,
        private val playerDeck: PlayerDeck,
        private val startingHandSize: Int,
        private val diseasePool: DiseasePool,
        private val researchStationPool: ResearchStationPool,
        private val theIntelligence: TheIntelligence
) {
    private var hand = mutableSetOf<PlayableCard>()

    fun executeActions(actions: List<ActionCommand>) {
        if(actions.size > 4) {
            throw GameException("Attempt to take more than 4 actions in a single turn")
        }
        for (action in actions) {
            when (action) {
                is DriveFerry -> driveFerry(action.location)
                is DirectFlight -> directFlight(action.location, action.cityCard)
                is CharteredFlight -> charteredFlight(action.location, action.cityCard)
                is ShuttleFlight -> shuttleFlight(action.location)
                is BuildResearchStation -> buildResearchStation(action.cityCard)
                is TreatDisease -> treatDisease(action.colour)
                is DiscoverCure -> discoverCure(action.colour, action.cityCards)
                is ShareKnowledgeGive -> shareKnowledgeGive(action.pawn, action.cityCard)
                is ShareKnowledgeTake -> shareKnowledgeTake(action.pawn, action.cityCard)
            }
        }
    }

    fun turnEnd() {
        val drawnCards = listOf(playerDeck.drawCard(), playerDeck.drawCard())
        var qtyEpidemics = 0
        drawnCards.forEach {
            when(it) {
                is PlayableCard -> hand.add(it)
                is EpidemicCard -> {
                    qtyEpidemics++
                    playerDeck.discard(it)
                }
            }
        }
        while (hand.size > 7) {
            theIntelligence.whichCardsToDiscard(this).map { playCard(it) }
        }
        if(qtyEpidemics > 0) {
            throw EpidemicException(qtyEpidemics)
        }
    }

    fun dealHand() {
        val cards = List(startingHandSize) { playerDeck.drawCard() }
        cards.forEach {
            when(it) {
                is PlayableCard -> hand.add(it)
                is EpidemicCard -> throw GameException("Dealt non-playable cards such as Epidemic cards")
            }
        }
    }

    fun getHand(): MutableSet<PlayableCard> {
        return hand
    }

    fun getLocation(): Location {
        return location
    }

    private fun driveFerry(location: Location) {
        if(!this.location.getConnectedLocations().contains(location)) {
            throw GameException("Attempt to drive to a non connected location. ${this.location} to $location")
        }
        this.location = location
    }

    private fun directFlight(location: Location, cityCard: CityCard) {
        if(location.city != cityCard.city) {
            throw GameException("Attempt to take direct flight to location without card. ${this.location} to $location")
        }
        playCard(cityCard)
        this.location = location
    }

    private fun charteredFlight(location: Location, cityCard: CityCard) {
        if(this.location.city != cityCard.city) {
            throw GameException("Attempt to take chartered flight to location without card. ${this.location} to $location")
        }
        playCard(cityCard)
        this.location = location
    }

    private fun shuttleFlight(location: Location) {
        if(!this.location.hasResearchStation()) {
            throw GameException("Attempt to shuttle flight from a location without a research station. ${this.location}")
        }

        if(!location.hasResearchStation()) {
            throw GameException("Attempt to shuttle flight to a location without a research station. $location")
        }

        this.location = location
    }

    private fun buildResearchStation(cityCard: CityCard) {
        if(this.location.city != cityCard.city) {
            throw GameException("Attempt to build research station without card. ${this.location}")
        }

        if(this.location.hasResearchStation()) {
            throw GameException("This location already has a research station. ${this.location}")
        }
        playCard(cityCard)
        researchStationPool.takeResearchStation()
        this.location.addResearchStation()
    }

    private fun treatDisease(colour: Colour) {
        val cubesOnLocation = this.location.getQtyOfDiseaseCubes()[colour] ?: 0
        if(cubesOnLocation <= 0) {
            throw GameException("Attempt to treat a location with no disease. ${this.location}")
        }
        val cubesToTreat = if(this.location.city.colour.isCured) { cubesOnLocation } else { 1 }
        this.location.treat(colour, cubesToTreat)
        diseasePool.putCubesBack(colour, cubesToTreat)
    }

    private fun discoverCure(colour: Colour, cityCards: Set<CityCard>) {
        if(cityCards.count() != 5 || cityCards.any { it.city.colour != colour }) {
            throw GameException("Must have 5 cards of the same colour to discover a cure: $cityCards")
        }
        if(!this.location.hasResearchStation()) {
            throw GameException("Attempt to discover cure in a location without a research station $location")
        }
        cityCards.map { playCard(it) }
        this.location.city.colour.isCured = true
    }

    private fun shareKnowledgeGive(other: Pawn, cityCard: CityCard) {
        other.shareKnowledgeTake(this, cityCard)
    }

    private fun shareKnowledgeTake(other: Pawn, cityCard: CityCard) {
        if(this.location.city != other.location.city) {
            throw GameException("Must be in the same location to share knowledge $location")
        }

        if(this.location.city != cityCard.city) {
            throw GameException("City card must match the location of both pawns to trade knowledge")
        }

        if(!other.hand.remove(cityCard)) {
            throw GameException("donating player must have the card to trade knowledge")
        }
        if(!this.hand.add(cityCard)) {
            throw GameException("receiving player already has the card in trade knowledge")
        }
        if(hand.size > 7) {
            theIntelligence.whichCardsToDiscard(this).map { playCard(it) }
        }
    }

    private fun playCard(playerCard: PlayerCard) {
        if(!hand.remove(playerCard)) {
            throw GameException("Necessary card $playerCard is not in hand")
        }
        playerDeck.discard(playerCard)
    }
}