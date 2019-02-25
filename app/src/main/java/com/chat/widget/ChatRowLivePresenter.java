package com.chat.widget;

import android.content.Context;
import android.widget.BaseAdapter;

import com.chat.Constant;
import com.chat.ChatHelper;
import com.chat.easeui.widget.chatrow.EaseChatRow;
import com.chat.easeui.widget.presenter.EaseChatRowPresenter;
import com.hyphenate.chat.EMMessage;

/**
 * Created by zhangsong on 17-10-12.
 */

public class ChatRowLivePresenter extends EaseChatRowPresenter {
    @Override
    protected EaseChatRow onCreateChatRow(Context cxt, EMMessage message, int position, BaseAdapter adapter) {
        return new ChatRowConferenceInvite(cxt, message, position, adapter);
    }

    @Override
    public void onBubbleClick(EMMessage message) {
        super.onBubbleClick(message);

        String confId = message.getStringAttribute(Constant.EM_CONFERENCE_ID, "");
        String confPassword = message.getStringAttribute(Constant.EM_CONFERENCE_PASSWORD,"");
        int type = message.getIntAttribute(Constant.EM_CONFERENCE_TYPE, 0);
        ChatHelper.getInstance().goLive(confId, confPassword, message.getFrom());
    }

}
