/**
 * Copyright (C) 2016 Hyphenate Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.chat.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

import com.sdk.db.BaseDataService;

public class PreferenceManager {
	/**
	 * name of preference
	 */
	public static final String PREFERENCE_NAME = "saveInfo";
//	private static SharedPreferences mSharedPreferences;
	private static PreferenceManager mPreferencemManager;
//	private static SharedPreferences.Editor editor;

	private String SHARED_KEY_SETTING_NOTIFICATION = "shared_key_setting_notification";
	private String SHARED_KEY_SETTING_SOUND = "shared_key_setting_sound";
	private String SHARED_KEY_SETTING_VIBRATE = "shared_key_setting_vibrate";
	private String SHARED_KEY_SETTING_SPEAKER = "shared_key_setting_speaker";

	private static String SHARED_KEY_SETTING_CHATROOM_OWNER_LEAVE = "shared_key_setting_chatroom_owner_leave";
    private static String SHARED_KEY_SETTING_DELETE_MESSAGES_WHEN_EXIT_GROUP = "shared_key_setting_delete_messages_when_exit_group";
	private static String SHARED_KEY_SETTING_TRANSFER_FILE_BY_USER = "shared_key_setting_transfer_file_by_user";
	private static String SHARED_KEY_SETTING_AUTODOWNLOAD_THUMBNAIL = "shared_key_setting_autodownload_thumbnail";
	private static String SHARED_KEY_SETTING_AUTO_ACCEPT_GROUP_INVITATION = "shared_key_setting_auto_accept_group_invitation";
    private static String SHARED_KEY_SETTING_ADAPTIVE_VIDEO_ENCODE = "shared_key_setting_adaptive_video_encode";
	private static String SHARED_KEY_SETTING_OFFLINE_PUSH_CALL = "shared_key_setting_offline_push_call";
	private static String SHARED_KEY_SETTING_OFFLINE_LARGE_CONFERENCE_MODE = "shared_key_setting_offline_large_conference_mode";

	private static String SHARED_KEY_SETTING_GROUPS_SYNCED = "SHARED_KEY_SETTING_GROUPS_SYNCED";
	private static String SHARED_KEY_SETTING_CONTACT_SYNCED = "SHARED_KEY_SETTING_CONTACT_SYNCED";
	private static String SHARED_KEY_SETTING_BALCKLIST_SYNCED = "SHARED_KEY_SETTING_BALCKLIST_SYNCED";

	private static String SHARED_KEY_CURRENTUSER_USERNAME = "SHARED_KEY_CURRENTUSER_USERNAME";
	private static String SHARED_KEY_CURRENTUSER_NICK = "SHARED_KEY_CURRENTUSER_NICK";
	private static String SHARED_KEY_CURRENTUSER_AVATAR = "SHARED_KEY_CURRENTUSER_AVATAR";

	private static String SHARED_KEY_REST_SERVER = "SHARED_KEY_REST_SERVER";
	private static String SHARED_KEY_IM_SERVER = "SHARED_KEY_IM_SERVER";
	private static String SHARED_KEY_ENABLE_CUSTOM_SERVER = "SHARED_KEY_ENABLE_CUSTOM_SERVER";
	private static String SHARED_KEY_ENABLE_CUSTOM_APPKEY = "SHARED_KEY_ENABLE_CUSTOM_APPKEY";
	private static String SHARED_KEY_CUSTOM_APPKEY = "SHARED_KEY_CUSTOM_APPKEY";
	private static String SHARED_KEY_MSG_ROAMING = "SHARED_KEY_MSG_ROAMING";
	private static String SHARED_KEY_SHOW_MSG_TYPING = "SHARED_KEY_SHOW_MSG_TYPING";

	private static String SHARED_KEY_CALL_MIN_VIDEO_KBPS = "SHARED_KEY_CALL_MIN_VIDEO_KBPS";
	private static String SHARED_KEY_CALL_MAX_VIDEO_KBPS = "SHARED_KEY_CALL_Max_VIDEO_KBPS";
	private static String SHARED_KEY_CALL_MAX_FRAME_RATE = "SHARED_KEY_CALL_MAX_FRAME_RATE";
	private static String SHARED_KEY_CALL_AUDIO_SAMPLE_RATE = "SHARED_KEY_CALL_AUDIO_SAMPLE_RATE";
	private static String SHARED_KEY_CALL_BACK_CAMERA_RESOLUTION = "SHARED_KEY_CALL_BACK_CAMERA_RESOLUTION";
	private static String SHARED_KEY_CALL_FRONT_CAMERA_RESOLUTION = "SHARED_KEY_FRONT_CAMERA_RESOLUTIOIN";
	private static String SHARED_KEY_CALL_FIX_SAMPLE_RATE = "SHARED_KEY_CALL_FIX_SAMPLE_RATE";

	private static String SHARED_KEY_PUSH_USE_FCM = "shared_key_push_use_fcm";

