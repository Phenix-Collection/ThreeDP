package com.sdk.views.date;

import android.app.AlertDialog;
import android.content.Context;
import android.support.annotation.StyleRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import com.sdk.utils.CalendarUtil;
import com.tdp.main.R;

import java.util.Calendar;
import java.util.Date;

/**
 * 日期控制器，可以选择年月日以及清空按钮的显示与隐藏，可以限制时间的选择范围。
 * Created by lwt on 2017/11/23.
 */

public class DateTimeDialog extends AlertDialog implements View.OnClickListener {
    private DatePicker mDatePicker;
    private MyOnDateSetListener onDateSetListener;
    private Button cancleButton, clearButton, okButton;
    private LinearLayout clearLl;

    // 控制 日期
    private int measureWidth;

    // 是否 显示 日选择器   true 显示 ，false 隐藏
    private boolean isDayVisible = true;
    // 是否 显示 月选择器   true 显示 ，false 隐藏
    private boolean isMonthVisible = true;
    // 是否 显示 年选择器   true 显示 ，false 隐藏
    private boolean isYearVisible = true;
    // 是否 显示 清空按钮   true 显示 ，false 隐藏
    private boolean isClearVisible = true;

    protected DateTimeDialog(Context context) {
        super(context);
    }

    protected DateTimeDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    protected DateTimeDialog(Context context, @StyleRes int themeResId) {
        super(context, themeResId);
    }

    /**
     * @param context        上下文对象
     * @param callBack       选择 监听器
     * @param isYearVisible  年 是否可见
     * @param isMonthVisible 月 是否可见
     * @param isDayVisible   日 是否可见
     */
    public DateTimeDialog(Context context, MyOnDateSetListener callBack, boolean isYearVisible, boolean isMonthVisible, boolean isDayVisible, boolean isClearVisible ) {
        super(context);
        this.isDayVisible = isDayVisible;
        this.isMonthVisible = isMonthVisible;
        this.isYearVisible = isYearVisible;
        this.isClearVisible = isClearVisible;
        this.onDateSetListener = callBack;
        init();
    }

    private void init() {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.zl_date_picker_dialog, null);
        setView(view);
        mDatePicker = (DatePicker) view.findViewById(R.id.datePicker);
        LinearLayout buttonGroup = (LinearLayout) view.findViewById(R.id.buttonGroup);

        cancleButton = (Button) view.findViewById(R.id.cancelButton);
        clearButton = (Button) view.findViewById(R.id.clearButton);
        okButton = (Button) view.findViewById(R.id.okButton);
        clearLl = (LinearLayout) view.findViewById(R.id.clear_ll);

        Calendar mCalendar = Calendar.getInstance();
        mCalendar.setTime(new Date());

        cancleButton.setOnClickListener(this);
        clearButton.setOnClickListener(this);
        okButton.setOnClickListener(this);

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.YEAR, -3);
        mDatePicker.setMinDate(calendar.getTime().getTime());//最早3年前
        mDatePicker.setMaxDate(new Date().getTime());//最晚现在

        // 是否 显示 年
        if (!this.isYearVisible) {
            ((ViewGroup) ((ViewGroup) mDatePicker.getChildAt(0)).getChildAt(0)).getChildAt(0).setVisibility(View.GONE);
        }

        // 是否 显示 月
        if (!this.isMonthVisible) {
            ((ViewGroup) ((ViewGroup) mDatePicker.getChildAt(0)).getChildAt(0)).getChildAt(1).setVisibility(View.GONE);
        }

        // 是否 显示 日
        if (!this.isDayVisible) {
            ((ViewGroup) ((ViewGroup) mDatePicker.getChildAt(0)).getChildAt(0)).getChildAt(2).setVisibility(View.GONE);
        }

        // 是否 显示 清空按钮
        if (!this.isClearVisible) {
            clearLl.setVisibility(View.GONE);
        }


        // 设置 显示 宽高
        int width = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int height = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);

        buttonGroup.measure(width, height);
        mDatePicker.measure(width, height);
        if (buttonGroup.getMeasuredWidth() > mDatePicker.getMeasuredWidth()) {
            this.measureWidth = buttonGroup.getMeasuredWidth();
        } else {
            this.measureWidth = mDatePicker.getMeasuredWidth();
        }
//        Log.i("testss", this.measureWidth + "measuredWidth");
    }

    /***
     * 设置日期
     * @param date
     */
    public DateTimeDialog setDate(String date){
        Date time = CalendarUtil.toDate(date);
        mDatePicker.init(CalendarUtil.getYear(time), CalendarUtil.getMonth(time), CalendarUtil.getDay(time), null);
        return this;
    }

    public void hideOrShow() {
        if (this == null) {
            return;
        }
        if (!this.isShowing()) {
            this.show();
            //设置 显示 的 宽高
            WindowManager.LayoutParams attributes = this.getWindow().getAttributes();
            attributes.width = this.measureWidth + 100;
            this.getWindow().setAttributes(attributes);
        } else {
            this.dismiss();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.cancelButton:
                dismiss();
                break;
            case R.id.clearButton:
                onDateSetListener.onDateSet(null);
                dismiss();
                break;
            case R.id.okButton:
                onOkButtonClick();
                dismiss();
                break;
        }
    }

    /**
     * 确认 按钮 点击 事件
     */
    private void onOkButtonClick() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, mDatePicker.getYear());
        calendar.set(Calendar.MONTH, mDatePicker.getMonth());
        calendar.set(Calendar.DAY_OF_MONTH, mDatePicker.getDayOfMonth());
        calendar.getTime();
        if (onDateSetListener != null) {
            onDateSetListener.onDateSet(calendar.getTime());
        }
        }

    /**
     * 时间  改变 监听
     */
    public interface MyOnDateSetListener {
        void onDateSet(Date date);
    }
}
