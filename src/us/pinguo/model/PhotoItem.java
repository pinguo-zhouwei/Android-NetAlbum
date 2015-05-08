package us.pinguo.model;

/**
 * Created by Mr 周先森 on 2015/4/6.
 */
public class PhotoItem {
    public int id;
    public String photoId;
    public String url;//照片的地址

    public String time;//入库的时间
    /**
     * headerId 用来分组照片
     */
    public int headerId;
    public int isUpload = 0;//0表示未上传，1表示上传
    public PhotoItem(){

    }
    public PhotoItem(String photoUri, String time) {
        this.url = photoUri;
        this.time = time;
    }
}
