package dev.louiem.pandemicplayer.domain.entity.board.diseasedeck

import dev.louiem.pandemicplayer.domain.valueobject.City
import dev.louiem.pandemicplayer.exception.GameException
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class DiseaseDeckSpec {

    @Test
    fun `getTop() returns the top card from deck and throws when empty`() {
        val diseaseDeck =  DiseaseDeck()
        val cards = mutableSetOf<DiseaseCard>()
        City.values().forEach { _ ->
            val topCard = diseaseDeck.getTop()
            Assertions.assertFalse(cards.contains(topCard))
            cards.add(topCard)
        }

        assertThrows<GameException> { diseaseDeck.getTop() }
    }

    @Test
    fun `getBottom() returns the top card from deck and throws when empty`() {
        val diseaseDeck =  DiseaseDeck()
        val cards = mutableSetOf<DiseaseCard>()
        City.values().forEach { _ ->
            val bottomCard = diseaseDeck.getBottom()
            Assertions.assertFalse(cards.contains(bottomCard))
            cards.add(bottomCard)
        }

        assertThrows<GameException> { diseaseDeck.getBottom() }
    }

    @Test
    fun `addShuffledDiscardToDeck() goes through the discarded cards again`() {
        val diseaseDeck =  DiseaseDeck()
        val cards = mutableSetOf<City>()
        List(9) {}.forEach { _ ->
            val topCard = diseaseDeck.getTop()
            cards.add(topCard.city)
        }

        diseaseDeck.addShuffledDiscardToDeck()

        List(9) {}.forEach { _ ->
            val topCard = diseaseDeck.getTop()
            Assertions.assertTrue(cards.contains(topCard.city))
        }

        Assertions.assertFalse(cards.contains(diseaseDeck.getTop().city))
    }
}