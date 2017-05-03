package forum.jiangyouluntan.com.videocropdemo.entity;

/**
 * Created by Administrator on 2017/4/18.
 */

public class VideoImageEntity {
    private String imagePath;
    private boolean isAsync = false;//是否在异步请求了，默认没有

    public boolean isAsync() {
        return isAsync;
    }

    public void setAsync(boolean async) {
        isAsync = async;
    }

    public VideoImageEntity(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
}
