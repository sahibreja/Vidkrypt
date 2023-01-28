package com.example.vidkrypt;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.vidkrypt.Adapter.SliderAdapter;
import com.example.vidkrypt.login.LoginActivity;
import com.example.vidkrypt.select.model.PrefManager;

public class IntroScreenActivity extends AppCompatActivity {

    private ViewPager mSlideViewPager;
    private LinearLayout mDotLayout;
    private Button backBtn, nextBtn, skipBtn;
    private ImageView imageView;
    private TextView[] mDots;
    private SliderAdapter sliderAdapter;
    private int mCurrentPage;
    PrefManager prefManager ;
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefManager = new PrefManager(this);
        if (!prefManager.isFirstTimeLaunch()) {
            launchHomeScreen();
            finish();
        }
        setContentView(R.layout.activity_intro_screen);

        backBtn = findViewById(R.id.backBtn);
        nextBtn = findViewById(R.id.nextBtn);
        skipBtn = findViewById(R.id.skipBtn);
        imageView =findViewById(R.id.image1);
        mSlideViewPager = findViewById(R.id.viewPager);
        mDotLayout=findViewById(R.id.indicatorLayout);
        // Checking for first time launch - before calling setContentView()
        int images[] = {

                R.drawable.s_one,
                R.drawable.s_two,
                R.drawable.s_three,
        };
        int headings[] = {

                R.string.heading_one,
                R.string.heading_two,
                R.string.heading_three,

        };
        int description[] = {

                R.string.desc_one,
                R.string.desc_two,
                R.string.desc_three,

        };
        sliderAdapter = new SliderAdapter(this,images,headings,description);
        mSlideViewPager.setAdapter(sliderAdapter);
        addDotsIndicator(0);
        mSlideViewPager.addOnPageChangeListener(viewListener);

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSlideViewPager.setCurrentItem(mCurrentPage+1);
                if(mCurrentPage ==mDots.length-1)
                {
                    nextBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            launchHomeScreen();
                        }
                    });
                }
            }
        });
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSlideViewPager.setCurrentItem(mCurrentPage -1);
            }
        });
        skipBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchHomeScreen();
            }
        });
    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void addDotsIndicator(int position)
    {
        mDots=new TextView[3];
        mDotLayout.removeAllViews();
        for (int i = 0 ; i < mDots.length ; i++)
        {
            mDots[i] = new TextView(this);
            mDots[i].setText(Html.fromHtml("&#8226"));
            mDots[i].setTextSize(35);
            mDots[i].setTextColor(getResources().getColor(R.color.inactive,getApplicationContext().getTheme()));
            mDotLayout.addView(mDots[i]);
        }
        if(mDots.length > 0)
        {
            mDots[position].setTextColor(getResources().getColor(R.color.active,getApplicationContext().getTheme()));
        }
    }
    private void launchHomeScreen() {
        prefManager.setFirstTimeLaunch(false);
        startActivity(new Intent(IntroScreenActivity.this, LoginActivity.class));
        finish();
    }
    ViewPager.OnPageChangeListener viewListener= new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public void onPageSelected(int position) {
            addDotsIndicator(position);
            mCurrentPage =position;
            if(position == 0)
            {
                nextBtn.setEnabled(true);
                backBtn.setEnabled(false);
                nextBtn.setText("Next");
                backBtn.setText("");
                backBtn.setVisibility(View.INVISIBLE);
                imageView.setVisibility(View.INVISIBLE);

            }else if(position == mDots.length-1){
                nextBtn.setEnabled(true);
                backBtn.setEnabled(true);
                nextBtn.setText("Start");
                backBtn.setText("Back");
                backBtn.setVisibility(View.VISIBLE);
                imageView.setVisibility(View.VISIBLE);
            }else
            {
                nextBtn.setEnabled(true);
                backBtn.setEnabled(true);
                nextBtn.setText("Next");
                backBtn.setText("Back");
                backBtn.setVisibility(View.VISIBLE);
                imageView.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };
}