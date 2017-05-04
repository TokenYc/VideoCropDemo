package forum.jiangyouluntan.com.videocropdemo;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import forum.jiangyouluntan.com.videocropdemo.TwoSideSeekBar.TwoSideSeekBar;
import forum.jiangyouluntan.com.videocropdemo.entity.VideoImageEntity;
import forum.jiangyouluntan.com.videocropdemo.listVideo.widget.TextureVideoView;
import forum.jiangyouluntan.com.videocropdemo.utils.BitmapUtils;
import forum.jiangyouluntan.com.videocropdemo.utils.FileUtils;

/**
 * Created by wangjing on 2017/5/3.
 */

public class EndProjectActivity extends AppCompatActivity {
    private TextureVideoView videoView;
    private RecyclerView recyclerView;
    private TwoSideSeekBar seekBar;

    private MyAdapter adapter;
    private LinearLayoutManager linearLayoutManager;

    private MediaMetadataRetriever mmr;
    private String videp_path;//视频路径
    private String video_name;//视频名称
    private String videoDuration;//视频时长

    private float mCurrentX;
    private float mCurrentY = 0;

    private List<VideoImageEntity> infos;//recyclerview集合

    private boolean isScroll = false;//是否正在滑动

    //    private ExecutorService executorService = Executors.newFixedThreadPool(8);
    private ExecutorService cacheThreadPool = Executors.newCachedThreadPool();


