package ng.canon.lited.Suite

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.widget.Toast
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.ObservableOnSubscribe
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.hash_view.*
import ng.canon.lited.HQ.EndlessRecyclerOnScrollListener
import ng.canon.lited.HQ.HashAdapter
import ng.canon.lited.HQ.HashModel
import ng.canon.lited.HQ.Saveit
import ng.canon.lited.R
import org.json.JSONObject
import org.jsoup.Jsoup

class HashView : AppCompatActivity() {
    var tagID = ""
    var countID = ""
    var gridLayoutManager: GridLayoutManager? = null
    var has_next_page = false
    var observable: Observable<String>? = null
    var timelist:ArrayList<HashModel>? = null
    var timex: HashAdapter? = null
    var end_cursor = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.hash_view)

        val intentID = intent.extras
        tagID = intentID.getString("tagID")
        countID = intentID.getString("countID")

        toolbarx.title = "#$tagID"
        setSupportActionBar(toolbarx)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true);
        supportActionBar!!.setHomeButtonEnabled(true);


        recyclerview.adapter = null
        gridLayoutManager = GridLayoutManager(applicationContext,3)
        recyclerview.layoutManager = gridLayoutManager
        recyclerview!!.isNestedScrollingEnabled = false
        recyclerview.addOnScrollListener(object : EndlessRecyclerOnScrollListener() {
            override fun onLoadMore() {
                if (has_next_page){



                    scanMore(tagID)

                }else{



                }
            }


        })

        scanStories(tagID)

    }
























    fun scanStories(tagsIDs:String)
    {
        swipes.isRefreshing = true

        timelist = ArrayList<HashModel>()


        observable = Observable.create(object: ObservableOnSubscribe<String> {
            override fun subscribe(subscriber: ObservableEmitter<String>) {



                try {



                    val user_respond = Saveit("https://www.instagram.com/explore/tags/$tagsIDs/?__a=1")
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

                            timelist!!.add(HashModel(shortcode,displayURL,isVideo,caption,""+like_count,owner_id))

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

                        swipes.isRefreshing = false

                    }

                    override fun onNext(response: String) {
                        swipes.isRefreshing = false

                        if (!response.contains("unavailable")){
                            timex = HashAdapter(this@HashView,this@HashView, timelist!!)
                            recyclerview.adapter = timex
                            timex!!.notifyDataSetChanged()
                            recyclerview.visibility = View.VISIBLE
                            swipes.isRefreshing = false

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
        swipes.isRefreshing = true

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

                            timelist!!.add(HashModel(shortcode,displayURL,isVideo,caption,""+like_count,owner_id))

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

                        swipes.isRefreshing = false

                    }

                    override fun onNext(response: String) {
                        swipes.isRefreshing = false

                        if (!response.contains("unavailable")){
                            timex!!.notifyDataSetChanged()
                            swipes.isRefreshing = false

                        }else{


                        }


                    }
                })

    }


}
