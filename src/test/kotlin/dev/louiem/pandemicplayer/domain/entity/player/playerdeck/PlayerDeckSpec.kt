package dev.louiem.pandemicplayer.domain.entity.player.playerdeck

import dev.louiem.pandemicplayer.domain.valueobject.City
import dev.louiem.pandemicplayer.exception.GameOverException
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class PlayerDeckSpec {

    @Test
    fun `drawCard() returns the top card from deck and throws when empty`() {
        val playerDeck =  PlayerDeck(5)
        val cityCards = mutableSetOf<PlayableCard>()
        var qtyEpidemics = 0

        City.values().forEach { _ ->
            val topCard = playerDeck.drawCard()
            if(topCard is CityCard) {
                Assertions.assertFalse(cityCards.contains(topCard))
                cityCards.add(topCard)
            } else {
                qtyEpidemics++
            }
        }
        Assertions.assertEquals(0, qtyEpidemics)
        assertThrows<GameOverException> { playerDeck.drawCard() }
    }

    @Test
    fun `addEpidemics() Adds epidemics`() {
        val playerDeck =  PlayerDeck(5)
        val cityCards = mutableSetOf<PlayableCard>()
        var qtyEpidemics = 0

        playerDeck.addEpidemics()

        List(City.values().count() + 5) {}.forEach { _ ->
            val topCard = playerDeck.drawCard()
            if(topCard is CityCard) {
                Assertions.assertFalse(cityCards.contains(topCard))
                cityCards.add(topCard)
            } else {
                qtyEpidemics++
            }
        }
        Assertions.assertEquals(5, qtyEpidemics)
        assertThrows<GameOverException> { playerDeck.drawCard() }
    }

    @Test
    fun `discard() puts cards in the discard`() {
        val playerDeck =  PlayerDeck(5)
        var cityCards = mutableSetOf<PlayableCard>()
        var qtyEpidemics = 0

        City.values().forEach { _ ->
            val topCard = playerDeck.drawCard()
            if(topCard is CityCard) {
                Assertions.assertFalse(cityCards.contains(topCard))
                cityCards.add(topCard)
            } else {
                qtyEpidemics++
            }
        }
        Assertions.assertEquals(0, qtyEpidemics)
        assertThrows<GameOverException> { playerDeck.drawCard() }

        cityCards.forEach { playerDeck.discard(it) }
        assertThrows<GameOverException> { playerDeck.drawCard() }
    }
}