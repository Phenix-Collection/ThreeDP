package com.tdp.main.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import com.google.gson.Gson;
import com.sdk.api.WebUcenterApi;
import com.sdk.api.entity.LoginInfoEntity;
import com.sdk.api.entity.UserInfoEntity;
import com.sdk.core.CacheImageControl;
import com.sdk.core.Globals;
import com.sdk.db.CacheDataService;
import com.sdk.net.HttpRequest;
import com.sdk.net.listener.OnProgressListener;
import com.sdk.net.listener.OnResultListener;
import com.sdk.net.msg.WebMsg;
import com.sdk.utils.ImageUtils;
import com.sdk.utils.StringUtils;
import com.sdk.utils.imgeloader.ImageLoadActivity;
import com.sdk.views.Menu.TopMenuView;
import com.sdk.views.dialog.Loading;
import com.sdk.views.dialog.PopWindow;
import com.sdk.views.dialog.Toast;
import com.sdk.views.dialog.adapter.BottomMenuSpinerAdapter;
import com.sdk.views.imageview.RoundImageView;
import com.tdp.base.BaseActivity;
import com.tdp.main.R;
import com.tdp.main.activity.userinfo.ChooseCityActivity;
import com.tdp.main.activity.userinfo.EditBirthdayActivity;
import com.tdp.main.activity.userinfo.EditGenderActivity;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Cache;

import static com.sdk.core.Globals.BASE_API;

public class EditUserInfoActivity extends BaseActivity implements View.OnClickListener, TextWatcher {

//    @BindView(R.id.id_menu)
//    TopMenuView menu;
    @BindView(R.id.tv_save)
    TextView saveTv;
    @BindView(R.id.id_logo)
    RoundImageView headIv;
    @BindView(R.id.id_nickname)
    EditText nicknameEdt;
    @BindView(R.id.id_phone)
    EditText phoneEdt;
    @BindView(R.id.id_email)
    EditText emailEdt;
    @BindView(R.id.id_address)
    TextView addressTv;
    @BindView(R.id.id_sex)
    TextView sexTv;
    @BindView(R.id.id_birthday)
    TextView birthdayTv;

    UserInfoEntity userInfo = null;
    String headUrl, nickName, birthday, address, phoneNumber, email;
    int sex;

    private boolean hasChanage = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_information);
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        // data
        userInfo = CacheDataService.getLoginInfo().getUserInfo();
        headUrl = userInfo.getProfilePhoto();
        nickName = userInfo.getNickName();
        sex = userInfo.getSex();
        birthday = userInfo.getBirthday();
        phoneNumber = userInfo.getPhoneNumber();
        email = userInfo.getEmail();
        address = userInfo.getAddress();


