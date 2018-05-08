package ng.canon.lited.Suite

import android.graphics.Bitmap
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.widget.Toast
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.ObservableOnSubscribe
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.greedy.*
import kotlinx.android.synthetic.main.plan.*
import kotlinx.android.synthetic.main.top_bin.*
import ng.canon.lited.GlideApp
import ng.canon.lited.HQ.*
import ng.canon.lited.R
import org.json.JSONObject

class Greedy : AppCompatActivity() {

    var tagID = ""
    var countID = ""
    var gridLayoutManager: GridLayoutManager? = null
    var lineLayoutManager: LinearLayoutManager? = null

    var has_next_page = false
    var observable: Observable<String>? = null
    var timelist:ArrayList<ashModel>? = null
    var taglist:ArrayList<LinearModel>? = null

    var timex: ashAdapter? = null
    var tagx: LinearAdapter? = null
    var pstCount = 0
    var tagURL = ""
    var end_cursor = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.greedy)
        val intentID = intent.extras
        tagID = intentID.getString("tagID")
        countID = intentID.getString("countID")


        top_recycler.adapter = null
        gridLayoutManager = GridLayoutManager(applicationContext,3)
        top_recycler.layoutManager = gridLayoutManager
        top_recycler.isNestedScrollingEnabled = true

        recycler.adapter = null
        lineLayoutManager = LinearLayoutManager(applicationContext,LinearLayoutManager.HORIZONTAL,false)
        recycler.layoutManager = lineLayoutManager

        tagID = tagID



        top_recycler.addOnScrollListener(object : EndlessRecyclerOnScrollListener() {
            override fun onLoadMore() {
                if (has_next_page){



                    scanMore(tagID)

                }else{



                }
            }


        })

        scanStories(tagID)

    }



    override fun onPause() {
        if (observable != null){
            observable!!.unsubscribeOn(Schedulers.io())
        }
        super.onPause()
    }

    override fun onDestroy() {
        if (observable != null){
            observable!!.unsubscribeOn(Schedulers.io())
        }
        super.onDestroy()
    }


    override fun onResume() {
        super.onResume()
    }






    fun scanStories(tagsIDs:String)
    {
        load_progress.visibility = View.GONE
        swipes!!.isRefreshing =true
        appbarlayout!!.visibility = View.GONE
        contents!!.visibility = View.GONE

        timelist = ArrayList<ashModel>()
        taglist = ArrayList<LinearModel>()

        container.visibility = View.GONE
        top_recycler!!.visibility = View.GONE

        observable = Observable.create(object: ObservableOnSubscribe<String> {
            override fun subscribe(subscriber: ObservableEmitter<String>) {



                try {


                    val hashUrl = "https://www.instagram.com/web/search/topsearch/?context=blended&query=%23$tagID"
                    val responded = Saveit(hashUrl)
                    val json = JSONObject(responded)
                    val hash = json.getJSONArray("hashtags")


                    for (i in 0..hash.length() -1){

                        val tagobj = hash.getJSONObject(i)
                        val name = tagobj.getJSONObject("hashtag").getString("name")
                        var id =   tagobj.getJSONObject("hashtag").getString("media_count")

                        taglist!!.add(LinearModel(name,id))
                    }



                    val user_respond = Saveit("https://www.instagram.com/explore/tags/$tagsIDs/?__a=1")
                    val timeline_json =  JSONObject(user_respond)

                    val stats = timeline_json.getJSONObject("graphql")


                    val timeline_post = stats.getJSONObject("hashtag").getJSONObject("edge_hashtag_to_media")
                    val timeline_top = stats.getJSONObject("hashtag").getJSONObject("edge_hashtag_to_top_posts")

                    tagURL = stats.getJSONObject("hashtag").getString("profile_pic_url")
                    pstCount = stats.getJSONObject("hashtag").getJSONObject("edge_hashtag_to_media").getInt("count")

                    end_cursor = timeline_post.getJSONObject("page_info").getString("end_cursor")
                    has_next_page = timeline_post.getJSONObject("page_info").getBoolean("has_next_page")


                    val timeline_array = timeline_post.getJSONArray("edges")
                    val top_array =  timeline_top.getJSONArray("edges")

                    if (top_array != null && top_array.length() > 0){


                        for (i in 0..top_array.length() -1){

                            val jsonobj = top_array.getJSONObject(i)
                            val displayURL = jsonobj.getJSONObject("node").getString("display_url")
                            val shortcode = jsonobj.getJSONObject("node").getString("shortcode")
                            val isVideo   = jsonobj.getJSONObject("node").getBoolean("is_video")
                            val caption =  jsonobj.getJSONObject("node").getJSONObject("edge_media_to_caption").getJSONArray("edges").getJSONObject(0).getJSONObject("node").getString("text")
                            val like_count = jsonobj.getJSONObject("node").getJSONObject("edge_liked_by").getInt("count")
                            val owner_id = jsonobj.getJSONObject("node").getJSONObject("owner").getString("id")

                            timelist!!.add(ashModel(shortcode,displayURL,isVideo,caption,""+like_count,owner_id))


                        }

                    }




                    if (timeline_array != null && timeline_array.length() > 0){


                        for (i in 0..timeline_array.length() -1){

                            val jsonobj = timeline_array.getJSONObject(i)
                            val displayURL = jsonobj.getJSONObject("node").getString("display_url")
                            val shortcode = jsonobj.getJSONObject("node").getString("shortcode")
                            val isVideo   = jsonobj.getJSONObject("node").getBoolean("is_video")
                            val caption =  jsonobj.getJSONObject("node").getJSONObject("edge_media_to_caption").getJSONArray("edges").getJSONObject(0).getJSONObject("node").getString("text")
                            val like_count = jsonobj.getJSONObject("node").getJSONObject("edge_liked_by").getInt("count")
                            val owner_id = jsonobj.getJSONObject("node").getJSONObject("owner").getString("id")

                            timelist!!.add(ashModel(shortcode,displayURL,isVideo,caption,""+like_count,owner_id))

                            subscriber.onNext("available")

                        }

                    }else{

                        subscriber.onNext("unavailable")

                    }




                }catch (e:Exception){

                    subscriber.onError(e)
                }


                subscriber.onComplete()
            }
        })

        observable!!.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object: Observer<String> {
                    override fun onSubscribe(d: Disposable) {



                    }

                    override fun onComplete() {
                    }

                    override fun onError(e: Throwable) {
                        Toast.makeText(applicationContext,""+e.message, Toast.LENGTH_LONG).show()

                        swipes!!.isRefreshing = false

                    }

                    override fun onNext(response: String) {

                        if (!response.contains("unavailable")){

                            tagx = LinearAdapter(applicationContext,this@Greedy, taglist!!)
                            recycler.adapter = tagx
                            recycler.visibility = View.VISIBLE
                            tagx!!.notifyDataSetChanged()
                            swipes.isRefreshing = false

                            timex = ashAdapter(applicationContext,this@Greedy, timelist!!)
                            top_recycler!!.adapter = timex
                            timex!!.notifyDataSetChanged()
                            top_recycler!!.visibility = View.VISIBLE
                            appbarlayout!!.visibility = View.VISIBLE
                            container.visibility = View.VISIBLE
                            contents!!.visibility = View.VISIBLE

                            psts!!.text = format(pstCount.toDouble()) + " posts"
                            GlideApp.with(applicationContext)
                                    .asBitmap()
                                    .dontAnimate()
                                    .load(tagURL)
                                    .into(object : SimpleTarget<Bitmap>(){
                                        override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {

                                            profile_image!!.setImageBitmap(resource)

                                        }


                                    })


                        }else{

                            Toast.makeText(applicationContext,"what?", Toast.LENGTH_LONG).show()

                        }


                    }
                })

    }



    //=====================================================================================================================================
    //LOad More HAshtags
    //=====================================================================================================================================





















    fun scanMore(tagsIDs:String)
    {
        swipes!!.isRefreshing = true

        observable = Observable.create(object: ObservableOnSubscribe<String> {
            override fun subscribe(subscriber: ObservableEmitter<String>) {



                try {



                    val user_respond = Saveit("https://www.instagram.com/explore/tags/$tagsIDs/?__a=1&max_id=$end_cursor")
                    val timeline_json =  JSONObject(user_respond)

                    val stats = timeline_json.getJSONObject("graphql")


                    val timeline_post = stats.getJSONObject("hashtag").getJSONObject("edge_hashtag_to_media")
                    end_cursor = timeline_post.getJSONObject("page_info").getString("end_cursor")
                    has_next_page = timeline_post.getJSONObject("page_info").getBoolean("has_next_page")

                    val timeline_array = timeline_post.getJSONArray("edges")

                    if (timeline_array != null && timeline_array.length() > 0){


                        for (i in 0..timeline_array.length() -1){

                            val jsonobj = timeline_array.getJSONObject(i)
                            val displayURL = jsonobj.getJSONObject("node").getString("display_url")
                            val shortcode = jsonobj.getJSONObject("node").getString("shortcode")
                            val isVideo   = jsonobj.getJSONObject("node").getBoolean("is_video")
                            val caption =  jsonobj.getJSONObject("node").getJSONObject("edge_media_to_caption").getJSONArray("edges").getJSONObject(0).getJSONObject("node").getString("text")
                            val like_count = jsonobj.getJSONObject("node").getJSONObject("edge_liked_by").getInt("count")
                            val owner_id = jsonobj.getJSONObject("node").getJSONObject("owner").getString("id")

                            timelist!!.add(ashModel(shortcode,displayURL,isVideo,caption,""+like_count,owner_id))

                            subscriber.onNext("available")

                        }

                    }else{

                        subscriber.onNext("unavailable")

                    }




                }catch (e:Exception){

                    subscriber.onError(e)
                }


                subscriber.onComplete()
            }
        })

        observable!!.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object: Observer<String> {
                    override fun onSubscribe(d: Disposable) {



                    }

                    override fun onComplete() {


                    }

                    override fun onError(e: Throwable) {
                        Toast.makeText(applicationContext,""+e.message, Toast.LENGTH_LONG).show()

                        swipes!!.isRefreshing = false

                    }

                    override fun onNext(response: String) {
                        swipes!!.isRefreshing = false

                        if (!response.contains("unavailable")){
                            timex!!.notifyDataSetChanged()
                            load_progress!!.visibility = View.GONE

                        }else{


                        }


                    }
                })

    }




    fun restart(tags:String){

        scanStories(tags)

    }

}
