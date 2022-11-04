package dev.louiem.pandemicplayer.domain.entity.board

import dev.louiem.pandemicplayer.domain.entity.board.diseasedeck.DiseaseDeck
import dev.louiem.pandemicplayer.domain.valueobject.City
import dev.louiem.pandemicplayer.domain.valueobject.Colour
import dev.louiem.pandemicplayer.exception.GameException

class Board(
        private val locationMap: Map<City, Location>,
        private val diseaseDeck: DiseaseDeck,
        private val infectionRate: InfectionRate,
        private val outbreakCounter: OutbreakCounter,
        private val diseasePool: DiseasePool,
) {

    fun setup() {
        infectCity(diseaseDeck.getTop().city, 3)
        infectCity(diseaseDeck.getTop().city, 3)
        infectCity(diseaseDeck.getTop().city, 3)
        infectCity(diseaseDeck.getTop().city, 2)
        infectCity(diseaseDeck.getTop().city, 2)
        infectCity(diseaseDeck.getTop().city, 2)
        infectCity(diseaseDeck.getTop().city, 1)
        infectCity(diseaseDeck.getTop().city, 1)
        infectCity(diseaseDeck.getTop().city, 1)
    }

    fun takeTurn() {
        val infectionRate = this.infectionRate.get()
        for(i in 0 until infectionRate) {
            val diseaseCard = this.diseaseDeck.getTop()
            infectCity(diseaseCard.city, 1)
        }
    }

    fun resolveEpidemic() {
        infectionRate.increase()

        val diseaseCard = diseaseDeck.getBottom()
        infectCity(diseaseCard.city, 3)
        diseaseDeck.addShuffledDiscardToDeck()
    }

    private fun infectCity(city: City, qty: Int) {
        val locationToInfect = locationMap[city]?: throw GameException("Attempt to infect a non-existent city")

        val locationsInOutbreak = mutableSetOf<Location>()
        val locationsToInfect = ArrayDeque(mutableListOf(locationToInfect))
        val colourToInfect = city.colour
        var qtyToInfect = qty

        while(locationsToInfect.isNotEmpty()) {
            val location = locationsToInfect.removeFirst()

            if(locationsInOutbreak.contains(location)) continue

            if(isOutbreak(location, colourToInfect, qtyToInfect)) {
                val cubesToPlace = cubesToPlace(location, colourToInfect, qtyToInfect)
                location.infect(colourToInfect, cubesToPlace)
                diseasePool.takeCubes(colourToInfect, qtyToInfect)
                locationsInOutbreak.add(location)
                locationsToInfect.addAll(location.getConnectedLocations())
                outbreakCounter.increment()
                qtyToInfect = 1
                continue
            }

            location.infect(colourToInfect, qtyToInfect)
        }

    }

    private fun cubesToPlace(location: Location, colourToInfect: Colour, qtyCubes: Int): Int {
        return qtyCubes - qtyCubesOnLocation(location, colourToInfect)
    }

    private fun isOutbreak(location: Location, colourToInfect: Colour, qtyCubes: Int): Boolean {
        return qtyCubesOnLocation(location, colourToInfect) + qtyCubes > 3
    }

    private fun qtyCubesOnLocation(location: Location, colourToInfect: Colour): Int {
        return location.getQtyOfDiseaseCubes()[colourToInfect]?:0
    }
}