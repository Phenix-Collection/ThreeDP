package com.tdp.main.activity;

import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;
import com.google.gson.Gson;
import com.sdk.api.WebUcenterApi;
import com.sdk.api.entity.LoginInfoEntity;
import com.sdk.api.entity.MirrorEntity;
import com.sdk.db.CacheDataService;
import com.sdk.net.HttpRequest;
import com.sdk.net.listener.OnResultListener;
import com.sdk.net.msg.WebMsg;
import com.sdk.views.dialog.Loading;
import com.sdk.views.dialog.Toast;
import com.sdk.views.listview.HorizontalListView;
import com.tdp.base.BaseActivity;
import com.tdp.main.R;
import com.tdp.main.adapter.FigureAdapter;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/***
 * @remark  形象
 * @author super_link
 *
 */
public class FigureActivity extends BaseActivity{

//    @BindView(R.id.gl_show)
//    GLSurfaceView showGl;
//    @BindView(R.id.tv_loading)
    TextView loadingTv;
    @BindView(R.id.hsv_prop)
    HorizontalListView propHsv;
    @BindView(R.id.v_prop_solid)
    View propSolidV;

    // 配件菜单ID
    //int[] propMenuIds = {R.id.tv_cloth, R.id.tv_glass, R.id.tv_hair, R.id.tv_suit};

    // 道具适配器
    FigureAdapter propAdapter;
    // 判断是否可以返回（因为界面渲染时会占用部分资源，直接返回会导致释放不完全导致无法再次渲染）
    private boolean canBack;

