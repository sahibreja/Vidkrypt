package com.example.vidkrypt.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.example.vidkrypt.R;


public class SliderAdapter extends PagerAdapter {


    Context context;
    LayoutInflater layoutInflater;
    int images[];
    int headings[];
    int description[];

    public SliderAdapter() {
    }


    public SliderAdapter(Context context, int[] images, int[] headings, int[] description) {
        this.context = context;
        this.images = images;
        this.headings = headings;
        this.description = description;
    }

    public SliderAdapter(Context context, int[] images) {
        this.context = context;
        this.images = images;
        headings =new int[0];
        description = new int[0];
    }

    @Override
    public int getCount() {
        return  images.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view ==(LinearLayout) object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {

        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.slide_layout,container,false);

        ImageView slidetitleimage = (ImageView) view.findViewById(R.id.titleImage);
        //TextView slideHeading = (TextView) view.findViewById(R.id.texttitle);
        //TextView slideDesciption = (TextView) view.findViewById(R.id.textdeccription);

        slidetitleimage.setImageResource(images[position]);
//        if(headings.length==0 && description.length==0)
//        {
//            slideHeading.setVisibility(View.GONE);
//            slideDesciption.setVisibility(View.GONE);
//        }else{
//
//            slideHeading.setText(headings[position]);
//            slideDesciption.setText(description[position]);
//        }
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        View view =(View) object;
        container.removeView(view);

    }
}
