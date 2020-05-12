package com.omari.ait.egyptianratscrew.models

import android.util.Log
import android.widget.Button
import com.omari.ait.egyptianratscrew.controllers.Game
import kotlin.random.Random

class Computer(name: String, dealButton: Button? = null, slapButton: Button? = null, var timeToSlap: Long, var timeToDeal: Long, var game: Game? = null) :
    Player(name, dealButton, slapButton) {

    var isAlive = false
    var startTime : Long = 0

    fun getInfo() : String {
        return "${this.name}: ${deck.size}"
    }

}