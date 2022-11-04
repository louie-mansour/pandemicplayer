package dev.louiem.pandemicplayer.domain.entity.board.diseasedeck

import dev.louiem.pandemicplayer.domain.valueobject.City
import dev.louiem.pandemicplayer.exception.GameException
import java.util.*

class DiseaseDeck {
    private var diseaseDiscard = mutableListOf<DiseaseCard>()
    private var diseaseCards: Deque<DiseaseCard> =
            ArrayDeque(City.values()
                    .map { DiseaseCard(it) }
                    .shuffled())

    fun getTop(): DiseaseCard {
        val card = diseaseCards.pollLast()?: throw GameException("Ran out of disease cards")
        diseaseDiscard.add(card)
        return card
    }

    fun getBottom(): DiseaseCard {
        val card = diseaseCards.pollFirst()?: throw GameException("Ran out of disease cards")
        diseaseDiscard.add(card)
        return card
    }

    fun addShuffledDiscardToDeck() {
        diseaseDiscard.shuffle()
        this.diseaseCards.addAll(diseaseDiscard)
        this.diseaseDiscard = mutableListOf()
    }
}