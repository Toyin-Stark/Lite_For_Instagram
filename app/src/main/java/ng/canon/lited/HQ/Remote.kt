package ng.canon.lited.HQ

import android.Manifest
import android.app.Activity
import android.app.Notification
import android.app.NotificationManager
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Handler
import android.support.design.widget.Snackbar
import android.support.v7.app.AlertDialog
import java.io.IOException
import java.util.*
import android.util.Base64
import android.view.View
import java.io.InputStream
import java.util.regex.Pattern
import android.support.v4.app.NotificationCompat
import android.support.v4.content.ContextCompat
import android.util.Patterns
import android.view.Gravity
import android.webkit.CookieManager
import android.webkit.WebView
import ng.canon.lited.R
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONObject
import java.text.DecimalFormat
import java.util.concurrent.TimeUnit



val suffix = arrayOf("", "k", "m", "b", "t")
val MAX_LENGTH = 4

fun format(number: Double): String {
    var r = DecimalFormat("##0E0").format(number)
    r = r.replace("E[0-9]".toRegex(), suffix[Character.getNumericValue(r.get(r.length - 1)) / 3])
    while (r.length > MAX_LENGTH || r.matches("[0-9]+\\.[a-z]".toRegex())) {
        r = r.substring(0, r.length - 2) + r.substring(r.length - 1)
    }
    return r
}



fun Cookieit(link:String,gis:String,cook:String):String{
    val cookies = CookieManager.getInstance().getCookie("https://www.instagram.com")

    val agent = "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2228.0 Safari/537.36"
    var pink = ""
    val saveclient = OkHttpClient().newBuilder()
            .addInterceptor(object : Interceptor {
                override fun intercept(chain: Interceptor.Chain?): Response {
                    val original = chain!!.request()
                    val authorized = original.newBuilder()
                            .addHeader("X-Instagram-GIS",gis)
                            .addHeader("Cookie", "csrftoken=$cook")
                            .addHeader("user-agent", agent)

                            .build()
                    return chain.proceed(authorized)
                }

            })
            .connectTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS).build()
    val saverequest = Request.Builder()
            .url(link)
            .build()
    val response = saveclient.newCall(saverequest).execute()


    val json = JSONObject(response.body()!!.string())


    return json.toString()
}






// INJECT JAVASCRIPT INTO WEBVIEW
 fun injectScriptFile(context: Context,scriptFile: String,mWebView: WebView) {
    val rand = Random()
    val verse = rand.nextInt(80 - 65) + 65
    val input: InputStream
    try {
        input = context.assets.open(scriptFile)
        val buffer = ByteArray(input.available())
        input.read(buffer)
        input.close()

        // String-ify the script byte-array using BASE64 encoding !!!
        val encoded = Base64.encodeToString(buffer, Base64.NO_WRAP)

        mWebView.evaluateJavascript("javascript:(function() {" +
                "var parent = document.getElementsByTagName('head').item(0);" +
                "var script = document.createElement('script');" +
                "script.type = 'text/javascript';" +
                // Tell the browser to BASE64-decode the string into your script !!!
                "script.innerHTML = decodeURIComponent(escape(window.atob('" + encoded + "')));" +
                "parent.appendChild(script)" +
                "})()") { }


    } catch (e: IOException) {
        // TODO Auto-generated catch block
        e.printStackTrace()
    }

}


// INJECT CSS INTO WEBVIEW
 fun injectCSS(context: Context,filespaces: String,mWebView: WebView) {

    try {
        val inputStream = context.assets.open(filespaces)
        val buffer = ByteArray(inputStream.available())
        inputStream.read(buffer)
        inputStream.close()
        val encoded = Base64.encodeToString(buffer, Base64.NO_WRAP)
        mWebView!!.loadUrl("javascript:(function() {" +
                "var parent = document.getElementsByTagName('head').item(0);" +
                "var style = document.createElement('style');" +
                "style.type = 'text/css';" +
                // Tell the browser to BASE64-decode the string into your script !!!
                "style.innerHTML = decodeURIComponent(escape(window.atob('" + encoded + "')));" +
                "parent.appendChild(style)" +
                "})()")
    } catch (e: Exception) {
        e.printStackTrace()
    }

}



// EXTRACT LINKS FROM STRINGS
fun pullLinks(text: String): ArrayList<String> {
    val links = ArrayList<String>()

    //String regex = "\\(?\\b(http://|www[.])[-A-Za-z0-9+&@#/%?=~_()|!:,.;]*[-A-Za-z0-9+&@#/%=~_()|]";
    val regex = "\\(?\\b(https?://|www[.]|ftp://)[-A-Za-z0-9+&@#/%?=~_()|!:,.;]*[-A-Za-z0-9+&@#/%=~_()|]"

    val p = Pattern.compile(regex)
    val m = p.matcher(text)

    while (m.find()) {
        var urlStr = m.group()

        if (urlStr.startsWith("(") && urlStr.endsWith(")")) {
            urlStr = urlStr.substring(1, urlStr.length - 1)
        }

        links.add(urlStr)
    }

    return links
}




// SHOW PERMISSION PROMT DIALOG

fun showDialog(context: Context)
{
    AlertDialog.Builder(context)
            .setTitle(R.string.permissionTitle)
            .setMessage(R.string.permissionMessage)
            .setPositiveButton(R.string.permissionPositive,object: DialogInterface.OnClickListener{
                override fun onClick(dialog: DialogInterface?, which: Int) {
                }


            })

            .setNegativeButton(R.string.permissionNegative,object:DialogInterface.OnClickListener{
                override fun onClick(dialog: DialogInterface?, which: Int) {
                }


            })
            .setCancelable(false)
            .show()

}



//SHOW SNACKBAR
fun snackUp(context: Context,message:String,view: View)
{
    val snacks = Snackbar.make(view, message, Snackbar.LENGTH_LONG)
    snacks.view.setBackgroundColor(ContextCompat.getColor(context, R.color.colorGreen))
    snacks.show()
}


//KONTROLLER CLASS
fun loopBoy(mWebView: WebView,context: Context){

    val handler = Handler()
    val delay = 3000 //milliseconds

    handler.postDelayed(object : Runnable {
        override fun run() {

            handler.postDelayed(this, delay.toLong())
        }
    }, delay.toLong())


}




// REQUEST STORAGE PERMISSION




//Permission Checker


fun Checkmate(activity: Activity,context: Context){

    if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {


    }else{


        // load permission request method here

    }

}















fun Saveit(link:String):String{

    var pink = ""
    val saveclient = OkHttpClient().newBuilder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS).build()
    val saverequest = Request.Builder()
            .url(link)
            .build()
    val response = saveclient.newCall(saverequest).execute()


    val json = JSONObject(response.body()!!.string())


    return json.toString()
}





//Save Video Dialog


//Save Video Dialog






fun extracTors(text: String): Array<String> {
    val links = ArrayList<String>()
    val m = Patterns.WEB_URL.matcher(text)
    while (m.find()) {
        val urls = m.group()
        links.add(urls)
    }

    return links.toTypedArray()
}











fun getScaledBounds(imageSize: Bounds, boundary: Bounds): Bounds
{
    val widthRatio = boundary.m_width / imageSize.m_width
    val heightRatio = boundary.m_height / imageSize.m_height
    val ratio = Math.min(widthRatio, heightRatio)
    return Bounds((imageSize.m_width * ratio).toInt().toDouble(), (imageSize.m_height * ratio).toInt().toDouble())
}









