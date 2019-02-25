package com.sdk.views.listview.pulllistview.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;

import com.sdk.net.msg.WebMsg;
import com.sdk.views.dialog.Toast;
import com.tdp.main.R;

import java.util.ArrayList;
import java.util.List;
import android.widget.RelativeLayout;
import android.widget.TextView;
/**
 * @author:zlcai
 * @createrDate:2018/8/2
 * @lastTime:2018/8/2
 * @detail:
 */

public abstract class BaseAdapter<T> extends android.widget.BaseAdapter {

    protected List<T> datas = new ArrayList<>();
    protected Context context;
    protected int state = -1; // 不显示， 2： 展示网络问题， 3：展示
    protected String errorMsg = "";
    public OnErrorClickListener onErrorClickListener;
    private OnScreen onScreen;
    private int contentHeight = 0;

    public void setOnScreen(OnScreen onScreen) {
        this.onScreen = onScreen;
    }

    public void setContentHeight(int contentHeight) {
        this.contentHeight = contentHeight;
    }

    public void setOnErrorClickListener(OnErrorClickListener onErrorClickListener) {
        this.onErrorClickListener = onErrorClickListener;
    }

    public BaseAdapter(Context context){
        this.context = context;
    }

    public void setDatas(T data){
       this.datas.clear();
        if(data != null){
            this.datas.add(data);
        }
        this.notifyDataSetChanged();
    }

    public void setDatas(List<T> datas){
        if(datas == null){
            this.datas.clear();
        } else {
            this.datas = datas;
        }
        this.notifyDataSetChanged();
    }

    public void showError(int errorCode, String errorMsg){
        state = errorCode;
        if(errorCode != WebMsg.STATE_OK){
            this.datas.clear();
        }
        this.notifyDataSetChanged();
    }

    public void showLoading(String msg){
        state = 0;
        this.datas.clear();
        String errorMsg = msg;
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return datas.size() == 0 ? state == -1 ? 0 : 1  : datas.size();
    }

    @Override
    public Object getItem(int i) {
        return datas.size() == 0 || datas.size() <= i ? null : datas.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    final public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (datas.size() == 0 && state != -1) {
            holder = new ViewHolder();

            if(state == 0){
                convertView = ((Activity) (context)).getLayoutInflater().inflate(R.layout.zl_dialog_loading, parent, false);
            } else if(state != 200){
                convertView = ((Activity) (context)).getLayoutInflater().inflate(R.layout.zl_refresh_page_neterror, parent, false);
                if(errorMsg != null && errorMsg.length() > 0){
                    TextView msgTv = (TextView)convertView.findViewById(R.id.item_status_nodata_msg_tv);
                    msgTv.setText(errorMsg);
                    Toast.show(context, errorMsg, Toast.LENGTH_LONG);
                }
                if(this.onErrorClickListener != null){
                    convertView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            BaseAdapter.this.onErrorClickListener.refresh();
                        }
                    });
                }
            } else {
                convertView = ((Activity) (context)).getLayoutInflater().inflate(R.layout.zl_refresh_page_nodata, parent, false);
            }

            holder.noDataRootLayout = (RelativeLayout) convertView.findViewById(R.id.root_layout);
            //convertView.setTag(holder);

            AbsListView.LayoutParams lp = new AbsListView.LayoutParams(getScreenWidth(), getScreenHeight());
            holder.noDataRootLayout.setLayoutParams(lp);

            return convertView;
        } else {

            return getView(position, convertView == null || convertView.getTag() == null ? null : convertView);
        }
    }

    public abstract View getView(int position, View convertView);

    protected int getScreenWidth() {
        if(onScreen == null){
            DisplayMetrics displayMetric = Resources.getSystem().getDisplayMetrics();
            return displayMetric.widthPixels;
        } else {
            return onScreen.contentWidth();
        }
    }

    protected int getScreenHeight() {
        if(onScreen == null){
            DisplayMetrics displayMetric = Resources.getSystem().getDisplayMetrics();
            return displayMetric.heightPixels;
        } else {
            return onScreen.contentHeight();
        }
    }

    private static final class ViewHolder {
        RelativeLayout noDataRootLayout;
    }

    public interface OnErrorClickListener{
        public void refresh();
    }

    public interface OnScreen{
        public int contentWidth();
        public int contentHeight();
    }
}
