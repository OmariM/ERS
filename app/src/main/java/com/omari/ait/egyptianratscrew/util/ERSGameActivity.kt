package com.omari.ait.egyptianratscrew.util

import android.graphics.drawable.Drawable
import com.omari.ait.egyptianratscrew.R
import com.omari.ait.egyptianratscrew.adapters.CPUInfoAdapter
import com.omari.ait.egyptianratscrew.controllers.Game
import com.omari.ait.egyptianratscrew.models.Card
import kotlinx.android.synthetic.main.activity_two_player.*
import kotlinx.android.synthetic.main.card.view.*
import kotlin.random.Random

interface ERSGameActivity {
    var game: Game
    var cpuInfoAdapter: CPUInfoAdapter

    fun getCardDrawable(card: Card?) : Drawable
    fun addCardToTop(c: Card)
    fun addCardToBottom(c: Card)
    fun clearCardsFromFrameView()
}