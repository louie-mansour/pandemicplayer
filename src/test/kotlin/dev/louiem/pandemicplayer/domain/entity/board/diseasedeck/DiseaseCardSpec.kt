package dev.louiem.pandemicplayer.domain.entity.board

import dev.louiem.pandemicplayer.domain.entity.board.diseasedeck.DiseaseCard
import dev.louiem.pandemicplayer.domain.valueobject.City
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class DiseaseCardSpec {

    @Test
    fun `can instantiate and get cities from disease cards`() {
        val diseaseCard = DiseaseCard(City.Mumbai)
        Assertions.assertEquals(City.Mumbai, diseaseCard.city)
    }
}