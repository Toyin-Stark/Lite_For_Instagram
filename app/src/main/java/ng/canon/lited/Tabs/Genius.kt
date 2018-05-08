package ng.canon.lited.Tabs


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.SearchView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import io.reactivex.Observable
import io.reactivex.Observable.create
import io.reactivex.ObservableEmitter
import io.reactivex.ObservableOnSubscribe
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.instagram.view.*
import lib.kingja.switchbutton.SwitchMultiButton
import ng.canon.lited.HQ.*
import ng.canon.lited.R

import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader


class Genius : Fragment(),SwipeRefreshLayout.OnRefreshListener {


    var isTag = false
    var observable: Observable<String>? = null
    var linkBox = ArrayList<String>()
    var userlist:ArrayList<UserModel>? = null
    var taglist:ArrayList<TagModel>? = null

    var userx: UserAdapter? = null
    var tagx: TagAdapter? = null

    var swipes:SwipeRefreshLayout? = null
    var recyclerview:RecyclerView? = null
    var queryString = ""
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val v = inflater.inflate(R.layout.instagram, container, false)

        swipes = v.swipes
        recyclerview = v.recyclerview

        v.findwidget.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            override fun onQueryTextChange(newText: String?): Boolean {


                return false
            }

            override fun onQueryTextSubmit(query: String?): Boolean {
                queryString = query!!


                if (isTag){

                    scanTags(query!!.replace("@","").replace(" ","").replace("#",""))

                }else{

                    scanUsers(query!!.replace("@","").replace(" ","").replace("#",""))

                }

                return false
            }


        })


        v.switchtabs.setOnSwitchListener(object: SwitchMultiButton.OnSwitchListener{
            override fun onSwitch(position: Int, tabText: String?) {

                if (position == 0){

                    isTag = false
                    generateUsers()

                }

                if (position == 1){


                    isTag = true
                    generateTag()

                }

            }


        })



        stories()

        return v
    }



    override fun onRefresh() {


    }


    fun scanUsers(userID:String)
    {

        swipes!!.isRefreshing = true
        swipes!!.visibility = View.VISIBLE

        userlist = ArrayList<UserModel>()
        recyclerview!!.adapter = null
        recyclerview!!.isNestedScrollingEnabled = false
        recyclerview!!.layoutManager =  LinearLayoutManager(activity!!.applicationContext, LinearLayoutManager.VERTICAL, false)

        observable = create(object: ObservableOnSubscribe<String> {
            override fun subscribe(subscriber: ObservableEmitter<String>) {


                try {

                    val instaUrl = "https://www.instagram.com/web/search/topsearch/?query=$userID&count=100"
                    val respond = Saveit(instaUrl)
                    val json = JSONObject(respond)
                    val users = json.getJSONArray("users")


                    for (i in 0..users.length() -1){

                        val jsonobj = users.getJSONObject(i)
                        val name = jsonobj.getJSONObject("user").getString("username")
                        var id =   jsonobj.getJSONObject("user").getString("pk")
                        val avatar = jsonobj.getJSONObject("user").getString("profile_pic_url")
                        val followers = jsonobj.getJSONObject("user").getString("byline")
                        var story = ""
                        if (jsonobj.getJSONObject("user").isNull("latest_reel_media")){
                            story = "loop"

                        }else{

                            story = "nurse"

                        }

                        userlist!!.add(UserModel(name,followers,story,avatar,id))
                    }



                    subscriber.onNext("")

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


                        swipes?.isRefreshing = true

                    }

                    override fun onComplete() {

                        userx = UserAdapter(activity!!.applicationContext, userlist!!)
                        recyclerview?.adapter = userx
                        userx!!.notifyDataSetChanged()
                        recyclerview?.visibility = View.VISIBLE
                        swipes?.isRefreshing = false

                    }

                    override fun onError(e: Throwable) {
                        Toast.makeText(activity!!.applicationContext,""+e.message, Toast.LENGTH_LONG).show()
                        swipes!!.isRefreshing = false
                    }

                    override fun onNext(response: String) {


                    }
                })

    }




    //======================================================================================================================================//
    // Search Tags on Instagram
    //=======================================================================================================================================//






    fun scanTags(tagID:String)
    {

        swipes!!.isRefreshing = true
        swipes!!.visibility = View.VISIBLE

        taglist = ArrayList<TagModel>()
        recyclerview!!.adapter = null
        recyclerview!!.isNestedScrollingEnabled = false
        recyclerview!!.layoutManager =  LinearLayoutManager(activity!!.applicationContext, LinearLayoutManager.VERTICAL, false)

        observable = create(object: ObservableOnSubscribe<String> {
            override fun subscribe(subscriber: ObservableEmitter<String>) {


                try {

                    val instaUrl = "https://www.instagram.com/web/search/topsearch/?context=blended&query=%23$tagID"
                    val respond = Saveit(instaUrl)
                    val json = JSONObject(respond)
                    val hash = json.getJSONArray("hashtags")


                    for (i in 0..hash.length() -1){

                        val jsonobj = hash.getJSONObject(i)
                        val name = jsonobj.getJSONObject("hashtag").getString("name")
                        var id =   jsonobj.getJSONObject("hashtag").getString("media_count")

                        taglist!!.add(TagModel(name,id))
                    }



                    subscriber.onNext("")

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


                        swipes?.isRefreshing = true

                    }

                    override fun onComplete() {

                        tagx = TagAdapter(activity!!.applicationContext, taglist!!)
                        recyclerview?.adapter = tagx
                        tagx!!.notifyDataSetChanged()
                        recyclerview?.visibility = View.VISIBLE
                        swipes?.isRefreshing = false

                    }

                    override fun onError(e: Throwable) {
                        Toast.makeText(activity!!.applicationContext,""+e.message, Toast.LENGTH_LONG).show()
                        swipes!!.isRefreshing = false
                    }

                    override fun onNext(response: String) {


                    }
                })

    }
















    //======================================================================================================================================//
    // Load User Presets
    //=======================================================================================================================================//


    fun stories()
    {
        userlist = ArrayList<UserModel>()
        recyclerview!!.adapter = null
        recyclerview!!.isNestedScrollingEnabled = false
        recyclerview!!.layoutManager =  LinearLayoutManager(activity!!.applicationContext, LinearLayoutManager.VERTICAL, false)

        observable = Observable.create(object: ObservableOnSubscribe<String> {
            override fun subscribe(subscriber: ObservableEmitter<String>) {


                val response = readFile("stories.json")

                subscriber.onNext(response)
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
                        Toast.makeText(activity!!.applicationContext,""+e.message, Toast.LENGTH_LONG).show()

                    }

                    override fun onNext(response: String) {
                        val json = JSONObject(response)
                        val jsonarr = json.getJSONArray("data")

                        for (i in 0..jsonarr.length() - 1) {

                            val jsonobj = jsonarr.getJSONObject(i)

                            val name = jsonobj.getString("name")
                            val id = jsonobj.getString("id")
                            val links = jsonobj.getString("code")
                            val images = jsonobj.getString("images")

                            userlist!!.add(UserModel(name,"3.0m followers",links,images,id))

                        }

                        userx = UserAdapter(activity!!.applicationContext, userlist!!)
                        recyclerview!!.adapter = userx
                        userx!!.notifyDataSetChanged()


                    }
                })

    }


    //======================================================================================================================================//
    // Load Tag Presets
    //=======================================================================================================================================//


    fun tags()
    {
        taglist = ArrayList<TagModel>()
        recyclerview!!.adapter = null
        recyclerview!!.isNestedScrollingEnabled = false
        recyclerview!!.layoutManager =  LinearLayoutManager(activity!!.applicationContext, LinearLayoutManager.VERTICAL, false)

        observable = Observable.create(object: ObservableOnSubscribe<String> {
            override fun subscribe(subscriber: ObservableEmitter<String>) {


                val response = readFile("tags.json")

                subscriber.onNext(response)
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
                        Toast.makeText(activity!!.applicationContext,""+e.message, Toast.LENGTH_LONG).show()

                    }

                    override fun onNext(response: String) {
                        val json = JSONObject(response)
                        val jsonarr = json.getJSONArray("data")

                        for (i in 0..jsonarr.length() - 1) {

                            val jsonobj = jsonarr.getJSONObject(i)

                            val tag_name = jsonobj.getString("name")
                            val post_count = jsonobj.getString("id")


                            taglist!!.add(TagModel(tag_name,post_count))

                        }

                        tagx = TagAdapter(activity!!.applicationContext, taglist!!)
                        recyclerview!!.adapter = tagx
                        tagx!!.notifyDataSetChanged()


                    }
                })

    }



    //read json data from file



    @Throws(IOException::class)
    fun readFile(fileName: String): String {
        var reader: BufferedReader? = null
        reader = BufferedReader(InputStreamReader(activity!!.assets.open(fileName), "UTF-8"))

        var content = ""
        while (true) {
            var line: String? = reader.readLine() ?: break
            content += line

        }

        return content
    }


    fun generateTag(){

        if (queryString == ""){
            tags()
        }else{

            scanTags(queryString)

        }

    }


    fun generateUsers(){

        if (queryString == ""){
            stories()
        }else{

            scanUsers(queryString)

        }

    }
}
