package com.kzax1l.oiv.sample;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.kzax1l.oiv.OperableItemView;

/**
 * Created by Zsago on 2017/7/3.
 *
 * @author Zsago
 */
public class TestActivity extends AppCompatActivity {
    float percent = 0f;
    boolean enable = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_test);
        findViewById(R.id.oiv_test).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "TEST!", Snackbar.LENGTH_SHORT).show();
                ((OperableItemView) view).setBodyText("法律是否");
                ((OperableItemView) view).setBodyTextColor(Color.parseColor("#ff0000"));
            }
        });
        findViewById(R.id.oiv_circle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((OperableItemView) view).enableBriefText(enable, true);
                enable = !enable;
            }
        });
        findViewById(R.id.oiv_percent).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((OperableItemView) view).enableBriefText(false, percent);
                percent += 0.1;
            }
        });
    }
}
