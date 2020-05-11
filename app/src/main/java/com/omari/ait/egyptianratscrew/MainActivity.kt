package com.omari.ait.egyptianratscrew

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.omari.ait.egyptianratscrew.adapters.CPUInfoAdapter
import com.omari.ait.egyptianratscrew.models.Card
import com.omari.ait.egyptianratscrew.controllers.Game
import com.omari.ait.egyptianratscrew.models.Computer
import com.omari.ait.egyptianratscrew.models.Player
import com.omari.ait.egyptianratscrew.util.getCardDrawableURI
import com.omari.ait.egyptianratscrew.util.unhighlightButton
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.card.view.*
import kotlin.random.Random


class MainActivity : AppCompatActivity() {

    lateinit var game: Game
    lateinit var cpuInfoAdapter: CPUInfoAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val p1 = Player("P1", btnPlayer1Deal, btnPlayer1Slap)
        val p2 = Player("P2", btnPlayer2Deal, btnPlayer2Slap)
        val cpu1 = Computer("CPU1", null, null, 1000, 1000, null)
        val cpu2 = Computer("CPU2", null, null, 1000, 1000, null)

        //TODO: get lists from an intent from the menu
        val players = mutableListOf<Player>(p1, p2)
        val computers = mutableListOf<Computer>(cpu1, cpu2)

        game = Game((players + computers).toMutableList(), this)
        cpuInfoAdapter = CPUInfoAdapter(computers, this)

        if (computers.size > 0) {
            rvCPUCards.visibility = View.VISIBLE
            rvCPUCards.layoutManager = LinearLayoutManager(this)
            rvCPUCards.adapter = cpuInfoAdapter
        }

        Log.d("${p1.name}_deck", p1.deck.toString())
        Log.d("${p2.name}_deck", p2.deck.toString())

        btnPlayer1Deal.text = "${p1.deck.size}"
        btnPlayer2Deal.text = "${p2.deck.size}"

        btnPlayer1Deal.setOnClickListener {
            unhighlightButton(btnPlayer1Deal)
            game.playCard(p1)
            btnPlayer1Deal.text = "${p1.deck.size}"
            Log.d("${p1.name}_deck", p1.deck.toString())
        }

        btnPlayer2Deal.setOnClickListener {
            unhighlightButton(btnPlayer2Deal)
            game.playCard(p2)
            btnPlayer2Deal.text = "${p2.deck.size}"
            Log.d("${p2.name}_deck", p2.deck.toString())
        }

        btnPlayer1Slap.setOnClickListener {
            game.slap(p1)
            btnPlayer1Deal.text = "${p1.deck.size}"
            Log.d("${p1.name}_deck", p1.deck.toString())
            btnPlayer1Slap.text = "SLAP"
        }

        btnPlayer2Slap.setOnClickListener {
            game.slap(p2)
            btnPlayer2Deal.text = "${p2.deck.size}"
            Log.d("${p2.name}_deck", p2.deck.toString())
            btnPlayer2Slap.text = "SLAP"
        }
    }

    fun getCardDrawable(card: Card?) : Drawable {
        val uri = getCardDrawableURI(card)
        val imageResource = resources.getIdentifier(uri, null, packageName)
        return resources.getDrawable(imageResource, null)
    }

    fun addCardToTop(c: Card) {
        val card = layoutInflater.inflate(R.layout.card, null, false)
        card.elevation = game.pile.size.toFloat()
        card.rotation = (Random.nextFloat() * 80) - 40
        card.translationX = (Random.nextFloat() * 200) - 100
        card.translationY += (Random.nextFloat() * 200) - 100
        card.ivCard.setImageDrawable(getCardDrawable(c))
        flTableTop.addView(card)
        game.invalidateCurrentThreads()
        game.allComputerSlap()
    }

    fun addCardToBottom(c: Card) {
        val card = layoutInflater.inflate(R.layout.card, null, false)
        card.elevation = -game.burnedCards.size.toFloat()
        card.rotation = (Random.nextFloat() * 80) - 40
        card.translationX = (Random.nextFloat() * 200) - 100
        card.translationY += (Random.nextFloat() * 200) - 100
        card.ivCard.setImageDrawable(getCardDrawable(c))
        flTableTop.addView(card)
    }

    fun clearCardsFromFrameView() {
        flTableTop.removeAllViews()
    }

    fun updateCPUInfoRV() {

    }
}