    @Override

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_endproject);

        initView();
        initData();
        initRecyclerVie();
        initSeekBar();
    }

    private void initView() {
        videoView = (TextureVideoView) findViewById(R.id.videoView);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        seekBar = (TwoSideSeekBar) findViewById(R.id.seekBar);

    }

    private void initData() {
        videp_path = getIntent().getStringExtra("videp_path");
        File file = new File(videp_path);
        if (!file.exists()) {
            Toast.makeText(this, "视频路径不正确！！！", Toast.LENGTH_SHORT).show();
            return;
        }
        video_name = file.getName().replace(".mp4", "");
        Log.e("FILE_PATH", "FILE_PATH==>" + videp_path);
        Log.e("video_name", "video_name==>" + video_name);
        mmr = new MediaMetadataRetriever();
        mmr.setDataSource(videp_path);
        videoDuration = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        Bitmap bitmap = mmr.getFrameAtTime();
        float width = bitmap.getWidth();
        float height = bitmap.getHeight();

        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) videoView.getLayoutParams();
        lp.width = getResources().getDisplayMetrics().widthPixels;
        lp.height = (int) (height * (lp.width / width));
        Log.d("video", "targetWidth=====>" + lp.width + "targetHeight======>" + lp.height);
        Log.d("video", "videoDuration====>" + videoDuration + "width====>" + width + "height======>" + height);
        videoView.setLayoutParams(lp);
        videoView.setVideoPath(videp_path);
        videoView.start();
        if (bitmap != null && !bitmap.isRecycled()) {
            bitmap.recycle();
        }
    }

    private void initRecyclerVie() {
        infos = new ArrayList<>();
        File file = new File(FileUtils.DIR_PATH);
        if (!file.exists()) {
            file.mkdirs();
        }
        for (int i = 0; i < Integer.parseInt(videoDuration) / 1000 + 2; i++) {
            infos.add(new VideoImageEntity(FileUtils.DIR_PATH + video_name + "_" + i + FileUtils.IMAGE_TYPE));
        }
        adapter = new MyAdapter();
        recyclerView.setAdapter(adapter);
        linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    Log.e("onScrollStateChanged", "滑动停止");
                    isScroll = false;
                    videoView.seekTo(getCurrentTime(mCurrentX, mCurrentY));
                    seekBar.resetIndicatorAnimator();
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                Log.e("onScrolled", "滑动了");
                isScroll = true;
            }
        });
    }

    private void initSeekBar() {
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
    }


    private int getCurrentTime(float x, float y) {
        int position = recyclerView.getChildAdapterPosition(recyclerView.findChildViewUnder(x, y));
        Log.d("currentTime", "currentTime====>" + (position - 1) * 1000);
        return (position - 1) * 1000;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mmr != null) {
            //释放资源
            mmr.release();

            seekBar.release();
        }
        if (cacheThreadPool != null) {
            cacheThreadPool.shutdown();
        }
    }

    class MyAdapter extends RecyclerView.Adapter {

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new MyAdapter.MyViewHolder(LayoutInflater.from(EndProjectActivity.this).inflate(R.layout.item, parent, false));
        }


        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
            final MyAdapter.MyViewHolder viewHolder = (MyAdapter.MyViewHolder) holder;
            viewHolder.imvCrop.setLayoutParams(new LinearLayout.LayoutParams(seekBar.getSingleWidth(), seekBar.getSingleHeight()));
            VideoImageEntity info = infos.get(position);
            Glide.with(EndProjectActivity.this)
                    .load("file://" + info.getImagePath())
                    .centerCrop()
                    .placeholder(R.color.colorPlaceHolder)
                    .into(viewHolder.imvCrop);
            if (!TextUtils.isEmpty(info.getImagePath()) && new File(info.getImagePath()).exists()) {//如果图片路径不为空或者路径图片存在
                Log.e("onBindViewHolder", "position=>" + position + "图片存在");
                info.setAsync(true);
            } else {//不存在
                Log.e("onBindViewHolder", "position=>" + position + "图片不存在");
                if (!info.isAsync()) {
//                    new ExtractFrameWorkTask().execute(position);
//                    ExtractFrameWorkTask task = new ExtractFrameWorkTask();
//                    task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, position);
                    if (!cacheThreadPool.isShutdown() && !isScroll) {
                        info.setAsync(true);
                        cacheThreadPool.execute(new Runnable() {
                            @Override
                            public void run() {

                                MediaMetadataRetriever metadataRetriever = null;
                                try {
                                    Log.e("doInBackground", position + "--start==>" + System.currentTimeMillis());
                                    metadataRetriever = new MediaMetadataRetriever();
                                    metadataRetriever.setDataSource(videp_path);
                                    Bitmap bitmap = metadataRetriever.getFrameAtTime(position * 1000 * 1000, MediaMetadataRetriever.OPTION_CLOSEST_SYNC);
                                    if (bitmap != null) {
                                        Bitmap scaleBitmap = BitmapUtils.scaleBitmap(bitmap, seekBar.getSingleWidth() * 1.0f / bitmap.getWidth(), bitmap.getWidth(), bitmap.getHeight());
                                        boolean issave = BitmapUtils.saveSViewoBitmapToSdCard(scaleBitmap, FileUtils.DIR_PATH, video_name + "_" + position + FileUtils.IMAGE_TYPE);
                                        if (scaleBitmap != null && !scaleBitmap.isRecycled()) {
                                            scaleBitmap.recycle();
                                            scaleBitmap = null;
                                        }
                                        if (issave) {
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    adapter.notifyItemChanged(position);
                                                }
                                            });

                                        }
                                    }
                                } catch (IllegalArgumentException e) {
                                    e.printStackTrace();
                                } finally {
                                    metadataRetriever.release();
                                }
                            }
                        });
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

    private int dp2px(Context context, int dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }


    class ExtractFrameWorkTask extends AsyncTask<Integer, Integer, Integer> {

        @Override
        protected Integer doInBackground(Integer... params) {
            MediaMetadataRetriever metadataRetriever = null;
            try {
                int position = params[0];
                Log.e("doInBackground", position + "--start==>" + System.currentTimeMillis());
                metadataRetriever = new MediaMetadataRetriever();
                metadataRetriever.setDataSource(videp_path);
                Bitmap bitmap = mmr.getFrameAtTime(position * 1000 * 1000, MediaMetadataRetriever.OPTION_CLOSEST_SYNC);
                if (bitmap != null) {
                    Bitmap scaleBitmap = BitmapUtils.scaleBitmap(bitmap, 100 * 1.0f / bitmap.getWidth(), bitmap.getWidth(), bitmap.getHeight());
                    boolean issave = BitmapUtils.saveSViewoBitmapToSdCard(scaleBitmap, FileUtils.DIR_PATH, video_name + "_" + position + FileUtils.IMAGE_TYPE);
                    if (scaleBitmap != null && !scaleBitmap.isRecycled()) {
                        scaleBitmap.recycle();
                        scaleBitmap = null;
                    }
                    if (issave) {
                        return position;
                    }
                }
                return -1;
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } finally {
                metadataRetriever.release();
            }
            return -1;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            if (adapter != null && integer != -1) {
                Log.e("onPostExecute", integer + "--end==>" + System.currentTimeMillis());
                adapter.notifyItemChanged(integer);
            }
        }
    }


}
