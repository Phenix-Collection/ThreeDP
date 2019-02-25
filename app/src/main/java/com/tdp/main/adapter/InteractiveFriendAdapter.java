package com.tdp.main.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.sdk.api.entity.UserInfoEntity;
import com.sdk.core.CacheImageControl;
import com.sdk.core.Globals;
import com.tdp.main.R;
import com.tdp.main.entity.FriendInfoEntity;
import java.util.ArrayList;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Cache;

public class InteractiveFriendAdapter extends BaseAdapter implements View.OnClickListener {

    private Context context;
    private List<FriendInfoEntity> datas = new ArrayList<>();
    private AdapterView.OnItemClickListener listener;

    public InteractiveFriendAdapter(Context context){
        this.context = context;
    }

    public void setDatas(List<FriendInfoEntity> datas){
        if(datas != null){
            this.datas = datas;
        } else {
            this.datas.clear();
        }
        this.notifyDataSetChanged();
    }

    public void setOnItemClickListener( AdapterView.OnItemClickListener listener){
        this.listener = listener;
    }

    @Override
    public int getCount() {
        return datas.size();
    }

    @Override
    public Object getItem(int i) {
        return datas.size() > i ? datas.get(i) : null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(final int position, View view, ViewGroup viewGroup) {

        ViewHolder viewHolder = null;

//        if(position == 0){
//            view = LayoutInflater.from(context).inflate(R.layout.item_fragment_interactive_search, null);
//            view.setTag(null);
//        } else {
            if(view == null || view.getTag() == null){
                view = LayoutInflater.from(context).inflate(R.layout.item_fragment_interactive_friend, null);
                viewHolder = new ViewHolder( view);
                view.setTag(viewHolder);
            }else {
                viewHolder = (ViewHolder) view.getTag();
            }
//            ImageLoader.getInstance().displayImage(datas.get(position - 1).getProfile_photo(), viewHolder.ulogoIv, ImageLoaderUtils.getDefaultOptions());
            FriendInfoEntity data = datas.get(position);
            CacheImageControl.getInstance().setImageView(viewHolder.ulogoIv, Globals.BASE_API + data.getProfile_photo(), true );
            viewHolder.unameTv.setText(data.getRemark().length() == 0 ? data.getNick_name() : data.getRemark());
//            viewHolder.detailTv.setText(data.getNick_name());
//        }

        return view;
    }

    @Override
    public void onClick(View view) {
        int position = Integer.parseInt(view.getTag().toString());
        if(listener != null){
            listener.onItemClick(null, view, position, -1);
        }
    }

    class ViewHolder {

        @BindView(R.id.iv_interactive_ulogo)
        ImageView ulogoIv;
        @BindView(R.id.tv_interactive_uname)
        TextView unameTv;
//        @BindView(R.id.id_detail)
//        TextView detailTv;

        public ViewHolder(View v){
            ButterKnife.bind(this,v);
        }
    }
}
