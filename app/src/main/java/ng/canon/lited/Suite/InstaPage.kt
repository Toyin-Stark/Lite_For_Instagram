package ng.canon.lited.Suite

import android.Manifest
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaScannerConnection
import android.opengl.Visibility
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.view.MenuItem
import android.view.View
import android.webkit.CookieManager
import android.widget.Toast
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.ObservableOnSubscribe
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.trial.*
import kotlinx.android.synthetic.main.user_stats.*
import org.json.JSONObject
import org.jsoup.Jsoup
import android.webkit.WebView
import android.webkit.WebViewClient
import com.downloader.*
import com.fondesa.kpermissions.extension.listeners
import com.fondesa.kpermissions.extension.permissionsBuilder
import com.kennyc.bottomsheet.BottomSheet
import com.kennyc.bottomsheet.BottomSheetListener
import com.lmntrx.android.library.livin.missme.ProgressDialog
import com.shashank.sony.fancygifdialoglib.FancyGifDialog
import com.shashank.sony.fancygifdialoglib.FancyGifDialogListener
import ng.canon.lited.GlideApp
import ng.canon.lited.HQ.*
import ng.canon.lited.R
import ng.canon.lited.Stories.StoryView
import java.io.File


class InstaPage : AppCompatActivity() {

    var observable: Observable<String>? = null
    var timelist:ArrayList<TimelineModel>? = null
    var timex: TimelineAdapter? = null
    var post = ""
    var follower = ""
    var following = ""
    var name = ""
    var username = ""
    var description = ""
    var end_cursor = ""
    var profile_pic_url = ""
    var has_next_page = false

    var userID = ""
    var nameID = ""
    var linkBox: ArrayList<String>? = null
    var counter = 0

    var vIDS = ""
    var linearLayoutManager:LinearLayoutManager? = null
    var progressBox:ProgressDialog? = null
    var csrf_token = ""
    var rhx_gis = ""
    var xgram = ""
    var hashment = ""

