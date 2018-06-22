package com.andova.oiv.test;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.andova.oiv.OperableItemView;
import com.github.florent37.expectanim.ExpectAnim;

import static com.github.florent37.expectanim.core.Expectations.bottomOfParent;
import static com.github.florent37.expectanim.core.Expectations.leftOfParent;
import static com.github.florent37.expectanim.core.Expectations.width;

/**
 * Created by Administrator on 2017-11-10.
 *
 * @author kzaxil
 * @since 1.0.0
 */
public class OivTestActivity extends AppCompatActivity implements AppBarLayout.OnOffsetChangedListener {
    private ExpectAnim mExpectAnim;
    private OperableItemView oivToDoNum;

    float percent = 0f;
    boolean enable = false;
    boolean reverse = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_oiv_test);
        oivToDoNum = (OperableItemView) findViewById(R.id.oiv_work_todo);
        findViewById(R.id.oiv_test).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "TEST!", Snackbar.LENGTH_SHORT).show();
                ((OperableItemView) view).setBodyText("法律是否");
                ((OperableItemView) view).setBodyTextColor(Color.parseColor("#ff0000"));
            }
        });
        findViewById(R.id.oiv_icon).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((OperableItemView) view).setDrawableVisible(!((OperableItemView) view).isEndDrawableVisible());
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
                if (reverse) {
                    percent -= 0.1;
                } else {
                    percent += 0.1;
                }
                if (percent >= 1) {
                    reverse = true;
                } else if (percent <= 0) {
                    reverse = false;
                }
                ((OperableItemView) view).setBriefText((int) (100 * percent) + "%");
                ((OperableItemView) view).enableBriefText(false, percent);
            }
        });

        mExpectAnim = new ExpectAnim()
                .expect(oivToDoNum)
                .toBe(
                        bottomOfParent().withMarginDp(9),
                        leftOfParent().withMarginDp(15),
                        width(30).toDp().keepRatio()
                )
                .toAnimation();

        ((AppBarLayout) findViewById(R.id.abl_layout)).addOnOffsetChangedListener(this);
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        float percent = (float) Math.abs(verticalOffset) / (float) appBarLayout.getTotalScrollRange();
        mExpectAnim.setPercent(percent);
        oivToDoNum.enableBodyText(false, percent);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            ((AppBarLayout) findViewById(R.id.abl_layout)).setExpanded(true, true);
        }
    }
}
