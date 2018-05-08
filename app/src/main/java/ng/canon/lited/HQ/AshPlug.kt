package ng.canon.lited.HQ

import android.content.Context
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.hash_row.view.*
import com.bumptech.glide.request.RequestOptions
import ng.canon.lited.GlideApp
import ng.canon.lited.R
import ng.canon.lited.Suite.Greedy
import ng.canon.lited.Suite.PostView
import ng.canon.lited.Suite.VideoPostView


data class ashModel(val shortcode:String,val display:String, val isVideos:Boolean,val caption:String,val likes:String, val id:String)
class ashAdapter(var c: Context, var hashs: Greedy, var lists: ArrayList<ashModel>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        var v = LayoutInflater.from(c).inflate(R.layout.hash_row, parent, false)
        return Item(v)
    }


    override fun getItemCount(): Int {

        return lists.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as Item).bindData(lists[position])


        val iru = lists[position].isVideos
        holder.itemView.cover.setOnClickListener {


            if (iru){
                val ontent = Intent(holder.itemView.context, VideoPostView::class.java)
                ontent.putExtra("shortcode",lists[position].shortcode)
                ontent.putExtra("like_counts",lists[position].likes)
                ontent.putExtra("caption",lists[position].caption)
                ontent.putExtra("owner_id",lists[position].id)
                holder.itemView.context.startActivity(ontent)

            }else{

                val ontent = Intent(holder.itemView.context, PostView::class.java)
                ontent.putExtra("shortcode",lists[position].shortcode)
                ontent.putExtra("displayurl",lists[position].display)
                ontent.putExtra("like_counts",lists[position].likes)
                ontent.putExtra("caption",lists[position].caption)
                ontent.putExtra("owner_id",lists[position].id)
                ontent.putExtra("isVideo",lists[position].isVideos)
                holder.itemView.context.startActivity(ontent)

            }



        }




    }

    class Item(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindData(_list: ashModel) {
            val requestOptions = RequestOptions()
            requestOptions.placeholder(R.drawable.ic_place)
            GlideApp.with(itemView.context).setDefaultRequestOptions(requestOptions).load(_list.display).into(itemView.cover)

            if (_list.isVideos){

                itemView.playImg.visibility = View.VISIBLE
            }

        }
    }
}