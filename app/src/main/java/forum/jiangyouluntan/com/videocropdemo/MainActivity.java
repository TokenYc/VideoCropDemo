package forum.jiangyouluntan.com.videocropdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.RelativeLayout;

import java.util.HashMap;

import forum.jiangyouluntan.com.videocropdemo.listVideo.widget.TextureVideoView;

public class MainActivity extends AppCompatActivity {

    private static final String FILE_PATH = "/storage/emulated/0/DCIM/Camera/VID_20170218_210205.mp4";
    private TextureVideoView videoView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        android.media.MediaMetadataRetriever mmr = new android.media.MediaMetadataRetriever();
        mmr.setDataSource(FILE_PATH);
        String duration;
        String width = null;
        String height = null;
        try {
            duration = mmr.extractMetadata(android.media.MediaMetadataRetriever.METADATA_KEY_DURATION);
            width = mmr.extractMetadata(android.media.MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH);
            height = mmr.extractMetadata(android.media.MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT);
            Log.d("video", "width====>" + width + "height======>" + height);

        } catch (Exception ex) {
            Log.e("video", "MediaMetadataRetriever exception " + ex);
        } finally {
            mmr.release();
        }

        videoView = (TextureVideoView) findViewById(R.id.videoView);
        RelativeLayout.LayoutParams lp= (RelativeLayout.LayoutParams) videoView.getLayoutParams();
        lp.width=Integer.parseInt(width)/2;
        lp.height=Integer.parseInt(height)/2;
        videoView.setVideoPath(FILE_PATH);
        videoView.start();
    }
}
