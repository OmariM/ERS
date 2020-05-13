package com.omari.ait.egyptianratscrew

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.omari.ait.egyptianratscrew.adapters.CPUInfoAdapter
import com.omari.ait.egyptianratscrew.controllers.Game
import com.omari.ait.egyptianratscrew.models.Card
import com.omari.ait.egyptianratscrew.models.Computer
import com.omari.ait.egyptianratscrew.models.Player
import com.omari.ait.egyptianratscrew.util.ERSGameActivity
import com.omari.ait.egyptianratscrew.util.getCardDrawableURI
import com.omari.ait.egyptianratscrew.util.unhighlightButton
import kotlinx.android.synthetic.main.activity_two_player.*
import kotlinx.android.synthetic.main.card.view.*
import kotlin.random.Random


class TwoPlayerActivity : AppCompatActivity(), ERSGameActivity {

    override lateinit var game: Game
    override lateinit var cpuInfoAdapter: CPUInfoAdapter

    val p1 = Player("Player 1")
    val p2 = Player("Player 2")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_two_player)

        p1.dealButton = btnPlayer1Deal
        p1.slapButton = btnPlayer1Slap
        p2.dealButton = btnPlayer2Deal
        p2.slapButton = btnPlayer2Slap

        val players = mutableListOf<Player>(p1, p2)
        val computers = mutableListOf<Computer>()

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
            btnPlayer1Slap.text = getString(R.string.button_slap)
        }

        btnPlayer2Slap.setOnClickListener {
            game.slap(p2)
            btnPlayer2Deal.text = "${p2.deck.size}"
            Log.d("${p2.name}_deck", p2.deck.toString())
            btnPlayer2Slap.text = getString(R.string.button_slap)
        }
    }

    override fun getCardDrawable(card: Card?): Drawable {
        val uri = getCardDrawableURI(card)
        val imageResource = resources.getIdentifier(uri, null, packageName)
        return resources.getDrawable(imageResource, null)
    }

    override fun addCardToTop(c: Card) {
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

    override fun addCardToBottom(c: Card) {
        val card = layoutInflater.inflate(R.layout.card, null, false)
        card.elevation = -game.burnedCards.size.toFloat()
        card.rotation = (Random.nextFloat() * 80) - 40
        card.translationX = (Random.nextFloat() * 200) - 100
        card.translationY += (Random.nextFloat() * 200) - 100
        card.ivCard.setImageDrawable(getCardDrawable(c))
        flTableTop.addView(card)
    }

    override fun clearCardsFromFrameView() {
        flTableTop.removeAllViews()
    }

    override fun gameOver(name: String) {
        Snackbar.make(
            layoutTwoPlayer,
            resources.getString(R.string.win_toast_text, name),
            Snackbar.LENGTH_INDEFINITE
        ).setAction(resources.getString(
            R.string.play_again_toast_button_string
        ), View.OnClickListener {
            clearCardsFromFrameView()
            game.reset()
            game.players.forEach { if (it is Computer) cpuInfoAdapter.updateItem(it) }
            btnPlayer1Deal.text = "${p1.deck.size}"
            btnPlayer2Deal.text = "${p2.deck.size}"
        }).show()
    }
}
