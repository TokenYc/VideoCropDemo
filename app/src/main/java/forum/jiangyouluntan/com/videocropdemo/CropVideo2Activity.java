package forum.jiangyouluntan.com.videocropdemo;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.ffmpeg.android.FfmpegController;
import org.ffmpeg.android.ShellUtils;

import java.io.File;
import java.io.IOException;

import forum.jiangyouluntan.com.videocropdemo.utils.FileUtils;


/**
 * Created by Administrator on 2017/5/5.
 * https://segmentfault.com/a/1190000002502526
 */

public class CropVideo2Activity extends AppCompatActivity {
    private TextView tv_path, tv_cutpath, tv_cuttime, tv_compresstime, tv_compresspath;
    private Button btn_cut, btn_compress;

    private String videp_path;//视频路径

    private String cutPath;//时间裁剪保存的路径

    private ProgressDialog progressDialog;
    FfmpegController fc;

    long startTime;
    long endTime;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cropvideo);
        tv_path = (TextView) findViewById(R.id.tv_path);
        tv_cutpath = (TextView) findViewById(R.id.tv_cutpath);
        tv_cuttime = (TextView) findViewById(R.id.tv_cuttime);
        tv_compresstime = (TextView) findViewById(R.id.tv_compresstime);
        tv_compresspath = (TextView) findViewById(R.id.tv_compresspath);


        btn_cut = (Button) findViewById(R.id.btn_cut);
        btn_compress = (Button) findViewById(R.id.btn_compress);

        videp_path = getIntent().getStringExtra("videp_path");
        tv_path.setText("原视频路径" + videp_path);
        btn_cut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
                cutVideo();
            }
        });
        btn_compress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
                compressVideo();
            }
        });

        initFFmpeg();
    }

    private void initFFmpeg() {
        File fileAppRoot = new File(
                getApplicationInfo().dataDir);
        try {
            fc = new FfmpegController(this, fileAppRoot);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void compressVideo() {
        if (TextUtils.isEmpty(cutPath)) {
            Toast.makeText(this, "视频路径不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        /**
         * 1、设置比特率 " -b:v 1800k"  33s
         * 2、设置裁剪后视频分辨率 " -s 960x540"
         * 3、用于指定输出视频的质量，取值范围是0-51，默认值为23，数字越小输出视频的质量越高  " -crf 30 "
         *
         */
        final String outPath = FileUtils.ROOT_PATH + "/abcd_compress.mp4";
        try {
            startTime = System.currentTimeMillis();
            fc.compressVideo(cutPath, outPath, new ShellUtils.ShellCallback() {
                @Override
                public void shellOut(String shellLine) {
                    Log.e("shellLine", "shellLine===>" + shellLine);
                }

                @Override
                public void processComplete(int exitValue) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            endTime = System.currentTimeMillis();
                            dissmissDialog();
                            Log.e("onFinish", "onFinish ==>" + endTime);
                            tv_compresspath.setText("压缩视频保存路径：" + outPath);
                            tv_compresstime.setText("压缩视频时间" + (endTime - startTime) * 1f / 1000 + "秒");
                        }
                    });
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void cutVideo() {
        File file = new File(FileUtils.ROOT_PATH);
        if (!file.exists()) {
            file.mkdirs();
        }
        final String outPath = FileUtils.ROOT_PATH + "/abcd.mp4";
        try {
            startTime = System.currentTimeMillis();
            fc.cutVideo(videp_path, outPath, new ShellUtils.ShellCallback() {
                @Override
                public void shellOut(String shellLine) {
                    Log.e("shellLine", "shellLine===>" + shellLine);
                }

                @Override
                public void processComplete(int exitValue) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            endTime = System.currentTimeMillis();
                            Log.e("onFinish", "onFinish==>" + endTime);
                            dissmissDialog();
                            cutPath = outPath;
                            tv_cutpath.setText("裁剪视频保存路径：" + outPath);
                            tv_cuttime.setText("裁剪视频时间：" + (endTime - startTime) * 1f / 1000 + "秒");
                        }
                    });
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
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


    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if (fc != null) {
                fc.killVideoProcessor(false, false);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
