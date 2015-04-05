package us.pinguo.album;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import us.pinguo.album.utils.BitmapCache;

import java.io.File;

/**
 * Created by zhouwei on 14-10-11.
 */
public class AlbumFragment extends Fragment {
    private File filePaths[];
    /**
     * 获取Fragment实例
     * @return
     */
    static AlbumFragment getInstance(){
        AlbumFragment fragment = new AlbumFragment();
        return fragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        File file = new  File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "CameraDemo");
        filePaths = file.listFiles();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
       View view = inflater.inflate(R.layout.fragment_album,container,false);
        GridView gridView = (GridView) view.findViewById(R.id.album_gridview);
        GridViewAdapter adapter = new GridViewAdapter();
        gridView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        gridView.setOnItemClickListener(new GridOnItemClickListener());
        return view;
    }

    class GridOnItemClickListener implements AdapterView.OnItemClickListener{

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Bundle bundle = new Bundle();
            bundle.putInt("index",position);
            FragmentManager fm = getFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            SeekBigPicFragment fragment = new SeekBigPicFragment();
            fragment.setArguments(bundle);
            ft.replace(R.id.album_container,fragment);
            ft.addToBackStack(null);
            ft.commit();

        }
    }
    class GridViewAdapter extends BaseAdapter{
        BitmapCache cache;
     public GridViewAdapter(){
         cache = BitmapCache.getCacheInstance();
     }
        @Override
        public int getCount() {
            return filePaths.length;
        }

        @Override
        public Object getItem(int position) {
            return filePaths[position];
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            int mWidth;
            int mHeight;
            ViewHolder viewHolder;
            if(convertView==null){
                viewHolder = new ViewHolder();
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.album_item,null);
                viewHolder.imageView = (ImageView) convertView.findViewById(R.id.album_item_image);
                convertView.setTag(viewHolder);
            }
            viewHolder = (ViewHolder) convertView.getTag();
            //获取view的宽，高
         //   mHeight = viewHolder.imageView.getLayoutParams().height/2;
         //   mWidth = viewHolder.imageView.getLayoutParams().width/2;
         //   System.out.println("mHeight:"+mHeight+"\nmWidth:"+mWidth);
            viewHolder.imageView.setImageBitmap(cache.getBitmap(filePaths[position].toString()));
            return convertView;
        }

        class ViewHolder{
            ImageView imageView;
        }

    }
}
