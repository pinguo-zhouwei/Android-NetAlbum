package us.pinguo.album;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import us.pinguo.model.PhotoItem;
import us.pinguo.stickygridheaders.StickyGridHeadersBaseAdapter;
import us.pinguo.stickygridheaders.StickyGridHeadersSimpleAdapter;

import java.util.List;

/**
 * Created by Mr 周先森 on 2015/4/4.
 */
public class TimeLineAdapter extends BaseAdapter implements StickyGridHeadersSimpleAdapter{
    private List<PhotoItem> mPhotoItemList;
    private DisplayImageOptions options = null;
    public TimeLineAdapter (List<PhotoItem> mPhotoItemList) {
        this.mPhotoItemList = mPhotoItemList;
    }

    @Override
    public int getCount() {
        return mPhotoItemList.size();
    }

    @Override
    public Object getItem(int position) {
        return mPhotoItemList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if(convertView==null){
             viewHolder = new ViewHolder();
             convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.time_line_photo_layout,null);
             viewHolder.photo = (ImageView) convertView.findViewById(R.id.photo_item_img);
             convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();

            String url = mPhotoItemList.get(position).photoUri;
            viewHolder.photo.setTag(url);
            ImageLoader.getInstance().loadImage(url,options,new ImageLoadingListener() {
                @Override
                public void onLoadingStarted(String imageUri, View view) {

                }

                @Override
                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

                }

                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        if(loadedImage!=null){
                            viewHolder.photo.setImageBitmap(loadedImage);
                        }

                }

                @Override
                public void onLoadingCancelled(String imageUri, View view) {

                }
            });
        }
        return convertView;
    }


    @Override
    public long getHeaderId(int position) {

        return mPhotoItemList.get(position).headerId;
    }

    @Override
    public View getHeaderView(int position, View convertView, ViewGroup parent) {
        ViewHolder mHeaderHolder;

        if (convertView == null) {
            mHeaderHolder = new ViewHolder();
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.time_line_photo_header,parent,false);
            mHeaderHolder.headerView = (TextView) convertView
                    .findViewById(R.id.header_text);
            convertView.setTag(mHeaderHolder);
        } else {
            mHeaderHolder = (ViewHolder) convertView.getTag();
        }
        mHeaderHolder.headerView.setText(mPhotoItemList.get(position).time);

        return convertView;

    }

    class ViewHolder{
        ImageView photo;
        TextView headerView;
    }
}
