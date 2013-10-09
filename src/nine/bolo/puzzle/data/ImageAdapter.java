package nine.bolo.puzzle.data;

import nine.bolo.puzzle.R;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

public class ImageAdapter extends BaseAdapter {
    public ImageAdapter(Context c) {
        mContext = c;
    }

    public int getCount() {
        return mThumbIds.length;
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(GridView.LayoutParams.WRAP_CONTENT, GridView.LayoutParams.WRAP_CONTENT));
            imageView.setAdjustViewBounds(false);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(20, 20, 20, 20);
        } else {
            imageView = (ImageView) convertView;
        }

        imageView.setImageResource(mThumbIds[position]);

        return imageView;
    }

    private Context mContext;

    private Integer[] mThumbIds = {
    		R.drawable.conew_songshu1,
    		
    		R.drawable.conew_cat1,
    		R.drawable.conew_cat2,
    		R.drawable.conew_cat3,
    		R.drawable.conew_cat4,
    		R.drawable.conew_cat5,
    		R.drawable.conew_cat6,
    		
    		R.drawable.conew_dog1,
    		R.drawable.conew_dog2,
    		R.drawable.conew_dog3,
    		R.drawable.conew_dog4,
    		R.drawable.conew_dog5,
    		R.drawable.conew_dog6,
    		R.drawable.conew_dog7,
    		
    		R.drawable.conew_bird1,
    		
    		R.drawable.conew_fox1,
    		
    		R.drawable.conew_horse1,
    		
    		R.drawable.conew_koala1,
    		
    		R.drawable.conew_mouse1,
    		
    		R.drawable.conew_wolf1,
    		
    		R.drawable.conew_ox1,
    		
    		R.drawable.conew_lion,
    		
    		R.drawable.conew_county1,
    		
    		R.drawable.conew_house1,
    		
    		R.drawable.conew_snow1,
    		
    		R.drawable.conew_sea1,
    		
    		R.drawable.conew_space1,
    		
    		R.drawable.conew_water1,
    		R.drawable.conew_water2,
    		R.drawable.conew_water3,
    		
    		
    		R.drawable.conew_road2,
    		

    };


}