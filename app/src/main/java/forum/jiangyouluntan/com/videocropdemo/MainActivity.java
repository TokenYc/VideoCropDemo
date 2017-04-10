package forum.jiangyouluntan.com.videocropdemo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.media.MediaMetadataRetriever;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import org.ffmpeg.android.FfmpegController;
import org.ffmpeg.android.ShellUtils;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Target;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

import forum.jiangyouluntan.com.videocropdemo.TwoSideSeekBar.TwoSideSeekBar;
import forum.jiangyouluntan.com.videocropdemo.listVideo.widget.TextureVideoView;

public class MainActivity extends AppCompatActivity {

    private static final String FILE_PATH = "/storage/emulated/0/Movies/fffff.mp4";
    private static final String TARGET_FILE_PATH = "/storage/emulated/0/Movies/cc.mp4";
    private static final String DIR_PATH = "/storage/emulated/0/Movies/";
    private static final String FILE_PATH_2 = "/storage/emulated/0/Movies/aa.jpg";

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

    Callable<Bitmap> callable = new Callable<Bitmap>() {
        @Override
        public Bitmap call() throws Exception {
            long currentTime = System.currentTimeMillis();
            Bitmap tempBitmap = mmr.getFrameAtTime(currentTime);
            Log.d("video", "getFrame use time====>" + (System.currentTimeMillis() - currentTime));
            Log.d("video", "tempBitmap width====>" + tempBitmap.getWidth() + "tempBitmap height====>" + tempBitmap.getHeight());
            Matrix matrix = new Matrix();
            matrix.postScale(0.1f, 0.1f);
            tempBitmap = Bitmap.createBitmap(tempBitmap, 0, 0, tempBitmap.getWidth(), tempBitmap.getHeight(), matrix, true);
            return tempBitmap;
        }
    };
    FutureTask<Bitmap> futureTask = new FutureTask<Bitmap>(callable);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        videoView = (TextureVideoView) findViewById(R.id.videoView);
        seekBar = (TwoSideSeekBar) findViewById(R.id.seekBar);

