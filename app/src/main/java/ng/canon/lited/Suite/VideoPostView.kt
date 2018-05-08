package ng.canon.lited.Suite

import android.Manifest
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.bumptech.glide.request.RequestOptions
import com.downloader.*
import com.fondesa.kpermissions.extension.listeners
import com.fondesa.kpermissions.extension.permissionsBuilder
import com.halilibo.bettervideoplayer.BetterVideoCallback
import com.halilibo.bettervideoplayer.BetterVideoPlayer
import com.kennyc.bottomsheet.BottomSheet
import com.kennyc.bottomsheet.BottomSheetListener
import com.lmntrx.android.library.livin.missme.ProgressDialog
import com.shashank.sony.fancygifdialoglib.FancyGifDialog
import com.shashank.sony.fancygifdialoglib.FancyGifDialogListener
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.ObservableOnSubscribe
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.video_post_row.*
import kotlinx.android.synthetic.main.video_post_view.*
import ng.canon.lited.GlideApp
import ng.canon.lited.HQ.Saveit
import ng.canon.lited.R
import ng.canon.lited.Suite.InstaPage
import org.json.JSONObject
import java.io.File

class VideoPostView : AppCompatActivity(), BetterVideoCallback {
    var observable: Observable<String>? = null
    var linkBox: ArrayList<String>? = null

    var usernames = ""
    var follower_count = ""
    var profile_pic = ""
    var counter = 0