//	@SuppressLint("CommitPrefEdits")
//	private PreferenceManager(Context cxt) {
//		mSharedPreferences = cxt.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
//		editor = mSharedPreferences.edit();
//	}
//
	public static void init(){
	    if(mPreferencemManager == null){
	        mPreferencemManager = new PreferenceManager();
	    }
	}

	/**
	 * get instance of PreferenceManager
	 *
	 * @param
	 * @return
	 */
	public synchronized static PreferenceManager getInstance() {
		if (mPreferencemManager == null) {
			mPreferencemManager = new PreferenceManager();
//			throw new RuntimeException("please init first!");
		}

		return mPreferencemManager;
	}

	public void setSettingMsgNotification(boolean paramBoolean) {
		 BaseDataService.saveValueToSharePerference(SHARED_KEY_SETTING_NOTIFICATION, paramBoolean);
		BaseDataService.saveValueToSharePerference(SHARED_KEY_SETTING_NOTIFICATION, paramBoolean);
	}

	public boolean getSettingMsgNotification() {
		return BaseDataService.getValueByBoolean(SHARED_KEY_SETTING_NOTIFICATION, true);
	}

	public void setSettingMsgSound(boolean paramBoolean) {
		BaseDataService.saveValueToSharePerference(SHARED_KEY_SETTING_SOUND, paramBoolean);
//		editor.apply();
	}

	public boolean getSettingMsgSound() {

		return BaseDataService.getValueByBoolean(SHARED_KEY_SETTING_SOUND, true);
	}

	public void setSettingMsgVibrate(boolean paramBoolean) {
		BaseDataService.saveValueToSharePerference(SHARED_KEY_SETTING_VIBRATE, paramBoolean);
//		editor.apply();
	}

	public boolean getSettingMsgVibrate() {
		return BaseDataService.getValueByBoolean(SHARED_KEY_SETTING_VIBRATE, true);
	}

	public void setSettingMsgSpeaker(boolean paramBoolean) {
		BaseDataService.saveValueToSharePerference(SHARED_KEY_SETTING_SPEAKER, paramBoolean);
//		editor.apply();
	}

	public boolean getSettingMsgSpeaker() {
		return BaseDataService.getValueByBoolean(SHARED_KEY_SETTING_SPEAKER, true);
	}

	public void setSettingAllowChatroomOwnerLeave(boolean value) {
		BaseDataService.saveValueToSharePerference(SHARED_KEY_SETTING_CHATROOM_OWNER_LEAVE, value);
    }

	public boolean getSettingAllowChatroomOwnerLeave() {
        return BaseDataService.getValueByBoolean(SHARED_KEY_SETTING_CHATROOM_OWNER_LEAVE, true);
    }

    public void setDeleteMessagesAsExitGroup(boolean value){
		BaseDataService.saveValueToSharePerference(SHARED_KEY_SETTING_DELETE_MESSAGES_WHEN_EXIT_GROUP, value);
    }

    public boolean isDeleteMessagesAsExitGroup() {
        return BaseDataService.getValueByBoolean(SHARED_KEY_SETTING_DELETE_MESSAGES_WHEN_EXIT_GROUP, true);
    }

	public void setTransferFileByUser(boolean value) {
		BaseDataService.saveValueToSharePerference(SHARED_KEY_SETTING_TRANSFER_FILE_BY_USER, value);
	}

	public boolean isSetTransferFileByUser() {
		return BaseDataService.getValueByBoolean(SHARED_KEY_SETTING_TRANSFER_FILE_BY_USER, true);
	}
	public void setAudodownloadThumbnail(boolean autodownload) {
		BaseDataService.saveValueToSharePerference(SHARED_KEY_SETTING_AUTODOWNLOAD_THUMBNAIL, autodownload);
	}

	public boolean isSetAutodownloadThumbnail() {
		return BaseDataService.getValueByBoolean(SHARED_KEY_SETTING_AUTODOWNLOAD_THUMBNAIL, true);
	}

	public void setAutoAcceptGroupInvitation(boolean value) {
		BaseDataService.saveValueToSharePerference(SHARED_KEY_SETTING_AUTO_ACCEPT_GROUP_INVITATION, value);
    }

    public boolean isAutoAcceptGroupInvitation() {
        return BaseDataService.getValueByBoolean(SHARED_KEY_SETTING_AUTO_ACCEPT_GROUP_INVITATION, true);
    }

    public void setAdaptiveVideoEncode(boolean value) {
		BaseDataService.saveValueToSharePerference(SHARED_KEY_SETTING_ADAPTIVE_VIDEO_ENCODE, value);
    }

    public boolean isAdaptiveVideoEncode() {
        return BaseDataService.getValueByBoolean(SHARED_KEY_SETTING_ADAPTIVE_VIDEO_ENCODE, false);
    }

	public void setPushCall(boolean value) {
		 BaseDataService.saveValueToSharePerference(SHARED_KEY_SETTING_OFFLINE_PUSH_CALL, value);
	}

	public boolean isPushCall() {
		return BaseDataService.getValueByBoolean(SHARED_KEY_SETTING_OFFLINE_PUSH_CALL, false);
	}

	public void setGroupsSynced(boolean synced){
	     BaseDataService.saveValueToSharePerference(SHARED_KEY_SETTING_GROUPS_SYNCED, synced);
	}

	public boolean isGroupsSynced(){
	    return BaseDataService.getValueByBoolean(SHARED_KEY_SETTING_GROUPS_SYNCED, false);
	}

	public void setContactSynced(boolean synced){
         BaseDataService.saveValueToSharePerference(SHARED_KEY_SETTING_CONTACT_SYNCED, synced);
    }

    public boolean isContactSynced(){
        return BaseDataService.getValueByBoolean(SHARED_KEY_SETTING_CONTACT_SYNCED, false);
    }

    public void setBlacklistSynced(boolean synced){
         BaseDataService.saveValueToSharePerference(SHARED_KEY_SETTING_BALCKLIST_SYNCED, synced);
         
    }

    public boolean isBacklistSynced(){
        return BaseDataService.getValueByBoolean(SHARED_KEY_SETTING_BALCKLIST_SYNCED, false);
    }

	public void setCurrentUserNick(String nick) {
		BaseDataService.saveValueToSharePerference(SHARED_KEY_CURRENTUSER_NICK, nick);
	}

	public void setCurrentUserAvatar(String avatar) {
		BaseDataService.saveValueToSharePerference(SHARED_KEY_CURRENTUSER_AVATAR, avatar);
	}

	public String getCurrentUserNick() {
		return BaseDataService.getValueByString(SHARED_KEY_CURRENTUSER_NICK, null);
	}

	public String getCurrentUserAvatar() {
		return BaseDataService.getValueByString(SHARED_KEY_CURRENTUSER_AVATAR, null);
	}

	public void setCurrentUserName(String username){
		BaseDataService.saveValueToSharePerference(SHARED_KEY_CURRENTUSER_USERNAME, username);
	}

	public String getCurrentUsername(){
		return BaseDataService.getValueByString(SHARED_KEY_CURRENTUSER_USERNAME, null);
	}

	public void setRestServer(String restServer){
		BaseDataService.saveValueToSharePerference(SHARED_KEY_REST_SERVER, restServer);
	}

	public String getRestServer(){
		return BaseDataService.getValueByString(SHARED_KEY_REST_SERVER, null);
	}

	public void setIMServer(String imServer){
		BaseDataService.saveValueToSharePerference(SHARED_KEY_IM_SERVER, imServer);
	}

	public String getIMServer(){
		return BaseDataService.getValueByString(SHARED_KEY_IM_SERVER, null);
	}

	public void enableCustomServer(boolean enable){
		 BaseDataService.saveValueToSharePerference(SHARED_KEY_ENABLE_CUSTOM_SERVER, enable);
	}

	public boolean isCustomServerEnable(){
		return BaseDataService.getValueByBoolean(SHARED_KEY_ENABLE_CUSTOM_SERVER, false);
	}

	public void enableCustomAppkey(boolean enable) {
		 BaseDataService.saveValueToSharePerference(SHARED_KEY_ENABLE_CUSTOM_APPKEY, enable);
	}

	public boolean isCustomAppkeyEnabled() {
		return BaseDataService.getValueByBoolean(SHARED_KEY_ENABLE_CUSTOM_APPKEY, false);
	}

	public String getCustomAppkey() {
		return BaseDataService.getValueByString(SHARED_KEY_CUSTOM_APPKEY, "");
	}

	public void setCustomAppkey(String appkey) {
		BaseDataService.saveValueToSharePerference(SHARED_KEY_CUSTOM_APPKEY, appkey);
	}

	public void removeCurrentUserInfo() {
		BaseDataService.remove(SHARED_KEY_CURRENTUSER_NICK);
		BaseDataService.remove(SHARED_KEY_CURRENTUSER_AVATAR);
	}

	public boolean isMsgRoaming() {
		return BaseDataService.getValueByBoolean(SHARED_KEY_MSG_ROAMING, false);
	}

	public void setMsgRoaming(boolean isRoaming) {
		 BaseDataService.saveValueToSharePerference(SHARED_KEY_MSG_ROAMING, isRoaming);
	}

	public boolean isShowMsgTyping() {
		return BaseDataService.getValueByBoolean(SHARED_KEY_SHOW_MSG_TYPING, false);
	}

	public void showMsgTyping(boolean show) {
		 BaseDataService.saveValueToSharePerference(SHARED_KEY_SHOW_MSG_TYPING, show);
	}

	/**
	 * ----------------------------------------- Call Option -----------------------------------------
	 */

	/**
	 * Min Video kbps
	 * if no value was set, return -1
	 * @return
	 */
	public int getCallMinVideoKbps() {
		return BaseDataService.getValueByInt(SHARED_KEY_CALL_MIN_VIDEO_KBPS, -1);
	}

	public void setCallMinVideoKbps(int minBitRate) {
		BaseDataService.saveValueToSharePerference(SHARED_KEY_CALL_MIN_VIDEO_KBPS, minBitRate);
	}

	/**
	 * Max Video kbps
	 * if no value was set, return -1
	 * @return
	 */
	public int getCallMaxVideoKbps() {
		return BaseDataService.getValueByInt(SHARED_KEY_CALL_MAX_VIDEO_KBPS, -1);
	}

	public void setCallMaxVideoKbps(int maxBitRate) {
		BaseDataService.saveValueToSharePerference(SHARED_KEY_CALL_MAX_VIDEO_KBPS, maxBitRate);
	}

	/**
	 * Max frame rate
	 * if no value was set, return -1
	 * @return
	 */
	public int getCallMaxFrameRate() {
		return BaseDataService.getValueByInt(SHARED_KEY_CALL_MAX_FRAME_RATE, -1);
	}

	public void setCallMaxFrameRate(int maxFrameRate) {
		BaseDataService.saveValueToSharePerference(SHARED_KEY_CALL_MAX_FRAME_RATE, maxFrameRate);
	}

	/**
	 * audio sample rate
	 * if no value was set, return -1
	 * @return
	 */
	public int getCallAudioSampleRate() {
		return BaseDataService.getValueByInt(SHARED_KEY_CALL_AUDIO_SAMPLE_RATE, -1);
	}

	public void setCallAudioSampleRate(int audioSampleRate) {
		BaseDataService.saveValueToSharePerference(SHARED_KEY_CALL_AUDIO_SAMPLE_RATE, audioSampleRate);
	}

	/**
	 * back camera resolution
	 * format: 320x240
	 * if no value was set, return ""
	 */
	public String getCallBackCameraResolution() {
		return BaseDataService.getValueByString(SHARED_KEY_CALL_BACK_CAMERA_RESOLUTION, "");
	}

	public void setCallBackCameraResolution(String resolution) {
		BaseDataService.saveValueToSharePerference(SHARED_KEY_CALL_BACK_CAMERA_RESOLUTION, resolution);
	}

	/**
	 * front camera resolution
	 * format: 320x240
	 * if no value was set, return ""
	 */
	public String getCallFrontCameraResolution() {
		return BaseDataService.getValueByString(SHARED_KEY_CALL_FRONT_CAMERA_RESOLUTION, "");
	}

	public void setCallFrontCameraResolution(String resolution) {
		BaseDataService.saveValueToSharePerference(SHARED_KEY_CALL_FRONT_CAMERA_RESOLUTION, resolution);
	}

	/**
	 * fixed video sample rate
	 *  if no value was set, return false
	 * @return
     */
	public boolean isCallFixedVideoResolution() {
		return BaseDataService.getValueByBoolean(SHARED_KEY_CALL_FIX_SAMPLE_RATE, false);
	}

	public void setCallFixedVideoResolution(boolean enable) {
		BaseDataService.saveValueToSharePerference(SHARED_KEY_CALL_FIX_SAMPLE_RATE, enable);
	}

	public void setUseFCM(boolean useFCM) {
		 BaseDataService.saveValueToSharePerference(SHARED_KEY_PUSH_USE_FCM, useFCM);
	}

	public boolean isUseFCM() {
		return BaseDataService.getValueByBoolean(SHARED_KEY_PUSH_USE_FCM, true);
	}

}
