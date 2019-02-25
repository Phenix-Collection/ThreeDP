package com.tdp.main.activity.video;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sdk.views.Menu.TopMenuView;
import com.sdk.views.imageview.RoundImageView;
import com.tdp.base.BaseActivity;
import com.tdp.main.R;
import com.tdp.main.activity.UserInfoActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CommentDetailActivity extends BaseActivity {
    @BindView(R.id.img_head)
    RoundImageView imgHead;
    @BindView(R.id.tv_realname)
    TextView tvRealname;
    @BindView(R.id.tv_time)
    TextView tvTime;
    @BindView(R.id.tv_like)
    TextView tvLike;
    @BindView(R.id.tv_comment)
    TextView tvComment;
    @BindView(R.id.id_content)
    LinearLayout idContent;
    @BindView(R.id.edt_comment)
    EditText edtComment;
    private ViewHolder viewHolder;
    private String TAG="CommentDetailActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment_detail);
        ButterKnife.bind(this);
        Log.e("ououou",TAG+"评论详细信息页面！");
        for (int i = 0; i < 3; i++)
            createUserView();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void createUserView() {
        View v = LayoutInflater.from(this).inflate(R.layout.item_activity_comment_detail, idContent, false);
        viewHolder = new ViewHolder(v);
        v.setTag(viewHolder);
        SpannableString spannableString = new SpannableString("清风明月：嗯，我觉得也是呢！");
        MyClickableSpan clickableSpan = new MyClickableSpan();
        ForegroundColorSpan colorSpan = new ForegroundColorSpan(Color.parseColor("#558ED5"));
        //从起始下标到终了下标，包括起始下标
        spannableString.setSpan(clickableSpan, 0, 4, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(colorSpan, 0, 4, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        viewHolder.tvCommentReply.setMovementMethod(LinkMovementMethod.getInstance());
        viewHolder.tvCommentReply.setHighlightColor(getResources().getColor(R.color.colorSkyBlue));
        viewHolder.tvCommentReply.setText(spannableString);
        idContent.addView(v);
    }

    class MyClickableSpan extends ClickableSpan {


        MyClickableSpan() {
        }

        @Override
        public void updateDrawState(TextPaint ds) {
            ds.setUnderlineText(false);
        }

        @Override
        public void onClick(View widget) {
            startActivity(new Intent(CommentDetailActivity.this, UserInfoActivity.class));
        }
    }


    class ViewHolder {
        @BindView(R.id.tv_comment_reply)
        TextView tvCommentReply;
        ViewHolder(View v) {
            ButterKnife.bind(this, v);
        }
    }

    @OnClick({TopMenuView.CLICK_LEFT, R.id.img_head, R.id.img_like, R.id.img_smile, R.id.img_img})
    public void onClick(View view) {
        switch (view.getId()) {
            case TopMenuView.CLICK_LEFT:
                finish();
                break;
            case R.id.img_head:
                startActivity(new Intent(this, UserInfoActivity.class));
                break;
            case R.id.img_like:
                break;
            case R.id.img_smile:
                break;
            case R.id.img_img:
                break;
        }
    }
}
