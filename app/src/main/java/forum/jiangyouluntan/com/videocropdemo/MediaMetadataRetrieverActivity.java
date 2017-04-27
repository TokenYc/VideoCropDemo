package forum.jiangyouluntan.com.videocropdemo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaMetadataRetriever;
import android.os.AsyncTask;
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

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import forum.jiangyouluntan.com.videocropdemo.TwoSideSeekBar.TwoSideSeekBar;
import forum.jiangyouluntan.com.videocropdemo.entity.VideoImageEntity;
import forum.jiangyouluntan.com.videocropdemo.listVideo.widget.TextureVideoView;

public class MediaMetadataRetrieverActivity extends AppCompatActivity {
    private final String ROOT_PATH = getInnerSDCardPath() + "/相机";
    private final String DIR_PATH = ROOT_PATH + "/images/";

    private TextureVideoView videoView;
    private RecyclerView recyclerView;
    private TwoSideSeekBar seekBar;

    private MyAdapter adapter;
    private LinearLayoutManager linearLayoutManager;
    private Bitmap bitmap;
    private MediaMetadataRetriever mmr = new MediaMetadataRetriever();
    private Executor executor;
    private float mCurrentX;
    private float mCurrentY = 0;
    private String videoDuration;

    private List<VideoImageEntity> infos;
    private String videp_path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mediametadataretriever);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        videoView = (TextureVideoView) findViewById(R.id.videoView);
        seekBar = (TwoSideSeekBar) findViewById(R.id.seekBar);
        Log.d("root dir", "root dir====>" + Environment.getExternalStorageDirectory().getPath());
        videp_path = getIntent().getStringExtra("videp_path");
        File file = new File(videp_path);
        if (!file.exists()) {
            Toast.makeText(this, "视频路径不正确！！！", Toast.LENGTH_SHORT).show();
            return;
        }
        Log.e("FILE_PATH", "FILE_PATH==>" + videp_path);
        mmr.setDataSource(videp_path);
        executor = Executors.newFixedThreadPool(1);
        initVideoSize();
//        executor.execute(futureTask);
        //允许在同一时刻有15个任务正在执行，并且最多能够存储200个任务。
        executor = new ThreadPoolExecutor(15, 200, 10, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());

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

//        getVideoAllImage();
//        getFrameWithMediaMetadataRetriever();
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
            return new MyViewHolder(LayoutInflater.from(MediaMetadataRetrieverActivity.this).inflate(R.layout.item, parent, false));
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
                if (!TextUtils.isEmpty(info.getImagePath()) && new File(info.getImagePath()).exists()) {//如果图片路径不为空或者路径图片存在
                    Log.e("onBindViewHolder", "position=>" + position + "图片存在");
                    Glide.with(MediaMetadataRetrieverActivity.this)
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
//                        viewHolder.imvCrop.setImageBitmap(mmr.getFrameAtTime(position * 1000 * 1000, MediaMetadataRetriever.OPTION_CLOSEST_SYNC));
//                        getImage(viewHolder.imvCrop, position);
                        new getVideoFrameTask(viewHolder.imvCrop).execute(position);
//                        new GetVideoFrameTask(viewHolder.imvCrop).executeOnExecutor(executor,position);
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

    private BitmapFactory.Options getBitmapOption(int inSampleSize) {
        System.gc();
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPurgeable = true;
        options.inSampleSize = inSampleSize;
        options.inPreferredConfig = Bitmap.Config.ARGB_4444;
        return options;
    }

    private int dp2px(Context context, int dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }


    class getVideoFrameTask extends AsyncTask<Integer, Integer, Bitmap> {

        private ImageView imageView;

        public getVideoFrameTask(ImageView imageView) {
            this.imageView = imageView;
        }


        @Override
        protected Bitmap doInBackground(Integer... params) {
            return mmr.getFrameAtTime(params[0] * 1000 * 1000, MediaMetadataRetriever.OPTION_PREVIOUS_SYNC);
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (bitmap != null) {
                imageView.setImageBitmap(bitmap);
            }
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

}
