package com.omari.ait.egyptianratscrew.controllers

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.omari.ait.egyptianratscrew.MainActivity
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
        dealCards(getSmallDeck(15), players)
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
        if (faceCardCounter > 0 && currentPlayer == prevPlayer) gameIsOver == true
        if (!player.isMyTurn || gameIsOver || checkGameOver()) return
        val cardToAdd = player.play()
        pile.add(0, cardToAdd)
        if (player is Computer) (context as MainActivity).cpuInfoAdapter.updateItem(player)
        (context as MainActivity).addCardToTop(cardToAdd)
        Log.d("GAME", "${player.name} just played $cardToAdd")
        if (isFaceCard(pile[0])) {
            faceCardCounter = getNumTries(pile[0])
            currentPlayer = getNextPlayer()
            currentPlayer.isMyTurn = true
            highlightButton(currentPlayer.dealButton)
            Log.d("GAME", "${pile[0]} seen, ${getNumTries(pile[0])} tries, placed by ${prevPlayer.name}")
        } else if (faceCardCounter > 0) {
            currentPlayer = player
            currentPlayer.isMyTurn = true
            if (!hasCards(currentPlayer)) {
                unhighlightButton(currentPlayer.dealButton)
                currentPlayer = getNextPlayer(false)
                currentPlayer.isMyTurn = true
                highlightButton(currentPlayer.dealButton)
            }
            faceCardCounter--
            Log.d("GAME", "$faceCardCounter tries left, placed by ${prevPlayer.name}")
            highlightButton(currentPlayer.dealButton)
            if (faceCardCounter == 0) {
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
                    prevPlayerSlapButton.text = "COLLECT"
                }
            }
        } else {
            currentPlayer = getNextPlayer()
            currentPlayer.isMyTurn = true
            highlightButton(currentPlayer.dealButton)
        }

        if (checkGameOver()) return

        if (currentPlayer is Computer) {
            (context as MainActivity).cpuInfoAdapter.updateItem(currentPlayer as Computer)
            computerDealCard(currentPlayer as Computer)
        }
    }

    private fun checkGameOver(): Boolean {
        val remainingPlayers = playersWithCards()

        if (remainingPlayers.size == 1 && faceCardCounter == 0) {
            gameIsOver = true
            val winner = remainingPlayers[0]
            Toast.makeText(context, "The winner is ${winner.name}!", Toast.LENGTH_LONG).show()
            return true
        }
        return false
    }

    fun slap(player: Player) {
        if (pile.size == 0) return
        if (isDouble() || isSandwich() || player.canCollectPile) {
            players.forEach {
                it.isMyTurn = false
                unhighlightButton(it.slapButton)
                unhighlightButton(it.dealButton)
            }
            player.canCollectPile
            faceCardCounter = 0 //TODO: you might want to put this in retrieveDeck
            Log.d("GAME", "deck slapped by ${player.name}")
            retrieveDeck(player)
        } else if (player.deck.size != 0) {
            val burnedCard = player.burn() // TODO: use .play() here
            burnedCards.add(burnedCard)
            (context as MainActivity).addCardToBottom(burnedCard)
            Log.d("GAME", "${player.name} burned a $burnedCard")
            checkGameOver()
        }
    }

    fun canRetrieveDeck(player: Player) : Boolean {
        return (pile.size != 0) && (isDouble() || isSandwich() || player.canCollectPile)
    }

    fun retrieveDeck(player: Player) {
        if (player.canCollectPile)
        players.forEach { it.isMyTurn = false }
        currentPlayer = player
        currentPlayer.deck.addAll((pile + burnedCards).reversed())
        pile.clear()
        (context as MainActivity).clearCardsFromFrameView()
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
            (context as MainActivity).runOnUiThread {
                playCard(cpu)
                context.cpuInfoAdapter.updateItem(cpu)
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
                (context as MainActivity).runOnUiThread {
                    slap(cpu)
                    context.cpuInfoAdapter.updateItem(cpu)
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

}