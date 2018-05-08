package ng.canon.lited.Stories

import android.content.Context
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.story_row.view.*
import ng.canon.lited.GlideApp
import ng.canon.lited.R
import ng.canon.lited.StoryCam.PhotoPlayer
import ng.canon.lited.StoryCam.VideoPlayer


data class Story_Model(var link:String,var images:String)

class StoryAdapter(var context: Context, var arraylists: ArrayList<Story_Model>) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        var v = LayoutInflater.from(context).inflate(R.layout.story_row, parent, false)
        return Item(v)

    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as Item).bindData(arraylists[position])


    }

    override fun getItemCount(): Int {
        return arraylists.size
    }



    class Item(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindData(_data: Story_Model) {
            val requestOptions = RequestOptions()
            requestOptions.placeholder(R.drawable.ic_place)
            GlideApp.with(itemView.context).setDefaultRequestOptions(requestOptions).load(_data.images).into(itemView.cover)
            if (_data.link.contains(".jpg")){

                itemView.playImg.visibility= View.GONE
            }else{

                itemView.playImg.visibility= View.VISIBLE

            }



            itemView.setOnClickListener {

                if (_data.link.contains(".jpg")){


                    val ontent = Intent(itemView.context, PhotoPlayer::class.java)
                    ontent.putExtra("imageID",_data.link)
                    itemView.context.startActivity(ontent)
                }else{
                    val bontent = Intent(itemView.context, VideoPlayer::class.java)
                    bontent.putExtra("videoID",_data.link)
                    itemView.context.startActivity(bontent)

                }

            }
        }





    }

}
