package com.hmspush.agent.push;

import android.os.Handler;
import android.os.Looper;

import com.hmspush.agent.HMSAgent;
import com.hmspush.agent.common.ApiClientMgr;
import com.hmspush.agent.common.BaseApiAgent;
import com.hmspush.agent.common.CallbackCodeRunnable;
import com.hmspush.agent.common.HMSAgentLog;
import com.hmspush.agent.common.StrUtils;
import com.hmspush.agent.common.ThreadUtil;
import com.hmspush.agent.push.handler.EnableReceiveNormalMsgHandler;
import com.huawei.hms.api.HuaweiApiClient;
import com.huawei.hms.support.api.push.HuaweiPush;

/**
 * 打开透传消息开关的接口。
 */
public class EnableReceiveNormalMsgApi extends BaseApiAgent {

    /**
     * 是否打开开关
     */
    boolean enable;

    /**
     * 调用接口回调
     */
    private EnableReceiveNormalMsgHandler handler;

    /**
     * HuaweiApiClient 连接结果回调
     *
     * @param rst    结果码
     * @param client HuaweiApiClient 实例
     */
    @Override
    public void onConnect(final int rst, final HuaweiApiClient client) {
        //需要在子线程中执行开关的操作
        ThreadUtil.INST.excute(new Runnable() {
            @Override
            public void run() {
                if (client == null || !ApiClientMgr.INST.isConnect(client)) {
                    HMSAgentLog.e("client not connted");
                    onEnableReceiveNormalMsgResult(rst);
                } else {
                    // 开启/关闭透传消息
                    HuaweiPush.HuaweiPushApi.enableReceiveNormalMsg(client, enable);
                    onEnableReceiveNormalMsgResult(HMSAgent.AgentResultCode.HMSAGENT_SUCCESS);
                }
            }
        });
    }

    void onEnableReceiveNormalMsgResult(int rstCode) {
        HMSAgentLog.i("enableReceiveNormalMsg:callback=" + StrUtils.objDesc(handler) +" retCode=" + rstCode);
        if (handler != null) {
            new Handler(Looper.getMainLooper()).post(new CallbackCodeRunnable(handler, rstCode));
            handler = null;
        }
    }

    /**
     * 打开/关闭透传消息
     * @param enable 打开/关闭
     */
    public void enableReceiveNormalMsg(boolean enable, EnableReceiveNormalMsgHandler handler) {
        HMSAgentLog.i("enableReceiveNormalMsg:enable=" + enable + "  handler=" + StrUtils.objDesc(handler));
        this.enable = enable;
        this.handler = handler;
        connect();
    }
}
