package com.tdp.main.adapter;

import android.content.Context;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.sdk.api.entity.AddFriendEntity;
import com.sdk.core.CacheImageControl;
import com.sdk.core.Globals;
import com.tdp.main.R;
import com.tdp.main.utils.MiscUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * ahtor: super_link
 * date: 2018/9/28 18:10
 * remark:
 */
public class MessageAdapter extends BaseAdapter {

    private Context context;
    private List<AddFriendEntity> datas = new ArrayList<>();
    private AdapterView.OnItemClickListener listener;


    public MessageAdapter(Context context, AdapterView.OnItemClickListener listener){
        this.context = context;
        this.listener = listener;
    }

    public void setDatas(List<AddFriendEntity> datas){
        if(datas == null){
            this.datas.clear();
        } else {
            this.datas = datas;
        }
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return datas.size();
    }

    @Override
    public Object getItem(int i) {
        return datas.size() > i ? datas.get(i) : datas.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(final int position, View view, ViewGroup viewGroup) {

        HolderView holderView = null;
        if(view == null){
            view = LayoutInflater.from(context).inflate(R.layout.item_activity_message, null);
            holderView = new HolderView(view);
            view.setTag(holderView);
        } else {
            holderView = (HolderView) view.getTag();
        }

        AddFriendEntity data = datas.get(position);
        CacheImageControl.getInstance().setImageView(holderView.logoIv, Globals.BASE_API + data.getProfile_photo(), true);
        holderView.unameTv.setText(data.getNick_name());
        holderView.okTv.setText(data.getStatus() == 0 ? "同意" : data.getStatus() == 1 ? "已同意" : "已拒绝");
        holderView.detailTv.setText(MiscUtil.unicodeToString(data.getRecommend()));

        final View tempView = view;
        holderView.okTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(null, tempView, position, -1);
            }
        });

        return view;
    }

    class HolderView {
        @BindView(R.id.id_logo)
        ImageView logoIv;
        @BindView(R.id.id_uname)
        TextView unameTv;
        @BindView(R.id.id_detail)
        TextView detailTv;
        @BindView(R.id.id_ok)
        TextView okTv;

        public HolderView(View v){
            ButterKnife.bind(this, v);
        }
    }

}
