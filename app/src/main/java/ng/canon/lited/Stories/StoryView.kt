package  ng.canon.lited.Stories

import android.Manifest
import android.app.DownloadManager
import android.app.DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.support.v4.content.ContextCompat
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.GridLayoutManager
import android.view.View
import android.widget.Toast
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.esafirm.rxdownloader.RxDownloader
import com.facebook.ads.Ad
import com.facebook.ads.AdError
import com.facebook.ads.InterstitialAd
import com.facebook.ads.InterstitialAdListener
import com.fondesa.kpermissions.extension.listeners
import com.fondesa.kpermissions.extension.permissionsBuilder
import com.shashank.sony.fancygifdialoglib.FancyGifDialog
import com.shashank.sony.fancygifdialoglib.FancyGifDialogListener
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.ObservableOnSubscribe
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.profile_lego.*
import kotlinx.android.synthetic.main.story_view.*
import ng.canon.lited.HQ.Saveit
import ng.canon.lited.R
import ng.canon.lited.Stories.StoryAdapter
import ng.canon.lited.Stories.Story_Model
import org.json.JSONObject
import java.io.File

class StoryView : AppCompatActivity(),SwipeRefreshLayout.OnRefreshListener {

    var observable: Observable<String>? = null
    var linkBox = ArrayList<String>()
    var storylist:ArrayList<Story_Model>? = null
    var storyx: StoryAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.story_view)

        val intentID = intent.extras
        val storyID = intentID.getString("storyID")
        swipes.setOnRefreshListener { this }

        toolbarx.title = "@$storyID"
        toolbarx.setTitleTextColor(ContextCompat.getColor(applicationContext,android.R.color.white) )
        setSupportActionBar(toolbarx)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true);
        supportActionBar!!.setHomeButtonEnabled(true);

        downloader.setOnClickListener {

            if (ContextCompat.checkSelfPermission(this@StoryView, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {

                saveAll()

            }else{

                lasma()
            }
        }


        scanStories(storyID)


    }



    override fun onRefresh() {


    }


    fun scanStories(storyIDs:String)
    {
        swipes.visibility = View.VISIBLE
        base.visibility   =View.GONE
        swipes.isRefreshing = true

        storylist = ArrayList<Story_Model>()
        recyclerview.adapter = null
        recyclerview.isNestedScrollingEnabled = true
        recyclerview.layoutManager =  GridLayoutManager(this@StoryView,2)

        observable = Observable.create(object: ObservableOnSubscribe<String> {
            override fun subscribe(subscriber: ObservableEmitter<String>) {


                try {

                    val instaUrl = "https://api.storiesig.com/stories/$storyIDs"
                    val respond = Saveit(instaUrl)
                    val json = JSONObject(respond)


                    val stories = json.getJSONArray("items")

                    if (stories != null && stories.length() > 0){


                        for (i in 0..stories.length() -1){

                            val jsonobj = stories.getJSONObject(i)
                            val type = jsonobj.getInt("media_type")

                            if (type == 1){

                                val link = jsonobj.getJSONObject("image_versions2").getJSONArray("candidates").getJSONObject(0).getString("url")
                                val images = jsonobj.getJSONObject("image_versions2").getJSONArray("candidates").getJSONObject(0).getString("url")
                                storylist!!.add(Story_Model(link, images))
                                linkBox.add(link)


                            }


                            if (type == 2){

                                val link = jsonobj.getJSONArray("video_versions").getJSONObject(0).getString("url")
                                val images = jsonobj.getJSONObject("image_versions2").getJSONArray("candidates").getJSONObject(0).getString("url")
                                storylist!!.add(Story_Model(link, images))
                                linkBox.add(link)


                            }


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

                        if (!response.contains("unavailable")){
                            storyx = StoryAdapter(this@StoryView, storylist!!)
                            recyclerview.adapter = storyx
                            storyx!!.notifyDataSetChanged()
                            recyclerview.visibility = View.VISIBLE
                            swipes.isRefreshing = false
                            base.visibility   = View.VISIBLE


                        }else{

                            swipes.isRefreshing = false
                            sadImage.visibility = View.VISIBLE
                            sadText.visibility  = View.VISIBLE


                        }


                    }
                })

    }


    fun saveAll(){

        for (i in 0..linkBox.size -1){

            mrSave(linkBox[i])

        }


    }








    // Method to save files


    fun mrSave(urld: String){

        Toast.makeText(this@StoryView,""+getString(R.string.downloadStart),Toast.LENGTH_LONG).show()
        val rxDownloader = RxDownloader(this@StoryView)
        var extension = ""
        var desc = ""



            if(urld.contains(".jpg")){
                extension = "jpg"
            }

            if(urld.contains(".png")){
                extension = "png"
            }


            if(urld.contains(".gif")){
                extension = "gif"
            }

            desc = getString(R.string.downloadPhoto)




        if (urld.contains(".mp4")){

            extension = "mp4"
            desc = getString(R.string.downloadVideo)


        }



        val timeStamp =  System.currentTimeMillis()
        val filename = "story_"+"_"+timeStamp
        val name = filename + "." + extension
        val dex = File(Environment.getExternalStorageDirectory().absolutePath, "lite")
        if (!dex.exists())
            dex.mkdirs()

        val Download_Uri = Uri.parse(urld)
        val downloadManager =  getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val request =  DownloadManager.Request(Download_Uri);
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE);
        request.setAllowedOverRoaming(false)
        request.setTitle(name)
        request.setDescription(desc)
        request.setVisibleInDownloadsUi(true)
        request.allowScanningByMediaScanner()
        request.setNotificationVisibility(VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        request.setDestinationInExternalPublicDir("/storysave",  name)

        rxDownloader.download(request).subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(object: Observer<String> {
                    override fun onComplete() {


                    }

                    override fun onError(e: Throwable) {


                    }

                    override fun onNext(t: String) {


                    }

                    override fun onSubscribe(d: Disposable) {


                    }


                })

    }


    //permission method

    fun lasma(){
        val request = permissionsBuilder(Manifest.permission.WRITE_EXTERNAL_STORAGE).build()
        request.send()
        request.listeners {

            onAccepted { permissions ->
                saveAll()
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







    //=================================================================================================//


    fun permissionDialog(){


        runOnUiThread {
            FancyGifDialog.Builder(this@StoryView)
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

                    .OnNegativeClicked(object :FancyGifDialogListener{
                        override fun OnClick() {

                            Toast.makeText(this@StoryView,""+getString(R.string.permissionMessage),Toast.LENGTH_LONG).show()
                            finish()

                        }


                    })
                    .build()
        }


    }
}
