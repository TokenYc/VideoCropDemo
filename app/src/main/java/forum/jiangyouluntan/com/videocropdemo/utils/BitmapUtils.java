package forum.jiangyouluntan.com.videocropdemo.utils;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by Administrator on 2017/5/3.
 */

public class BitmapUtils {

    /**
     * 按比例缩放图片
     *
     * @param origin 原图
     * @param ratio  比例
     * @return 新的bitmap
     */
    public static Bitmap scaleBitmap(Bitmap origin, float ratio, int width, int height) {
        Log.e("BitmapUtils", "scaleBitmap==>ratio==>" + ratio + "==width==>" + width + "==height==>" + height);
        if (origin == null || width <= 0 || height <= 0) {
            return null;
        }
        Matrix matrix = new Matrix();
        matrix.preScale(ratio, ratio);
        Bitmap newBM = Bitmap.createBitmap(origin, 0, 0, width, height, matrix, true);
        if (newBM.equals(origin)) {
            return newBM;
        }
        origin.recycle();
        return newBM;
    }

    /**
     * 保存小视屏编辑时预览的小图到sd卡
     *
     * @param bitmap   bitmap
     * @param dirpath  保存图片的路径
     * @param fileName 保存图片的名字
     * @return
     */
    public static Boolean saveSViewoBitmapToSdCard(Bitmap bitmap, String dirpath, String fileName) {
        if (bitmap == null) {
            return false;
        }
        File dir_file = new File(dirpath);
        if (!dir_file.exists()) {
            dir_file.mkdirs();
        }
        File file = new File(dir_file, fileName);//谷歌推荐这种写法
        try {
            FileOutputStream fout = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, fout);
            fout.flush();
            fout.close();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