    var user_respond = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.trial)

         val intentID = intent.extras
         userID = intentID.getString("userID")
         nameID = intentID.getString("nameID")

        toolbar.title = "@$nameID"


        open.setOnClickListener {

            val ontent = Intent(applicationContext, StoryView::class.java)
            ontent.putExtra("storyID",username)
            startActivity(ontent)
        }


        val config = PRDownloaderConfig.newBuilder()
                .setDatabaseEnabled(true)
                .build()
        PRDownloader.initialize(applicationContext, config)

        recyclerview.adapter = null
        linearLayoutManager = LinearLayoutManager(applicationContext)
        linearLayoutManager!!.orientation = LinearLayoutManager.VERTICAL;
        recyclerview.layoutManager = linearLayoutManager
        recyclerview.addOnScrollListener(object : EndlessRecyclerOnScrollListener() {
            override fun onLoadMore() {
                if (has_next_page){


                    scanMore(userID,nameID)

                }else{



                }
            }


        })



        scanStories(userID,nameID)

    }






    fun scanStories(userIDs:String,nameIDs:String)
    {
        swipes.isRefreshing = true

        timelist = ArrayList<TimelineModel>()


        observable = Observable.create(object: ObservableOnSubscribe<String> {
            override fun subscribe(subscriber: ObservableEmitter<String>) {



                try {

                   val doc = Jsoup.connect("https://www.instagram.com/$nameIDs/")
                           .userAgent("Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2228.0 Safari/537.36")
                           .timeout(100000)
                           .get()

                    val elements = doc.getElementsByTag("script")

                    for (element in elements) {


                        if (element.data().contains("window._sharedData =")){

                            val fulltext = element.data()
                            user_respond = fulltext.replace("window._sharedData =","")



                        }
                    }


                    val user_json =  JSONObject(user_respond)
                    csrf_token = user_json.getJSONObject("config").getString("csrf_token")
                    rhx_gis =  user_json.getString("rhx_gis")


                    val stats = user_json.getJSONObject("entry_data").getJSONArray("ProfilePage").getJSONObject(0).getJSONObject("graphql")
                    post = stats.getJSONObject("user").getJSONObject("edge_owner_to_timeline_media").getInt("count").toString()
                    follower = stats.getJSONObject("user").getJSONObject("edge_followed_by").getInt("count").toString()
                    following = stats.getJSONObject("user").getJSONObject("edge_follow").getInt("count").toString()
                    name =  stats.getJSONObject("user").getString("full_name")
                    description = stats.getJSONObject("user").getString("biography")
                    username  = nameIDs
                    profile_pic_url = stats.getJSONObject("user").getString("profile_pic_url")

                    val timeline_post = stats.getJSONObject("user").getJSONObject("edge_owner_to_timeline_media")
                    end_cursor = timeline_post.getJSONObject("page_info").getString("end_cursor")
                    has_next_page = timeline_post.getJSONObject("page_info").getBoolean("has_next_page")

                    val timeline_array = timeline_post.getJSONArray("edges")

                    if (timeline_array != null && timeline_array.length() > 0){


                        for (i in 0..timeline_array.length() -1){

                            val jsonobj = timeline_array.getJSONObject(i)
                            val type = jsonobj.getJSONObject("node").getString("__typename")
                            val name = name
                            val usernames = nameIDs
                            val id =   jsonobj.getJSONObject("node").getString("id")
                            val kaptionArray = jsonobj.getJSONObject("node").getJSONObject("edge_media_to_caption").getJSONArray("edges")
                            var caption = ""
                            if (kaptionArray != null && kaptionArray.length() > 0){

                                caption = kaptionArray.getJSONObject(0).getJSONObject("node").getString("text")
                            }

                            val comment_count = jsonobj.getJSONObject("node").getJSONObject("edge_media_to_comment").getInt("count").toString()
                            val likes = jsonobj.getJSONObject("node").getJSONObject("edge_liked_by").getInt("count").toString()
                            val owner_id = jsonobj.getJSONObject("node").getJSONObject("owner").getString("id")
                            val profile_photo = profile_pic_url
                            val followerx = ""
                            val displayURL = jsonobj.getJSONObject("node").getString("display_url")
                            val shortcode = jsonobj.getJSONObject("node").getString("shortcode")

                            timelist!!.add(TimelineModel(name,usernames,type,id,caption,comment_count,likes,owner_id,profile_photo,displayURL,shortcode,followerx))


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

                        GlideApp.with(applicationContext).load(profile_pic_url).into(imageView)
                        posts.text = format(post.toDouble())
                        followet.text = format(follower.toDouble())
                        followingz.text = format(following.toDouble())
                        usernamer.text = name
                        bio.text = description
                        handle.text = username
                       appbarlayout.visibility = View.VISIBLE
                        if (!response.contains("unavailable")){
                            timex = TimelineAdapter(this@InstaPage,this@InstaPage, timelist!!)
                            recyclerview.adapter = timex
                            timex!!.notifyDataSetChanged()
                            recyclerview.visibility = View.VISIBLE



                        }else{


                        }


                    }
                })

    }


    // LOAD MORE POSTS FROM TIMELINE










    fun scanMore(userIDs:String,nameIDs:String)
    {

        swipes.isRefreshing = true

        observable = Observable.create(object: ObservableOnSubscribe<String> {
            override fun subscribe(subscriber: ObservableEmitter<String>) {


                try {

                     hashment = "$rhx_gis:{\"id\":\"$userIDs\",\"first\":50,\"after\":\"$end_cursor\"}"
                    xgram = Converters.getMd5Key(hashment)
                    val user_respond = Cookieit("https://www.instagram.com/graphql/query/?query_hash=42323d64886122307be10013ad2dcc44&variables={\"id\":\"$userIDs\",\"first\":50,\"after\":\"$end_cursor\"}",xgram,csrf_token)
                    val timeline_json =  JSONObject(user_respond)

                    val stats = timeline_json.getJSONObject("data")


                    val timeline_post = stats.getJSONObject("user").getJSONObject("edge_owner_to_timeline_media")
                    end_cursor = timeline_post.getJSONObject("page_info").getString("end_cursor")
                    has_next_page = timeline_post.getJSONObject("page_info").getBoolean("has_next_page")

                    val timeline_array = timeline_post.getJSONArray("edges")

                    if (timeline_array != null && timeline_array.length() > 0){


                        for (i in 0..timeline_array.length() -1){

                            val jsonobj = timeline_array.getJSONObject(i)
                            val type = jsonobj.getJSONObject("node").getString("__typename")
                            val name = name
                            val usernames = nameIDs
                            val id =   jsonobj.getJSONObject("node").getString("id")
                            val kaptionArray = jsonobj.getJSONObject("node").getJSONObject("edge_media_to_caption").getJSONArray("edges")
                            var caption = ""
                            if (kaptionArray != null && kaptionArray.length() > 0){

                                caption = kaptionArray.getJSONObject(0).getJSONObject("node").getString("text")
                            }

                            val comment_count = jsonobj.getJSONObject("node").getJSONObject("edge_media_to_comment").getString("count")
                            val likes = jsonobj.getJSONObject("node").getJSONObject("edge_media_preview_like").getString("count")
                            val owner_id = jsonobj.getJSONObject("node").getJSONObject("owner").getString("id")
                            val profile_photo = profile_pic_url
                            val followerx = ""
                            val displayURL = jsonobj.getJSONObject("node").getString("display_url")
                            val shortcode = jsonobj.getJSONObject("node").getString("shortcode")

                            timelist!!.add(TimelineModel(name,usernames,type,id,caption,comment_count,likes,owner_id,profile_photo,displayURL,shortcode,followerx))


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
                            recyclerview.visibility = View.VISIBLE


                        }else{


                        }


                    }
                })

    }




    // Method to check out


    fun looku(videoIdent:String){

        vIDS = videoIdent

        if (ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {


            linkCore(videoIdent)


        }else{
            runOnUiThread {

                lasma()

            }
        }

    }




    fun linkCore(videoID: String) {
        progressBox = ProgressDialog(this@InstaPage)
        progressBox!!.setTextColor(android.R.color.primary_text_light)
        progressBox!!.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL)
        progressBox!!.setIndeterminate(true)
        progressBox!!.setMessage("Please wait")
        progressBox!!.setCancelable(false)
        progressBox!!.show()
        linkBox = ArrayList<String>()
        observable = Observable.create(object : ObservableOnSubscribe<String> {
            override fun subscribe(subscriber: ObservableEmitter<String>) {


                try {

                    val start = "https://www.instagram.com/p/$videoID/"
                    val end = "?__a=1"
                    val instaUrl = start + end
                    val responding = Saveit(instaUrl)
                    val jsons = JSONObject(responding)
                    val iru = jsons.getJSONObject("graphql").getJSONObject("shortcode_media").getString("__typename")

                    if (iru.contains("GraphSidecar")) {

                        val mediaBox = jsons.getJSONObject("graphql").getJSONObject("shortcode_media").getJSONObject("edge_sidecar_to_children").getJSONArray("edges")

                        for (i in 0..mediaBox.length() - 1) {

                            val jsonobj = mediaBox.getJSONObject(i)
                            val genre = jsonobj.getJSONObject("node").getString("__typename")
                            if (genre.contains("GraphVideo")) {

                                val videoURL = jsonobj.getJSONObject("node").getString("video_url")
                                val viewURL = jsonobj.getJSONObject("node").getString("display_url")

                                linkBox!!.add(videoURL)
                            } else {

                                val photoURL = jsonobj.getJSONObject("node").getString("display_url")
                                linkBox!!.add(photoURL)

                            }
                        }

                    }


                    if (iru.contains("GraphImage")) {

                        val imageLink = jsons.getJSONObject("graphql").getJSONObject("shortcode_media").getString("display_url")
                        linkBox!!.add(imageLink)

                    }


                    if (iru.contains("GraphVideo")) {


                        val videoLink = jsons.getJSONObject("graphql").getJSONObject("shortcode_media").getString("video_url")
                        val photoLink = jsons.getJSONObject("graphql").getJSONObject("shortcode_media").getString("display_url")

                        linkBox!!.add(videoLink)


                    }



                    subscriber.onNext("")

                } catch (e: Exception) {

                    subscriber.onError(e)
                }


                subscriber.onComplete()
            }
        })

        observable!!.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<String> {
                    override fun onSubscribe(d: Disposable) {


                    }

                    override fun onComplete() {

                        counter = 0
                        mrSave()



                    }

                    override fun onError(e: Throwable) {
                        Toast.makeText(applicationContext, "" + e.message, Toast.LENGTH_LONG).show()

                    }

                    override fun onNext(response: String) {


                    }
                })

    }





    // mrSave Method



    fun mrSave() {

        progressBox!!.setIndeterminate(false)
        progressBox!!.setMax(100)
        progressBox!!.setProgress(0)
        progressBox!!.setCancelable(false)
        var extension = ""
        val urld = linkBox!![counter]

        if(urld.contains(".jpg")){
            extension = "jpg"
        }

        if(urld.contains(".png")){
            extension = "png"
        }


        if(urld.contains(".gif")){
            extension = "gif"
        }


        if (urld.contains(".mp4")){

            extension = "mp4"
        }
        val sizes = linkBox!!.size
        progressBox!!.setMessage("Downloading $counter /  $sizes")
        var desc = getString(R.string.bannerTitle)
        val timeStamp = System.currentTimeMillis()
        val name = "insta_$timeStamp.$extension"
        val dex = File(Environment.getExternalStorageDirectory().absolutePath, "lite")
        if (!dex.exists())
            dex.mkdirs()

        val filed = File(dex, name)


        val downloadId = PRDownloader.download(urld, dex.absolutePath, name)
                .build()
                .setOnProgressListener(object : OnProgressListener {
                    override fun onProgress(progresss: Progress?) {
                        val progressPercent = progresss!!.currentBytes * 100 / progresss.totalBytes
                        progressBox!!.setProgress(progressPercent.toInt())


                    }


                }).start(object : OnDownloadListener {
            override fun onError(error: Error?) {


            }

            override fun onDownloadComplete() {
                counter += 1
                progressBox!!.setMessage("Downloading $counter /  $sizes")

                MediaScannerConnection.scanFile(applicationContext, arrayOf(filed.absolutePath), null) { path, uri ->

                    if (counter != linkBox!!.size){

                        runOnUiThread {
                            mrSave()
                        }
                    }else{


                        runOnUiThread {
                            counter = 0
                            progressBox!!.dismiss()

                        }


                    }

                }


            }


        })


    }
















    fun lasma(){
        val request = permissionsBuilder(Manifest.permission.WRITE_EXTERNAL_STORAGE).build()
        request.send()
        request.listeners {

            onAccepted { permissions ->

                looku(vIDS)

            }

            onDenied { permissions ->

                permissionDialog()
            }

            onPermanentlyDenied { permissions ->
                permissionDialog()

            }

            onShouldShowRationale { permissions, nonce ->
                permissionDialog()

            }
        }
        // load permission methods here
    }





    fun permissionDialog(){


        runOnUiThread {
            FancyGifDialog.Builder(this@InstaPage)
                    .setTitle(getString(R.string.permissionTitle))
                    .setMessage(getString(R.string.permissionMessage))
                    .setNegativeBtnText(getString(R.string.permissionNegative))
                    .setPositiveBtnBackground("#FF4081")
                    .setPositiveBtnText(getString(R.string.permissionPositive))
                    .setNegativeBtnBackground("#FFA9A7A8")
                    .setGifResource(R.drawable.permit)   //Pass your Gif here
                    .isCancellable(false)
                    .OnPositiveClicked(object : FancyGifDialogListener {
                        override fun OnClick() {

                            lasma()


                        }


                    })

                    .OnNegativeClicked(object : FancyGifDialogListener {
                        override fun OnClick() {

                            Toast.makeText(this@InstaPage,""+getString(R.string.permissionMessage),Toast.LENGTH_LONG).show()
                            finish()

                        }


                    })
                    .build()
        }


    }




    // Bottom Menu

   fun showDialog(postId:String){

       BottomSheet.Builder(this@InstaPage)
               .setSheet(R.menu.insta)
               .setTitle(R.string.options)
               .setListener(object:BottomSheetListener{
                   override fun onSheetItemSelected(p0: BottomSheet, item: MenuItem?, p2: Any?) {

                       val id = item!!.itemId
                       when(id){

                           R.id.download -> {

                               runOnUiThread {

                                   linkCore(postId)
                               }

                           }

                           R.id.copy -> {

                              runOnUiThread {
                                  val message = getString(R.string.copied)
                                  val sendURL = "https://www.instagram.com/p/$postId"
                                  val clipboard =  getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                  val clip = ClipData.newPlainText("instagram", sendURL)
                                  clipboard.primaryClip = clip;
                                  Toast.makeText(applicationContext,""+message,Toast.LENGTH_LONG).show()
                              }
                           }

                           R.id.share ->{


                               runOnUiThread {

                                   val sendURL = "https://www.instagram.com/p/$postId/?r=wa1"
                                   val sharingIntent = Intent(Intent.ACTION_SEND)
                                   sharingIntent.type = "text/plain"
                                   sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Instagram")
                                   sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, sendURL)
                                   startActivity(Intent.createChooser(sharingIntent, resources.getString(R.string.share_using)));

                               }

                           }

                       }


                   }

                   override fun onSheetDismissed(p0: BottomSheet, p1: Any?, p2: Int) {


                   }

                   override fun onSheetShown(p0: BottomSheet, p1: Any?) {

                   }


               })
               .show();
   }













}
