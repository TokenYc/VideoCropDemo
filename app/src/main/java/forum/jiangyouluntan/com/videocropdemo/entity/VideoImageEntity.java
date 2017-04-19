package forum.jiangyouluntan.com.videocropdemo.entity;

/**
 * Created by Administrator on 2017/4/18.
 */

public class VideoImageEntity {
    private String imagePath;


    public VideoImageEntity() {
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
