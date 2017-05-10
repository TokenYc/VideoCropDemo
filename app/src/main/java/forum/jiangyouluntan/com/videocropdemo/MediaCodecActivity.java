package forum.jiangyouluntan.com.videocropdemo;

import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

import forum.jiangyouluntan.com.videocropdemo.utils.VideoDecode;

public class MediaCodecActivity extends AppCompatActivity {
    //   /Movies/fffff.mp4
    //   /ZongHeng/temp/video/del_1492062006409.mp4
    //  /ddpaiSDK/video/video.M6.00e00100b534/L_20170412100733_173_173.mp4
    private final String ROOT_PATH = getInnerSDCardPath() + "/相机";
    //    private final String FILE_PATH = getInnerSDCardPath() + "/ZongHeng/temp/video/del_1492062006409.mp4";
    private final String DIR_PATH = ROOT_PATH + "/images/";


    private Button btn_mediacodec;
    private String video_path;
    VideoDecode videodecoder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mediacodec);
        Log.d("root dir", "root dir====>" + Environment.getExternalStorageDirectory().getPath());
        video_path = getIntent().getStringExtra("videp_path");
        File file = new File(video_path);
        if (!file.exists()) {
            Toast.makeText(this, "视频路径不正确！！！", Toast.LENGTH_SHORT).show();
            return;
        }
        Log.e("FILE_PATH", "FILE_PATH==>" + video_path);
        btn_mediacodec = (Button) findViewById(R.id.btn_mediacodec);
        btn_mediacodec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (videodecoder == null) {
                    videodecoder = new VideoDecode();
                }
                try {
                    videodecoder.setSaveFrames(DIR_PATH, 3);   //这里用于设置输出YUV的位置和YUV的格式
                    videodecoder.VideoDecodePrepare(video_path);  //这里要输入待解码视频文件的地址
                    videodecoder.excuate();

                } catch (IOException el) {

                }
            }
        });
    }

    /**
     * 获取内置SD卡路径
     *
     * @return
     */
    public String getInnerSDCardPath() {
        return Environment.getExternalStorageDirectory().getPath();
    }
}
