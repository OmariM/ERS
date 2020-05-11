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
    var faceCardCounter = 0
    var gameIsOver = false
    var validComputerThreads : MutableList<Thread>

    init {
        pile = mutableListOf<Card>()
        burnedCards = mutableListOf<Card>()
        validComputerThreads = mutableListOf<Thread>()
        currentPlayer = players[0]
        currentPlayer.isMyTurn = true
        highlightButton(currentPlayer.dealButton)
        players.forEach { if (it is Computer) it.game = this  }
        dealCards(getShuffledDeck(), players)
    }

    fun getNextPlayer() : Player {
        val currentPlayerPosition = players.indexOf(currentPlayer)
        return players[(currentPlayerPosition + 1) % players.size]
    }

    fun getPrevPlayer() : Player {
        val currentPlayerPosition = players.indexOf(currentPlayer)
        return players[(players.size + currentPlayerPosition - 1) % players.size]
    }

    fun playCard(player: Player) {
        if (!player.isMyTurn || gameIsOver) return
        val cardToAdd = player.play()
        pile.add(0, cardToAdd)
        if (isFaceCard(pile[0])) {
            Log.d("FACE", "${pile[0]} seen, ${getNumTries(pile[0])} tries")
            faceCardCounter = getNumTries(pile[0])
            currentPlayer = getNextPlayer()
            currentPlayer.isMyTurn = true
            highlightButton(currentPlayer.dealButton)
        } else if (faceCardCounter > 0) {
            currentPlayer = player
            currentPlayer.isMyTurn = true
            faceCardCounter--
            Log.d("FACE", "$faceCardCounter tries left")
            highlightButton(currentPlayer.dealButton)
            if (faceCardCounter == 0) {
                players.forEach {
                    it.isMyTurn = false
                    unhighlightButton(it.dealButton)
                }
                getPrevPlayer().canCollectPile = true
                val prevPlayerSlapButton = getPrevPlayer().slapButton
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

        (context as MainActivity).addCardToTop(cardToAdd)

        if (gameOver()) {
            gameIsOver = true
            val winner = players[0]
            Toast.makeText(context, "The winner is ${winner.name}!", Toast.LENGTH_LONG).show()
        }

        if (currentPlayer is Computer) {
            computerDealCard(currentPlayer as Computer)
        }
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
            retrieveDeck(player)
        } else if (player.deck.size != 0) {
            val burnedCard = player.burn() // TODO: use .play() here
            burnedCards.add(burnedCard)
            (context as MainActivity).addCardToBottom(burnedCard)
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

    fun cullEmptyPlayers() {
        players.removeAll { !hasCards(it) }
    }

    fun gameOver() : Boolean {
        cullEmptyPlayers()
        return players.size == 1 && faceCardCounter == 0
    }

    fun computerDealCard(cpu: Computer) {
        thread {
            Thread.sleep(cpu.timeToDeal)
            (context as MainActivity).runOnUiThread { playCard(cpu) }
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
                    playCard(cpu)
                }
            }
        }
    }

    fun allComputerSlap () {
        Log.d("ALL_CPU_SLAP", "deck: ${pile.subList(0, min(pile.size, 3))}")
        players.shuffle()
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