package ng.canon.lited.StoryCam
import android.media.MediaPlayer
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.halilibo.bettervideoplayer.BetterVideoCallback
import com.halilibo.bettervideoplayer.BetterVideoPlayer
import kotlinx.android.synthetic.main.story_video_player.*
import ng.canon.lited.R
import java.lang.Exception


class VideoPlayer : AppCompatActivity(), BetterVideoCallback {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.story_video_player)


        val ints = intent.extras
        val source = ints.getString("videoID")
        player.setCallback(this)
        val uri = Uri.parse(source)
        player.setSource(uri)

    }


    override fun onPaused(player: BetterVideoPlayer?) {

        player!!.pause();

    }

    override fun onToggleControls(player: BetterVideoPlayer?, isShowing: Boolean) {


    }

    override fun onError(player: BetterVideoPlayer?, e: Exception?) {


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


    override fun onDestroy() {
        super.onDestroy()
    }

}
