/*
 * 				Twidere - Twitter client for Android
 * 
 *  Copyright (C) 2012-2014 Mariotaku Lee <mariotaku.lee@gmail.com>
 * 
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 * 
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.mariotaku.twidere.app;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.multidex.MultiDexApplication;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.squareup.okhttp.internal.Network;
import com.squareup.otto.Bus;

import org.acra.ACRA;
import org.acra.annotation.ReportsCrashes;
import org.acra.sender.HttpSender;
import org.mariotaku.twidere.BuildConfig;
import org.mariotaku.twidere.Constants;
import org.mariotaku.twidere.activity.AssistLauncherActivity;
import org.mariotaku.twidere.activity.MainActivity;
import org.mariotaku.twidere.activity.MainHondaJOJOActivity;
import org.mariotaku.twidere.service.RefreshService;
import org.mariotaku.twidere.util.AsyncTaskManager;
import org.mariotaku.twidere.util.AsyncTwitterWrapper;
import org.mariotaku.twidere.util.DebugModeUtils;
import org.mariotaku.twidere.util.ErrorLogger;
import org.mariotaku.twidere.util.KeyboardShortcutsHandler;
import org.mariotaku.twidere.util.MultiSelectManager;
import org.mariotaku.twidere.util.ReadStateManager;
import org.mariotaku.twidere.util.StrictModeUtils;
import org.mariotaku.twidere.util.UserAgentUtils;
import org.mariotaku.twidere.util.UserColorNameManager;
import org.mariotaku.twidere.util.Utils;
import org.mariotaku.twidere.util.VideoLoader;
import org.mariotaku.twidere.util.content.TwidereSQLiteOpenHelper;
import org.mariotaku.twidere.util.imageloader.TwidereImageDownloader;
import org.mariotaku.twidere.util.net.TwidereHostAddressResolver;

import edu.tsinghua.spice.SpiceService;

import static org.mariotaku.twidere.util.Utils.initAccountColor;
import static org.mariotaku.twidere.util.Utils.startRefreshServiceIfNeeded;
import static org.mariotaku.twidere.util.Utils.startUsageStatisticsServiceIfNeeded;

@ReportsCrashes(formUri = "https://mariotaku.cloudant.com/acra-twidere/_design/acra-storage/_update/report",
        reportType = HttpSender.Type.JSON,
        httpMethod = HttpSender.Method.PUT,
        formUriBasicAuthLogin = "membeentlyposedistderryb",
        formUriBasicAuthPassword = "oYETEB0KXUThmyXketa8V4XY",
        buildConfigClass = BuildConfig.class)
public class TwidereApplication extends MultiDexApplication implements Constants,
        OnSharedPreferenceChangeListener {

    private static final String KEY_UCD_DATA_PROFILING = "ucd_data_profiling";
    private static final String KEY_SPICE_DATA_PROFILING = "spice_data_profiling";
    private static final String KEY_KEYBOARD_SHORTCUT_INITIALIZED = "keyboard_shortcut_initialized";

    private Handler mHandler;
    private AsyncTaskManager mAsyncTaskManager;
    private SharedPreferences mPreferences;
    private AsyncTwitterWrapper mTwitterWrapper;
    private MultiSelectManager mMultiSelectManager;
    private TwidereImageDownloader mImageDownloader, mFullImageDownloader;
    private SQLiteOpenHelper mSQLiteOpenHelper;
    private Network mNetwork;
    private SQLiteDatabase mDatabase;
    private Bus mMessageBus;
    private VideoLoader mVideoLoader;
    private ReadStateManager mReadStateManager;
    private KeyboardShortcutsHandler mKeyboardShortcutsHandler;
    private UserColorNameManager mUserColorNameManager;

    private String mDefaultUserAgent;

    @NonNull
    public static TwidereApplication getInstance(@NonNull final Context context) {
        return (TwidereApplication) context.getApplicationContext();
    }

    public AsyncTaskManager getAsyncTaskManager() {
        if (mAsyncTaskManager != null) return mAsyncTaskManager;
        return mAsyncTaskManager = AsyncTaskManager.getInstance();
    }

    public String getDefaultUserAgent() {
        return mDefaultUserAgent;
    }

    public UserColorNameManager getUserColorNameManager() {
        if (mUserColorNameManager != null) return mUserColorNameManager;
        return mUserColorNameManager = new UserColorNameManager(this);
    }

    public Handler getHandler() {
        return mHandler;
    }

    public Network getNetwork() {
        if (mNetwork != null) return mNetwork;
        return mNetwork = new TwidereHostAddressResolver(this);
    }

    public ReadStateManager getReadStateManager() {
        if (mReadStateManager != null) return mReadStateManager;
        return mReadStateManager = new ReadStateManager(this);
    }

    public KeyboardShortcutsHandler getKeyboardShortcutsHandler() {
        if (mKeyboardShortcutsHandler != null) return mKeyboardShortcutsHandler;
        mKeyboardShortcutsHandler = new KeyboardShortcutsHandler(this);
        final SharedPreferences preferences = getSharedPreferences();
        if (!preferences.getBoolean(KEY_KEYBOARD_SHORTCUT_INITIALIZED, false)) {
            mKeyboardShortcutsHandler.reset();
            preferences.edit().putBoolean(KEY_KEYBOARD_SHORTCUT_INITIALIZED, true).apply();
        }
        return mKeyboardShortcutsHandler;
    }

    public VideoLoader getVideoLoader() {
        if (mVideoLoader != null) return mVideoLoader;
        final VideoLoader loader = new VideoLoader(this);
        return mVideoLoader = loader;
    }

    @Nullable
    public Bus getMessageBus() {
        return mMessageBus;
    }

    public MultiSelectManager getMultiSelectManager() {
        if (mMultiSelectManager != null) return mMultiSelectManager;
        return mMultiSelectManager = new MultiSelectManager();
    }

    public SQLiteDatabase getSQLiteDatabase() {
        if (mDatabase != null) return mDatabase;
        StrictModeUtils.checkDiskIO();
        return mDatabase = getSQLiteOpenHelper().getWritableDatabase();
    }

    public SQLiteOpenHelper getSQLiteOpenHelper() {
        if (mSQLiteOpenHelper != null) return mSQLiteOpenHelper;
        return mSQLiteOpenHelper = new TwidereSQLiteOpenHelper(this, DATABASES_NAME, DATABASES_VERSION);
    }

    public AsyncTwitterWrapper getTwitterWrapper() {
        if (mTwitterWrapper != null) return mTwitterWrapper;
        return mTwitterWrapper = new AsyncTwitterWrapper(this);
    }

    @Override
    public void onCreate() {
        if (BuildConfig.DEBUG) {
            StrictModeUtils.detectAllVmPolicy();
        }
        super.onCreate();
        initDebugMode();
        initBugReport();
        Fresco.initialize(this);
        mDefaultUserAgent = UserAgentUtils.getDefaultUserAgentString(this);
        mHandler = new Handler();
        mMessageBus = new Bus();
        initializeAsyncTask();
        initAccountColor(this);

        final PackageManager pm = getPackageManager();
        final ComponentName main = new ComponentName(this, MainActivity.class);
        final ComponentName main2 = new ComponentName(this, MainHondaJOJOActivity.class);
        final boolean mainDisabled = pm.getComponentEnabledSetting(main) != PackageManager.COMPONENT_ENABLED_STATE_ENABLED;
        final boolean main2Disabled = pm.getComponentEnabledSetting(main2) != PackageManager.COMPONENT_ENABLED_STATE_ENABLED;
        final boolean noEntry = mainDisabled && main2Disabled;
        if (noEntry) {
            pm.setComponentEnabledSetting(main, PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                    PackageManager.DONT_KILL_APP);
        } else if (!mainDisabled) {
            pm.setComponentEnabledSetting(main2, PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                    PackageManager.DONT_KILL_APP);
        }
        if (!Utils.isComposeNowSupported(this)) {
            final ComponentName assist = new ComponentName(this, AssistLauncherActivity.class);
            pm.setComponentEnabledSetting(assist, PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                    PackageManager.DONT_KILL_APP);
        }

        migrateUsageStatisticsPreferences();
        startUsageStatisticsServiceIfNeeded(this);
        startRefreshServiceIfNeeded(this);

        reloadConnectivitySettings();
    }

    private void initDebugMode() {
        DebugModeUtils.initForApplication(this);
    }

    private void initBugReport() {
        ACRA.init(this);
        ErrorLogger.setEnabled(BuildConfig.DEBUG);
    }

    private void migrateUsageStatisticsPreferences() {
        final SharedPreferences preferences = getSharedPreferences();
        final boolean hasUsageStatistics = preferences.contains(KEY_USAGE_STATISTICS);
        if (hasUsageStatistics) return;
        if (preferences.contains(KEY_UCD_DATA_PROFILING) || preferences.contains(KEY_SPICE_DATA_PROFILING)) {
            final boolean prevUsageEnabled = preferences.getBoolean(KEY_UCD_DATA_PROFILING, false)
                    || preferences.getBoolean(KEY_SPICE_DATA_PROFILING, false);
            final Editor editor = preferences.edit();
            editor.putBoolean(KEY_USAGE_STATISTICS, prevUsageEnabled);
            editor.remove(KEY_UCD_DATA_PROFILING);
            editor.remove(KEY_SPICE_DATA_PROFILING);
            editor.apply();
        }
    }

    private SharedPreferences getSharedPreferences() {
        if (mPreferences != null) return mPreferences;
        mPreferences = getSharedPreferences(SHARED_PREFERENCES_NAME, MODE_PRIVATE);
        mPreferences.registerOnSharedPreferenceChangeListener(this);
        return mPreferences;
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    public void onSharedPreferenceChanged(final SharedPreferences preferences, final String key) {
        if (KEY_REFRESH_INTERVAL.equals(key)) {
            stopService(new Intent(this, RefreshService.class));
            startRefreshServiceIfNeeded(this);
        } else if (KEY_ENABLE_PROXY.equals(key) || KEY_CONNECTION_TIMEOUT.equals(key) || KEY_PROXY_HOST.equals(key)
                || KEY_PROXY_PORT.equals(key)) {
            reloadConnectivitySettings();
        } else if (KEY_USAGE_STATISTICS.equals(key)) {
            //spice
            stopService(new Intent(this, SpiceService.class));
            startUsageStatisticsServiceIfNeeded(this);
            //end
        } else if (KEY_CONSUMER_KEY.equals(key) || KEY_CONSUMER_SECRET.equals(key) || KEY_API_URL_FORMAT.equals(key)
                || KEY_AUTH_TYPE.equals(key) || KEY_SAME_OAUTH_SIGNING_URL.equals(key) || KEY_THUMBOR_ENABLED.equals(key)
                || KEY_THUMBOR_ADDRESS.equals(key) || KEY_THUMBOR_SECURITY_KEY.equals(key)) {
            final SharedPreferences.Editor editor = preferences.edit();
            editor.putLong(KEY_API_LAST_CHANGE, System.currentTimeMillis());
            editor.apply();
        }
    }

    public void reloadConnectivitySettings() {
        if (mImageDownloader != null) {
            mImageDownloader.reloadConnectivitySettings();
        }
        if (mFullImageDownloader != null) {
            mFullImageDownloader.reloadConnectivitySettings();
        }
    }

    private void initializeAsyncTask() {
        // AsyncTask class needs to be loaded in UI thread.
        // So we load it here to comply the rule.
        try {
            Class.forName(AsyncTask.class.getName());
        } catch (final ClassNotFoundException ignore) {
        }
    }

}
