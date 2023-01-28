package com.example.vidkrypt.profile;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.vidkrypt.R;

public class HelpActivity extends AppCompatActivity {
    private ImageView backBtn;
    private TextView que1,que2,que3;
    private LinearLayout ans1,ans2,ans3;
    private boolean q1=false,q2=false,q3=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        init();
        buttonClick();

    }

    private void buttonClick() {

        que1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(q1)
                {
                    ans1.setVisibility(View.GONE);
                    q1=false;
                }else
                {
                    ans1.setVisibility(View.VISIBLE);
                    q1=true;
                }
            }
        });
        que2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(q2)
                {
                    ans2.setVisibility(View.GONE);
                    q2=false;
                }else
                {
                    ans2.setVisibility(View.VISIBLE);
                    q2=true;
                }
            }
        });
        que3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(q3)
                {
                    ans3.setVisibility(View.GONE);
                    q3=false;
                }else
                {
                    ans3.setVisibility(View.VISIBLE);
                    q3=true;
                }
            }
        });
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private void init()
    {
        backBtn=findViewById(R.id.backBtn);
        que1=findViewById(R.id.que1);
        que2=findViewById(R.id.que2);
        que3=findViewById(R.id.que3);
        ans1=findViewById(R.id.ans1);
        ans2=findViewById(R.id.ans2);
        ans3=findViewById(R.id.ans3);

    }
}