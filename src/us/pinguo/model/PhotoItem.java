package us.pinguo.model;

/**
 * Created by Mr 周先森 on 2015/4/6.
 */
public class PhotoItem {
    public String photoUri;//照片的地址

    public String time;//入库的时间
    /**
     * headerId 用来分组照片
     */
    public int headerId;
    public PhotoItem(){

    }
    public PhotoItem(String photoUri, String time) {
        this.photoUri = photoUri;
        this.time = time;
    }
}
