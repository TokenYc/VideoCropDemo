package forum.jiangyouluntan.com.videocropdemo;

import android.Manifest;
import android.app.ProgressDialog;
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

import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.PermissionListener;
import com.yanzhenjie.permission.Rationale;
import com.yanzhenjie.permission.RationaleListener;

import java.io.File;
import java.util.List;

import forum.jiangyouluntan.com.videocropdemo.utils.FileUtils;
import forum.jiangyouluntan.com.videocropdemo.utils.VideoDecoder;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int REQUEST_CODE = 520;

    private Button btn_chooseVideo, btn_FFmpegAndroidLibraryActivity, btn_androidffmpeglibrary, btn_FFmpegAndroidLibraryGetAllImageActivity, btn_MediaCodecActivity, btn_MediaMetadataRetrieverActivity, btn_FFmpegMediaMetadataRetrieverActivity, btn_MediaMetadataRetrieverVideoViewActivity, btn_End, btn_clearCache, btn_cropVideo;

    private boolean isgetPermission = false;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        initListener();

        getPermission();

    }

    private void getPermission() {
        AndPermission.with(this)
                .requestCode(100)
                .permission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .callback(listener)
                .rationale(new RationaleListener() {
                    @Override
                    public void showRequestPermissionRationale(int requestCode, Rationale rationale) {
                        AndPermission.rationaleDialog(MainActivity.this, rationale).show();
                    }
                })
                .start();
    }

    private PermissionListener listener = new PermissionListener() {
        @Override
        public void onSucceed(int requestCode, List<String> grantedPermissions) {
            // 权限申请成功回调。
            // 这里的requestCode就是申请时设置的requestCode。
            // 和onActivityResult()的requestCode一样，用来区分多个不同的请求。
            if (requestCode == 100) {
                isgetPermission = true;
            }
        }

        @Override
        public void onFailed(int requestCode, List<String> deniedPermissions) {
            // 权限申请失败回调。
            if (requestCode == 100) {
                isgetPermission = false;
            }
        }
    };


    private void initView() {
        btn_chooseVideo = (Button) findViewById(R.id.btn_chooseVideo);
        btn_FFmpegAndroidLibraryActivity = (Button) findViewById(R.id.btn_FFmpegAndroidLibraryActivity);
        btn_androidffmpeglibrary = (Button) findViewById(R.id.btn_androidffmpeglibrary);
        btn_FFmpegAndroidLibraryGetAllImageActivity = (Button) findViewById(R.id.btn_FFmpegAndroidLibraryGetAllImageActivity);
        btn_MediaCodecActivity = (Button) findViewById(R.id.btn_MediaCodecActivity);
        btn_MediaMetadataRetrieverActivity = (Button) findViewById(R.id.btn_MediaMetadataRetrieverActivity);
        btn_FFmpegMediaMetadataRetrieverActivity = (Button) findViewById(R.id.btn_FFmpegMediaMetadataRetrieverActivity);
        btn_MediaMetadataRetrieverVideoViewActivity = (Button) findViewById(R.id.btn_MediaMetadataRetrieverVideoViewActivity);
        btn_End = (Button) findViewById(R.id.btn_End);
        btn_clearCache = (Button) findViewById(R.id.btn_clearCache);
        btn_cropVideo = (Button) findViewById(R.id.btn_cropVideo);

    }

    private void initListener() {
        btn_chooseVideo.setOnClickListener(this);
        btn_FFmpegAndroidLibraryActivity.setOnClickListener(this);
        btn_androidffmpeglibrary.setOnClickListener(this);
        btn_FFmpegAndroidLibraryGetAllImageActivity.setOnClickListener(this);
        btn_MediaCodecActivity.setOnClickListener(this);
        btn_MediaMetadataRetrieverActivity.setOnClickListener(this);
        btn_FFmpegMediaMetadataRetrieverActivity.setOnClickListener(this);
        btn_MediaMetadataRetrieverVideoViewActivity.setOnClickListener(this);
        btn_End.setOnClickListener(this);
        btn_clearCache.setOnClickListener(this);
        btn_cropVideo.setOnClickListener(this);
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
                startActivity(new Intent(this, FFmpegMediaMetadataRetrieverActivity.class).putExtra("video_path", "" + btn_chooseVideo.getText().toString()));
                break;
            case R.id.btn_MediaMetadataRetrieverVideoViewActivity:
                if (isPathEmpty()) {
                    return;
                }
                startActivity(new Intent(this, MediaMetadataRetrieverVideoViewActivity.class).putExtra("videp_path", "" + btn_chooseVideo.getText().toString()));
                break;
            case R.id.btn_End:
                if (isPathEmpty()) {
                    return;
                }
                startActivity(new Intent(this, EndProjectActivity.class).putExtra("videp_path", "" + btn_chooseVideo.getText().toString()));
                break;
            case R.id.btn_clearCache:
                FileUtils.clearImageCache(new File(FileUtils.DIR_PATH));
//                Glide.get(getApplicationContext()).clearMemory();
//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        Glide.get(getApplicationContext()).clearDiskCache();
//                        Log.e("onClick","清除成功");
//
//                    }
//                });
                Toast.makeText(MainActivity.this, "清除成功", Toast.LENGTH_SHORT).show();
                break;
            case R.id.btn_cropVideo:
                if (isPathEmpty()) {
                    return;
                }
                startActivity(new Intent(this, CropVideoActivity.class).putExtra("videp_path", "" + btn_chooseVideo.getText().toString()));
                break;

        }
    }

    private boolean isPathEmpty() {
        if (TextUtils.isEmpty(btn_chooseVideo.getText().toString())) {
            Toast.makeText(this, "请先选择视频", Toast.LENGTH_SHORT).show();
            return true;
        }
        if (!isgetPermission) {
            getPermission();
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