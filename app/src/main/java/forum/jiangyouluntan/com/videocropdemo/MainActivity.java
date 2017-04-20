package forum.jiangyouluntan.com.videocropdemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {


    private Button btn_FFmpegAndroidLibraryActivity
            ,btn_androidffmpeglibrary
            ,btn_FFmpegAndroidLibraryGetAllImageActivity
            ,btn_MediaCodecActivity
            ,btn_MediaMetadataRetrieverActivity
            ,btn_FFmpegMediaMetadataRetrieverActivity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        initListener();
    }


    private void initView() {
        btn_FFmpegAndroidLibraryActivity = (Button) findViewById(R.id.btn_FFmpegAndroidLibraryActivity);
        btn_androidffmpeglibrary = (Button) findViewById(R.id.btn_androidffmpeglibrary);
        btn_FFmpegAndroidLibraryGetAllImageActivity = (Button) findViewById(R.id.btn_FFmpegAndroidLibraryGetAllImageActivity);
        btn_MediaCodecActivity = (Button) findViewById(R.id.btn_MediaCodecActivity);
        btn_MediaMetadataRetrieverActivity= (Button) findViewById(R.id.btn_MediaMetadataRetrieverActivity);
        btn_FFmpegMediaMetadataRetrieverActivity= (Button) findViewById(R.id.btn_FFmpegMediaMetadataRetrieverActivity);

    }

    private void initListener() {
        btn_FFmpegAndroidLibraryActivity.setOnClickListener(this);
        btn_androidffmpeglibrary.setOnClickListener(this);
        btn_FFmpegAndroidLibraryGetAllImageActivity.setOnClickListener(this);
        btn_MediaCodecActivity.setOnClickListener(this);
        btn_MediaMetadataRetrieverActivity.setOnClickListener(this);
        btn_FFmpegMediaMetadataRetrieverActivity.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_FFmpegAndroidLibraryActivity:
                startActivity(new Intent(this, FFmpegAndroidLibraryActivity.class));
                break;
            case R.id.btn_androidffmpeglibrary:
                startActivity(new Intent(this, AndroidFFmpegLibraryActivity.class));
                break;
            case R.id.btn_FFmpegAndroidLibraryGetAllImageActivity:
                startActivity(new Intent(this, FFmpegAndroidLibraryGetAllImageActivity.class));
                break;
            case R.id.btn_MediaCodecActivity:
                startActivity(new Intent(this, MediaCodecActivity.class));
                break;
            case R.id.btn_MediaMetadataRetrieverActivity:
                startActivity(new Intent(this, MediaMetadataRetrieverActivity.class));
                break;
            case R.id.btn_FFmpegMediaMetadataRetrieverActivity:
                startActivity(new Intent(this, FFmpegMediaMetadataRetrieverActivity.class));
                break;


        }
    }
}
