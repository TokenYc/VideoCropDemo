package forum.jiangyouluntan.com.videocropdemo;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int REQUEST_CODE = 520;

    private Button btn_chooseVideo, btn_FFmpegAndroidLibraryActivity, btn_androidffmpeglibrary, btn_FFmpegAndroidLibraryGetAllImageActivity, btn_MediaCodecActivity, btn_MediaMetadataRetrieverActivity, btn_FFmpegMediaMetadataRetrieverActivity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        initListener();
    }


    private void initView() {
        btn_chooseVideo = (Button) findViewById(R.id.btn_chooseVideo);
        btn_FFmpegAndroidLibraryActivity = (Button) findViewById(R.id.btn_FFmpegAndroidLibraryActivity);
        btn_androidffmpeglibrary = (Button) findViewById(R.id.btn_androidffmpeglibrary);
        btn_FFmpegAndroidLibraryGetAllImageActivity = (Button) findViewById(R.id.btn_FFmpegAndroidLibraryGetAllImageActivity);
        btn_MediaCodecActivity = (Button) findViewById(R.id.btn_MediaCodecActivity);
        btn_MediaMetadataRetrieverActivity = (Button) findViewById(R.id.btn_MediaMetadataRetrieverActivity);
        btn_FFmpegMediaMetadataRetrieverActivity = (Button) findViewById(R.id.btn_FFmpegMediaMetadataRetrieverActivity);

    }

    private void initListener() {
        btn_chooseVideo.setOnClickListener(this);
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
            case R.id.btn_chooseVideo:
                Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, REQUEST_CODE);
                break;
            case R.id.btn_FFmpegAndroidLibraryActivity:
                if (isPathEmpty()) {
                    return;
                }
                startActivity(new Intent(this, FFmpegAndroidLibraryActivity.class).putExtra("videp_path", "" + btn_chooseVideo.getText().toString()));
                break;
            case R.id.btn_androidffmpeglibrary:
                if (isPathEmpty()) {
                    return;
                }
                startActivity(new Intent(this, AndroidFFmpegLibraryActivity.class).putExtra("videp_path", "" + btn_chooseVideo.getText().toString()));
                break;
            case R.id.btn_FFmpegAndroidLibraryGetAllImageActivity:
                if (isPathEmpty()) {
                    return;
                }
                startActivity(new Intent(this, FFmpegAndroidLibraryGetAllImageActivity.class).putExtra("videp_path", "" + btn_chooseVideo.getText().toString()));
                break;
            case R.id.btn_MediaCodecActivity:
                if (isPathEmpty()) {
                    return;
                }
                startActivity(new Intent(this, MediaCodecActivity.class).putExtra("videp_path", "" + btn_chooseVideo.getText().toString()));
                break;
            case R.id.btn_MediaMetadataRetrieverActivity:
                if (isPathEmpty()) {
                    return;
                }
                startActivity(new Intent(this, MediaMetadataRetrieverActivity.class).putExtra("videp_path", "" + btn_chooseVideo.getText().toString()));
                break;
            case R.id.btn_FFmpegMediaMetadataRetrieverActivity:
                if (isPathEmpty()) {
                    return;
                }
                startActivity(new Intent(this, FFmpegMediaMetadataRetrieverActivity.class).putExtra("videp_path", "" + btn_chooseVideo.getText().toString()));
                break;


        }
    }

    private boolean isPathEmpty() {
        if (TextUtils.isEmpty(btn_chooseVideo.getText().toString())) {
            Toast.makeText(this, "请先选择视频", Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK && null != data) {
            Uri selectedVideo = data.getData();
            String[] filePathColumn = {MediaStore.Video.Media.DATA};

            Cursor cursor = getContentResolver().query(selectedVideo,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String videoPath = cursor.getString(columnIndex);
            btn_chooseVideo.setText("" + videoPath);
            cursor.close();
        }
    }
}