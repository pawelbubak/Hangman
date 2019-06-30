package com.put.hangman.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.put.hangman.R
import com.put.hangman.model.Ranking

class RankingItemAdapter(context: Context, rankingList: MutableList<Ranking>) : BaseAdapter() {
    private val mInflater: LayoutInflater = LayoutInflater.from(context)
    private var items = rankingList

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val email: String = items[position].email as String
        val points: Int = items[position].points as Int
        val view: View
        val vh: ListRowHolder
        println("$position, $email, $points")

        if (convertView == null) {
            view = mInflater.inflate(R.layout.row_items, parent, false)
            vh = ListRowHolder(view)
            view.tag = vh
        } else {
            view = convertView
            vh = view.tag as ListRowHolder
        }

        vh.position.text = "${position + 1}."
        vh.email.text = email
        vh.points.text = points.toString()

        return view
    }

    override fun getItem(index: Int): Any {
        return items[index]
    }

    override fun getItemId(index: Int): Long {
        return index.toLong()
    }

    override fun getCount(): Int {
        return items.size
    }

    private class ListRowHolder(row: View?) {
        val position: TextView = row!!.findViewById(R.id.position) as TextView
        val email: TextView = row!!.findViewById(R.id.email) as TextView
        val points: TextView = row!!.findViewById(R.id.points) as TextView
    }
}