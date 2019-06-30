package com.put.hangman

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.put.hangman.adapter.RankingItemAdapter
import com.put.hangman.model.Ranking
import kotlinx.android.synthetic.main.activity_ranking.*

class RankingActivity : AppCompatActivity() {
    private val rankRef = FirebaseDatabase.getInstance().getReference("ranking")
    lateinit var adapter: RankingItemAdapter
    private val rank = mutableListOf<Ranking>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ranking)

        adapter = RankingItemAdapter(this@RankingActivity, rank)
        ranking_list.adapter = adapter

        getRanking()

        back_button.setOnClickListener { this.finish() }
    }

    private fun getRanking() {
        val rankListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                rank.clear()
                val items = dataSnapshot.children.iterator()

                while (items.hasNext()) {
                    val currentItem = items.next()
                    val map = currentItem.value as HashMap<String, Any>
                    val row = Ranking(map["email"] as String, (map["points"] as Long).toString().toInt())
                    rank.add(row)
                }

                rank.sortByDescending { it.points }
                println(rank)
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.w("RankingActivity", "getRanking:onCancelled", databaseError.toException())
            }
        }

        rankRef.addValueEventListener(rankListener)
    }
}
