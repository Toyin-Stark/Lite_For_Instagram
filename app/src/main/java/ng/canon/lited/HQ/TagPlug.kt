package ng.canon.lited.HQ

import android.content.Context
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.tag_row.view.*
import ng.canon.lited.R
import ng.canon.lited.Suite.Greedy
import ng.canon.lited.Suite.HashView

data class TagModel(val tagname:String, val post_count:String)
class TagAdapter(var c: Context, var lists: ArrayList<TagModel>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        var v = LayoutInflater.from(c).inflate(R.layout.tag_row, parent, false)
        return Item(v)
    }


    override fun getItemCount(): Int {

        return lists.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as Item).bindData(lists[position])

    }

    class Item(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindData(_list: TagModel) {


            itemView.username.text = _list.tagname
            itemView.followx.text = _list.post_count



            itemView.setOnClickListener {

                val ontent = Intent(itemView.context, Greedy::class.java)
                ontent.putExtra("tagID",_list.tagname)
                ontent.putExtra("countID",_list.post_count)

                itemView.context.startActivity(ontent)

            }
        }
    }
}