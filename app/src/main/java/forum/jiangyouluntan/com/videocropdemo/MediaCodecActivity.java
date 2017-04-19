package forum.jiangyouluntan.com.videocropdemo;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaCodec;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.media.MediaMetadataRetriever;
import android.opengl.GLES20;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import forum.jiangyouluntan.com.videocropdemo.TwoSideSeekBar.TwoSideSeekBar;
import forum.jiangyouluntan.com.videocropdemo.entity.VideoImageEntity;

public class MediaCodecActivity extends AppCompatActivity implements View.OnTouchListener, View.OnClickListener, SurfaceHolder.Callback {
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


    private SurfaceView surfaceView;
    private SurfaceHolder surfaceHolder;
    private PlayerThread playerThread = null;
    private ByteBuffer mPixelBuf;
    private Button btn_play;
    private boolean playButtonVisible;
    private boolean playPause;

    private RecyclerView recyclerView;
    private TwoSideSeekBar seekBar;

    private MyAdapter adapter;
    private LinearLayoutManager linearLayoutManager;
    private MediaMetadataRetriever mmr = new MediaMetadataRetriever();
    private Executor executor;
    private float mCurrentX;
    private float mCurrentY = 0;
    private String videoDuration;

    private List<VideoImageEntity> infos;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mediacodec);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        surfaceView = (SurfaceView) findViewById(R.id.surfaceView);
        seekBar = (TwoSideSeekBar) findViewById(R.id.seekBar);
        btn_play = (Button) findViewById(R.id.btn_play);
        btn_play.setOnClickListener(this);
        Log.d("root dir", "root dir====>" + Environment.getExternalStorageDirectory().getPath());
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            Toast.makeText(this, "视频路径不正确！！！", Toast.LENGTH_SHORT).show();
            return;
        }
        Log.e("FILE_PATH", "FILE_PATH==>" + FILE_PATH);
        mmr.setDataSource(FILE_PATH);
        executor = Executors.newFixedThreadPool(1);
        initVideoSize();

        initRecyclerView();
        mCurrentX = dp2px(this, 30);
