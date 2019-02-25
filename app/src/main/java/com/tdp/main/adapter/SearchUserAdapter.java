package com.tdp.main.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.sdk.api.entity.UserInfoEntity;
import com.sdk.core.CacheImageControl;
import com.sdk.core.Globals;
import com.tdp.main.R;
import java.util.ArrayList;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * ahtor: super_link
 * date: 2018/10/26 16:20
 * remark:
 */
public class SearchUserAdapter extends BaseAdapter {

    private Context context;
    private List<UserInfoEntity> datas = new ArrayList<>();

    public SearchUserAdapter(Context context){
        this.context = context;
    }

    public void setDatas(List<UserInfoEntity> datas){
        if(datas == null){
            this.datas.clear();
        } else {
            this.datas = datas;
        }

        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return datas.size();
    }

    @Override
    public Object getItem(int position) {
        return datas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder;

        if(convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.item_activity_search_user, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        UserInfoEntity data = datas.get(position);
        viewHolder.accountTv.setText(data.getAccount());
//        ImageLoader.getInstance().displayImage(Globals.BASE_API + data.getProfilePhoto(), viewHolder.logoIv, ImageLoaderUtils.getHeaderOptions());
        CacheImageControl.getInstance().setImageView(viewHolder.logoIv, Globals.BASE_API + data.getProfilePhoto(), false);
        viewHolder.phoneTv.setText(data.getNickName());
        convertView.setBackgroundResource(R.drawable.ra_white_btn);
        return convertView;
    }

    class ViewHolder {

        ViewHolder(View v){
            ButterKnife.bind(this, v);
        }

        @BindView(R.id.id_logo)
        ImageView logoIv;
        @BindView(R.id.id_account)
        TextView accountTv;
        @BindView(R.id.id_phone)
        TextView phoneTv;
    }
}
