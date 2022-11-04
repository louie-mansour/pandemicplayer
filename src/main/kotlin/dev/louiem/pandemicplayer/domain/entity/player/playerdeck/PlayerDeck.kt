package dev.louiem.pandemicplayer.domain.entity.player.playerdeck

import dev.louiem.pandemicplayer.domain.valueobject.City
import dev.louiem.pandemicplayer.exception.GameOverException
import java.util.*

class PlayerDeck(private val qtyEpidemics: Int) {
    private val playerDiscard = mutableListOf<PlayerCard>()
    private var playerCards: Deque<PlayerCard> =
            ArrayDeque(City.values()
                    .map { CityCard(it) }
                    .shuffled())

    fun drawCard(): PlayerCard {
        return playerCards.pollFirst()?: throw GameOverException("Ran out of player cards")
    }

    fun discard(playerCard: PlayerCard) {
        playerDiscard.add(playerCard)
    }

    fun addEpidemics() {
        val cardPiles = List(qtyEpidemics) { mutableListOf<PlayerCard>() }
        for(i in playerCards.indices) {
            cardPiles[i % qtyEpidemics].add(playerCards.pollFirst())
        }
        for(cardPile in cardPiles) {
            cardPile.add(EpidemicCard())
            cardPile.shuffle()
        }
        playerCards = ArrayDeque(cardPiles.flatten())
    }
}