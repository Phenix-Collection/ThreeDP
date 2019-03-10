package com.tdp.main.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import com.sdk.views.listview.pulllistview.adapter.BaseAdapter;
import com.tdp.main.R;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * ahtor: super_link
 * date: 2019/3/10 17:49
 * remark:
 */
public class EditFaceMenuAdapter extends BaseAdapter {

    private Context context;
    /** 当前选中下标 */
    private int position = 0;
    private String[] datas;

    public EditFaceMenuAdapter(Context context, String[] datas) {
        super(context);
        this.context = context;
        this.datas = datas;
    }

    public int getPosition() {
        return position;
    }

    /***
     * 选中某一项
     * @param position
     */
    public void setPosition(int position) {
        this.position = position;
        this.notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView) {

        HolderView holderView = null;
        if(convertView == null){
            convertView = (View) LayoutInflater.from(context).inflate(R.layout.fragment_editface_figure, null);
            holderView = new HolderView(convertView);
            convertView.setTag(holderView);
        } else {
            holderView = (HolderView) convertView.getTag();
        }

        holderView.titleTv.setText(datas[position]);

        holderView.vChecked.setVisibility(this.position != position ? View.INVISIBLE : View.VISIBLE);

        return convertView;
    }

    class HolderView{
        /** 标题 */
        @BindView(R.id.tv_title)
        TextView titleTv;
        /** 选中View */
        @BindView(R.id.v_checked)
        View vChecked;

        public HolderView(View view){
            ButterKnife.bind(this, view);
        }

    }

}
