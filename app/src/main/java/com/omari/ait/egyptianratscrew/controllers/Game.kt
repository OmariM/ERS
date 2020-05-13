package com.omari.ait.egyptianratscrew.controllers

import android.content.Context
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.omari.ait.egyptianratscrew.R
import com.omari.ait.egyptianratscrew.models.Card
import com.omari.ait.egyptianratscrew.models.Computer
import com.omari.ait.egyptianratscrew.models.Player
import com.omari.ait.egyptianratscrew.util.*
import kotlin.concurrent.thread
import kotlin.random.Random

class Game(val players: MutableList<Player>, val context: Context) {

    val pile : MutableList<Card>
    val burnedCards : MutableList<Card>
    var currentPlayer : Player
    var prevPlayer : Player
    var faceCardCounter = 0
    var gameIsOver = false
    var validComputerThreads : MutableList<Thread>

    init {
        pile = mutableListOf<Card>()
        burnedCards = mutableListOf<Card>()
        validComputerThreads = mutableListOf<Thread>()
        currentPlayer = players[0]
        prevPlayer = players[0]
        currentPlayer.isMyTurn = true
        highlightButton(currentPlayer.dealButton)
        players.forEach { if (it is Computer) it.game = this }
        dealCards(getShuffledDeck(), players)
    }

    fun getNextPlayer(updatePrevPlayer: Boolean = true) : Player {
        var currentPlayerPosition = players.indexOf(currentPlayer)
        if (updatePrevPlayer) prevPlayer = players[currentPlayerPosition]
        var nextPlayer = players[(currentPlayerPosition + 1) % players.size]
        while (!hasCards(nextPlayer)) {
            Log.d("NEXT_PLAYER_NO_CARDS", "${nextPlayer.name}")
            currentPlayerPosition ++
            nextPlayer = players[(currentPlayerPosition) % players.size]
        }
        Log.d("NEXT_PLAYER", "${nextPlayer.name}")
        return nextPlayer
    }

    fun playCard(player: Player) {
        if (!player.isMyTurn || gameIsOver || checkGameOver()) return

        dealCard()

        if (isFaceCard(pile[0])) handleDealtCardIsFaceCard()
        else if (faceCardCounter > 0) handleInFaceCardStreak()
        else goToNextPlayer()

        if (checkGameOver()) return

        if (currentPlayer is Computer) {
            (context as ERSGameActivity).cpuInfoAdapter.updateItem(currentPlayer as Computer)
            computerDealCard(currentPlayer as Computer)
        }
    }

    private fun handleEndFaceCardStreak() {
        players.forEach {
            it.isMyTurn = false
            unhighlightButton(it.dealButton)
        }
        prevPlayer.canCollectPile = true
        if (prevPlayer is Computer) computerSlap(prevPlayer as Computer)
        Log.d("GAME", "${prevPlayer.name} can collect the pile")
        val prevPlayerSlapButton = prevPlayer.slapButton
        if (prevPlayerSlapButton != null) {
            highlightButton(prevPlayerSlapButton)
            prevPlayerSlapButton.text = context.getString(R.string.button_collect)
        }
    }

    private fun handleInFaceCardStreak() {
        currentPlayer.isMyTurn = true
        if (!hasCards(currentPlayer)) {
            unhighlightButton(currentPlayer.dealButton)
            currentPlayer.isMyTurn = false
            if (currentPlayer is Computer) {
                (context as ERSGameActivity).cpuInfoAdapter.updateItem(currentPlayer as Computer)
            }
            currentPlayer = getNextPlayer(false)
            currentPlayer.isMyTurn = true
            highlightButton(currentPlayer.dealButton)
        }
        faceCardCounter--
        Log.d("GAME", "$faceCardCounter tries left, placed by ${prevPlayer.name}")
        highlightButton(currentPlayer.dealButton)
        if (faceCardCounter == 0) handleEndFaceCardStreak()
    }

    private fun handleDealtCardIsFaceCard() {
        faceCardCounter = getNumTries(pile[0])
        goToNextPlayer()
        Log.d(
            "GAME",
            "${pile[0]} seen, ${getNumTries(pile[0])} tries, placed by ${prevPlayer.name}"
        )
    }

    private fun goToNextPlayer() {
        currentPlayer = getNextPlayer()
        currentPlayer.isMyTurn = true
        highlightButton(currentPlayer.dealButton)
    }

    private fun dealCard() {
        val cardToAdd = currentPlayer.play()
        pile.add(0, cardToAdd)
        if (currentPlayer is Computer) (context as ERSGameActivity).cpuInfoAdapter.updateItem(currentPlayer as Computer)
        (context as ERSGameActivity).addCardToTop(cardToAdd)
        Log.d("GAME", "${currentPlayer.name} just played $cardToAdd")
    }

