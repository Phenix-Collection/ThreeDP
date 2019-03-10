package com.tdp.main.fragment.editface;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.sdk.views.listview.HorizontalListView;
import com.tdp.base.BaseFragment;
import com.tdp.main.R;
import com.tdp.main.adapter.EditFaceMenuAdapter;
import com.tdp.main.constant.EditFaceMenuConstant;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * ahtor: super_link
 * date: 2019/3/10 17:36
 * remark:
 */
public class EditFaceFragment extends BaseFragment {

    /** 菜单适配器 */
    private EditFaceMenuAdapter menuAdapter;
    /** 菜单 */
    @BindView(R.id.hsv_menu)
    private HorizontalListView menuLv;
    /** 内容 */
    @BindView(R.id.hsv_content)
    HorizontalListView coantentHlv;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_editface_figure, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ButterKnife.bind(this, view);
        init();
//        loadFriendData();

    }

    private void init(){
        // 初始化菜单（根据男女来选定菜单）
        menuAdapter = new EditFaceMenuAdapter(this.getActivity(), EditFaceMenuConstant.title_boy);
        menuLv.setAdapter(menuAdapter);

        // listener
        menuLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // 选中某一项后更新适配器UI
                menuAdapter.setPosition(position);
                // 呈现相应的功能界面


            }
        });
    }


}
