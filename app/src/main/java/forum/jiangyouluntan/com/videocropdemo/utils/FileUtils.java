package forum.jiangyouluntan.com.videocropdemo.utils;

import android.os.Environment;
import android.util.Log;

import java.io.File;

/**
 * Created by Administrator on 2017/5/3.
 */

public class FileUtils {
    public final static String IMAGE_TYPE = ".jpeg";
    public final static String ROOT_PATH = getInnerSDCardPath() + "/相机";
    public final static String DIR_PATH = ROOT_PATH + "/images/";

    /**
     * 获取内置SD卡路径
     *
     * @return
     */
    public static String getInnerSDCardPath() {
        return Environment.getExternalStorageDirectory().getPath();
    }


    public static void clearImageCache(File file) {
        if (file.exists()) {//如果文件夹存在
            if (file.isFile()) {//如果是文件
                file.delete();
            }
            if (file.isDirectory()) {//如果是目录
                File[] childFile = file.listFiles();
                if (childFile == null || childFile.length == 0) {
                    file.delete();
                }
                for (File f : childFile) {
                    clearImageCache(f);
                }
                file.delete();
            }
        }
    }
}
