package forum.jiangyouluntan.com.videocropdemo.utils;

import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;

/**
 * 一句话功能简述 视频工具类
 * 功能详细描述
 *
 * @author 杨晨 on 2016/8/14 18:55
 * @e-mail 247067345@qq.com
 * @see [相关类/方法](可选)
 */
public class VideoUtil {
    public static int getVideoWidth(String path){
        MediaMetadataRetriever retr = new MediaMetadataRetriever();
        retr.setDataSource(path);
        return Integer.parseInt(retr.extractMetadata(android.media.MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH));
    }

    public static int getVideoHeight(String path){
        MediaMetadataRetriever retr = new MediaMetadataRetriever();
        retr.setDataSource(path);
        return Integer.parseInt(retr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT));
    }

    public static int getDuration(String path){
        MediaMetadataRetriever retr = new MediaMetadataRetriever();
        retr.setDataSource(path);
        return Integer.parseInt(retr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));
    }

    public static Bitmap getCover(String path){
        MediaMetadataRetriever retr = new MediaMetadataRetriever();
        retr.setDataSource(path);
        return retr.getFrameAtTime(1);
    }



}
