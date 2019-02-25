package com.tdp.main.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;

import com.tdp.main.R;

public class InteractiveChatAdapter extends BaseAdapter implements View.OnClickListener {

    private Context context;
   // private List<FriendInfoEntity> datas = new ArrayList<>();
    private AdapterView.OnItemClickListener listener;

    public InteractiveChatAdapter(Context context){
        this.context = context;
    }

//    public void setDatas(List<FriendInfoEntity> datas){
//        if(datas != null){
//            this.datas = datas;
//        } else {
//            this.datas.clear();
//        }
//        this.notifyDataSetChanged();
//    }

    public void setOnItemClickListener( AdapterView.OnItemClickListener listener){
        this.listener = listener;
    }

    @Override
    public int getCount() {
        return 12;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(final int position, View view, ViewGroup viewGroup) {

        ViewHolder viewHolder = null;
        if(view == null){
            view = LayoutInflater.from(context).inflate(R.layout.item_fragment_video, null);
//            viewHolder = new ViewHolder(view);
            view.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) view.getTag();
        }

//        viewHolder.unameTv.setText(datas.get(position).getUname());
//        viewHolder.dateTv.setText(datas.get(position).getDate());

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

//        @BindView(R.id.iv_interactive_ulogo)
//        ImageView ulogoIv;
//        @BindView(R.id.tv_interactive_uname)
//        TextView unameTv;
//        @BindView(R.id.tv_interactive_date)
//        TextView dateTv;
//        @BindView(R.id.tv_interactive_number)
//        TextView numberTv;
//
//        public ViewHolder(View v){
//            ButterKnife.bind(this,v);
//        }
    }
}