    // 用户装扮配置
    private MirrorEntity mirror;
    // 衣服选中下标
    private int clothesPosition;
    // 眼镜选中下标
    private int glassesPosition;
    // 服装选中下标
    private int decorationPosition;
    // 发型选中下标
    private int hairPosition;
    // cosplay选中下标
    private int cosplayPosition;
    // 当前操作类型
//    private int currentIndex = R.id.tv_cloth;
    @BindView(R.id.tv_cloth)
    public TextView currentIndex;

//    AvatarService avatarService;
    public String[] MEI_BODY_BUNDLE_ARRAY = {"none", "b_01.bundle", "b_02.bundle", "b_03.bundle", "b_04.bundle"};
    public int[] MEI_BODY_RES_ARRAY = {R.mipmap.ic_delete_all, R.mipmap.b_01, R.mipmap.b_02, R.mipmap.b_03, R.mipmap.b_04};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_editface_figure);
        ButterKnife.bind(this);
        init();
    }

    private void init(){

        // 获取用户装扮数据
        mirror = CacheDataService.getLoginInfo().getUserInfo().getMirror();

        try{
            clothesPosition = Integer.parseInt(mirror.getCloth());
        }catch(NumberFormatException e){
            clothesPosition = 0;
        }


//        hairPosition = Integer.parseInt(mirror.getHats());

        // 初始化道具适配器
//        propAdapter = new FigureAdapter(this);
//        propHsv.setAdapter(propAdapter);
//
//        if(mirror.getSex() == 1){
//            propAdapter.setDatas(AvatarConstant.CLOTHES_BOY_BUNDLE, AvatarConstant.CLOTHES_BOY_RES);
//        } else {
//            propAdapter.setDatas(AvatarConstant.CLOTHES_GIRL_BUNDLE, AvatarConstant.CLOTHES_GIRL_RES);
//        }
//
//        avatarService= new AvatarService(showGl, this, new FURenderer.OnLoadBodyListener() {
//            @Override
//            public void onLoadBodyCompleteListener() {
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        loadingTv.setVisibility(View.GONE);
//                        avatarService.setHadLoad(true);
//                        canBack=true;
//                    }
//                });
//            }
//        });
//
//        // listener
//        propHsv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                String bundleName = (String) propAdapter.getItem(position);
//
//
//
//                switch (currentIndex.getId()){
//                    case R.id.tv_cloth: // 衣服
//                        Log.v("FigureActivity" , "选择了衣服：" + bundleName);
//
//                        clothesPosition = position;
//                        avatarService.getNowAvatarP2A().setClothesIndex(position);
//                        avatarService.reLoadAvatar();
//                        //propAdapter.notifyDataSetChanged();
//                        break;
//                    case R.id.tv_glass: // 眼镜（暂无）
//                        Log.v("FigureActivity" , "选择了眼镜：" + bundleName);
//
//                        glassesPosition = position;
//                        break;
//                    case R.id.tv_hair: // 发型
//                        Log.v("FigureActivity" , "选择了发型：" + bundleName);
//
//                        hairPosition = position;
//                        avatarService.getNowAvatarP2A().setHairIndex(position);
//                        avatarService.reLoadAvatar();
//                        //propAdapter.notifyDataSetChanged();
//                        break;
//                    case R.id.tv_suit: // 套装（暂无）
//                        Log.v("FigureActivity" , "选择了套装：" + bundleName);
//
//                        decorationPosition = position;
//                        break;
//                }
//
//            }
//        });
    }

    @Override
    public void toImmersion() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    @Override
    public void onBackPressed() {
        onClick(findViewById(R.id.id_back));
    }

    @OnClick({R.id.tv_cloth, R.id.tv_glass, R.id.tv_hair, R.id.tv_suit, R.id.id_back, R.id.tv_save})
    public void onClick(View view){

//        if(currentIndex != null){
//            currentIndex.setTextColor(this.getResources().getColor(R.color.voip_interface_text_color));
//        }
//
//        switch (view.getId()){
//            case R.id.tv_cloth: // 衣服
//                if(currentIndex == null || currentIndex.getId() != R.id.tv_cloth){
//                    if(mirror.getSex() == 1){ // 男装衣服
//                        propAdapter.setDatas(AvatarConstant.CLOTHES_BOY_BUNDLE, AvatarConstant.CLOTHES_BOY_RES);
//                    } else { // 女装衣服
//                        propAdapter.setDatas(AvatarConstant.CLOTHES_GIRL_BUNDLE, AvatarConstant.CLOTHES_GIRL_RES);
//                    }
//                }
//                currentIndex = (TextView) view;
//                setMargins(propSolidV, this.getResources().getDimensionPixelSize(R.dimen.x4), 0, 0, 0);
//                break;
//            case R.id.tv_glass: // 眼镜（暂无）
//                if(currentIndex == null || currentIndex.getId() != R.id.tv_glass){
////                    propAdapter.setDatas(AvatarConstant.);
//                    propAdapter.setDatas(null, null);
//                }
//                currentIndex = (TextView) view;
//                setMargins(propSolidV, this.getResources().getDimensionPixelOffset(R.dimen.x65), 0, 0, 0);
//                break;
//            case R.id.tv_hair: // 发型
//                if(currentIndex == null || currentIndex.getId() != R.id.tv_hair){
//                    if(mirror.getSex() == 1) { // 男生发型
//                        propAdapter.setDatas(AvatarConstant.HAIR_BOY_BUNDLE, AvatarConstant.HAIR_BOY_RES);
//                    } else { // 女生发型
//                        propAdapter.setDatas(AvatarConstant.HAIR_GIRL_BUNDLE, AvatarConstant.HAIR_GIRL_RES);
//                    }
//                }
//                currentIndex = (TextView) view;
//                setMargins(propSolidV, this.getResources().getDimensionPixelSize(R.dimen.x126), 0, 0, 0);
//                break;
//            case R.id.tv_suit: // 套装（暂无）
//                if(currentIndex == null || currentIndex.getId() != R.id.tv_suit){
////                    propAdapter.setDatas(AvatarConstant.);
//                    propAdapter.setDatas(null, null);
//                }
//                currentIndex = (TextView) view;
//                setMargins(propSolidV, this.getResources().getDimensionPixelSize(R.dimen.x186), 0, 0, 0);
//                break;
//            case R.id.id_back:
//                if(canBack){
//                    avatarService.getmCameraRenderer().onDestroy();
//                    Intent intent = new Intent(this, MainActivity.class);
//                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
//                    intent.putExtra(MainActivity.TAG, MainActivity.FROM_FIGURE);
//                    startActivity(intent);
//                    finish();
//                }
//                break;
//            case R.id.tv_save:
//                save();
//                break;
//        }
//
//        if(currentIndex != null){
//            currentIndex.setTextColor(this.getResources().getColor(R.color.colorBlack));
//        }
    }

    public static void setMargins(View v, int l, int t, int r, int b) {
        if (v.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            p.setMargins(l, t, r, b);
            v.requestLayout();
        }
    }

    /***
     * 保存用户信息
     */
    private void save(){
        Loading.start(this);
        MirrorEntity mirror = CacheDataService.getLoginInfo().getUserInfo().getMirror();
        HttpRequest.instance().doPost(HttpRequest.create(WebUcenterApi.class).editMirror(
                mirror.getUrl(),
                mirror.getName(),
                String.valueOf(mirror.getSex()),
                String.valueOf(mirror.getSkinColor()),
                String.valueOf(clothesPosition),
                mirror.getGlass(),
                mirror.getHats(),
                mirror.getCosplay()
        ), new OnResultListener() {
            @Override
            public void onWebUiResult(WebMsg webMsg) {
                Log.e("ououou", new Gson().toJson(webMsg));
                if (webMsg.isSuccess()) {
                    Log.e("ououou", "更新成功保存到本地！"+clothesPosition);
                    LoginInfoEntity loginInfoEntity1 = CacheDataService.getLoginInfo();
                    MirrorEntity mirror = loginInfoEntity1.getUserInfo().getMirror();
                    mirror.setCloth(String.valueOf(clothesPosition));
                    loginInfoEntity1.getUserInfo().setMirror(mirror);
                    CacheDataService.saveLoginInfo(loginInfoEntity1);
                    Loading.stop();
                    Toast.makeText(FigureActivity.this, "保存成功！", Toast.LENGTH_SHORT).show();
                } else {
                    webMsg.showMsg(FigureActivity.this);
                }
            }
        });
    }

}
