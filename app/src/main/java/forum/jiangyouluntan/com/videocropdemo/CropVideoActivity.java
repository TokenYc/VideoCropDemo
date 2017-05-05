package forum.jiangyouluntan.com.videocropdemo;

import android.app.ProgressDialog;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import forum.jiangyouluntan.com.videocropdemo.commonvideolibrary.VideoResampler;
import forum.jiangyouluntan.com.videocropdemo.utils.FileUtils;
import forum.jiangyouluntan.com.videocropdemo.utils.VideoDecoder;

/**
 * Created by Administrator on 2017/5/5.
 */

public class CropVideoActivity extends AppCompatActivity {
    private TextView tv_path;
    private Button btn_crop;

    private String videp_path;//视频路径

    private ProgressDialog progressDialog;
    VideoResampler resampler ;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cropvideo);
        tv_path = (TextView) findViewById(R.id.tv_path);
        btn_crop = (Button) findViewById(R.id.btn_crop);

        videp_path = getIntent().getStringExtra("videp_path");
        tv_path.setText(videp_path);
        btn_crop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                showDialog();
//                if (VideoDecoder.decodeVideo(videp_path, 10*1000*1000, 10*1000*1000)){
//                    dissmissDialog();
//                }
                resampler = new VideoResampler();
                resampler.setOutput(Uri.parse(FileUtils.ROOT_PATH+"/aabbcc.mp4"));
                try {
                    resampler.start();
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
            }
        });
    }

    private void showDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("正在压缩");
        }
        progressDialog.show();
    }

    private void dissmissDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }
}
