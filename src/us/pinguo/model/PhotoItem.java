package us.pinguo.model;

/**
 * Created by Mr 周先森 on 2015/4/6.
 */
public class PhotoItem {
    private String photoUri;//照片的地址

    private String time;//入库的时间
    /**
     * headerId 用来分组照片
     */
    private int headerId;

    public PhotoItem(String photoUri, String time) {
        this.photoUri = photoUri;
        this.time = time;
    }
}
