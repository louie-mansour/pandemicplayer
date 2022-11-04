package dev.louiem.pandemicplayer.domain.entity.board

import dev.louiem.pandemicplayer.domain.entity.TheIntelligence
import dev.louiem.pandemicplayer.domain.valueobject.City
import io.mockk.every
import io.mockk.mockkClass
import io.mockk.verify
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class ResearchStationPoolSpec {
    private var theIntelligence = mockkClass(TheIntelligence::class)
    private var atlanta = Location.locationMapSingleton()[City.Atlanta]!!
    private var researchStationPool: ResearchStationPool = ResearchStationPool(theIntelligence)

    @BeforeEach
    fun setup() {
        every { theIntelligence.whichResearchStationToRemove() } returns atlanta
        researchStationPool = ResearchStationPool(theIntelligence)
    }

    @Test
    fun `building a research station reduces the qty by 1`() {
        Assertions.assertEquals(ResearchStationPool.MAX_RESEARCH_STATIONS, researchStationPool.getQty())
        researchStationPool.takeResearchStation()
        Assertions.assertEquals(ResearchStationPool.MAX_RESEARCH_STATIONS - 1, researchStationPool.getQty())
    }

    @Test
    fun `Only calls the intelligence for which station to remove after there are none`() {
        while(researchStationPool.getQty() > 0) {
            researchStationPool.takeResearchStation()
        }
        Assertions.assertEquals(0, researchStationPool.getQty())

        verify(exactly = 0) { theIntelligence.whichResearchStationToRemove() }

        researchStationPool.takeResearchStation()
        verify(exactly = 1) { theIntelligence.whichResearchStationToRemove() }
        Assertions.assertEquals(0, researchStationPool.getQty())

        researchStationPool.takeResearchStation()
        verify(exactly = 2) { theIntelligence.whichResearchStationToRemove() }
        Assertions.assertEquals(0, researchStationPool.getQty())
    }
}