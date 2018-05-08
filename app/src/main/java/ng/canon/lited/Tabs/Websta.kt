package ng.canon.lited.Tabs


import android.Manifest
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v4.widget.SwipeRefreshLayout
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.widget.ProgressBar
import android.widget.Toast
import com.downloader.*
import com.facebook.ads.Ad
import com.facebook.ads.AdError
import com.facebook.ads.InterstitialAd
import com.facebook.ads.InterstitialAdListener
import com.fondesa.kpermissions.extension.listeners
import com.fondesa.kpermissions.extension.permissionsBuilder
import com.lmntrx.android.library.livin.missme.ProgressDialog
import com.shashank.sony.fancygifdialoglib.FancyGifDialog
import com.shashank.sony.fancygifdialoglib.FancyGifDialogListener
import im.delight.android.webview.AdvancedWebView
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.ObservableOnSubscribe
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.websta.view.*
import ng.canon.lited.HQ.Saveit
import ng.canon.lited.HQ.injectCSS
import ng.canon.lited.HQ.injectScriptFile

import ng.canon.lited.R
import org.json.JSONObject
import java.io.File


/**
 * A simple [Fragment] subclass.
 */
class Websta : Fragment(), SwipeRefreshLayout.OnRefreshListener, AdvancedWebView.Listener {
    var observable: Observable<String>? = null
    var linkBox: ArrayList<String>? = null
    var counter = 0
    var showing = false
    var inject = false
    var isStory =false
    var mInterstitialAd: InterstitialAd? = null
    var id = ""
    var mines = ""
    var progressBox: ProgressDialog? = null
    var webby:AdvancedWebView? = null
    var swipes:SwipeRefreshLayout? = null
    var loader:ProgressBar? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val v = inflater.inflate(R.layout.websta, container, false)

        webby = v.webby
        swipes = v.swipes
        loader = v.loader

        val address = "https://www.instagram.com"
        webby!!.setListener(activity!!, this);
        initBrowser(address)

        val config = PRDownloaderConfig.newBuilder()
                .setDatabaseEnabled(true)
                .build()
        PRDownloader.initialize(activity!!.applicationContext, config)



        mInterstitialAd = InterstitialAd(activity!!.applicationContext,getString(R.string.intersistal))


        return v
    }





    override fun onPageError(errorCode: Int, description: String?, failingUrl: String?) {

    }

    override fun onDownloadRequested(url: String?, suggestedFilename: String?, mimeType: String?, contentLength: Long, contentDisposition: String?, userAgent: String?) {

    }

    override fun onExternalPageRequest(url: String?) {

    }

    override fun onPageStarted(url: String?, favicon: Bitmap?) {
        swipes!!.isRefreshing = true
        loader!!.progress = 0
        loader!!.visibility = View.VISIBLE
    }

    override fun onPageFinished(url: String?) {
        swipes!!.isRefreshing = false
        injectScriptFile(activity!!.applicationContext, "jquery.js",webby!!)
        injectScriptFile(activity!!.applicationContext, "arrive.js",webby!!)
        injectScriptFile(activity!!.applicationContext, "gram/photo.js", webby!!)
        injectScriptFile(activity!!.applicationContext, "gram/video.js", webby!!)
        injectScriptFile(activity!!.applicationContext, "gram/story.js", webby!!)
        injectCSS(activity!!.applicationContext, "gram/insta.css",webby!!)
        injectCSS(activity!!.applicationContext, "gram/style.css",webby!!)

        loader!!.visibility = View.GONE
    }



    override fun onRefresh() {

    }





    // Method to  initialize Webview
    fun initBrowser( address:String){


        val jsInterface = JavaScriptInterface(activity!!)
        val webSettings = webby!!.settings
        webSettings.javaScriptEnabled = true
        webSettings.allowUniversalAccessFromFileURLs = true
        webSettings.domStorageEnabled = true
        webSettings.cacheMode = WebSettings.LOAD_NO_CACHE;
        webby!!.setLayerType(View.LAYER_TYPE_HARDWARE, null);

        webby!!.webChromeClient = object : WebChromeClient() {

            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                super.onProgressChanged(view, newProgress)

                loader!!.progress = newProgress


            }
        };
        webby!!.loadUrl(address)
        webby!!.addJavascriptInterface(jsInterface, "JSInterface")
        webby!!.setOnKeyListener(object: View.OnKeyListener{
            override fun onKey(v: View?, keyCode: Int, event: KeyEvent?): Boolean {

                if (event!!.getAction()!=KeyEvent.ACTION_DOWN)
                    return true;

                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    if (webby!!.canGoBack()) {
                        webby!!.goBack()
                    } else {

                    }
                    return true
                }
                return false

            }


        })

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)
        webby!!.onActivityResult(requestCode, resultCode, intent)





    }

    override fun onPause() {
        webby!!.onPause()
        super.onPause()
    }

    override fun onResume() {
        webby!!.onResume()
        super.onResume()
    }

    override fun onDestroy() {
        webby!!.onDestroy()
        super.onDestroy()
    }




