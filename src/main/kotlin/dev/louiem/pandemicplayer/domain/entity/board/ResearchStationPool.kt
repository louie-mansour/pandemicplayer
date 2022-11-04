package dev.louiem.pandemicplayer.domain.entity.board

import dev.louiem.pandemicplayer.domain.entity.TheIntelligence

class ResearchStationPool(private val theIntelligence: TheIntelligence) {
    private var qtyResearchStations = MAX_RESEARCH_STATIONS

    fun takeResearchStation() {
        qtyResearchStations -= 1
        while(qtyResearchStations < 0) {
            theIntelligence.whichResearchStationToRemove()
            qtyResearchStations += 1
        }
    }

    fun getQty(): Int {
        return qtyResearchStations
    }

    companion object {
        const val MAX_RESEARCH_STATIONS = 6
    }
}