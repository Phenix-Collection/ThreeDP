package com.sdk.views.table;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import java.lang.reflect.Field;
import com.sdk.utils.GenericsUtils;
import com.sdk.utils.ScreenUtils;
import com.tdp.main.R;

import org.apache.commons.lang.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author:zlcai
 * @createrDate:2018/7/31
 * @lastTime:2018/7/31
 * @detail:
 */

public class TableView<T> extends RelativeLayout {

    private Context context;

    private int screenHeight;
    private int screenWidth;

    private Map<String,Integer> columWidth = new HashMap<>();
    private String[] columNames;
    TableLayout titleTl ;
    TableLayout contentTl;

    @SuppressWarnings("rawtypes")
    protected Class clazz;


    public TableView(Context context) {
        super(context);
        init();
    }

    public TableView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TableView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){
        this.context = getContext();
        View v = LayoutInflater.from(context).inflate(R.layout.zl_table_view, null);
        addView(v);

        titleTl = v.findViewById(R.id.id_top);
        contentTl = v.findViewById(R.id.id_content);

        screenHeight = ScreenUtils.getScreenHeight(context);
        screenWidth = ScreenUtils.getScreenWidth(context);

        // 通过反射机制获取子类传递过来的实体类的类型信息
        //ParameterizedType type = (ParameterizedType) this.getClass().getGenericSuperclass();
        clazz = (Class<T>) GenericsUtils.getSuperClassGenricType(TableView.class, 0);
    }

    public void setTitles(String[] titles, String[] columNames){
        titleTl.removeAllViews();
        columWidth.clear();
        this.columNames = columNames;

        TextView tv = null;
        TableRow tr = new TableRow(context);
        tr.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        tr.setBackgroundColor(getResources().getColor(R.color.colorLightGray));

        titleTl.addView(tr);

        int count = StringUtils.join(titles).length();

        for(int i = 0; i < titles.length; i ++){

            String title = titles[i];

            tv = new TextView(context);

            // colLp.weight = 1;
            tv.setTextColor(context.getResources().getColor(R.color.colorPrimaryBlue));
            tv.setGravity(CENTER_HORIZONTAL);
            tv.setPadding(8,8,8,8);
            columWidth.put(columNames[i],screenWidth * title.length() / count);
            tv.setWidth(columWidth.get(columNames[i]));
            //Log.v("app", "screenWidth:"+screenWidth+screenWidth * (title.length() / count));
            tv.setText(title);
            tr.addView(tv);
        }
    }

    public void setContents(List<T> datas) {
        contentTl.removeAllViews();
        TextView tv = null;
        TableRow tr = new TableRow(context);
        // key是属性名，value是对应值
        //  Map<String, Object> fieldValueMap = new HashMap<String, Object>();
        // 获取当前加载的实体类中所有属性（字段）
//        Field[] fields = this.clazz.getDeclaredFields();
        Field[] fields = null;

        for (T t : datas) {
            tr.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
           // tr.setBackgroundColor(getResources().getColor(R.color.colorLightGray));
            contentTl.addView(tr);

            fields = t.getClass().getDeclaredFields();
            Map<String, String> methods = new HashMap<>();
            for (int i = 0; i < fields.length; i++) {
                Field f = fields[i];
                String key = f.getName();// 属性名
                Object value = null;//属性值

                if ("$change".equals(key)) continue;
                if (!"serialVersionUID".equals(key)) {// 忽略序列化版本ID号
                    f.setAccessible(true);// 取消Java语言访问检查
                    try {
                        value = f.get(t);
                    } catch (IllegalArgumentException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                    methods.put(key, "" + value);
                }
            }
            for (int i = 0; i < columNames.length; i++) {

                tv = new TextView(context);
                // colLp.weight = 1;
                tv.setTextColor(context.getResources().getColor(R.color.grgray));
                tv.setGravity(CENTER_HORIZONTAL);
                tv.setPadding(8,8,8,8);
                tv.setWidth(columWidth.get(columNames[i]));
                tv.setText(methods.get(columNames[i]) == null ? String.valueOf(i + 1) : methods.get(columNames[i]));
                tr.addView(tv);

                //fieldValueMap.put(key, value);
            }
        }
    }
}
