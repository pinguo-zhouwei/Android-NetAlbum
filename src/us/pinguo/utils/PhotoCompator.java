package us.pinguo.utils;

import us.pinguo.model.PhotoItem;

import java.util.Comparator;

/**
 * Created by Mr 周先森 on 2015/4/8.
 */
public class PhotoCompator implements Comparator<PhotoItem> {
    @Override
    public int compare(PhotoItem lhs, PhotoItem rhs) {
        return (lhs.time).compareTo(rhs.time);
    }
}
