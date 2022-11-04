package dev.louiem.pandemicplayer

import dev.louiem.pandemicplayer.domain.entity.Game
import dev.louiem.pandemicplayer.domain.entity.TheIntelligence
import dev.louiem.pandemicplayer.domain.entity.board.*
import dev.louiem.pandemicplayer.domain.entity.board.diseasedeck.DiseaseDeck
import dev.louiem.pandemicplayer.domain.entity.player.Pawn
import dev.louiem.pandemicplayer.domain.entity.player.playerdeck.PlayerDeck
import dev.louiem.pandemicplayer.domain.valueobject.City

class GameFactory {
    companion object {
        fun create(): Game {
            val theIntelligence = TheIntelligence()

            val diseasePool = DiseasePool()
            val researchStationPool = ResearchStationPool(theIntelligence)
            val locationMap = Location.locationMapSingleton()
            val infectionRate = InfectionRate()
            val outbreakCounter = OutbreakCounter()

            val diseaseDeck = DiseaseDeck()

            val playerDeck = PlayerDeck(4)

            val pawns = List(2) {
                    Pawn(locationMap[City.Atlanta]!!, playerDeck, 4, diseasePool, researchStationPool, theIntelligence)
            }

            val board = Board(locationMap, diseaseDeck, infectionRate, outbreakCounter, diseasePool)

            theIntelligence.setLocationMap(locationMap)

            return Game(board, playerDeck, theIntelligence, pawns)
        }
    }
}