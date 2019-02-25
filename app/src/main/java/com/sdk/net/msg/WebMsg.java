package com.sdk.net.msg;

import android.content.Context;
import android.net.ParseException;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.sdk.views.dialog.Loading;
import com.sdk.views.dialog.Toast;
import org.json.JSONException;
import retrofit2.adapter.rxjava.HttpException;


/**
 * @author:zlcai
 * @createrDate:2017/7/27 14:57
 * @lastTime:2017/7/27 14:57
 * @detail:
 **/

public class WebMsg {

	// 一些网络状态常量
	public final static int STATE_OK = 200; // 正常
	private static final int STATE_UNAUTHORIZED = 401;
	private static final int STATE_FORBIDDEN = 403;
	private static final int STATE_NOT_FOUND = 404;
	private static final int STATE_REQUEST_TIMEOUT = 408;
	private static final int STATE_INTERNAL_SERVER_ERROR = 500; // 服务器错误
	private static final int STATE_BAD_GATEWAY = 502;
	private static final int STATE_SERVICE_UNAVAILABLE = 503;
	public final static int STATE_IO_ERROR = -1; // 读取服务器数据出错
	public final static int STATE_GATEWAY_TIMEOUT = 504; // 网络超时
	public final static int STATE_NO_CONTENT = 204; // 无网络连接
	public final static int STATE_ENCODING_UNSUPPORT = 998;
	public final static int STATE_ERROR = 999; // 未知错误

	// 关于APP API状态码常量
	public final static int STATUS_USER_VALIDATE = 10001; // 用户名或密码错误
	public final static int STATUS_USER_SMSCODE_LIMIT = 10002; // 短信验证码次数超出
	public final static int STATUS_USER_SMSCODE_ERROR = 10003; // 验证码无效
	public final static int STATUS_USER_NOTEXIST = 10004; // 用户不存在
	public final static int STATUS_USER_NOTLOGIN = 10005; // 用户未登录
	public final static int STATUS_USER_OUTMODED = 10006; // 登录已过时
	public final static int STATUS_USER_EXIST = 10007; // 用户已存在
	public final static int STATUS_JOB_NODATA = 99995; // 数据不存在
	public final static int STATUS_ERROR_WRONGFUL = 99996; // 访问不合法
	public final static int STATUS_ERROR_PARAMS = 99997; //  参数有误
	public final static int STATUS_ERROR = 99998; //  操作失败
	public final static int STATUS_OK = 99999; // 成功

    // 普通变量
    private String errorMsg = "";
    private Object data;
    private boolean success; // 是否成功
    private int errorCode = -1; // 内容错误代码
	private int webCode = 200; // 网络错误代码

	public String getErrorMsg() {
		return errorMsg;
	}

	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}

	public String getData() {
		return data != null ? new Gson().toJson(data) : null;
	}

	public void setData(Object data) {
		this.data = data;
	}

	public boolean isSuccess() {
		return webCode == STATE_OK && errorCode == STATUS_OK;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public int getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}

	public int getWebCode() {
		return webCode;
	}

	public void setWebCode(int webCode) {
		this.webCode = webCode;
	}

	public static WebMsg getSuccessed(){
		WebMsg webMsg = new WebMsg();
		webMsg.setWebCode(STATE_OK);
		webMsg.setData("");
		return webMsg;
	}

	public static WebMsg getFailed(Throwable e){

		WebMsg webMsg = new WebMsg();
		webMsg.setData("");
		if(e != null){
			if (e instanceof HttpException){             //HTTP错误
				HttpException httpException = (HttpException) e;
				webMsg.setWebCode(httpException.code());
			}
		} else {
			webMsg.setErrorCode(-1);
		}

		return webMsg;
	}


	public void showMsg(Context context){
		Loading.stop();
		switch (errorCode){
			case STATUS_OK:
				//Toast.makeText(context, "恭喜，操作成功！", Toast.LENGTH_LONG).show();
				break;
			case STATE_IO_ERROR:
				Toast.show(context, "服务器连接出错！", Toast.LENGTH_SHORT);
				break;
			case STATUS_USER_VALIDATE:// = 10001; // 用户名或密码错误
				Toast.show(context, "用户名或密码错误！", Toast.LENGTH_SHORT);
				break;
			case STATUS_USER_SMSCODE_LIMIT: // 10002
				Toast.show(context, "短信验证码发送过于频繁，请一小时后再试！", Toast.LENGTH_SHORT);
				break;
			case STATUS_USER_SMSCODE_ERROR:
				Toast.show(context, "验证码无效！", Toast.LENGTH_SHORT);
				break;
			case STATUS_USER_NOTEXIST:// = 10004; // 用户不存在
				Toast.show(context, "用户不存在！", Toast.LENGTH_SHORT);
				break;
			case STATUS_USER_NOTLOGIN:// = 10005; // 用户未登录
				Toast.show(context, "用户未登录！", Toast.LENGTH_SHORT);
				break;
			case STATUS_USER_OUTMODED:// = 10006; // 登录已过时
				Toast.show(context, "登录已过时！", Toast.LENGTH_SHORT);
				break;
			case STATUS_USER_EXIST: // 用户已存在
				Toast.show(context, "用户已存在！", Toast.LENGTH_SHORT);
				break;
			case STATUS_JOB_NODATA:// = 99995; // 数据不存在
				Toast.show(context, "数据不存在！", Toast.LENGTH_SHORT);
				break;
			case STATUS_ERROR_WRONGFUL:// = 99996; // 访问不合法
				Toast.show(context, "访问不合法！", Toast.LENGTH_SHORT);
				break;
			case STATUS_ERROR_PARAMS:// = 99997; //  参数有误
				Toast.show(context, "参数有误！", Toast.LENGTH_SHORT);
				break;
			default:
				if(errorMsg != null && errorMsg.length() > 3){
					Toast.show(context, errorMsg, Toast.LENGTH_SHORT);
				} else {
					Toast.show(context, "未知错误！", Toast.LENGTH_SHORT);
				}
				break;
		}
	}
}