// Method to communicate with the webviews's javascript and receive callbacks


    inner class JavaScriptInterface(private val activity: Activity) {

        @JavascriptInterface
        fun startVideo(videoAddress: String,mime:String) {

            id = videoAddress.replace("/p/","").replace("/","")
            mines = mime

            if (ContextCompat.checkSelfPermission(activity!!.applicationContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {


                activity!!.runOnUiThread {

                    if (mime.contains("video")){



                        linkCore(id)

                    }else{
                        linkCore(id)

                    }


                }











            }else{

                Toast.makeText(activity!!.applicationContext,"No permissions",Toast.LENGTH_LONG).show()

                activity!!.runOnUiThread {

                    lasma()

                }

            }





        }

        @JavascriptInterface
        fun reloader(vinky: String){


            if (ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {






            }else{

                activity!!.runOnUiThread {

                    lasma()

                }
            }




        }
    }


    fun mrSave(url:String,name:String){

    }



    fun permissionDialog(){


        activity!!.runOnUiThread {
            FancyGifDialog.Builder(activity!!)
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

                            Toast.makeText(activity!!.applicationContext,""+getString(R.string.permissionMessage),Toast.LENGTH_LONG).show()
                            activity!!.finish()

                        }


                    })
                    .build()
        }


    }




    fun lasma(){
        val request = permissionsBuilder(Manifest.permission.WRITE_EXTERNAL_STORAGE).build()
        request.send()
        request.listeners {

            onAccepted { permissions ->


                activity!!.runOnUiThread {

                    if (mines.contains("video")){

                        linkCore(id)
                        loadMoney()


                    }else{


                        linkCore(id)
                        loadMoney()


                    }

                }






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









    fun opeStore(){

        val uri = Uri.parse("market://details?id=" + activity!!.packageName)
        val goToMarket = Intent(Intent.ACTION_VIEW, uri)
        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY or
                Intent.FLAG_ACTIVITY_NEW_DOCUMENT or
                Intent.FLAG_ACTIVITY_MULTIPLE_TASK)

        try {
            startActivity(goToMarket)
        }catch (e: ActivityNotFoundException){
            startActivity(Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=" + activity!!.packageName)))

        }
    }














    fun loadMoney(){

        mInterstitialAd!!.setAdListener(object : InterstitialAdListener {
            override fun onLoggingImpression(p0: Ad?) {


            }

            override fun onAdLoaded(p0: Ad?) {

                mInterstitialAd!!.show();

            }

            override fun onError(p0: Ad?, p1: AdError?) {


            }

            override fun onInterstitialDismissed(p0: Ad?) {


            }

            override fun onAdClicked(p0: Ad?) {


            }

            override fun onInterstitialDisplayed(p0: Ad?) {


            }


        })



        // Load ads into Interstitial Ads
        mInterstitialAd!!.loadAd()


    }













    ///Save Instagram Media


    fun linkCore(videoID: String) {
        progressBox = ProgressDialog(activity!!)
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
                        Toast.makeText(activity!!.applicationContext, "" + e.message, Toast.LENGTH_LONG).show()

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

                MediaScannerConnection.scanFile(activity!!.applicationContext, arrayOf(filed.absolutePath), null) { path, uri ->

                    if (counter != linkBox!!.size){

                        activity!!.runOnUiThread {
                            mrSave()
                        }
                    }else{


                        activity!!.runOnUiThread {
                            counter = 0
                            progressBox!!.dismiss()

                        }


                    }

                }


            }


        })


    }

}