//        seekBar.setOnVideoStateChangeListener(new TwoSideSeekBar.OnVideoStateChangeListener() {
//            @Override
//            public void onStart(float x, float y) {
//                mCurrentX = x;
//                mCurrentY = y;
//                videoView.seekTo(getCurrentTime(mCurrentX, mCurrentY));
//            }
//
//            @Override
//            public void onPause() {
//                if (videoView.isPlaying()) {
//                    videoView.pause();
//                }
//            }
//
//            @Override
//            public void onEnd() {
//                videoView.seekTo(getCurrentTime(mCurrentX, mCurrentY));
//            }
//        });
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);
        mPixelBuf = ByteBuffer.allocateDirect(640 * 480 * 4);
        mPixelBuf.order(ByteOrder.LITTLE_ENDIAN);
    }

    private void initVideoSize() {
        int width;
        int height;
        videoDuration = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
//        width = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH);
//        height = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT);
//        long currentTime = System.currentTimeMillis();
//        bitmap = mmr.getFrameAtTime();
//        width = bitmap.getWidth();
//        height = bitmap.getHeight();
//        Log.d("video", "getFrame use time====>" + (System.currentTimeMillis() - currentTime));
//        Log.d("video", "videoDuration====>" + videoDuration + "width====>" + width + "height======>" + height);
//
//        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) videoView.getLayoutParams();
//        lp.width = getResources().getDisplayMetrics().widthPixels;
//        lp.height = (int) (height * (lp.width / (float) width));
//        Log.d("video", "targetWidth=====>" + lp.width + "targetHeight======>" + lp.height);
//        videoView.setLayoutParams(lp);
//        videoView.setVideoPath(FILE_PATH);
//        videoView.start();
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
//        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
//                super.onScrollStateChanged(recyclerView, newState);
//                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
//                    videoView.seekTo(getCurrentTime(mCurrentX, mCurrentY));
//                    seekBar.resetIndicatorAnimator();
//                }
//            }
//
//            @Override
//            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//                super.onScrolled(recyclerView, dx, dy);
//            }
//        });
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (playerThread == null) {
            playerThread = new PlayerThread(holder.getSurface());
            playerThread.start();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (playerThread != null) {
            playerThread.interrupt();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_play:
                if (!playPause) {
                    btn_play.setText("Pause");
                } else {
                    btn_play.setText("Play");
                }
                playPause = !playPause;
                break;
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (!playButtonVisible) {
            btn_play.setVisibility(View.VISIBLE);
            btn_play.setEnabled(true);
        } else {
            btn_play.setVisibility(View.INVISIBLE);
        }
        playButtonVisible = !playButtonVisible;
        return false;
    }


    class MyAdapter extends RecyclerView.Adapter {

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new MyViewHolder(LayoutInflater.from(MediaCodecActivity.this).inflate(R.layout.item, parent, false));
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

                    Glide.with(MediaCodecActivity.this)
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
//                        getImage(viewHolder.imvCrop, position);
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

    private void writeFrameToSDCard(byte[] bytes, int i, int sampleSize) {                i++;
        if (i%10 == 0) {
            try {
                Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, sampleSize);
                mPixelBuf.rewind();
                bmp.copyPixelsFromBuffer(mPixelBuf);
                String path = DIR_PATH  + i + ".jpg";
                FileOutputStream fileOutputStream = null;
                try {
                    fileOutputStream = new FileOutputStream(path);
                    bmp.compress(Bitmap.CompressFormat.JPEG, 90, fileOutputStream);
                    bmp.recycle();
                    Log.i("writeFrameToSDCard", "i: " + i);
                } catch (Exception e) {
                    Log.i("writeFrameToSDCard", "Error: " + i);
                    e.printStackTrace();
                }
                finally {
                    if (fileOutputStream != null) {
                        fileOutputStream.close();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    private class PlayerThread extends Thread {

        private MediaExtractor extractor;
        private MediaCodec mediaCodec;
        private Surface surface;

        public PlayerThread(Surface surface) {
            this.surface = surface;
        }

        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
        @Override
        public void run() {
            extractor = new MediaExtractor();
            try {
                extractor.setDataSource(FILE_PATH);
            } catch (IOException e1) {
                Log.i("PlayerThread", "Error");
                e1.printStackTrace();
            }

            for (int i = 0; i < extractor.getTrackCount(); i++) {
                MediaFormat format = extractor.getTrackFormat(i);
                String mime = format.getString(MediaFormat.KEY_MIME);
                if (mime.startsWith("video/")) {
                    extractor.selectTrack(i);
                    try {
                        mediaCodec = MediaCodec.createDecoderByType(mime);
                        mediaCodec.configure(format, surface, null, 0);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                }
            }

            if (mediaCodec == null) {
                Log.e("PlayerThread", "Can't find video info!");
                return;
            }

            mediaCodec.start();

            ByteBuffer[] inputBuffers = mediaCodec.getInputBuffers();
            ByteBuffer[] outputBuffers = mediaCodec.getOutputBuffers();
            MediaCodec.BufferInfo info = new MediaCodec.BufferInfo();
            boolean isEOS = false;
            long startMs = System.currentTimeMillis();

            int i = 0;
            while (!Thread.interrupted()) {
                if (!isEOS) {
                    int inIndex = mediaCodec.dequeueInputBuffer(10000);
                    if (inIndex >= 0) {
                        ByteBuffer buffer = inputBuffers[inIndex];
                        int sampleSize = extractor.readSampleData(buffer, 0);
                        if (sampleSize < 0) {
                            // We shouldn't stop the playback at this point, just pass the EOS
                            // flag to mediaCodec, we will get it again from the dequeueOutputBuffer
                            Log.d("PlayerThread", "InputBuffer BUFFER_FLAG_END_OF_STREAM");
                            mediaCodec.queueInputBuffer(inIndex, 0, 0, 0, MediaCodec.BUFFER_FLAG_END_OF_STREAM);
                            isEOS = true;
                        } else {
                            mediaCodec.queueInputBuffer(inIndex, 0, sampleSize, extractor.getSampleTime(), 0);
                            extractor.advance();
                        }
                    }
                }

                int outIndex = mediaCodec.dequeueOutputBuffer(info, 100000);

                switch (outIndex) {
                    case MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED:
                        Log.d("PlayerThread", "INFO_OUTPUT_BUFFERS_CHANGED");
                        outputBuffers = mediaCodec.getOutputBuffers();
                        break;
                    case MediaCodec.INFO_OUTPUT_FORMAT_CHANGED:
                        Log.d("PlayerThread","New format " + mediaCodec.getOutputFormat());
                        break;
                    case MediaCodec.INFO_TRY_AGAIN_LATER:
                        Log.d("PlayerThread", "dequeueOutputBuffer timed out!");
                        break;
                    default:
                        ByteBuffer buffer = outputBuffers[outIndex];
                        Log.v("PlayerThread","We can't use this buffer but render it due to the API limit, " + buffer);

                        // We use a very simple clock to keep the video FPS, or the video
                        // playback will be too fast
                        while (info.presentationTimeUs / 1000 > System.currentTimeMillis() - startMs) {
                            try {
                                sleep(10);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                                break;
                            }
                        }
                        mediaCodec.releaseOutputBuffer(outIndex, true);

                    /* saves frame to SDcard */
                        mPixelBuf.rewind();
                        GLES20.glReadPixels(0, 0, 640, 480, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, mPixelBuf);
                        try {
                            ByteBuffer outByteBuffer = outputBuffers[outIndex];
                            outByteBuffer.position(info.offset);
                            outByteBuffer.limit(info.offset + info.size);//info的两个参数值始终为0，所保存的.png也都是0KB。
                            outByteBuffer.limit(2);
                            byte[] dst = new byte[outByteBuffer.capacity()];
                            outByteBuffer.get(dst);
                            writeFrameToSDCard(dst, i, dst.length);
                            i++;
                        } catch (Exception e) {
                            Log.d("PlayerThread", "Error while creating bitmap with: " + e.getMessage());
                        }
                        break;
                }

                // All decoded frames have been rendered, we can stop playing now
                if ((info.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
                    Log.d("PlayerThread",    "OutputBuffer BUFFER_FLAG_END_OF_STREAM");
                    break;
                }
            }

            mediaCodec.stop();
            mediaCodec.release();
            extractor.release();
        }
    }
}
