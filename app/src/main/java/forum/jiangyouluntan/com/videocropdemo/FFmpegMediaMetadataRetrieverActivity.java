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

import forum.jiangyouluntan.com.videocropdemo.TwoSideSeekBar.TwoSideSeekBar;
import forum.jiangyouluntan.com.videocropdemo.entity.VideoImageEntity;
import forum.jiangyouluntan.com.videocropdemo.listVideo.widget.TextureVideoView;
import wseemann.media.FFmpegMediaMetadataRetriever;

public class FFmpegMediaMetadataRetrieverActivity extends AppCompatActivity {
    //   /Movies/fffff.mp4
    //   /ZongHeng/temp/video/del_1492062006409.mp4
    //  /ddpaiSDK/video/video.M6.00e00100b534/L_20170412100733_173_173.mp4
    private final String ROOT_PATH = getInnerSDCardPath() + "/相机";
    //    private final String FILE_PATH = getInnerSDCardPath() + "/ZongHeng/temp/video/del_1492062006409.mp4";
    private final String FILE_PATH = getInnerSDCardPath() + "/Movies/fffff.mp4";
    //    private final String FILE_PATH = getInnerSDCardPath() + "/ddpaiSDK/video/video.M6.00e00100b534/L_20170412100733_173_173.mp4";
    //    private static final String FILE_PATH = ROOT_PATH+"/video_20170413_085109.mp4";
    private final String TARGET_FILE_PATH = ROOT_PATH + "/cc.mp4";
    private final String DIR_PATH = ROOT_PATH + "/images/";

    private final String FILE_PATH_2 = ROOT_PATH + "/aa.jpg";

//    private static final String FILE_PATH = "/storage/emulated/0/DCIM/Camera/VID_20170411_145656.mp4";
//    private static final String TARGET_FILE_PATH = "/storage/emulated/0/DCIM/Camera/cc.mp4";
//    private static final String DIR_PATH = "/storage/emulated/0/DCIM/Camera/images2/";
//    private static final String FILE_PATH_2 = "/storage/emulated/0/DCIM/Camera/aa.jpg";

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
    private FFmpegMediaMetadataRetriever ffmpeg_mmr;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mediametadataretriever);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        videoView = (TextureVideoView) findViewById(R.id.videoView);
        seekBar = (TwoSideSeekBar) findViewById(R.id.seekBar);
        Log.d("root dir", "root dir====>" + Environment.getExternalStorageDirectory().getPath());
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            Toast.makeText(this, "视频路径不正确！！！", Toast.LENGTH_SHORT).show();
            return;
        }
        Log.e("FILE_PATH", "FILE_PATH==>" + FILE_PATH);
        mmr.setDataSource(FILE_PATH);
        initVideoSize();
//        executor.execute(futureTask);


        ffmpeg_mmr = new FFmpegMediaMetadataRetriever();
        ffmpeg_mmr.setDataSource(FILE_PATH);
        ffmpeg_mmr.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_ALBUM);
        ffmpeg_mmr.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_ARTIST);

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
        videoView.setVideoPath(FILE_PATH);
        videoView.start();
    }


    class MyAdapter extends RecyclerView.Adapter {

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new MyViewHolder(LayoutInflater.from(FFmpegMediaMetadataRetrieverActivity.this).inflate(R.layout.item, parent, false));
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
                    Glide.with(FFmpegMediaMetadataRetrieverActivity.this)
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
//                        new getVideoFrameTask(viewHolder.imvCrop).executeOnExecutor(executor,position);
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
//            int position = holder.getAdapterPosition();
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


    class getVideoFrameTask extends AsyncTask<Integer, Integer, Bitmap> {

        private ImageView imageView;

        public getVideoFrameTask(ImageView imageView) {
            this.imageView = imageView;
        }


        @Override
        protected Bitmap doInBackground(Integer... params) {
            return ffmpeg_mmr.getFrameAtTime(params[0] * 1000 * 1000, FFmpegMediaMetadataRetriever.OPTION_CLOSEST);
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

}
