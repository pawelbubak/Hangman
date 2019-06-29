package com.put.hangman.model

class Question(var question: String, var options: Map<String, String>, var answer: String) {
    constructor() : this("", emptyMap<String, String>(), "")

    override fun toString(): String {
        return "Question(question='$question', options=$options, answer=$answer)"
    }


}