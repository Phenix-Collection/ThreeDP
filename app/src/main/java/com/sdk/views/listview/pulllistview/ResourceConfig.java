package com.sdk.views.listview.pulllistview;

import com.tdp.main.R;

/**
 * customize image resources and text resources
 * <p/>
 * Created by woxingxiao on 2016/5/22.
 */
public abstract class ResourceConfig {

    public int[] getImageResIds() {
        return configImageResIds() == null || configImageResIds().length != 5 ?
                configImageResIdsByDefault() : configImageResIds();
    }

    public int[] getTextResIds() {
        return configTextResIds() == null || configTextResIds().length != 10 ?
                configTextResIdsByDefault() : configTextResIds();
    }

    /**
     * customize image resources
     *
     * @return must 6 elements
     */
    public abstract int[] configImageResIds();

    /**
     * customize text resources
     *
     * @return must 10 elements
     */
    public abstract int[] configTextResIds();

    private int[] configImageResIdsByDefault() {
        return new int[]{R.drawable.icon_down, R.drawable.icon_ok,
                R.drawable.icon_failed, R.drawable.icon_ok,
                R.drawable.icon_failed};
    }

    private int[] configTextResIdsByDefault() {
        return new int[]{R.string.prompt_to_refresh, R.string.prompt_release_to_refresh, R.string.prompt_refreshing,
                R.string.prompt_refresh_succeeded, R.string.prompt_refresh_failed, R.string.prompt_up_to_load,
                R.string.prompt_release_to_load, R.string.prompt_loading, R.string.prompt_load_succeeded,
                R.string.prompt_load_failed};
    }
}