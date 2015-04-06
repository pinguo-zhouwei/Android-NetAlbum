package us.pinguo.album;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import com.nostra13.universalimageloader.core.ImageLoader;
import us.pinguo.model.PhotoItem;
import us.pinguo.stickygridheaders.StickyGridHeadersBaseAdapter;
import us.pinguo.stickygridheaders.StickyGridHeadersSimpleAdapter;

import java.util.List;

/**
 * Created by Mr 周先森 on 2015/4/4.
 */
public class TimeLineAdapter extends BaseAdapter implements StickyGridHeadersSimpleAdapter{
    private List<PhotoItem> mPhotoItemList;

    public TimeLineAdapter(List<PhotoItem> mPhotoItemList) {
        this.mPhotoItemList = mPhotoItemList;
    }

    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return null;
    }


    @Override
    public long getHeaderId(int position) {
        return 0;
    }

    @Override
    public View getHeaderView(int position, View convertView, ViewGroup parent) {
        return null;
    }
}
