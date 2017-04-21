package forum.jiangyouluntan.com.videocropdemo;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.FFmpegExecuteResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.LoadBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegNotSupportedException;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import forum.jiangyouluntan.com.videocropdemo.TwoSideSeekBar.TwoSideSeekBar;
import forum.jiangyouluntan.com.videocropdemo.entity.VideoImageEntity;
import forum.jiangyouluntan.com.videocropdemo.listVideo.widget.TextureVideoView;

public class FFmpegAndroidLibraryGetAllImageActivity extends AppCompatActivity {
    private final String ROOT_PATH = getInnerSDCardPath() + "/相机";
    private final String DIR_PATH = ROOT_PATH + "/images/";

    FFmpeg ffmpeg;

    private TextureVideoView videoView;
    private RecyclerView recyclerView;
    private TwoSideSeekBar seekBar;

    private MyAdapter adapter;
    private LinearLayoutManager linearLayoutManager;
    private Bitmap bitmap;
    private MediaMetadataRetriever mmr = new MediaMetadataRetriever();
    private float mCurrentX;
    private float mCurrentY = 0;
    private String videoDuration;

    private List<VideoImageEntity> infos;
    private String videp_path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ffmpegandroidlibrary);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        videoView = (TextureVideoView) findViewById(R.id.videoView);
        seekBar = (TwoSideSeekBar) findViewById(R.id.seekBar);
        Log.d("root dir", "root dir====>" + Environment.getExternalStorageDirectory().getPath());
        loadFFMpegBinary();
        videp_path=getIntent().getStringExtra("videp_path");
        File file = new File(videp_path);
        if (!file.exists()) {
            Toast.makeText(this, "视频路径不正确！！！", Toast.LENGTH_SHORT).show();
            return;
        }
        Log.e("FILE_PATH", "FILE_PATH==>" + videp_path);
        mmr.setDataSource(videp_path);
        initVideoSize();

        initRecyclerView();
        mCurrentX = dp2px(this, 30);
        seekBar.setOnVideoStateChangeListener(new TwoSideSeekBar.OnVideoStateChangeListener() {
            @Override
            public void onStart(float x, float y) {
                mCurrentX = x;
                mCurrentY = y;
                videoView.seekTo(getCurrentTime(mCurrentX, mCurrentY));
            }

            @Override
            public void onPause() {
                if (videoView.isPlaying()) {
                    videoView.pause();
                }
            }

            @Override
            public void onEnd() {
                videoView.seekTo(getCurrentTime(mCurrentX, mCurrentY));
            }
        });
        getVideoAllImage();
    }


    private void getVideoAllImage() {
        try {

            for (int i = 0; i < infos.size(); i++) {
                if (!new File(infos.get(i).getImagePath()).exists()) {//图片不存在
                    String ffpmegString = "-ss " + getTime(i * 1000L) + " -i " + videp_path + " -s 80*40 -frames:v 1 " + infos.get(i).getImagePath();
                    Log.e("getVideoAllImage", "ffpmegString==>" + ffpmegString);
                    String[] command = ffpmegString.split(" ");
                    ffmpeg.execute(command, new FFmpegExecuteResponseHandler() {
                        @Override
                        public void onStart() {
                            Log.e("onStart", "onStart");
                        }

                        @Override
                        public void onFinish() {
                            Log.e("onFinish", "onFinish");
                        }

                        @Override
                        public void onSuccess(String message) {

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Log.e("onSuccess", "onSuccess");
                                    adapter.notifyDataSetChanged();
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


                }
            }
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
                        FFmpegAndroidLibraryGetAllImageActivity.this.finish();
                    }
                })
                .create()
                .show();

    }

    private void initRecyclerView() {
        infos = new ArrayList<>();
        File file = new File(DIR_PATH);
        if (!file.exists()) {
            file.mkdirs();
        }
        for (int i = 0; i < Integer.parseInt(videoDuration) / 1000 + 2; i++) {
            infos.add(new VideoImageEntity(DIR_PATH + i + ".jpg"));
        }
        adapter = new MyAdapter();
        recyclerView.setAdapter(adapter);
        linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    videoView.seekTo(getCurrentTime(mCurrentX, mCurrentY));
                    seekBar.resetIndicatorAnimator();
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });
    }


    private void initVideoSize() {
        int width;
        int height;
        videoDuration = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
//        width = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH);
//        height = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT);
        long currentTime = System.currentTimeMillis();
        bitmap = mmr.getFrameAtTime();
        width = bitmap.getWidth();
        height = bitmap.getHeight();
        Log.d("video", "getFrame use time====>" + (System.currentTimeMillis() - currentTime));
        Log.d("video", "videoDuration====>" + videoDuration + "width====>" + width + "height======>" + height);

        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) videoView.getLayoutParams();
        lp.width = getResources().getDisplayMetrics().widthPixels;
        lp.height = (int) (height * (lp.width / (float) width));
        Log.d("video", "targetWidth=====>" + lp.width + "targetHeight======>" + lp.height);
        videoView.setLayoutParams(lp);
        videoView.setVideoPath(videp_path);
        videoView.start();
    }

    class MyAdapter extends RecyclerView.Adapter {

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new MyViewHolder(LayoutInflater.from(FFmpegAndroidLibraryGetAllImageActivity.this).inflate(R.layout.item, parent, false));
        }


        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
            final MyViewHolder viewHolder = (MyViewHolder) holder;
            viewHolder.imvCrop.setLayoutParams(new LinearLayout.LayoutParams(seekBar.getSingleWidth(), seekBar.getSingleHeight()));
            if (position == 0 || position == getItemCount() - 1) {
                viewHolder.imvCrop.setBackgroundColor(Color.WHITE);
                viewHolder.imvCrop.setImageBitmap(null);
            } else {
                viewHolder.imvCrop.setImageBitmap(null);
                VideoImageEntity info = infos.get(position);
                if (!TextUtils.isEmpty(info.getImagePath()) && new File(info.getImagePath()).exists()) {//如果图片路径为空或者路径图片不存在
                    Log.e("onBindViewHolder", "position=>" + position + "图片存在");

                    Glide.with(FFmpegAndroidLibraryGetAllImageActivity.this)
                            .load("file://" + info.getImagePath())
                            .centerCrop()
                            .into(viewHolder.imvCrop);
                } else {//不存在
                    File file = new File(DIR_PATH + position + ".jpg");
                    if (file.exists()) {//文件夹中存在相同名字的图片直接加载
                        Log.e("onBindViewHolder", "position=>" + position + "info图片不存在,SD卡存在");
                        viewHolder.imvCrop.setImageBitmap(BitmapFactory.decodeFile(file.getPath()));
                    } else {
                        Log.e("onBindViewHolder", "position=>" + position + "图片不存在");
//                        getFFmpegImage(viewHolder.imvCrop, position);
                    }


                }
            }
        }

        @Override
        public int getItemCount() {
            return infos.size();
        }

        @Override
        public void onViewRecycled(RecyclerView.ViewHolder holder) {
            super.onViewRecycled(holder);
        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            ImageView imvCrop;

            public MyViewHolder(View itemView) {
                super(itemView);
                imvCrop = (ImageView) itemView.findViewById(R.id.imv_crop);
            }
        }
    }


    private int getCurrentTime(float x, float y) {
        int position = recyclerView.getChildAdapterPosition(recyclerView.findChildViewUnder(x, y));
        Log.d("currentTime", "currentTime====>" + (position - 1) * 1000);
        return (position - 1) * 1000;
    }


    private int dp2px(Context context, int dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }


    private void getFFmpegImage(final ImageView imageView, final int position) {
        try {
            File fileDir = new File(DIR_PATH);
            if (!fileDir.exists()) {
                fileDir.mkdirs();
            }
            final File targetFile = new File(DIR_PATH + position + ".jpg");
            Log.d("position", "position====>" + position);
            if (targetFile.exists()) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        imageView.setImageBitmap(BitmapFactory.decodeFile(targetFile.getPath()));
                    }
                });
                return;
            }
            String ffpmegString = "-ss " + getTime(position * 1000L) + " -i " + videp_path + " -s 40*20 -frames:v 1 " + targetFile.getPath();
            Log.e("getFFmpegImages", "ffpmegString==>" + ffpmegString);
            String[] command = ffpmegString.split(" ");
            ffmpeg.execute(command, new FFmpegExecuteResponseHandler() {
                @Override
                public void onStart() {
                    Log.e("onStart", "onStart");
                }

                @Override
                public void onFinish() {
                    Log.e("onFinish", "onFinish");
                }

                @Override
                public void onSuccess(String message) {

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Log.e("onSuccess", "onSuccess");
                            infos.get(position).setImagePath("" + targetFile.getPath());
//                            adapter.notifyItemChanged(position);
                            adapter.notifyDataSetChanged();
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取内置SD卡路径
     *
     * @return
     */
    public String getInnerSDCardPath() {
        return Environment.getExternalStorageDirectory().getPath();
    }

    /**
     * 毫秒转化时分秒毫秒
     */
    public static String getTime(Long ms) {
        Integer ss = 1000;
        Integer mi = ss * 60;
        Integer hh = mi * 60;
        Integer dd = hh * 24;

        Long day = ms / dd;
        Long hour = (ms - day * dd) / hh;
        Long minute = (ms - day * dd - hour * hh) / mi;
        Long second = (ms - day * dd - hour * hh - minute * mi) / ss;

        StringBuffer sb = new StringBuffer();
        if (hour > 0) {
            if (hour > 9) {
                sb.append(hour + ":");
            } else {
                sb.append("0" + hour + ":");
            }
        } else {
            sb.append("00:");
        }
        if (minute > 0) {
            if (minute > 9) {
                sb.append(minute + ":");
            } else {
                sb.append("0" + minute + ":");
            }
        } else {
            sb.append("00:");
        }
        if (second > 0) {
            if (second > 9) {
                sb.append(second);
            } else {
                sb.append("0" + second);
            }
        } else {
            sb.append("00");
        }
        return sb.toString();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (videoView != null) {
            videoView.pause();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (videoView != null) {
            videoView.stop();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (ffmpeg != null ) {
            ffmpeg.killRunningProcesses();
        }
    }
}
