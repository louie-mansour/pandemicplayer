package dev.louiem.pandemicplayer.domain.entity.player.playerdeck

import dev.louiem.pandemicplayer.domain.valueobject.City

abstract class PlayerCard

class EpidemicCard : PlayerCard()
abstract class PlayableCard : PlayerCard()

class CityCard(val city: City) : PlayableCard()
class EventCard : PlayableCard() // TODO: Not supported yet