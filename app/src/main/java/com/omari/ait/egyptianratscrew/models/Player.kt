package com.omari.ait.egyptianratscrew.models

import android.widget.Button

open class Player(var name: String, val dealButton: Button?, val slapButton: Button?) {

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