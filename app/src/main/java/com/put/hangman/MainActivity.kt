package com.put.hangman

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.put.hangman.model.Question
import kotlinx.android.synthetic.main.activity_main.*
import com.put.hangman.model.Ranking
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.math.floor
import kotlin.random.Random


class MainActivity : AppCompatActivity() {
    private val mAuth = FirebaseAuth.getInstance()
    private var mDatabase = FirebaseDatabase.getInstance().reference
    private var rankRef: DatabaseReference? = null
    private var questionsRef: DatabaseReference? = null

    private var questions: ArrayList<Question>? = null
    private var globalScore = 0
    private var localRound = 0
    private var localScore = 0
    private var mistake = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        logout_button.setOnClickListener { logout() }
        start_button.setOnClickListener { startGame() }
        rank_button.setOnClickListener { showRanking() }
        reset_button.setOnClickListener { giveUp() }

        answer_1.setOnClickListener { checkAnswer("o1") }
        answer_2.setOnClickListener { checkAnswer("o2") }
        answer_3.setOnClickListener { checkAnswer("o3") }
        answer_4.setOnClickListener { checkAnswer("o4") }


        rankRef = FirebaseDatabase.getInstance().getReference("ranking").child(mAuth.currentUser!!.uid)
        questionsRef = FirebaseDatabase.getInstance().getReference("questions")
        getGlobalScore()
    }

    private fun getGlobalScore() {
        val userListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val rank = dataSnapshot.getValue(Ranking::class.java)
                    globalScore = rank?.points!!
                    score.text = globalScore.toString()
                } else {
                    mAuth.currentUser?.let { mDatabase.child("ranking").child(it.uid).setValue(Ranking(it.email!!, 0)) }
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        }

        rankRef?.addValueEventListener(userListener)
    }

    private fun logout() {
        AuthUI.getInstance().signOut(this@MainActivity).addOnCompleteListener {
            val intent = Intent(this, com.put.hangman.LogoActivity::class.java)
            startActivity(intent)
            this.finish()
        }.addOnFailureListener {
            Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun startGame() {
        showGameControls()
        getQuestions()
        result_picture.setImageResource(R.drawable.s0)
    }

    private fun showRanking() {
        val intent = Intent(this, com.put.hangman.RankingActivity::class.java)
        startActivity(intent)
    }

    private fun giveUp() {
        localScore = floor((localScore / 2).toDouble()).toInt()
        endGame()
        score.text = globalScore.toString()
    }

    private fun endGame() {
        saveScore()
        showStartControls()
        Toast.makeText(this, "You've earned $localScore points!", Toast.LENGTH_SHORT).show()
        localScore = 0
        localRound = 0
        mistake = 0
        question.text = getText(R.string.question)
    }


    private fun showGameControls() {
        rank_button.visibility = View.INVISIBLE
        start_button.visibility = View.INVISIBLE
        reset_button.visibility = View.VISIBLE
        answers.visibility = View.VISIBLE
        round_text.visibility = View.VISIBLE
        round.visibility = View.VISIBLE
    }

    private fun showStartControls() {
        rank_button.visibility = View.VISIBLE
        start_button.visibility = View.VISIBLE
        reset_button.visibility = View.INVISIBLE
        answers.visibility = View.INVISIBLE
        round_text.visibility = View.INVISIBLE
        round.visibility = View.INVISIBLE
    }

    private fun getQuestions() {
        val userListener = object : ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val possibleQuestions = ArrayList<Question>()
                val items = dataSnapshot.children.iterator()

                while (items.hasNext()) {
                    val currentItem = items.next()
                    val map = currentItem.value as HashMap<String, *>
                    val question = Question(
                        map["question"] as String,
                        map["options"] as Map<String, String>,
                        map["answer"] as String
                    )

                    possibleQuestions.add(question)
                }

                drawQuestions(possibleQuestions)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.w("MainActivity", "getQuestions:onCancelled", databaseError.toException())
            }
        }

        questionsRef?.addValueEventListener(userListener)
    }

    private fun drawQuestions(possibleQuestions: ArrayList<Question>) {
        questions = ArrayList()
        while (questions!!.size < 10) {
            val question = drawQuestion(possibleQuestions)
            if (!questions!!.contains(question)) {
                questions!!.add(question)
            }
        }
        showQuestion()
    }

    private fun drawQuestion(possibleQuestions: ArrayList<Question>): Question {
        return possibleQuestions[Random.nextInt(0, possibleQuestions.size)]
    }

    private fun showQuestion() {
        val temp = questions!![localRound]
        question.text = temp.question
        answer_1.text = temp.options["o1"]
        answer_2.text = temp.options["o2"]
        answer_3.text = temp.options["o3"]
        answer_4.text = temp.options["o4"]

        score.text = localScore.toString()
        round.text = (localRound + 1).toString()
    }

    private fun saveScore() {
        globalScore += localScore
        mAuth.currentUser?.let {
            mDatabase.child("ranking").child(it.uid).setValue(Ranking(it.email!!, globalScore))
        }
    }

    private fun checkAnswer(value: String) {
        if (value == questions!![localRound].answer) {
            localScore += localRound + 1
            if (localRound < 9) {
                localRound++
                showQuestion()
            } else {
                endGame()
            }
        } else {
            mistake++
            if (mistake < 4) {
                localScore = floor((localScore / 2).toDouble()).toInt()
                when (mistake) {
                    1 -> result_picture.setImageResource(R.drawable.s1)
                    2 -> result_picture.setImageResource(R.drawable.s2)
                    3 -> result_picture.setImageResource(R.drawable.s3)
                    4 -> result_picture.setImageResource(R.drawable.s4)
                }
                score.text = localScore.toString()
            } else {
                localScore = 0
                endGame()
                result_picture.setImageResource(R.drawable.s5)
                score.text = globalScore.toString()
            }
        }
    }
}
