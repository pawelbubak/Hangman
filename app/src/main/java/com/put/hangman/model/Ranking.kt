package com.put.hangman.model

class Ranking(var email: String?, var points: Int?) {
    constructor() : this("", 0)

    override fun toString(): String {
        return "Ranking(email='$email', points=$points)"
    }
}