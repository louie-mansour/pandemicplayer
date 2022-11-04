package dev.louiem.pandemicplayer.domain.entity.player

import dev.louiem.pandemicplayer.domain.entity.board.Location
import dev.louiem.pandemicplayer.domain.entity.player.playerdeck.CityCard
import dev.louiem.pandemicplayer.domain.valueobject.Colour

abstract class ActionCommand
class DriveFerry(val location: Location): ActionCommand()
class DirectFlight(val location: Location, val cityCard: CityCard): ActionCommand()
class CharteredFlight(val location: Location, val cityCard: CityCard): ActionCommand()
class ShuttleFlight(val location: Location): ActionCommand()
class BuildResearchStation(val cityCard: CityCard): ActionCommand()
class TreatDisease(val colour: Colour): ActionCommand()
class DiscoverCure (val colour: Colour, val cityCards: Set<CityCard>): ActionCommand()
class ShareKnowledgeGive(val pawn: Pawn, val cityCard: CityCard): ActionCommand()
class ShareKnowledgeTake(val pawn: Pawn, val cityCard: CityCard): ActionCommand()
