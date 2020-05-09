package com.omari.ait.egyptianratscrew.controllers

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.omari.ait.egyptianratscrew.MainActivity
import com.omari.ait.egyptianratscrew.models.Card
import com.omari.ait.egyptianratscrew.models.Player
import com.omari.ait.egyptianratscrew.util.*

class Game(val players: MutableList<Player>, val context: Context) {

    val pile : MutableList<Card>
    val burnedCards : MutableList<Card>
    var currentPlayer : Player
    var faceCardCounter = 0
    var gameIsOver = false

    init {
        pile = mutableListOf<Card>()
        burnedCards = mutableListOf<Card>()
        currentPlayer = players[0]
        currentPlayer.isMyTurn = true
        highlightButton(currentPlayer.dealButton)
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
        (context as MainActivity).addCard(cardToAdd)
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
                highlightButton(getPrevPlayer().slapButton)
                getPrevPlayer().slapButton.text = "COLLECT"
            }
        } else {
            currentPlayer = getNextPlayer()
            currentPlayer.isMyTurn = true
            highlightButton(currentPlayer.dealButton)
        }

        if (gameOver()) {
            gameIsOver = true
            val winner = players[0]
            Toast.makeText(context, "The winner is ${winner.name}!", Toast.LENGTH_LONG).show()
        }
    }

    fun slap(player: Player) {
        if (pile.size == 0 || player.deck.size == 0) return
        if (isDouble() || isSandwich() || player.canCollectPile) {
            players.forEach {
                it.isMyTurn = false
                unhighlightButton(it.slapButton)
                unhighlightButton(it.dealButton)
            }
            player.canCollectPile
            faceCardCounter = 0 //TODO: you might want to put this in retrieveDeck
            retrieveDeck(player)
        } else {
            val burnedCard = player.burn() // TODO: use .play() here
            burnedCards.add(burnedCard)
            (context as MainActivity).burnCard(burnedCard)
        }
    }

    fun retrieveDeck(player: Player) {
        if (player.canCollectPile)
        players.forEach { it.isMyTurn = false }
        currentPlayer = player
        currentPlayer.deck.addAll((pile + burnedCards).reversed())
        pile.clear()
        (context as MainActivity).retrievePile()
        highlightButton(currentPlayer.dealButton)
        currentPlayer.isMyTurn = true
        currentPlayer.canCollectPile = false
        burnedCards.clear()
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
        return players.size == 1
    }

}