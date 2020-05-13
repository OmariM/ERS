package com.omari.ait.egyptianratscrew.models

import android.widget.Button

open class Player(var name: String, var dealButton: Button? = null, var slapButton: Button? = null) {

    val deck = mutableListOf<Card>()
    var isMyTurn = false
    var canCollectPile = false

    fun play() : Card {
        isMyTurn = false
        return deck.removeAt(0)
    }

    fun burn() : Card {
        return deck.removeAt(0)
    }


}