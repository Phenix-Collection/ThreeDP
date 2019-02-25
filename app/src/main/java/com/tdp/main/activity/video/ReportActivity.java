package com.tdp.main.activity.video;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.sdk.views.Menu.TopMenuView;
import com.tdp.base.BaseActivity;
import com.tdp.main.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ReportActivity extends BaseActivity {
    @BindView(R.id.img1)
    ImageView img1;
    @BindView(R.id.img2)
    ImageView img2;
    @BindView(R.id.img3)
    ImageView img3;
    @BindView(R.id.img4)
    ImageView img4;
    @BindView(R.id.img5)
    ImageView img5;
    @BindView(R.id.img6)
    ImageView img6;
    @BindView(R.id.img7)
    ImageView img7;
    @BindView(R.id.tv_submit)
    TextView tvSubmit;
    ImageView[] img_list;
    String[] choose_list=new String[]{"广告","色情低俗","惊悚恐怖","反动","欺诈或恶意营销","标题夸张","抄袭"};
    boolean[] if_choose=new boolean[]{false,false,false,false,false,false,false};
    List<String> choose_result=new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        ButterKnife.bind(this);
        img_list=new ImageView[]{img1,img2,img3,img4,img5,img6,img7};
        tvSubmit.setClickable(false);
    }

    @OnClick({TopMenuView.CLICK_LEFT, R.id.rl1, R.id.rl2, R.id.rl3, R.id.rl4, R.id.rl5, R.id.rl6, R.id.rl7, R.id.tv_submit})
    public void onClick(View view) {
        switch (view.getId()) {
            case TopMenuView.CLICK_LEFT:
                finish();
                break;
            case R.id.rl1:
                choose(0);
                break;
            case R.id.rl2:
                choose(1);
                break;
            case R.id.rl3:
                choose(2);
                break;
            case R.id.rl4:
                choose(3);
                break;
            case R.id.rl5:
                choose(4);
                break;
            case R.id.rl6:
                choose(5);
                break;
            case R.id.rl7:
                choose(6);
                break;
            case R.id.tv_submit:
                Toast.makeText(this,choose_result.toString(),Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void choose(int index){
        if(if_choose[index]){
            img_list[index].setVisibility(View.GONE);
            choose_result.remove(choose_list[index]);
            if_choose[index]=false;
        }else{
            img_list[index].setVisibility(View.VISIBLE);
            choose_result.add(choose_list[index]);
            if_choose[index]=true;
        }
        if(choose_result.size()>0){
            tvSubmit.setClickable(true);
            tvSubmit.setBackground(getResources().getDrawable(R.drawable.r8_deepskyblue_btn));
        }else {
            tvSubmit.setClickable(false);
            tvSubmit.setBackground(getResources().getDrawable(R.drawable.r8_deep_gray));
        }
    }
}
