package com.tdp.main.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import com.sdk.views.listview.pulllistview.adapter.BaseAdapter;
import com.tdp.main.R;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * ahtor: super_link
 * date: 2019/1/29 12:40
 * remark:
 */
public class FigureAdapter extends BaseAdapter {

    private Context context;
    private String[] bundleArray;
    private int[] icons;

    public FigureAdapter(Context context) {
        super(context);
        this.context = context;
    }

    /***
     * 设置数据
     * @param bundleArray
     */
    public void setDatas(String[] bundleArray, int[] icons){
//        this.type = type;
        this.bundleArray = bundleArray;
        this.icons = icons;
        this.notifyDataSetChanged();
    }

    @Override
    public Object getItem(int i) {
        return bundleArray[i];
    }

    @Override
    public int getCount() {

        return bundleArray != null ? bundleArray.length : 0;
//        switch (type){
//            case 1: // 衣服
//                break;
//            case 2: // 帽子
//                break;
//            case 3: // 套服
//
//                break;
//        }

//        return super.getCount();
    }

    @Override
    public View getView(int position, View convertView) {

        HolderView holderView;
        if(convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.item_figure_part, null);
            holderView = new HolderView(convertView);
            convertView.setTag(holderView);
        } else {
            holderView = (HolderView) convertView.getTag();
        }

        holderView.partIv.setImageDrawable(context.getResources().getDrawable(icons[position]));

        return convertView;
    }

    class HolderView {
        @BindView(R.id.iv_part)
        ImageButton partIv;

        public HolderView(View v){
            ButterKnife.bind(this, v);
        }
    }

}