        mmr.setDataSource(FILE_PATH);
        executor = Executors.newFixedThreadPool(4);
        initVideoSize();
//        executor.execute(futureTask);
        adapter = new MyAdapter();
        initRecyclerView();
        mCurrentX = dp2px(this, 30);
        seekBar.setOnVideoStateChangeListener(new TwoSideSeekBar.OnVideoStateChangeListener() {
            @Override
            public void onStart(float x, float y) {
                mCurrentX = x;
                mCurrentY = y;
                videoView.resume();
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
//        ffmpegTest();
    }


    private void initRecyclerView() {
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

    private void ffmpegTest() {
        try {
            File fileAppRoot = new File(
                    getApplicationInfo().dataDir);
            FfmpegController fc = new FfmpegController(
                    MainActivity.this, fileAppRoot);
            Log.d("cropimage", "cropImage start time=====>" + System.currentTimeMillis());
            fc.getVideoImage(FILE_PATH, FILE_PATH_2, new ShellUtils.ShellCallback() {
                @Override
                public void shellOut(String shellLine) {
                    Log.d("shellLine", "shellLine===>" + shellLine);
                }

                @Override
                public void processComplete(int exitValue) {
                    Log.d("cropimage", "cropImage end time=====>" + System.currentTimeMillis());
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initVideoSize() {
        String duration;
        String width = null;
        String height = null;
        duration = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        width = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH);
        height = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT);

        long currentTime = System.currentTimeMillis();
        bitmap = mmr.getFrameAtTime();
        Log.d("video", "getFrame use time====>" + (System.currentTimeMillis() - currentTime));

        Log.d("video", "duration====>" + duration + "width====>" + width + "height======>" + height);

        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) videoView.getLayoutParams();
        lp.width = Integer.parseInt(width) / 2;
        lp.height = Integer.parseInt(height) / 2;
        videoView.setVideoPath(FILE_PATH);
        videoView.start();
    }

    public void crop(View view) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    long startTime = getCurrentTime(mCurrentX, mCurrentY);
                    long cropTime = seekBar.getCropTime();
                    Log.d("crop", "startTime======>" + startTime + "cropTime=====>" + cropTime);
                    File fileAppRoot = new File(
                            getApplicationInfo().dataDir);
                    FfmpegController ffmpegController = new FfmpegController(MainActivity.this, fileAppRoot);
                    ffmpegController.cropVideo(FILE_PATH, TARGET_FILE_PATH, startTime, cropTime, new ShellUtils.ShellCallback() {
                        @Override
                        public void shellOut(String shellLine) {
                            Log.d("shellout", "shellLine====>" + shellLine);
                        }

                        @Override
                        public void processComplete(int exitValue) {

                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    public void getImage(View view) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    File file = new File(FILE_PATH_2);
                    if (file.exists()) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(MainActivity.this, "已经存在", Toast.LENGTH_SHORT).show();
                            }
                        });
                        return;
                    }
                    File fileAppRoot = new File(
                            getApplicationInfo().dataDir);
                    FfmpegController fc = new FfmpegController(
                            MainActivity.this, fileAppRoot);
                    Log.d("cropimage", "cropImage start time=====>" + System.currentTimeMillis());
                    fc.getVideoImage(FILE_PATH, FILE_PATH_2, new ShellUtils.ShellCallback() {
                        @Override
                        public void shellOut(String shellLine) {
                            Log.d("shellLine", "shellLine===>" + shellLine);
                        }

                        @Override
                        public void processComplete(int exitValue) {
                            Log.d("cropimage", "cropImage end time=====>" + System.currentTimeMillis());
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    class MyAdapter extends RecyclerView.Adapter {

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new MyViewHolder(LayoutInflater.from(MainActivity.this).inflate(R.layout.item, parent, false));
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
            final MyViewHolder viewHolder = (MyViewHolder) holder;
            viewHolder.imvCrop.setLayoutParams(new LinearLayout.LayoutParams(seekBar.getSingleWidth(), seekBar.getSingleHeight()));
            if (position == 0) {
                viewHolder.imvCrop.setBackgroundColor(Color.BLACK);
                viewHolder.imvCrop.setImageBitmap(null);
            } else {
                viewHolder.imvCrop.setImageBitmap(bitmap);
            }
////            try {
//                currentTime=position*1000*1000;
////                FutureTask<Bitmap> futureTask = new FutureTask<Bitmap>(callable);
//////                if (position>0)
////                executor.execute(futureTask);
////            new Thread(new Runnable() {
////                @Override
////                public void run() {
////                    long currentTime=System.currentTimeMillis();
////                    Bitmap tempBitmap = mmr.getFrameAtTime(position*1000*1000);
////                    Log.d("video", "getFrame use time====>" + (System.currentTimeMillis() - currentTime));
////                    Matrix matrix = new Matrix();
////                    matrix.postScale(0.1f, 0.1f);
////                    tempBitmap = Bitmap.createBitmap(tempBitmap, 0, 0, tempBitmap.getWidth(), tempBitmap.getHeight(), matrix, true);
////                    final Bitmap finalTempBitmap = tempBitmap;
////                    runOnUiThread(new Runnable() {
////                        @Override
////                        public void run() {
////                            viewHolder.imvCrop.setImageBitmap(finalTempBitmap);
////                        }
////                    });
////                }
////            }).start();
//                executor.execute(new Runnable() {
//                    @Override
//                    public void run() {
////                        long currentTime=System.currentTimeMillis();
//                        Bitmap tempBitmap = mmr.getFrameAtTime(position*1000*1000);
////                        Log.d("video", "getFrame use time====>" + (System.currentTimeMillis() - currentTime));
//                        Matrix matrix = new Matrix();
//                        matrix.postScale(0.1f, 0.1f);
//                        tempBitmap = Bitmap.createBitmap(tempBitmap, 0, 0, tempBitmap.getWidth(), tempBitmap.getHeight(), matrix, true);
//                        final Bitmap finalTempBitmap = tempBitmap;
//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                viewHolder.imvCrop.setImageBitmap(finalTempBitmap);
//                            }
//                        });
//                    }
//                });
////                viewHolder.imvCrop.setImageBitmap(futureTask.get());
////            } catch (InterruptedException e) {
////                e.printStackTrace();
////            } catch (ExecutionException e) {
////                e.printStackTrace();
////            }
        }

        @Override
        public int getItemCount() {
            return 40;
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
}
