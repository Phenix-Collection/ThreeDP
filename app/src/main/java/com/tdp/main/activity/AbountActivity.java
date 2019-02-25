package com.tdp.main.activity;

import android.os.Bundle;
import android.view.View;

import com.sdk.views.Menu.TopMenuView;
import com.tdp.base.BaseActivity;
import com.tdp.main.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * ahtor: super_link
 * date: 2018/9/27 15:08
 * remark:
 */
public class AbountActivity extends BaseActivity {
    @BindView(R.id.id_menu)
    TopMenuView menuView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        ButterKnife.bind(this);
        init();
    }

    private void init(){
        //data

        // listener
        menuView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()){
                    case TopMenuView.CLICK_LEFT:
                        finish();
                        break;
//                    case TopMenuView.CLICK_CENTER:
//
//                        break;
                }
            }
        });
    }
}
