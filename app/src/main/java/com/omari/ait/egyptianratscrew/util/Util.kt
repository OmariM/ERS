package com.omari.ait.egyptianratscrew.util

import android.graphics.Color
import android.graphics.drawable.Drawable
import android.util.Log
import android.widget.Button
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import com.omari.ait.egyptianratscrew.models.Card
import com.omari.ait.egyptianratscrew.models.Player
import kotlin.coroutines.coroutineContext

fun getShuffledDeck() : MutableList<Card> {
    var deck = mutableListOf<Card>()
    Suite.values().forEach { for (i in 2..14) deck.add(Card(i, it)) }
    deck.shuffle()
    return deck
}

fun getSmallDeck(size: Int) : MutableList<Card> {
    return getShuffledDeck().subList(0, size)
}

fun dealCards(cards: MutableList<Card>, players: MutableList<Player>) {
    var piles = cards.chunked(cards.size / players.size) { pile: List<Card> -> pile.toMutableList() }.toMutableList()
    players.forEach { it.deck.addAll(piles.removeAt(0))}
}

fun isFaceCard(card: Card) : Boolean {
    return card.value > 10
}

fun getNumTries(card: Card) : Int {
    if (!isFaceCard(card)) throw Exception("$card is not a face card!")
    return card.value - 10
}

fun getCardDrawableURI(card: Card?) : String {
    if (card == null) return "@drawable/card_back_01"
    else return "@drawable/card_${card.toString().toLowerCase()}"
}

fun highlightButton(btn: Button?) {
    if (btn == null) return
    btn.setBackgroundColor(Color.rgb(23, 200, 45))
}

fun unhighlightButton(btn: Button?) {
    if (btn == null) return
    btn.setBackgroundColor(Color.rgb(218, 218, 218))
}

// in order to maintain the minimum api requirement of 21 (integer min requires 24)
fun min(a: Int, b: Int) : Int {
    if (a < b) return a
    return b
}
