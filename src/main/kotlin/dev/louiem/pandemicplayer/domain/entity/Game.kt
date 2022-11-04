package dev.louiem.pandemicplayer.domain.entity

import dev.louiem.pandemicplayer.domain.entity.board.Board
import dev.louiem.pandemicplayer.domain.entity.player.Pawn
import dev.louiem.pandemicplayer.domain.entity.player.playerdeck.PlayerDeck
import dev.louiem.pandemicplayer.exception.EpidemicException

class Game(
        private val board: Board,
        private val playerDeck: PlayerDeck,
        private val theIntelligence: TheIntelligence,
        private val pawns: List<Pawn>,
) {
    fun setup() {
        board.setup()
        pawns.forEach {
            it.dealHand()
        }
        playerDeck.addEpidemics()
    }

    fun start() {
        while(true) {
            pawns.forEach {
                val actions = theIntelligence.determineActionsForTurn(it)
                it.executeActions(actions)
                try {
                    it.turnEnd()
                } catch (epidemic: EpidemicException) {
                    List(epidemic.qty) {
                        board.resolveEpidemic()
                    }
                }
                board.takeTurn()
            }
        }
    }
}