    private fun checkGameOver(): Boolean {
        val remainingPlayers = playersWithCards()

        val gameOverLastPlayer = remainingPlayers.size == 1 && faceCardCounter == 0
        val gameOverLastPlayerInFaceSequence = faceCardCounter > 0 && currentPlayer == prevPlayer && remainingPlayers.size == 1

        if (gameOverLastPlayer || gameOverLastPlayerInFaceSequence) {
            gameIsOver = true
            val winner = remainingPlayers[0]
            (context as ERSGameActivity).gameOver(winner.name)
            return true
        }
        return false
    }

    fun slap(player: Player) {
        if (pile.size == 0) return
        if (isDouble() || isSandwich() || player.canCollectPile) {
            handleValidSlap(player)
        } else if (player.deck.size != 0) {
            handleBurn(player)
        }
    }

    private fun handleValidSlap(player: Player) {
        players.forEach {
            it.isMyTurn = false
            it.canCollectPile = false
            unhighlightButton(it.slapButton)
            unhighlightButton(it.dealButton)
        }
        faceCardCounter = 0
        Log.d("GAME", "deck slapped by ${player.name}")
        retrieveDeck(player)
    }

    private fun handleBurn(player: Player) {
        val burnedCard = player.burn()
        burnedCards.add(burnedCard)
        (context as ERSGameActivity).addCardToBottom(burnedCard)
        Log.d("GAME", "${player.name} burned a $burnedCard")
        checkGameOver()
    }

    fun canRetrieveDeck(player: Player) : Boolean {
        return (pile.size != 0) && (isDouble() || isSandwich() || player.canCollectPile)
    }

    fun retrieveDeck(player: Player) {
        if (player.canCollectPile) players.forEach {
            it.isMyTurn = false
            it.canCollectPile = false
        }
        currentPlayer = player
        currentPlayer.deck.addAll((pile + burnedCards).reversed())
        pile.clear()
        (context as ERSGameActivity).clearCardsFromFrameView()
        highlightButton(currentPlayer.dealButton)
        currentPlayer.isMyTurn = true
        currentPlayer.canCollectPile = false
        burnedCards.clear()
        invalidateCurrentThreads()
    }

    fun isDouble() : Boolean {
        if (pile.size < 2) return false
        return pile[0].value == pile[1].value
    }

    fun isSandwich() : Boolean {
        if (pile.size < 3) return false
        return pile[0].value == pile[2].value
    }

    fun hasCards(player: Player) : Boolean {
        return player.deck.size != 0
    }

    fun playersWithCards() : List<Player> {
        return players.filter { hasCards(it) }
    }

    fun computerDealCard(cpu: Computer) {
        thread {
            Thread.sleep(cpu.timeToDeal)
            (context as AppCompatActivity).runOnUiThread {
                playCard(cpu)
                (context as ERSGameActivity).cpuInfoAdapter.updateItem(cpu)
            }
        }
    }

    fun computerSlap(cpu: Computer) {
        thread {
            cpu.startTime = System.currentTimeMillis()
            val minTimeToSlap = (cpu.timeToSlap*.8).toLong()
            val maxTimeToSlap = (cpu.timeToSlap*1.2).toLong()
            val randTimeToSlap = Random.nextLong(minTimeToSlap, maxTimeToSlap)
            while (cpu.isAlive && System.currentTimeMillis() - cpu.startTime < randTimeToSlap) {  }
            Log.d("CPU_SLAP", "${cpu.name} is alive: ${cpu.isAlive}")
            if (cpu.isAlive) {
                (context as AppCompatActivity).runOnUiThread {
                    slap(cpu)
                    (context as ERSGameActivity).cpuInfoAdapter.updateItem(cpu)
                    computerDealCard(cpu)
                }
            }
        }
    }

    fun allComputerSlap () {
        Log.d("ALL_CPU_SLAP", "deck: ${pile.subList(0, min(pile.size, 3))}")
        players.forEach {
            if (it is Computer) {
                Log.d("ALL_CPU_SLAP", "${it.name} can retrieve deck: ${canRetrieveDeck(it)}")
                it.isAlive = true
                if (canRetrieveDeck(it)) computerSlap(it)
            }
        }
    }

    fun invalidateCurrentThreads() {
        players.forEach {
            if (it is Computer) {
                it.isAlive = false
            }
        }
    }

    fun reset() {
        pile.clear()
        burnedCards.clear()
        validComputerThreads.clear()
        faceCardCounter = 0
        players.forEach {
            it.deck.clear()
            it.canCollectPile = false
            it.isMyTurn = false
            unhighlightButton(it.dealButton)
            unhighlightButton(it.slapButton)
            if (it is Computer) it.isAlive = false
        }
        currentPlayer = players[0]
        prevPlayer = players[0]
        currentPlayer.isMyTurn = true
        highlightButton(currentPlayer.dealButton)
        dealCards(getShuffledDeck(), players)
        gameIsOver = false
    }

}