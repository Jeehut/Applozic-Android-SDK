package com.applozic.mobicomkit.api.authentication;

import android.content.Context;

import com.applozic.mobicomkit.api.MobiComKitClientService;
import com.applozic.mobicomkit.api.account.register.RegisterUserClientService;
import com.applozic.mobicomkit.api.account.user.MobiComUserPreference;
import com.applozic.mobicomkit.listners.AlCallback;
import com.applozic.mobicommons.task.AlAsyncTask;
import com.applozic.mobicommons.task.AlTask;

import java.lang.ref.WeakReference;

/**
 * This task can be used to refresh the jwt auth token for a user once the original one has expired.
 * Simply call this task and the token will be refreshed and updated in the local storage.
 *
 * <p>This task is an async wrapper around the {@link RegisterUserClientService#refreshAuthToken(String, String)} method.</p>
 *
 * <code>
 *     RefreshAuthTokenTask refreshAuthTokenTask = new RefreshAuthTokenTask(context, new AlCallback() {
 *             @Override
 *             public void onSuccess(Object response) {
 *                 //(Boolean) response will be true
 *             }
 *
 *             @Override
 *             public void onError(Object error) { }
 *         });
 *         AlTask.execute(refreshAuthTokenTask);
 *
 *         //for versions prior to v5.95 use:
 *         //refreshAuthTokenTask.execute();
 * </code>
 */
public class RefreshAuthTokenTask extends AlAsyncTask<Void, Boolean> {

    private final String applicationId;
    private final String userId;
    private final WeakReference<Context> context;
    private final AlCallback callback;

    /**
     * @deprecated Use {@link #RefreshAuthTokenTask(Context, AlCallback)} instead.
     */
    @Deprecated
    public RefreshAuthTokenTask(Context context, String applicationId, String userId, AlCallback callback) {
        this.context = new WeakReference<>(context);
        this.applicationId = applicationId;
        this.userId = userId;
        this.callback = callback;
    }

    /**
     * Returns an instance of the <code>RefreshAuthTokenTask</code>.
     */
    public RefreshAuthTokenTask(Context context, AlCallback callback) {
        this(context, MobiComKitClientService.getApplicationKey(context), MobiComUserPreference.getInstance(context).getUserId(), callback);
    }

    @Override
    protected Boolean doInBackground() {
        return new RegisterUserClientService(context.get()).refreshAuthToken(applicationId, userId);
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        if (callback != null) {
            if (aBoolean) {
                callback.onSuccess(true);
            } else {
                callback.onError(false);
            }
        }
    }
}