//        ImageLoader.getInstance().displayImage(Globals.BASE_API + headUrl, headIv, ImageLoaderUtils.getHeaderOptions());
        CacheImageControl.getInstance().setImageView(headIv, Globals.BASE_API + headUrl, false);
        nicknameEdt.setText(nickName == null ? "" : nickName);
        sexTv.setText(sex == 2 ? "女" : "男");
        birthdayTv.setText(userInfo.getBirthday() == null ? "未设置生日" : userInfo.getBirthday());
        addressTv.setText(address == null ? "" : address);
        phoneEdt.setText(phoneNumber == null ? "" : phoneNumber);
        emailEdt.setText(email == null ? "" : email);

        Log.v("---", headUrl);
        // lisntener
        nicknameEdt.addTextChangedListener(this);
        phoneEdt.addTextChangedListener(this);
        emailEdt.addTextChangedListener(this);
    }

    @OnClick({R.id.id_back, R.id.tv_save, R.id.rl_set_head, R.id.id_address_rl, R.id.id_sex_rl, R.id.id_birthday_rl})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.id_back:
                onBack();
                break;
            case R.id.id_address_rl:
                Intent intent = new Intent(this, ChooseCityActivity.class);
                startActivityForResult(intent, 2);
                break;
            case R.id.id_sex_rl: // 选择性别
                intent = new Intent(this, EditGenderActivity.class);
                intent.putExtra("sex", userInfo.getSex());
                startActivityForResult(intent, 3);
                break;
            case R.id.id_birthday_rl:
                intent = new Intent(this, EditBirthdayActivity.class);
                String birthday = userInfo.getBirthday();
                if(birthday != null && birthday.length() > 3){
                    String[] temps = birthday.split("-");
                    intent.putExtra("year", Integer.parseInt(temps[0]));
                    intent.putExtra("month", Integer.parseInt(temps[1]));
                    intent.putExtra("day", Integer.parseInt(temps[2]));
                }
                startActivityForResult(intent, 4);
                break;
            case R.id.tv_save: // 保存
                if(!hasChanage) return;

                nickName = nicknameEdt.getText().toString().trim();
                phoneNumber = phoneEdt.getText().toString().trim();
                email = emailEdt.getText().toString().trim();
                if(email.length() > 0){
                    if(!StringUtils.checkEmail(email)){
                        Toast.show(this, "请输入正确的电子邮箱！", Toast.LENGTH_SHORT);
                        return;
                    }
                }
                if(phoneNumber.length() > 0){
                    if(!StringUtils.checkMobileNumber(phoneNumber)){
                        Toast.show(this, "请输入正确的手机号！", Toast.LENGTH_SHORT);
                        return;
                    }
                }

                if(nickName.length() == 0){
                    Toast.show(this, "昵称不能为空！", android.widget.Toast.LENGTH_LONG);
                    return;
                }

                Map<String, String> params = new HashMap<>();
                params.put("file","uploadFile");

                Loading.start(this,"正在保存用户信息，请稍等....", false);
                if(!headUrl.equals(userInfo.getProfilePhoto())){ // 头像变了，需要上传头像
                    final File file = new File(headUrl);

                    HttpRequest.instance().upload(BASE_API + "file/upload", new OnProgressListener() {
                        @Override
                        public void onProgress(long currentBytes, long contentLength) {

                        }

                        @Override
                        public void onFinished(WebMsg webMsg) {
                            file.delete();
                            Log.v("====", new Gson().toJson(webMsg));

                            if(webMsg.isSuccess()){ // 继续上传其他资料
                                headUrl = new Gson().fromJson(webMsg.getData(), String.class);
                                saveInfo();
                            } else {
                                Loading.stop();
                                webMsg.showMsg(EditUserInfoActivity.this);
                            }
                        }
                    },params, file);
                } else {
                    saveInfo();
                }

                break;
            case R.id.rl_set_head: // 弹出选择框选择拍照或者选择图片
                final PopWindow popWindow = new PopWindow(this);
                popWindow.addView(new BottomMenuSpinerAdapter().setOnclickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    popWindow.dismiss();
                    switch (v.getId()){
                        case R.id.id_cutphoto:
                            Intent intent = new Intent();
                            intent.setClass(EditUserInfoActivity.this, ImageLoadActivity.class);
                            intent.putExtra("choose_istakephoto", true); // 拍照模式
                            intent.putExtra("choose_iscut", true); // 需要裁剪
                            intent.putExtra("choose_isedit", true); // 设置编辑模式
                            intent.putExtra("aspectX", 1);
                            intent.putExtra("aspectY", 1);
                            intent.putExtra("outputX", 800);
                            intent.putExtra("outputY", 800);
                            EditUserInfoActivity.this.startActivityForResult(intent, 1);
                            break;
                        case R.id.id_file:
                            intent = new Intent();
                            intent.setClass(EditUserInfoActivity.this, ImageLoadActivity.class);
                            intent.putExtra("choose_iscut", true); // 需要裁剪
                            intent.putExtra("choose_isedit", true); // 设置编辑模式
                            intent.putExtra("aspectX", 1);
                            intent.putExtra("aspectY", 1);
                            intent.putExtra("outputX", 800);
                            intent.putExtra("outputY", 800);
                            EditUserInfoActivity.this.startActivityForResult(intent, 1);
                            break;
                    }
                    }
                }));
                popWindow.setAnimationStyle(R.style.popwin_anim_style);
                popWindow.showAtLocation(findViewById(R.id.rl_all), Gravity.BOTTOM, 0, 0);
                break;
        }
    }

    /***
     * 保存用户信息到服务器
     */
    private void saveInfo(){

        HttpRequest.instance().doPost(HttpRequest.create(WebUcenterApi.class).edit(nickName, headUrl, "", sex, birthday, address,
                "", "", email, phoneNumber, 0), new OnResultListener() {
            @Override
            public void onWebUiResult(WebMsg webMsg) {
                Loading.stop();
                if(webMsg.isSuccess()){
                    Toast.show(EditUserInfoActivity.this, "恭喜修改成功！", android.widget.Toast.LENGTH_LONG);
                    UserInfoEntity userinfo = new Gson().fromJson(webMsg.getData(), UserInfoEntity.class);
                    if(userinfo != null){
                        LoginInfoEntity data = CacheDataService.getLoginInfo();
                        data.setUserInfo(userinfo);
                        CacheDataService.saveLoginInfo(data);
                        finish();
                    } else {
                        Toast.show(EditUserInfoActivity.this, "修改失败，服务器忙！", android.widget.Toast.LENGTH_LONG);
                    }
                } else {
                    webMsg.showMsg(EditUserInfoActivity.this);
                }
            }
        });
    }

    //更新本地的信息库
    public void update() {
        LoginInfoEntity loginInfo = CacheDataService.getLoginInfo();
        loginInfo.getUserInfo().setSex(1);
        CacheDataService.saveLoginInfo(loginInfo);
        Toast.show(EditUserInfoActivity.this, "保存成功！", Toast.LENGTH_SHORT);
        setResult(2, new Intent(EditUserInfoActivity.this, MainActivity.class));
        finish();
    }


    String file_path;
    //拍照/选文件响应函数
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if(intent == null) return;
        if(requestCode == 1){ // 拍照结果集
            headUrl = intent.getStringExtra("result");
            headIv.setImageBitmap(ImageUtils.readBitMap(headUrl, true));
        } else if(requestCode == 2){
            address = intent.getStringExtra("result");
            addressTv.setText(address);
        } else if(requestCode == 3){
            sex = intent.getIntExtra("result", 1);
            sexTv.setText(sex == 2 ? "女" : "男");
        } else if(requestCode == 4){
            birthday = intent.getStringExtra("result");
            birthdayTv.setText(birthday);
        }

        hasChanage = true;
        saveTv.setTextColor(getResources().getColor(R.color.colorDeepBlue));
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        hasChanage = true;
        saveTv.setTextColor(getResources().getColor(R.color.colorDeepBlue));
    }
}
