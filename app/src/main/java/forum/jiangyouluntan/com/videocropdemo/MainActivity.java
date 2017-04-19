package forum.jiangyouluntan.com.videocropdemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {


    private Button btn_FFmpegAndroidLibraryActivity,btn_androidffmpeglibrary;


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

    }

    private void initListener() {
        btn_FFmpegAndroidLibraryActivity.setOnClickListener(this);
        btn_androidffmpeglibrary.setOnClickListener(this);
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

        }
    }
}
