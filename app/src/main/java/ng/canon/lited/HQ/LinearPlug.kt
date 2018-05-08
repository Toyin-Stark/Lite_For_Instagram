package ng.canon.lited.HQ

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.linear_row.view.*
import ng.canon.lited.R
import ng.canon.lited.Suite.Greedy

data class LinearModel(val tagname:String, val post_count:String)
class LinearAdapter(var c: Context, var greed:Greedy, var lists: ArrayList<LinearModel>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        var v = LayoutInflater.from(c).inflate(R.layout.linear_row, parent, false)
        return Item(v)
    }


    override fun getItemCount(): Int {

        return lists.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as Item).bindData(lists[position])

        holder.itemView.setOnClickListener {

            greed.restart(lists[position].tagname)
        }

    }

    class Item(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindData(_list: LinearModel) {


            itemView.username.text = "#" + _list.tagname



        }
    }
}