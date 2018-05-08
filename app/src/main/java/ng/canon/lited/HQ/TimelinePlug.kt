package ng.canon.lited.HQ
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Typeface
import android.support.v7.widget.RecyclerView
import android.text.Spannable
import android.text.SpannableString
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import kotlinx.android.synthetic.main.post_row.view.*

import ng.canon.lited.GlideApp
import ng.canon.lited.R
import ng.canon.lited.Suite.InstaPage
import ng.canon.lited.Suite.VideoPostView

data class TimelineModel(val name:String,val usernamed:String, val type:String,val id:String ,val caption:String,val comment_count:String,val likes:String,val owner_id:String,val profile_url:String,val display_url:String,val shortcode:String,val followers:String)
class TimelineAdapter(var c: Context, var insta: InstaPage, var lists: ArrayList<TimelineModel>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        var v = LayoutInflater.from(c).inflate(R.layout.post_row, parent, false)
        return Item(v)
    }


    override fun getItemCount(): Int {

        return lists.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as Item).bindData(lists[position])

        holder.itemView.saver.setOnClickListener {

            insta.looku(lists[position].shortcode)
        }

        holder.itemView.more.setOnClickListener {

            insta.showDialog(lists[position].shortcode)
        }

        val irus = lists[position].type

        holder.itemView.playImg.setOnClickListener {



            if (irus.contains("GraphVideo")){

                val ontent = Intent(holder.itemView.context, VideoPostView::class.java)
                ontent.putExtra("shortcode",lists[position].shortcode)
                ontent.putExtra("like_counts",lists[position].likes)
                ontent.putExtra("caption",lists[position].caption)
                ontent.putExtra("owner_id",lists[position].id)
                holder.itemView.context.startActivity(ontent)
            }


        }

    }

    class Item(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindData(_list: TimelineModel) {

            val like_num = format(_list.likes.toDouble())
            itemView.likes.text = "❤ $like_num likes"
            itemView.followers.text = format(_list.comment_count.toDouble()) + " comments"
            val  str =  SpannableString(_list.usernamed +" "+ _list.caption)
            str.setSpan(StyleSpan(Typeface.BOLD), 0, _list.usernamed.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            val follower_num = ""
            itemView.description.text = str
            itemView.username.text = _list.usernamed

            val requestOptions = RequestOptions()
            requestOptions.placeholder(R.drawable.ic_place)
            GlideApp.with(itemView.context).setDefaultRequestOptions(requestOptions).load(_list.display_url).into(itemView.cover)



            GlideApp.with(itemView.context)
                    .asBitmap()
                    .dontAnimate()
                    .load(_list.profile_url)
                    .into(object : SimpleTarget<Bitmap>(){
                        override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {

                            itemView.profile_image.setImageBitmap(resource)

                        }


                    })


            if (_list.type.contains("GraphVideo")){

                itemView.playImg.visibility = View.VISIBLE
                itemView.album.visibility = View.GONE
                itemView.saver.text = itemView.context.getString(R.string.save_video)

            }


            if (_list.type.contains("GraphSidecar")){

                itemView.playImg.visibility = View.GONE
                itemView.album.visibility = View.VISIBLE
                itemView.saver.text = itemView.context.getString(R.string.save_media)

            }

            if (_list.type.contains("GraphImage")){

                itemView.playImg.visibility = View.GONE
                itemView.album.visibility = View.GONE
                itemView.saver.text = itemView.context.getString(R.string.save_photo)
            }






        }
    }
}