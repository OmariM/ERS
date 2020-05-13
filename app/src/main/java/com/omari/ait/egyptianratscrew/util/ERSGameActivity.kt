package com.omari.ait.egyptianratscrew.util

import android.graphics.drawable.Drawable
import com.omari.ait.egyptianratscrew.adapters.CPUInfoAdapter
import com.omari.ait.egyptianratscrew.controllers.Game
import com.omari.ait.egyptianratscrew.models.Card

interface ERSGameActivity {
    var game: Game
    var cpuInfoAdapter: CPUInfoAdapter

    fun getCardDrawable(c: Card?): Drawable
    fun addCardToTop(c: Card)
    fun addCardToBottom(c: Card)
    fun clearCardsFromFrameView()
    fun gameOver(s: String)
}