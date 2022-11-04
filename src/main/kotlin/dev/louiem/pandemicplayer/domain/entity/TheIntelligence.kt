package dev.louiem.pandemicplayer.domain.entity

import dev.louiem.pandemicplayer.domain.entity.board.Location
import dev.louiem.pandemicplayer.domain.entity.player.ActionCommand
import dev.louiem.pandemicplayer.domain.entity.player.DriveFerry
import dev.louiem.pandemicplayer.domain.entity.player.Pawn
import dev.louiem.pandemicplayer.domain.entity.player.playerdeck.EventCard
import dev.louiem.pandemicplayer.domain.entity.player.playerdeck.PlayableCard
import dev.louiem.pandemicplayer.domain.valueobject.City
import dev.louiem.pandemicplayer.exception.GameException

class TheIntelligence {
    private var locationMap: Map<City, Location>? = null

    fun setLocationMap(locationMap: Map<City, Location>) {
        this.locationMap = locationMap
    }
    /*
    What is the order of best moves?

    1. 2+ cities can outbreak in current epidemic
    2. Can cure
    3. Can trade cards for a cure
    4. 1 city can outbreak in current epidemic
    5. Can trade cards to get 1 away from cure
    6. Can build research location in good spot
    7. Can treat disease
    8. Can be in a position far from players and research stations
    9. Can eradicate
    10. Can be near research station

     */
    fun whichCardsToDiscard(pawn: Pawn): List<PlayableCard> {
        // TODO: Real implementation
        return listOf(pawn.getHand().random())
    }

    fun determineIfAndWhichEventCard(): EventCard? {
        // TODO: Add support for event cards after base game decision making is completes
        return null
    }

    fun determineActionsForTurn(pawn: Pawn): List<ActionCommand> {
        // TODO: Real implementation
        return listOf(
                DriveFerry(pawn.getLocation().getConnectedLocations().first()),
        )
    }

    fun whichResearchStationToRemove(): Location {
        // TODO: Real implementation
        return locationMap?.values?.first { it.hasResearchStation() }?: throw GameException("The Intelligence not setup properly")
    }
}