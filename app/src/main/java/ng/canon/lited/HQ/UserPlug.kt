package ng.canon.lited.HQ

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import kotlinx.android.synthetic.main.profile_row.view.*
import ng.canon.lited.GlideApp
import ng.canon.lited.R
import ng.canon.lited.Suite.InstaPage


data class UserModel(val title:String, val followers:String,val story:String ,val imagelink:String,val id:String)
class UserAdapter(var c: Context, var lists: ArrayList<UserModel>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        var v = LayoutInflater.from(c).inflate(R.layout.profile_row, parent, false)
        return Item(v)
    }


    override fun getItemCount(): Int {

        return lists.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as Item).bindData(lists[position])

    }

    class Item(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindData(_list: UserModel) {

            GlideApp.with(itemView.context)
                    .asBitmap()
                    .dontAnimate()
                    .load(_list.imagelink)
                    .into(object : SimpleTarget<Bitmap>(){
                        override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {

                            itemView.profile_image.setImageBitmap(resource)

                        }


                    })


            itemView.username.text = _list.title
            itemView.followers.text = _list.followers



            itemView.setOnClickListener {

                val ontent = Intent(itemView.context, InstaPage::class.java)
                ontent.putExtra("userID",_list.id)
                ontent.putExtra("nameID",_list.title)

                itemView.context.startActivity(ontent)

            }
        }
    }
}