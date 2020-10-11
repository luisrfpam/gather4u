package org.ufam.gather4u.utils;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import org.ufam.gather4u.R;

public class CustomSwipeAdapter extends PagerAdapter {

    private int[] imgs = {R.drawable.splash01, R.drawable.splash02, R.drawable.splash03};
    private Context context = null;
    private LayoutInflater inflater = null;

    public CustomSwipeAdapter(Context ctx){
        this.context = ctx;
    }

    @Override
    public int getCount() {
        return imgs.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return ( view == (LinearLayout)object );
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate( R.layout.swipe_layout, container, false);

        ImageView img = (ImageView) view.findViewById (R.id.imgChange);

        img.setImageResource(imgs[position]);
        container.addView(view);

        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((LinearLayout)object);
    }

}
