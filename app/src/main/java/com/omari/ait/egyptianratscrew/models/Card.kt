package com.omari.ait.egyptianratscrew.models

import com.omari.ait.egyptianratscrew.util.Suite

data class Card(val value: Int, val suite: Suite) {
    override fun toString(): String {
        if (value == 11) return "J${suite.str}"
        else if (value == 12) return "Q${suite.str}"
        else if (value == 13) return "K${suite.str}"
        else if (value == 14) return "A${suite.str}"
        else return "$value${suite.str}"
    }
}