    var post_likes = ""
    var post_caption = ""
    var display_url = ""
    var shortcode = ""
    var userid = ""
    var isVideo = false
    var videoSource = ""
    var progressBox: ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.video_post_view)

        val intentID = intent.extras
        shortcode = intentID.getString("shortcode")
        post_likes = intentID.getString("like_counts")
        post_caption = intentID.getString("caption")
        userid =  intentID.getString("owner_id")
        progressBox = ProgressDialog(this@VideoPostView)


        val config = PRDownloaderConfig.newBuilder()
                .setDatabaseEnabled(true)
                .build()
        PRDownloader.initialize(applicationContext, config)

        profile_image.setOnClickListener {

            val ontent = Intent(applicationContext, InstaPage::class.java)
            ontent.putExtra("userID",userid)
            ontent.putExtra("nameID",usernames)
            startActivity(ontent)

        }

        username.setOnClickListener {

            val ontent = Intent(applicationContext, InstaPage::class.java)
            ontent.putExtra("userID",userid)
            ontent.putExtra("nameID",usernames)
            startActivity(ontent)
        }


        followers.setOnClickListener {

            val ontent = Intent(applicationContext, InstaPage::class.java)
            ontent.putExtra("userID",userid)
            ontent.putExtra("nameID",usernames)
            startActivity(ontent)

        }


            toolbarx.title = getString(R.string.is_video)


        setSupportActionBar(toolbarx)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true);
        supportActionBar!!.setHomeButtonEnabled(true);


        more.setOnClickListener {

            showDialog(shortcode,userid,"")
        }


        saver.setOnClickListener {
            looku(shortcode)

        }


        linkCore(shortcode)


    }


    override fun onPause() {
        super.onPause()
        player.pause()
    }


    override fun onDestroy() {
        player.stop()
        super.onDestroy()

    }

    override fun onBackPressed() {
        super.onBackPressed()
    }

    fun linkCore(postID: String) {
        graft.visibility = View.GONE
        swipes.isRefreshing = true

        linkBox = ArrayList<String>()
        observable = Observable.create(object : ObservableOnSubscribe<String> {
            override fun subscribe(subscriber: ObservableEmitter<String>) {


                try {

                    val start = "https://www.instagram.com/p/$postID/"
                    val end = "?__a=1"
                    val instaUrl = start + end
                    val responding = Saveit(instaUrl)
                    val jsons = JSONObject(responding)
                    val owner = jsons.getJSONObject("graphql").getJSONObject("shortcode_media").getJSONObject("owner")
                    profile_pic = owner.getString("profile_pic_url")
                    usernames = owner.getString("username")
                    val iru = jsons.getJSONObject("graphql").getJSONObject("shortcode_media").getString("__typename")

                    if (iru.contains("GraphVideo")) {
                         videoSource = jsons.getJSONObject("graphql").getJSONObject("shortcode_media").getString("video_url")
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
                        GlideApp.with(applicationContext).load(profile_pic).into(profile_image)
                        player.setCallback(this@VideoPostView)
                        val uri = Uri.parse(videoSource)
                        player.setSource(uri)
                        username.text = usernames
                        likes.text = "❤ $post_likes likes"
                        description.text = post_caption
                        followers.text = getString(R.string.profile_tap)
                        if (isVideo){

                            playImg.visibility = View.VISIBLE
                        }else{

                            playImg.visibility = View.GONE

                        }
                        graft.visibility = View.VISIBLE
                        swipes.isRefreshing = false

                        counter = 0




                    }

                    override fun onError(e: Throwable) {
                        Toast.makeText(applicationContext, "" + e.message, Toast.LENGTH_LONG).show()

                    }

                    override fun onNext(response: String) {


                    }
                })

    }






    // Bottom Menu

    fun showDialog(postId:String,useID:String,usename:String){

        BottomSheet.Builder(this@VideoPostView)
                .setSheet(R.menu.page)
                .setTitle(R.string.options)
                .setListener(object: BottomSheetListener {
                    override fun onSheetItemSelected(p0: BottomSheet, item: MenuItem?, p2: Any?) {

                        val id = item!!.itemId
                        when(id){

                            R.id.download -> {

                                runOnUiThread {

                                    linkCore(postId)
                                }

                            }



                            R.id.open -> {

                                runOnUiThread {

                                    val ontent = Intent(applicationContext, InstaPage::class.java)
                                    ontent.putExtra("userID",userid)
                                    ontent.putExtra("nameID",usernames)
                                    startActivity(ontent)
                                }

                            }

                            R.id.copy -> {

                                runOnUiThread {
                                    val message = getString(R.string.copied)
                                    val sendURL = "https://www.instagram.com/p/$postId"
                                    val clipboard =  getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    val clip = ClipData.newPlainText("instagram", sendURL)
                                    clipboard.primaryClip = clip;
                                    Toast.makeText(applicationContext, "" + message, Toast.LENGTH_LONG).show()
                                }
                            }

                            R.id.share ->{


                                runOnUiThread {

                                    val sendURL = "https://www.instagram.com/p/$postId/?r=wa1"
                                    val sharingIntent = Intent(Intent.ACTION_SEND)
                                    sharingIntent.type = "text/plain"
                                    sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "Instagram")
                                    sharingIntent.putExtra(Intent.EXTRA_TEXT, sendURL)
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



    //------------------------------------------------------------------------------------------------------------------
    // PErmission Dialog
    //------------------------------------------------------------------------------------------------------------------



    fun permissionDialog(){


        runOnUiThread {
            FancyGifDialog.Builder(this@VideoPostView)
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

                            Toast.makeText(this@VideoPostView, "" + getString(R.string.permissionMessage), Toast.LENGTH_LONG).show()
                            finish()

                        }


                    })
                    .build()
        }


    }




    //------------------------------------------------------------------------------------------------------------------
    //Permission kite
    //------------------------------------------------------------------------------------------------------------------



    fun lasma(){


        val request = permissionsBuilder(Manifest.permission.WRITE_EXTERNAL_STORAGE).build()
        request.send()
        request.listeners {

            onAccepted { permissions ->

                looku(shortcode)

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



    //----------------------------------------------------------------------------------------------------------------
    // Method to Save Media
    //----------------------------------------------------------------------------------------------------------------




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
                        progressBox!!.setIndeterminate(false)
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
                            progressBox!!.dismiss()

                            counter = 0

                        }


                    }

                }


            }


        })


    }



    //Method to check write permissions

    fun looku(kite:String){


        if (ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {


            runOnUiThread {

                saveCore(kite)

            }


        }else{
            runOnUiThread {

                lasma()

            }
        }

    }


    //-----------------------------------------------------------------------------------------------------------------
    //DownloadMan
    //-----------------------------------------------------------------------------------------------------------------





    fun saveCore(videoID: String) {
        progressBox = ProgressDialog(this@VideoPostView)
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








    override fun onPaused(player: BetterVideoPlayer?) {

        player!!.pause();

    }

    override fun onToggleControls(player: BetterVideoPlayer?, isShowing: Boolean) {


    }

    override fun onError(player: BetterVideoPlayer?, e: java.lang.Exception?) {


    }

    override fun onPreparing(player: BetterVideoPlayer?) {


    }

    override fun onBuffering(percent: Int) {


    }

    override fun onCompletion(player: BetterVideoPlayer?) {


    }

    override fun onStarted(player: BetterVideoPlayer?) {


    }

    override fun onPrepared(player: BetterVideoPlayer?) {

        player!!.start()


    }


}