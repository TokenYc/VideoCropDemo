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
            ,btn_MediaCodecActivity;


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

    }

    private void initListener() {
        btn_FFmpegAndroidLibraryActivity.setOnClickListener(this);
        btn_androidffmpeglibrary.setOnClickListener(this);
        btn_FFmpegAndroidLibraryGetAllImageActivity.setOnClickListener(this);
        btn_MediaCodecActivity.setOnClickListener(this);
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

        }
    }
}
