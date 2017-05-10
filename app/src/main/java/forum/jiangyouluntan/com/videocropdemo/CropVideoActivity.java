package forum.jiangyouluntan.com.videocropdemo;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.FFmpegExecuteResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.LoadBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegNotSupportedException;

import forum.jiangyouluntan.com.videocropdemo.utils.FileUtils;


/**
 * Created by Administrator on 2017/5/5.
 * https://segmentfault.com/a/1190000002502526
 */

public class CropVideoActivity extends AppCompatActivity {
    private TextView tv_path, tv_cutpath, tv_cuttime, tv_compresstime, tv_compresspath;
    private Button btn_cut, btn_compress;

    private String videp_path;//视频路径

    private String cutPath;//时间裁剪保存的路径

    private ProgressDialog progressDialog;

    FFmpeg ffmpeg;

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

        loadFFMpegBinary();
    }

    private void compressVideo() {
        if (TextUtils.isEmpty(cutPath)) {
            Toast.makeText(this, "视频路径不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        final String outPath = FileUtils.ROOT_PATH + "/abcd_compress.mp4";
        String ffpmegString ="-threads 2"+ " -i " + cutPath + " -vcodec libx264" + " -acodec copy" +" -preset ultrafast"+" -crf 30 " + outPath;
        Log.d("compressVideo", "ffpmegString==>" + ffpmegString);
        String[] command = ffpmegString.split(" ");
        try {
            ffmpeg.execute(command, new FFmpegExecuteResponseHandler() {
                @Override
                public void onStart() {
                    startTime = System.currentTimeMillis();
                    Log.e("onStart", "onStart==>" + startTime);
                }

                @Override
                public void onProgress(String message) {
                    Log.e("onProgress", "" + message);
                }

                @Override
                public void onSuccess(String message) {
                    Log.e("onSuccess", "" + message);
                }

                @Override
                public void onFinish() {
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

                @Override
                public void onFailure(String message) {
                    Log.e("onFailure", "" + message);
                }


            });
        } catch (FFmpegCommandAlreadyRunningException e) {
            e.printStackTrace();
        }
    }

    private void cutVideo() {
//        String ffpmegString = "-ss " + getTime(position * 1000L) + " -i " + videp_path + " -s 40*20 -frames:v 1 " + targetFile.getPath();
        final String outPath = FileUtils.ROOT_PATH + "/abcd.mp4";
        String ffpmegString = "-ss " + "00:00:30" + " -t " + "00:00:10" + " -i " + videp_path + " -vcodec copy" + " -acodec copy " + outPath;
        Log.d("cutVideo", "ffpmegString==>" + ffpmegString);
        String[] command = ffpmegString.split(" ");

        try {
            ffmpeg.execute(command, new FFmpegExecuteResponseHandler() {
                @Override
                public void onStart() {
                    startTime = System.currentTimeMillis();
                    Log.e("onStart", "onStart==>" + startTime);
                }

                @Override
                public void onFinish() {
                    Log.e("onFinish", "onFinish==>" + endTime);
                }

                @Override
                public void onSuccess(final String message) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            endTime = System.currentTimeMillis();
                            dissmissDialog();
                            Log.e("onSuccess", "onSuccess ==>" + message);
                            cutPath = outPath;
                            tv_cutpath.setText("裁剪视频保存路径：" + outPath);
                            tv_cuttime.setText("裁剪视频时间：" + (endTime - startTime) * 1f / 1000 + "秒");
                        }
                    });
                }

                @Override
                public void onProgress(String message) {
                    Log.e("onProgress", "" + message);
                }

                @Override
                public void onFailure(final String message) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Log.e("onFailure", "" + message);
                        }
                    });

                }
            });
        } catch (FFmpegCommandAlreadyRunningException e) {
            e.printStackTrace();
        }
    }


    private void loadFFMpegBinary() {
        try {
            ffmpeg = FFmpeg.getInstance(this);
            ffmpeg.loadBinary(new LoadBinaryResponseHandler() {
                @Override
                public void onStart() {
                    super.onStart();
                }

                @Override
                public void onSuccess() {
                    super.onSuccess();
                }

                @Override
                public void onFinish() {
                    super.onFinish();
                }

                @Override
                public void onFailure() {
                    showUnsupportedExceptionDialog();
                }
            });
        } catch (FFmpegNotSupportedException e) {
            showUnsupportedExceptionDialog();
        }
    }


    private void showUnsupportedExceptionDialog() {
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle(getString(R.string.device_not_supported))
                .setMessage(getString(R.string.device_not_supported_message))
                .setCancelable(false)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        CropVideoActivity.this.finish();
                    }
                })
                .create()
                .show();

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
        if (ffmpeg != null) {
            ffmpeg.killRunningProcesses();
        }
    }
}
