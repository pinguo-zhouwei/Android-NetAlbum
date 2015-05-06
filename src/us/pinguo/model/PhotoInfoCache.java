package us.pinguo.model;

import us.pinguo.db.DBPhotoTable;
import us.pinguo.db.SandBoxSql;

import java.util.List;

/**
 * Created by Mr 周先森 on 2015/4/15.
 */
public class PhotoInfoCache {
    /**内存缓存，避免每一次都去数据库去**/
    private List<PhotoItem> photoItemsCache;
    public static List<PhotoItem> localPhoto;
    /**
     * 获取所有照片
     * @return
     */
    public List<PhotoItem> getPhoto(){
        if(photoItemsCache!=null){
            return photoItemsCache;
        }
        try {
            DBPhotoTable dbPhotoTable = new DBPhotoTable(SandBoxSql.getInstance());
            photoItemsCache =  dbPhotoTable.queryPhoto();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return photoItemsCache;
    }

    public static void setLocalPhoto(List<PhotoItem> localPhoto) {
        PhotoInfoCache.localPhoto = localPhoto;
    }

    public static List<PhotoItem> getLocalPhoto() {
        return localPhoto;
    }
}
