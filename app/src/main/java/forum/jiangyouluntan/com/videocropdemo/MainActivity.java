package forum.jiangyouluntan.com.videocropdemo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
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

import com.bumptech.glide.Glide;

import org.ffmpeg.android.FfmpegController;
import org.ffmpeg.android.ShellUtils;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

import forum.jiangyouluntan.com.videocropdemo.TwoSideSeekBar.TwoSideSeekBar;
import forum.jiangyouluntan.com.videocropdemo.listVideo.widget.TextureVideoView;

public class MainActivity extends AppCompatActivity {
    //   /Movies/fffff.mp4
    //   /ZongHeng/temp/video/del_1492062006409.mp4
    private static final String ROOT_PATH = "/storage/sdcard0/相机";
    private final String FILE_PATH = getInnerSDCardPath() + "/ZongHeng/temp/video/del_1492062006409.mp4";
    //    private static final String FILE_PATH = ROOT_PATH+"/video_20170413_085109.mp4";
    private static final String TARGET_FILE_PATH = ROOT_PATH + "/cc.mp4";
    private static final String DIR_PATH = ROOT_PATH + "/images/";

    private static final String FILE_PATH_2 = ROOT_PATH + "/aa.jpg";

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
    private Executor executor;
    private float mCurrentX;
    private float mCurrentY = 0;
    private String videoDuration;
    private Queue<CropImageEntity> queue;


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
        Log.d("root dir", "root dir====>" + Environment.getExternalStorageDirectory().getPath());
        queue = new LinkedList<>();
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            Toast.makeText(this, "视频路径不正确！！！", Toast.LENGTH_SHORT).show();
            return;
        }
        Log.e("FILE_PATH", "FILE_PATH==>" + FILE_PATH);
        mmr.setDataSource(FILE_PATH);
        executor = Executors.newFixedThreadPool(1);
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


//        getVideoAllImage();
    }

    private void getVideoAllImage() {
        File fileAppRoot = new File(
                getApplicationInfo().dataDir);
        try {
            FfmpegController fc = new FfmpegController(
                    MainActivity.this, fileAppRoot);
            Log.d("getVideoAllImage", "  processComplete start time=====>" + System.currentTimeMillis());
            fc.getAllVideoImage(FILE_PATH, DIR_PATH + "out％d.png", new ShellUtils.ShellCallback() {
                @Override
                public void shellOut(String shellLine) {
                    Log.e("getVideoAllImage", "shellLine===>" + shellLine);
                }

                @Override
                public void processComplete(int exitValue) {
                    Log.d("getVideoAllImage", "  processComplete end time=====>" + System.currentTimeMillis());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
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
        executor.execute(new Runnable() {
                             @Override
                             public void run() {
                                 try {
                                     File fileAppRoot = new File(
                                             getApplicationInfo().dataDir);
                                     FfmpegController fc = new FfmpegController(
                                             MainActivity.this, fileAppRoot);
                                     Log.d("cropimage", "cropImage start time=====>" + System.currentTimeMillis());
                                     fc.getVideoImage(FILE_PATH, "", 0, new ShellUtils.ShellCallback() {
                                         @Override
                                         public void shellOut(String shellLine) {
                                             Log.d("shellLine", "shellLine===>" + shellLine);
                                         }

                                         @Override
                                         public void processComplete(int exitValue) {
                                             Log.d("cropimage", "cropImage end time=====>" + System.currentTimeMillis());
                                             runOnUiThread(new Runnable() {
                                                 @Override
                                                 public void run() {
//                                    imageView.setImageBitmap(BitmapFactory.decodeFile(targetFile.getPath()));
                                                 }
                                             });
                                         }
                                     });
                                 } catch (IOException e) {
                                     e.printStackTrace();
                                 } catch (Exception e) {
                                     e.printStackTrace();
                                 }
                             }
                         }
        );
    }

    public void getImage(final ImageView imageView, final int position) {
        executor.execute(new MyImageCropRunnable(imageView, position));
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
            if (position == 0 || position == getItemCount() - 1) {
                viewHolder.imvCrop.setBackgroundColor(Color.WHITE);
                viewHolder.imvCrop.setImageBitmap(null);
            } else {
                viewHolder.imvCrop.setImageBitmap(null);
                getImage(viewHolder.imvCrop, position);
            }
        }

        @Override
        public int getItemCount() {
            return Integer.parseInt(videoDuration) / 1000 + 2;
        }

        @Override
        public void onViewRecycled(RecyclerView.ViewHolder holder) {
            super.onViewRecycled(holder);
            int position = holder.getAdapterPosition();
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

    class MyImageCropRunnable implements Runnable {

        ImageView imageView;
        int position;

        public MyImageCropRunnable(ImageView imageView, int position) {
            this.imageView = imageView;
            this.position = position;
        }

        @Override
        public void run() {
            try {
                File fileDir = new File(DIR_PATH);
                if (!fileDir.exists()) {
                    fileDir.mkdir();
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
                File fileAppRoot = new File(
                        getApplicationInfo().dataDir);
                FfmpegController fc = new FfmpegController(
                        MainActivity.this, fileAppRoot);
                Log.d("cropimage", "position====>" + position + "  cropImage start time=====>" + System.currentTimeMillis());
//                fc.getVideoImage(FILE_PATH, targetFile.getPath(), position, new ShellUtils.ShellCallback() {
//                    @Override
//                    public void shellOut(String shellLine) {
//                        Log.d("shellLine", "shellLine===>" + shellLine);
//                    }
//
//                    @Override
//                    public void processComplete(int exitValue) {
//                        Log.d("cropimage", "position====>" + position + "  cropImage end time=====>" + System.currentTimeMillis());
//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                imageView.setImageBitmap(BitmapFactory.decodeFile(targetFile.getPath()));
//                            }
//                        });
//                    }
//                });
                fc.getVideoImage3(position, FILE_PATH, targetFile.getPath(), new ShellUtils.ShellCallback() {

                    @Override
                    public void shellOut(String shellLine) {
                        Log.e("shellLine", "shellLine===>" + shellLine);
                    }

                    @Override
                    public void processComplete(int exitValue) {
                        Log.d("cropimage", "position====>" + position + "  cropImage end time=====>" + System.currentTimeMillis());
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Glide.with(MainActivity.this)
                                        .load("file://" + targetFile.getPath())
                                        .centerCrop()
//                                        .crossFade()
                                        .into(imageView);
//                                imageView.setImageBitmap(BitmapFactory.decodeFile(targetFile.getPath()));
                            }
                        });
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
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
