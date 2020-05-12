package com.omari.ait.egyptianratscrew

import android.content.Intent
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.omari.ait.egyptianratscrew.models.Card
import com.omari.ait.egyptianratscrew.util.getCardDrawableURI
import com.omari.ait.egyptianratscrew.util.getSmallDeck
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnDuel.setOnClickListener {
            var intentDetails = Intent()
            intentDetails.setClass(applicationContext, TwoPlayerActivity::class.java)
            startActivity(intentDetails)
        }

        btnSingle.setOnClickListener {
            var intentDetails = Intent()
            intentDetails.setClass(applicationContext, OnePlayerActivity::class.java)
            startActivity(intentDetails)
        }

        btnMulti.setOnClickListener {
            Toast.makeText(this, "Coming Soon...!", Toast.LENGTH_SHORT).show()
        }

        btnProfile.setOnClickListener {
            Toast.makeText(this, "Coming Soon...!", Toast.LENGTH_SHORT).show()
        }

        ivCard1.setOnClickListener {
            val card = getSmallDeck(1)[0]
            ivCard1.setImageDrawable(getCardDrawable(card))
        }

        ivCard2.setOnClickListener {
            val card = getSmallDeck(1)[0]
            ivCard2.setImageDrawable(getCardDrawable(card))
        }
    }



    fun getCardDrawable(card: Card?) : Drawable {
        val uri = getCardDrawableURI(card)
        val imageResource = resources.getIdentifier(uri, null, packageName)
        return resources.getDrawable(imageResource, null)
    }
}
