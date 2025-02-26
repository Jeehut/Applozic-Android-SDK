package com.applozic.mobicomkit.uiwidgets.conversation.activity;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.app.TaskStackBuilder;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.applozic.mobicomkit.Applozic;
import com.applozic.mobicomkit.ApplozicClient;
import com.applozic.mobicomkit.api.MobiComKitClientService;
import com.applozic.mobicomkit.api.MobiComKitConstants;
import com.applozic.mobicomkit.api.account.register.RegisterUserClientService;
import com.applozic.mobicomkit.api.account.user.MobiComUserPreference;
import com.applozic.mobicomkit.api.account.user.User;
import com.applozic.mobicomkit.api.attachment.FileClientService;
import com.applozic.mobicomkit.api.conversation.Message;
import com.applozic.mobicomkit.api.conversation.MessageWorker;
import com.applozic.mobicomkit.api.conversation.MobiComConversationService;
import com.applozic.mobicomkit.api.conversation.MobiComMessageService;
import com.applozic.mobicomkit.api.conversation.service.ConversationService;
import com.applozic.mobicomkit.broadcast.BroadcastService;
import com.applozic.mobicomkit.broadcast.ConnectivityReceiver;
import com.applozic.mobicomkit.channel.database.ChannelDatabaseService;
import com.applozic.mobicomkit.channel.service.ChannelService;
import com.applozic.mobicomkit.contact.AppContactService;
import com.applozic.mobicomkit.contact.BaseContactService;
import com.applozic.mobicomkit.listners.AlLogoutHandler;
import com.applozic.mobicomkit.uiwidgets.AlCustomizationSettings;
import com.applozic.mobicomkit.uiwidgets.ApplozicSetting;
import com.applozic.mobicomkit.uiwidgets.R;
import com.applozic.mobicomkit.uiwidgets.conversation.ConversationUIService;
import com.applozic.mobicomkit.uiwidgets.conversation.MessageCommunicator;
import com.applozic.mobicomkit.uiwidgets.conversation.MobiComKitBroadcastReceiver;
import com.applozic.mobicomkit.uiwidgets.conversation.fragment.AudioMessageFragment;
import com.applozic.mobicomkit.uiwidgets.conversation.fragment.ConversationFragment;
import com.applozic.mobicomkit.uiwidgets.conversation.fragment.MobiComQuickConversationFragment;
import com.applozic.mobicomkit.uiwidgets.conversation.fragment.MultimediaOptionFragment;
import com.applozic.mobicomkit.uiwidgets.customization.FragmentTransitionCustomization;
import com.applozic.mobicomkit.uiwidgets.instruction.ApplozicPermissions;
import com.applozic.mobicomkit.uiwidgets.instruction.InstructionUtil;
import com.applozic.mobicomkit.uiwidgets.people.activity.MobiComKitPeopleActivity;
import com.applozic.mobicomkit.uiwidgets.people.fragment.ProfileFragment;
import com.applozic.mobicomkit.uiwidgets.uilistener.ALStoragePermission;
import com.applozic.mobicomkit.uiwidgets.uilistener.ALStoragePermissionListener;
import com.applozic.mobicomkit.uiwidgets.uilistener.CustomToolbarListener;
import com.applozic.mobicomkit.uiwidgets.uilistener.MobicomkitUriListener;
import com.applozic.mobicommons.ALSpecificSettings;
import com.applozic.mobicommons.ApplozicService;
import com.applozic.mobicommons.commons.core.utils.PermissionsUtils;
import com.applozic.mobicommons.commons.core.utils.Utils;
import com.applozic.mobicommons.file.ALFileProvider;
import com.applozic.mobicommons.file.FileUtils;
import com.applozic.mobicommons.json.GsonUtils;
import com.applozic.mobicommons.people.SearchListFragment;
import com.applozic.mobicommons.people.channel.Channel;
import com.applozic.mobicommons.people.channel.Conversation;
import com.applozic.mobicommons.people.contact.Contact;
import com.applozic.mobicommons.task.AlAsyncTask;
import com.applozic.mobicommons.task.AlTask;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.giphy.sdk.core.models.Media;
import com.giphy.sdk.ui.GPHContentType;
import com.giphy.sdk.ui.GPHSettings;
import com.giphy.sdk.ui.views.GiphyDialogFragment;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by devashish on 6/25/2015.
 */
public class ConversationActivity extends AppCompatActivity implements MessageCommunicator, MobiComKitActivityInterface, ActivityCompat.OnRequestPermissionsResultCallback, MobicomkitUriListener, SearchView.OnQueryTextListener, OnClickReplyInterface, ALStoragePermissionListener, CustomToolbarListener {

    public static final int LOCATION_SERVICE_ENABLE = 1001;
    public static final String TAKE_ORDER = "takeOrder";
    public static final String CONTACT = "contact";
    public static final String CHANNEL = "channel";
    public static final String CONVERSATION_ID = "conversationId";
    public static final String ACTIVITY_TO_OPEN_ONCLICK_OF_CALL_BUTTON_META_DATA = "activity.open.on.call.button.click";
    protected static final long UPDATE_INTERVAL = 500;
    protected static final long FASTEST_INTERVAL = 1;
    private static final String LOAD_FILE = "loadFile";
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private static final String API_KYE_STRING = "YOUR_GEO_API_KEY";
    private static final String CAPTURED_IMAGE_URI = "capturedImageUri";
    private static final String CAPTURED_VIDEO_URI = "capturedVideoUri";
    private static final String SHARE_TEXT = "share_text";
    public static final String CONTACTS_GROUP_ID = "CONTACTS_GROUP_ID";
    private static Uri capturedImageUri;
    private static String inviteMessage;
    private static int retry;
    public Contact contact;
    Integer parentGroupKey;
    String parentClientGroupKey;
    public LinearLayout layout;
    public boolean isTakePhoto;
    public boolean isAttachment;
    public Integer currentConversationId;
    public Snackbar snackbar;
    protected ConversationFragment conversation;
    protected MobiComQuickConversationFragment quickConversationFragment;
    protected MobiComKitBroadcastReceiver mobiComKitBroadcastReceiver;
    protected ActionBar mActionBar;
    protected FusedLocationProviderClient fusedLocationClient;
    String geoApiKey;
    String activityToOpenOnClickOfCallButton;
    int resourceId;
    RelativeLayout childFragmentLayout;
    ProfileFragment profilefragment;
    MobiComMessageService mobiComMessageService;
    AlCustomizationSettings alCustomizationSettings;
    ConnectivityReceiver connectivityReceiver;
    File mediaFile;
    File profilePhotoFile;
    SyncAccountStatusAsyncTask accountStatusAsyncTask;
    String contactsGroupId, userDisplayName;
    private Channel channel;
    private BaseContactService baseContactService;
    private ApplozicPermissions applozicPermission;
    private Uri videoFileUri;
    private Uri imageUri;
    private ConversationUIService conversationUIService;
    private SearchView searchView;
    private String searchTerm;
    private SearchListFragment searchListFragment;
    private LinearLayout serviceDisconnectionLayout;
    private ALStoragePermission alStoragePermission;
    private ImageView conversationContactPhoto;
    private TextView toolbarTitle;
    private TextView toolbarSubtitle;
    private boolean isActivityDestroyed;

    public ConversationActivity() {

    }

    public static void addFragment(FragmentActivity fragmentActivity, Fragment fragmentToAdd, String fragmentTag) {
        if (fragmentActivity.isFinishing() || (fragmentActivity instanceof ConversationActivity && ((ConversationActivity) fragmentActivity).isActivityDestroyed)) {
            return;
        }
        if (Utils.hasJellyBeanMR1()) {
            if (fragmentActivity.isDestroyed()) {
                return;
            }
        }

        String jsonString = FileUtils.loadSettingsJsonFile(fragmentActivity.getApplicationContext());
        AlCustomizationSettings alCustomizationSettings;
        if (!TextUtils.isEmpty(jsonString)) {
            alCustomizationSettings = (AlCustomizationSettings) GsonUtils.getObjectFromJson(jsonString, AlCustomizationSettings.class);
        } else {
            alCustomizationSettings = new AlCustomizationSettings();
        }

        FragmentManager supportFragmentManager = fragmentActivity.getSupportFragmentManager();

        // Fragment activeFragment = UIService.getActiveFragment(fragmentActivity);
        FragmentTransaction fragmentTransaction = supportFragmentManager
                .beginTransaction();

        FragmentTransitionCustomization fragmentTransitionCustomization = FragmentTransitionCustomization.getInstance(fragmentActivity, alCustomizationSettings);
        FragmentTransitionCustomization.SingleFragmentTransitionResourceIds singleFragmentTransitionResourceIds = fragmentTransitionCustomization.getTransitionResourceIdsForFragment(fragmentTag);
        if(singleFragmentTransitionResourceIds != null) {
            fragmentTransaction.setCustomAnimations(singleFragmentTransitionResourceIds.enterTransitionResourceId, singleFragmentTransitionResourceIds.exitTransitionFileResourceId, singleFragmentTransitionResourceIds.popEnterTransitionFileResourceId, singleFragmentTransitionResourceIds.popExitTransitionFileResourceId);
        }

        fragmentTransaction.replace(R.id.layout_child_activity, fragmentToAdd,
                fragmentTag);

        if (supportFragmentManager.getBackStackEntryCount() > 1
                && !ConversationUIService.MESSGAE_INFO_FRAGMENT.equalsIgnoreCase(fragmentTag) && !ConversationUIService.USER_PROFILE_FRAMENT.equalsIgnoreCase(fragmentTag)) {
            supportFragmentManager.popBackStackImmediate();
        }

        fragmentTransaction.addToBackStack(fragmentTag);
        fragmentTransaction.commitAllowingStateLoss();
        supportFragmentManager.executePendingTransactions();
        //Log.i(TAG, "BackStackEntryCount: " + supportFragmentManager.getBackStackEntryCount());
    }

    public static Uri getCapturedImageUri() {
        return capturedImageUri;
    }

    public static void setCapturedImageUri(Uri capturedImageUri) {
        ConversationActivity.capturedImageUri = capturedImageUri;
    }

    @Override
    public void showErrorMessageView(String message) {
        try {
            layout.setVisibility(View.VISIBLE);
            snackbar = Snackbar.make(layout, message, Snackbar.LENGTH_LONG);
            snackbar.setAction(this.getString(R.string.ok_alert), new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    snackbar.dismiss();
                }
            });
            snackbar.setDuration(Snackbar.LENGTH_LONG);
            ViewGroup group = (ViewGroup) snackbar.getView();
            TextView textView = (TextView) group.findViewById(R.id.snackbar_action);
            textView.setTextColor(Color.YELLOW);
            group.setBackgroundColor(getResources().getColor(R.color.error_background_color));
            TextView txtView = (TextView) group.findViewById(R.id.snackbar_text);
            txtView.setMaxLines(5);
            snackbar.show();
        } catch (Exception e) {

        }

    }

    @Override
    public void retry() {
        retry++;
    }

    @Override
    public int getRetryCount() {
        return retry;
    }

    public void dismissErrorMessage() {
        if (snackbar != null) {
            snackbar.dismiss();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        Applozic.disconnectPublish(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Applozic.connectPublishWithVerifyTokenAfter(this, getString(R.string.auth_token_loading_message), 0);
        if (!Utils.isInternetAvailable(getApplicationContext())) {
            String errorMessage = getResources().getString(R.string.internet_connection_not_available);
            showErrorMessageView(errorMessage);
        }
    }

    @Override
    protected void onPause() {
        //ApplozicMqttService.getInstance(this).unSubscribe();

        super.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putSerializable(CONTACT, contact);
        savedInstanceState.putSerializable(CHANNEL, channel);
        savedInstanceState.putSerializable(CONVERSATION_ID, currentConversationId);

        if (capturedImageUri != null) {
            savedInstanceState.putString(CAPTURED_IMAGE_URI, capturedImageUri.toString());
        }
        if (videoFileUri != null) {
            savedInstanceState.putString(CAPTURED_VIDEO_URI, videoFileUri.toString());
        }
        if (mediaFile != null) {
            savedInstanceState.putSerializable(LOAD_FILE, mediaFile);
        }
        if (userDisplayName != null) {
            savedInstanceState.putSerializable(ConversationUIService.DISPLAY_NAME, userDisplayName);
        }
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public boolean onSupportNavigateUp() {
        if (isFromSearch()) {
            return true;
        }

        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            if (getSupportFragmentManager().getBackStackEntryCount() == 1) {
                Intent upIntent = ApplozicSetting.getInstance(this).getParentActivityIntent(this);
                if (upIntent != null && isTaskRoot()) {
                    TaskStackBuilder.create(this).addNextIntentWithParentStack(upIntent).startActivities();
                }
                ConversationActivity.this.finish();
                return true;
            }
            Boolean takeOrder = getIntent().getBooleanExtra(TAKE_ORDER, false);
            if (takeOrder && getSupportFragmentManager().getBackStackEntryCount() == 2) {
                try {
                    String parentActivity = ApplozicSetting.getInstance(this).getParentActivityName(this);
                    if (parentActivity != null && isTaskRoot()) {
                        Intent intent = new Intent(this, Class.forName(parentActivity));
                        startActivity(intent);
                    }
                    ConversationActivity.this.finish();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                    return false;
                }
                return true;
            } else {
                getSupportFragmentManager().popBackStack();
            }
            Utils.toggleSoftKeyBoard(this, true);
            return true;
        } else {
            super.onSupportNavigateUp();
        }
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ApplozicService.initWithContext(getApplication());
        String jsonString = FileUtils.loadSettingsJsonFile(getApplicationContext());
        if (!TextUtils.isEmpty(jsonString)) {
            alCustomizationSettings = (AlCustomizationSettings) GsonUtils.getObjectFromJson(jsonString, AlCustomizationSettings.class);
        } else {
            alCustomizationSettings = new AlCustomizationSettings();
        }
        if (!TextUtils.isEmpty(alCustomizationSettings.getChatBackgroundImageName())) {
            resourceId = getResources().getIdentifier(alCustomizationSettings.getChatBackgroundImageName(), "drawable", getPackageName());
        }
        if (resourceId != 0) {
            getWindow().setBackgroundDrawableResource(resourceId);
        }
        setContentView(R.layout.quickconversion_activity);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        conversationContactPhoto = myToolbar.findViewById(R.id.conversation_contact_photo);
        toolbarTitle = myToolbar.findViewById(R.id.toolbar_title);
        toolbarSubtitle = myToolbar.findViewById(R.id.toolbar_subtitle);
        setSupportActionBar(myToolbar);
        baseContactService = new AppContactService(this);
        conversationUIService = new ConversationUIService(this);
        mobiComMessageService = new MobiComMessageService(this, MessageWorker.class);
        quickConversationFragment = new MobiComQuickConversationFragment();
        connectivityReceiver = new ConnectivityReceiver();
        geoApiKey = Applozic.getInstance(this).getGeoApiKey();
        activityToOpenOnClickOfCallButton = Utils.getMetaDataValue(getApplicationContext(), ACTIVITY_TO_OPEN_ONCLICK_OF_CALL_BUTTON_META_DATA);
        layout = (LinearLayout) findViewById(R.id.footerAd);
        applozicPermission = new ApplozicPermissions(this, layout);
        childFragmentLayout = (RelativeLayout) findViewById(R.id.layout_child_activity);
        profilefragment = new ProfileFragment();
        profilefragment.setAlCustomizationSettings(alCustomizationSettings);
        contactsGroupId = MobiComUserPreference.getInstance(this).getContactsGroupId();
        serviceDisconnectionLayout = findViewById(R.id.serviceDisconnectionLayout);

        if (!Utils.isDebugBuild(this) && ALSpecificSettings.getInstance(this).isLoggingEnabledForReleaseBuild()) {
            showLogWarningForReleaseBuild();
        }

        if (Utils.hasMarshmallow() && (!alCustomizationSettings.isGlobalStoagePermissionDisabled() || ALSpecificSettings.getInstance(this).isTextLoggingEnabled())) {
            applozicPermission.checkRuntimePermissionForStorage();
        }

        mActionBar = getSupportActionBar();
        if (!TextUtils.isEmpty(alCustomizationSettings.getThemeColorPrimary()) && !TextUtils.isEmpty(alCustomizationSettings.getThemeColorPrimaryDark())) {
            mActionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor(alCustomizationSettings.getThemeColorPrimary())));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().setStatusBarColor(Color.parseColor(alCustomizationSettings.getThemeColorPrimaryDark()));
            }
        }
        inviteMessage = Utils.getMetaDataValue(getApplicationContext(), SHARE_TEXT);
        retry = 0;
        if (getIntent() != null) {
            parentClientGroupKey = getIntent().getStringExtra(ConversationUIService.PARENT_CLIENT_GROUP_ID);
            if (!TextUtils.isEmpty(parentClientGroupKey)) {
                parentGroupKey = ChannelService.getInstance(this).getParentGroupKeyByClientGroupKey(parentClientGroupKey);
            } else {
                parentGroupKey = getIntent().getIntExtra(ConversationUIService.PARENT_GROUP_KEY, 0);
            }
            if (parentGroupKey != null && parentGroupKey != 0) {
                BroadcastService.parentGroupKey = parentGroupKey;
                MobiComUserPreference.getInstance(this).setParentGroupKey(parentGroupKey);
            }
        }

        if (ApplozicClient.getInstance(this).isServiceDisconnected()) {
            serviceDisconnectionLayout.setVisibility(View.VISIBLE);
        } else {
            if (savedInstanceState != null) {
                capturedImageUri = savedInstanceState.getString(CAPTURED_IMAGE_URI) != null ?
                        Uri.parse(savedInstanceState.getString(CAPTURED_IMAGE_URI)) : null;
                videoFileUri = savedInstanceState.getString(CAPTURED_VIDEO_URI) != null ?
                        Uri.parse(savedInstanceState.getString(CAPTURED_VIDEO_URI)) : null;
                mediaFile = savedInstanceState.getSerializable(LOAD_FILE) != null ? (File) savedInstanceState.getSerializable(LOAD_FILE) : null;
                userDisplayName = savedInstanceState.getString(ConversationUIService.DISPLAY_NAME);
                contact = (Contact) savedInstanceState.getSerializable(CONTACT);
                channel = (Channel) savedInstanceState.getSerializable(CHANNEL);
                currentConversationId = savedInstanceState.getInt(CONVERSATION_ID);
                if (contact != null || channel != null) {
                    if (channel != null) {
                        conversation = ConversationFragment.newInstance(null, channel, currentConversationId, null, userDisplayName);
                    } else {
                        conversation = ConversationFragment.newInstance(contact, null, currentConversationId, null, userDisplayName);
                    }
                    addFragment(this, conversation, ConversationUIService.CONVERSATION_FRAGMENT);
                }
            } else {
                setSearchListFragment(quickConversationFragment);
                addFragment(this, quickConversationFragment, ConversationUIService.QUICK_CONVERSATION_FRAGMENT);
            }
        }
        mobiComKitBroadcastReceiver = new MobiComKitBroadcastReceiver(this);
        InstructionUtil.showInfo(this, R.string.info_message_sync, BroadcastService.INTENT_ACTIONS.INSTRUCTION.toString());

        setToolbarTitle(getString(R.string.conversations));
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setHomeButtonEnabled(true);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        onNewIntent(getIntent());

        Boolean takeOrder = getIntent().getBooleanExtra(TAKE_ORDER, false);

        if (!takeOrder) {
            new MobiComConversationService(this.getApplicationContext()).updateLastSeenAtForAllUsers();
        }

        if (ApplozicClient.getInstance(this).isAccountClosed() || ApplozicClient.getInstance(this).isNotAllowed()) {
            accountStatusAsyncTask = new SyncAccountStatusAsyncTask(this, layout, snackbar);
            AlTask.execute(accountStatusAsyncTask);
        }
        registerReceiver(connectivityReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

        if (getIntent() != null) {
            Set<String> userIdLists = new HashSet<String>();
            if (getIntent().getStringArrayListExtra(ConversationUIService.GROUP_NAME_LIST_CONTACTS) != null) {
                MobiComUserPreference.getInstance(this).setIsContactGroupNameList(true);
                userIdLists.addAll(getIntent().getStringArrayListExtra(ConversationUIService.GROUP_NAME_LIST_CONTACTS));
            } else if (getIntent().getStringArrayListExtra(ConversationUIService.GROUP_ID_LIST_CONTACTS) != null) {
                MobiComUserPreference.getInstance(this).setIsContactGroupNameList(false);
                userIdLists.addAll(getIntent().getStringArrayListExtra(ConversationUIService.GROUP_ID_LIST_CONTACTS));
            }

            if (!userIdLists.isEmpty()) {
                MobiComUserPreference.getInstance(this).setContactGroupIdList(userIdLists);
            }
        }

        LocalBroadcastManager.getInstance(this).registerReceiver(mobiComKitBroadcastReceiver, BroadcastService.getIntentFilter());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        //setIntent(intent);
        if (!MobiComUserPreference.getInstance(this).isLoggedIn()) {
            //user is not logged in
            Utils.printLog(this, "AL", "user is not logged in yet.");
            return;
        }

        try {
            if (ApplozicClient.getInstance(this).isServiceDisconnected()) {
                serviceDisconnectionLayout.setVisibility(View.VISIBLE);
            } else {
                if (intent.getExtras() != null) {
                    BroadcastService.setContextBasedChat(intent.getExtras().getBoolean(ConversationUIService.CONTEXT_BASED_CHAT));
                    if (BroadcastService.isIndividual() && intent.getExtras().getBoolean(MobiComKitConstants.QUICK_LIST)) {
                        setSearchListFragment(quickConversationFragment);
                        addFragment(this, quickConversationFragment, ConversationUIService.QUICK_CONVERSATION_FRAGMENT);
                    } else {
                        conversationUIService.checkForStartNewConversation(intent);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void animateToolbarTitle() {
        if (toolbarTitle != null) {
            ObjectAnimator animation = ObjectAnimator.ofFloat(toolbarTitle, "translationY", 0f);
            animation.setDuration(0);
            animation.start();
        }
    }

    @Override
    public void setToolbarTitle(String title) {
        toolbarTitle.setText(title);
        if (toolbarSubtitle != null && toolbarSubtitle.getVisibility() == View.GONE) {
            animateToolbarTitle();
        }
    }

    @Override
    public void setToolbarSubtitle(String subtitle) {
        if (subtitle.length() == 0) {
            toolbarSubtitle.setVisibility(View.GONE);
            animateToolbarTitle();
            return;
        }
        toolbarSubtitle.setVisibility(View.VISIBLE);
        toolbarSubtitle.setText(subtitle);
        ObjectAnimator animation = ObjectAnimator.ofFloat(toolbarTitle, "translationY", -20f);
        animation.setDuration(0);
        animation.start();
        ObjectAnimator animationSub = ObjectAnimator.ofFloat(toolbarSubtitle, "translationY", -20f);
        animationSub.setDuration(0);
        animationSub.start();
    }

    @Override
    public void setToolbarImage(Contact contact, Channel channel) {
        if (ApplozicSetting.getInstance(this).isShowImageOnToolbar() || alCustomizationSettings.isShowImageOnToolbar()) {
            conversationContactPhoto.setVisibility(View.VISIBLE);
            if (contact != null) {
                Glide.with(this)
                        .load(contact.getImageURL())
                        .apply(new RequestOptions().placeholder(R.drawable.applozic_ic_contact_picture_holo_light))
                        .into(conversationContactPhoto);
            } else if (channel != null) {
                Glide.with(this)
                        .load(channel.getImageUrl())
                        .apply(new RequestOptions().placeholder(R.drawable.applozic_group_icon))
                        .into(conversationContactPhoto);
            } else {
                conversationContactPhoto.setImageResource(R.drawable.applozic_ic_contact_picture_holo_light);
            }
        }
    }

    private void showActionBar() {
        mActionBar.setDisplayShowTitleEnabled(true);
    }

    @SuppressLint("NewApi")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        showActionBar();
        //return false;
        getMenuInflater().inflate(R.menu.mobicom_basic_menu_for_normal_message, menu);
        MenuItem searchItem = menu.findItem(R.id.menu_search);
        searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setQueryHint(getResources().getString(R.string.search_hint));
        if (Utils.hasICS()) {
            searchItem.collapseActionView();
        }
        searchView.setOnQueryTextListener(this);
        searchView.setSubmitButtonEnabled(true);
        searchView.setIconifiedByDefault(true);

        if (quickConversationFragment != null && !TextUtils.isEmpty(quickConversationFragment.getSearchString())) {
            searchView.setIconified(false);
            searchView.setQuery(quickConversationFragment.getSearchString(), false);
        }

        return super.onCreateOptionsMenu(menu);
    }

    public void showLogWarningForReleaseBuild() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setTitle(R.string.warning);
        dialogBuilder.setMessage(R.string.release_log_warning_message);
        dialogBuilder.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            super.onActivityResult(requestCode, resultCode, data);
            conversationUIService.onActivityResult(requestCode, resultCode, data);
            handleOnActivityResult(requestCode, data);
            if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                if (resultCode == RESULT_OK) {
                    if (data == null) {
                        return;
                    }
                    if (imageUri != null) {
                        imageUri = result.getUri();
                        if (imageUri != null && profilefragment != null) {
                            profilefragment.handleProfileimageUpload(true, imageUri, profilePhotoFile);
                        }
                    } else {
                        imageUri = result.getUri();
                        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                        String imageFileName = "JPEG_" + timeStamp + "_" + ".jpeg";
                        profilePhotoFile = FileClientService.getFilePath(imageFileName, this, "image/jpeg");
                        if (imageUri != null && profilefragment != null) {
                            profilefragment.handleProfileimageUpload(true, imageUri, profilePhotoFile);
                        }
                    }
                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    Utils.printLog(this, ConversationActivity.class.getName(), "Cropping failed:" + result.getError());
                }
            }
            if (requestCode == LOCATION_SERVICE_ENABLE) {
                if (((LocationManager) getSystemService(Context.LOCATION_SERVICE))
                        .isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    processingLocation();
                } else {
                    Toast.makeText(ConversationActivity.this, R.string.unable_to_fetch_location, Toast.LENGTH_LONG).show();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void handleOnActivityResult(int requestCode, Intent intent) {

        switch (requestCode) {

            case ProfileFragment.REQUEST_CODE_ATTACH_PHOTO:
                Uri selectedFileUri = (intent == null ? null : intent.getData());
                imageUri = null;
                beginCrop(selectedFileUri);
                break;

            case ProfileFragment.REQUEST_CODE_TAKE_PHOTO:
                beginCrop(imageUri);
                break;

        }
    }

    void beginCrop(Uri imageUri) {
        try {
            CropImage.activity(imageUri)
                    .setGuidelines(CropImageView.Guidelines.OFF)
                    .setMultiTouchEnabled(true)
                    .start(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PermissionsUtils.REQUEST_STORAGE) {
            if (alStoragePermission != null) {
                alStoragePermission.onAction(PermissionsUtils.verifyPermissions(grantResults));
            }
            if (PermissionsUtils.verifyPermissions(grantResults)) {
                showSnackBar(R.string.storage_permission_granted);
                if (isAttachment) {
                    isAttachment = false;
                    processAttachment();
                }
            } else {
                showSnackBar(R.string.storage_permission_not_granted);
            }
        } else if (requestCode == PermissionsUtils.REQUEST_LOCATION) {
            if (PermissionsUtils.verifyPermissions(grantResults)) {
                showSnackBar(R.string.location_permission_granted);
                processingLocation();
            } else {
                showSnackBar(R.string.location_permission_not_granted);
            }

        } else if (requestCode == PermissionsUtils.REQUEST_PHONE_STATE) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showSnackBar(R.string.phone_state_permission_granted);
            } else {
                showSnackBar(R.string.phone_state_permission_not_granted);
            }
        } else if (requestCode == PermissionsUtils.REQUEST_AUDIO_RECORD) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showSnackBar(R.string.record_audio_permission_granted);
                showAudioRecordingDialog();
            } else {
                showSnackBar(R.string.record_audio_permission_not_granted);
            }
        } else if (requestCode == PermissionsUtils.REQUEST_CAMERA) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showSnackBar(R.string.phone_camera_permission_granted);
                if (isTakePhoto) {
                    processCameraAction();
                } else {
                    processVideoRecording();
                }
            } else {
                showSnackBar(R.string.phone_camera_permission_not_granted);
            }
        } else if (requestCode == PermissionsUtils.REQUEST_CAMERA_FOR_PROFILE_PHOTO) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showSnackBar(R.string.phone_camera_permission_granted);
                if (profilefragment != null) {
                    profilefragment.processPhotoOption();
                }
            } else {
                showSnackBar(R.string.phone_camera_permission_not_granted);
            }
        } else if (requestCode == PermissionsUtils.REQUEST_STORAGE_FOR_PROFILE_PHOTO) {
            if (PermissionsUtils.verifyPermissions(grantResults)) {
                showSnackBar(R.string.storage_permission_granted);
                if (profilefragment != null) {
                    profilefragment.processPhotoOption();
                }
            } else {
                showSnackBar(R.string.storage_permission_not_granted);
            }
        } else if (requestCode == PermissionsUtils.REQUEST_CAMERA_AUDIO) {
            if (PermissionsUtils.verifyPermissions(grantResults)) {
                showSnackBar(R.string.phone_camera_and_audio_permission_granted);
            } else {
                showSnackBar(R.string.audio_or_camera_permission_not_granted);
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    public void processingLocation() {
        if (!((LocationManager) getSystemService(Context.LOCATION_SERVICE))
                .isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.location_services_disabled_title)
                    .setMessage(R.string.location_services_disabled_message)
                    .setCancelable(false)
                    .setPositiveButton(R.string.location_service_settings, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivityForResult(intent, LOCATION_SERVICE_ENABLE);
                        }
                    })
                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                            Toast.makeText(ConversationActivity.this, R.string.location_sending_cancelled, Toast.LENGTH_LONG).show();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        } else if (alCustomizationSettings.isLocationShareViaMap() && !TextUtils.isEmpty(geoApiKey) && !API_KYE_STRING.equals(geoApiKey)) {
            Intent toMapActivity = new Intent(this, MobicomLocationActivity.class);
            startActivityForResult(toMapActivity, MultimediaOptionFragment.REQUEST_CODE_SEND_LOCATION);
        } else {
            currentLocation();
        }
    }

    public void currentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationClient.getCurrentLocation(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY, null)
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null && conversation != null) {
                            conversation.attachLocation(location);
                        } else {
                            Toast.makeText(ConversationActivity.this, R.string.unable_to_fetch_location, Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    public void processLocation() {
        if (Utils.hasMarshmallow()) {
            new ApplozicPermissions(ConversationActivity.this, layout).checkRuntimePermissionForLocation();
        } else {
            processingLocation();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.start_new) {
            if (!TextUtils.isEmpty(contactsGroupId)) {
                if (Utils.isInternetAvailable(this)) {
                    conversationUIService.startContactActivityForResult();
                } else {
                    Intent intent = new Intent(this, MobiComKitPeopleActivity.class);
                    ChannelDatabaseService channelDatabaseService = ChannelDatabaseService.getInstance(this);
                    String[] userIdArray = channelDatabaseService.getChannelMemberByName(contactsGroupId, null);
                    if (userIdArray != null) {
                        conversationUIService.startContactActivityForResult(intent, null, null, userIdArray);
                    }
                }
            } else {
                conversationUIService.startContactActivityForResult();
            }
        } else if (id == R.id.conversations) {
            Intent intent = new Intent(this, ChannelCreateActivity.class);
            intent.putExtra(ChannelCreateActivity.GROUP_TYPE, Channel.GroupType.PUBLIC.getValue().intValue());
            startActivity(intent);
        } else if (id == R.id.broadcast) {
            Intent intent = new Intent(this, ContactSelectionActivity.class);
            intent.putExtra(ContactSelectionActivity.GROUP_TYPE, Channel.GroupType.BROADCAST.getValue().intValue());
            startActivity(intent);
        } else if (id == R.id.refresh) {
            Toast.makeText(this, getString(R.string.info_message_sync), Toast.LENGTH_LONG).show();
            AlTask.execute(new SyncMessagesAsyncTask(this));
        } else if (id == R.id.shareOptions) {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setAction(Intent.ACTION_SEND)
                    .setType("text/plain").putExtra(Intent.EXTRA_TEXT, inviteMessage);
            startActivity(Intent.createChooser(intent, "Share Via"));
            return super.onOptionsItemSelected(item);
        } else if (id == R.id.applozicUserProfile) {
            profilefragment.setApplozicPermissions(applozicPermission);
            addFragment(this, profilefragment, ProfileFragment.ProfileFragmentTag);
        } else if (id == R.id.logout) {

            if (!TextUtils.isEmpty(alCustomizationSettings.getLogoutPackage())) {
                Applozic.logoutUser(ConversationActivity.this, new AlLogoutHandler() {
                    @Override
                    public void onSuccess(Context context) {
                        try {
                            Class loginActivity = Class.forName(alCustomizationSettings.getLogoutPackage().trim());
                            if (loginActivity != null) {
                                Toast.makeText(getBaseContext(), getString(R.string.user_logout_info), Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(ConversationActivity.this, loginActivity);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                startActivity(intent);
                                finish();
                            }
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Exception exception) {

                    }
                });
            }
        } else if (id == R.id.sendTextLogs) {
            try {
                Intent emailIntent = new Intent(Intent.ACTION_SEND);
                emailIntent.setType("vnd.android.cursor.dir/email");
                String receivers[] = {ALSpecificSettings.getInstance(this).getSupportEmailId()};
                emailIntent.putExtra(Intent.EXTRA_EMAIL, receivers);
                emailIntent.putExtra(Intent.EXTRA_STREAM, Utils.getTextLogFileUri(this));
                emailIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name) + " " + getString(R.string.log_email_subject));
                startActivity(Intent.createChooser(emailIntent, getString(R.string.select_email_app_chooser_title)));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    @Override
    public void onQuickConversationFragmentItemClick(View view, Contact contact, Channel channel, Integer conversationId, String searchString) {
        conversation = ConversationFragment.newInstance(contact, channel, conversationId, searchString, null);
        addFragment(this, conversation, ConversationUIService.CONVERSATION_FRAGMENT);
        this.channel = channel;
        this.contact = contact;
        this.currentConversationId = conversationId;
    }

    @Override
    public void startContactActivityForResult() {
        conversationUIService.startContactActivityForResult();
    }

    @Override
    public void addFragment(ConversationFragment conversationFragment) {
        addFragment(this, conversationFragment, ConversationUIService.CONVERSATION_FRAGMENT);
        conversation = conversationFragment;
    }

    @Override
    public void onBackPressed() {
        if (isFromSearch()) {
            return;
        }
        if (getSupportFragmentManager().getBackStackEntryCount() == 1) {
            try {
                Intent upIntent = ApplozicSetting.getInstance(this).getParentActivityIntent(this);
                if (upIntent != null && isTaskRoot()) {
                    TaskStackBuilder.create(this).addNextIntentWithParentStack(upIntent).startActivities();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            this.finish();
            return;
        }

        Boolean takeOrder = getIntent().getBooleanExtra(TAKE_ORDER, false);
        ConversationFragment conversationFragment = (ConversationFragment) getSupportFragmentManager().findFragmentByTag(ConversationUIService.CONVERSATION_FRAGMENT);
        if (conversationFragment != null && conversationFragment.isVisible() && (conversationFragment.multimediaPopupGrid.getVisibility() == View.VISIBLE)) {
            conversationFragment.hideMultimediaOptionGrid();
            return;
        }

        if (takeOrder && getSupportFragmentManager().getBackStackEntryCount() == 2) {
            Intent upIntent = ApplozicSetting.getInstance(this).getParentActivityIntent(this);
            if (upIntent != null && isTaskRoot()) {
                TaskStackBuilder.create(this).addNextIntentWithParentStack(upIntent).startActivities();
            }
            ConversationActivity.this.finish();
        } else if (getSupportFragmentManager().getBackStackEntryCount() > 1) {
            getSupportFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }

    public boolean isFromSearch() {
        if (searchView != null && !searchView.isIconified() && quickConversationFragment != null && quickConversationFragment.isVisible()) {
            quickConversationFragment.stopSearching();
            searchView.onActionViewCollapsed();
            return true;
        }
        return false;
    }

    @Override
    public void updateLatestMessage(Message message, String formattedContactNumber) {
        conversationUIService.updateLatestMessage(message, formattedContactNumber);

    }

    @Override
    public void removeConversation(Message message, String formattedContactNumber) {
        conversationUIService.removeConversation(message, formattedContactNumber);
    }

    public void setChildFragmentLayoutBG() {

        childFragmentLayout.setBackgroundResource(R.color.conversation_list_all_background);
    }

    public void setChildFragmentLayoutBGToTransparent() {

        childFragmentLayout.setBackgroundResource(android.R.color.transparent);
    }

    void showErrorDialog(int code) {
        GooglePlayServicesUtil.getErrorDialog(code, this,
                CONNECTION_FAILURE_RESOLUTION_REQUEST).show();
    }

    public Contact getContact() {
        return contact;
    }

    public Channel getChannel() {
        return channel;
    }

    public Integer getConversationId() {
        return currentConversationId;
    }

    public void showSnackBar(int resId) {
        snackbar = Snackbar.make(layout, resId,
                Snackbar.LENGTH_SHORT);
        snackbar.show();
    }

    public Uri getVideoFileUri() {
        return videoFileUri;
    }

    public void setVideoFileUri(Uri videoFileUri) {
        this.videoFileUri = videoFileUri;
    }

    public void isTakePhoto(boolean takePhoto) {
        this.isTakePhoto = takePhoto;
    }

    public void isAttachment(boolean attachment) {
        this.isAttachment = attachment;
    }

    public File getFileObject() {
        return mediaFile;
    }

    public void showAudioRecordingDialog() {

        if (Utils.hasMarshmallow() && PermissionsUtils.checkSelfPermissionForAudioRecording(this)) {
            new ApplozicPermissions(this, layout).requestAudio();
        } else if (PermissionsUtils.isAudioRecordingPermissionGranted(this)) {

            FragmentManager supportFragmentManager = getSupportFragmentManager();
            DialogFragment fragment = AudioMessageFragment.newInstance();

            FragmentTransaction fragmentTransaction = supportFragmentManager
                    .beginTransaction().add(fragment, "AudioMessageFragment");

            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commitAllowingStateLoss();
        } else {

            if (alCustomizationSettings.getAudioPermissionNotFoundMsg() == null) {
                showSnackBar(R.string.applozic_audio_permission_missing);
            } else {
                snackbar = Snackbar.make(layout, alCustomizationSettings.getAudioPermissionNotFoundMsg(),
                        Snackbar.LENGTH_SHORT);
                snackbar.show();
            }

        }
    }

    public void processVideoCall(Contact contactObj, Integer conversationId) {
        this.contact = baseContactService.getContactById(contactObj.getContactIds());
        if (ApplozicClient.getInstance(getApplicationContext()).isIPCallEnabled()) {
            try {
                if (Utils.hasMarshmallow() && !PermissionsUtils.checkPermissionForCameraAndMicrophone(this)) {
                    applozicPermission.checkRuntimePermissionForCameraAndAudioRecording();
                    return;
                }
                String activityName = ApplozicSetting.getInstance(this).getActivityCallback(ApplozicSetting.RequestCode.VIDEO_CALL);
                Class activityToOpen = Class.forName(activityName);
                Intent intent = new Intent(this, activityToOpen);
                intent.putExtra("CONTACT_ID", contact.getUserId());
                startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public void processCall(Contact contactObj, Integer conversationId) {
        this.contact = baseContactService.getContactById(contactObj.getContactIds());
        this.currentConversationId = conversationId;
        try {

            if (ApplozicClient.getInstance(getApplicationContext()).isIPCallEnabled()) {
                if (Utils.hasMarshmallow() && !PermissionsUtils.checkPermissionForCameraAndMicrophone(this)) {
                    applozicPermission.checkRuntimePermissionForCameraAndAudioRecording();
                    return;
                }
                //Audio Call
                String activityName = ApplozicSetting.getInstance(this).getActivityCallback(ApplozicSetting.RequestCode.AUDIO_CALL);
                Class activityToOpen = Class.forName(activityName);
                Intent intent = new Intent(this, activityToOpen);
                intent.putExtra("CONTACT_ID", contact.getUserId());
                startActivity(intent);
                return;
            }

            if (activityToOpenOnClickOfCallButton != null) {
                Intent callIntent = new Intent(this, Class.forName(activityToOpenOnClickOfCallButton));
                if (currentConversationId != null) {
                    Conversation conversation = ConversationService.getInstance(this).getConversationByConversationId(currentConversationId);
                    callIntent.putExtra(ConversationUIService.TOPIC_ID, conversation.getTopicId());
                }
                callIntent.putExtra(ConversationUIService.CONTACT, contact);
                startActivity(callIntent);
            } else {
                snackbar = Snackbar.make(layout, R.string.phone_call_permission_not_granted,
                        Snackbar.LENGTH_SHORT);
                snackbar.show();
            }
        } catch (
                Exception e) {
            Utils.printLog(this, "ConversationActivity", "Call permission is not added in androidManifest");
        }
    }


    public void processCameraAction() {
        try {
            if (PermissionsUtils.isCameraPermissionGranted(this)) {
                imageCapture();
            } else {
                if (Utils.hasMarshmallow() && PermissionsUtils.checkSelfForCameraPermission(this)) {
                    applozicPermission.requestCameraPermission();
                } else {
                    imageCapture();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void processVideoRecording() {
        try {
            if (PermissionsUtils.isCameraPermissionGranted(this)) {
                showVideoCapture();
            } else {
                if (Utils.hasMarshmallow() && PermissionsUtils.checkSelfForCameraPermission(this)) {
                    applozicPermission.requestCameraPermission();
                } else {
                    showVideoCapture();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void imageCapture() {
        try {
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String imageFileName = "JPEG_" + timeStamp + "_" + ".jpeg";

            mediaFile = FileClientService.getFilePath(imageFileName, getApplicationContext(), "image/jpeg");

            capturedImageUri = ALFileProvider.getUriForFile(this, Utils.getMetaDataValue(this, MobiComKitConstants.PACKAGE_NAME) + ".applozic.provider", mediaFile);

            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, capturedImageUri);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                cameraIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                cameraIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                ClipData clip =
                        ClipData.newUri(getContentResolver(), "a Photo", capturedImageUri);

                cameraIntent.setClipData(clip);
                cameraIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                cameraIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            } else {
                List<ResolveInfo> resInfoList =
                        getPackageManager()
                                .queryIntentActivities(cameraIntent, PackageManager.MATCH_DEFAULT_ONLY);

                for (ResolveInfo resolveInfo : resInfoList) {
                    String packageName = resolveInfo.activityInfo.packageName;
                    grantUriPermission(packageName, capturedImageUri,
                            Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                    grantUriPermission(packageName, capturedImageUri,
                            Intent.FLAG_GRANT_READ_URI_PERMISSION);
                }
            }

            if (cameraIntent.resolveActivity(getApplicationContext().getPackageManager()) != null) {
                if (mediaFile != null) {
                    startActivityForResult(cameraIntent, MultimediaOptionFragment.REQUEST_CODE_TAKE_PHOTO);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void processAttachment() {
        if (Utils.hasMarshmallow() && PermissionsUtils.checkSelfForStoragePermission(this)) {
            applozicPermission.requestStoragePermissions();
        } else {
            Intent intentPick = new Intent(this, MobiComAttachmentSelectorActivity.class);
            startActivityForResult(intentPick, MultimediaOptionFragment.REQUEST_MULTI_ATTCAHMENT);
        }
    }

    public void processGif() {
        final String GIPHY_DIALOG_TAG = "GIPHY_DIALOG_TAG";
        String giphyApiKey = Utils.getMetaDataValue(ApplozicService.getContext(this), MobiComKitClientService.GIPHY_API_METADATA_KEY);

        if (TextUtils.isEmpty(giphyApiKey)) {
            Toast.makeText(this, getString(R.string.gif_not_enabled), Toast.LENGTH_LONG).show();
            return;
        }

        GPHSettings gphSettings = new GPHSettings();
        gphSettings.setMediaTypeConfig(new GPHContentType[] {GPHContentType.gif, GPHContentType.sticker});

        GiphyDialogFragment giphyDialogFragment = GiphyDialogFragment.Companion.newInstance(gphSettings, giphyApiKey, false);

        giphyDialogFragment.setGifSelectionListener(new GiphyDialogFragment.GifSelectionListener() {
            @Override
            public void onGifSelected(@NotNull Media media, @Nullable String s, @NotNull GPHContentType gphContentType) {
                if (conversationUIService == null) {
                    return;
                }

                conversationUIService.sendGifMessageFromGifMedia(media);
            }

            @Override
            public void onDismissed(@NotNull GPHContentType gphContentType) { }

            @Override
            public void didSearchTerm(@NotNull String s) { }
        });

        giphyDialogFragment.show(getSupportFragmentManager(), GIPHY_DIALOG_TAG);
    }

    public void showVideoCapture() {

        try {
            Intent videoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String imageFileName = "VID_" + timeStamp + "_" + ".mp4";

            mediaFile = FileClientService.getFilePath(imageFileName, getApplicationContext(), "video/mp4");

            videoFileUri = ALFileProvider.getUriForFile(this, Utils.getMetaDataValue(this, MobiComKitConstants.PACKAGE_NAME) + ".applozic.provider", mediaFile);

            videoIntent.putExtra(MediaStore.EXTRA_OUTPUT, videoFileUri);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                videoIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                videoIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                ClipData clip =
                        ClipData.newUri(getContentResolver(), "a Video", videoFileUri);

                videoIntent.setClipData(clip);
                videoIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                videoIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            } else {
                List<ResolveInfo> resInfoList =
                        getPackageManager()
                                .queryIntentActivities(videoIntent, PackageManager.MATCH_DEFAULT_ONLY);

                for (ResolveInfo resolveInfo : resInfoList) {
                    String packageName = resolveInfo.activityInfo.packageName;
                    grantUriPermission(packageName, videoFileUri,
                            Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                    grantUriPermission(packageName, videoFileUri,
                            Intent.FLAG_GRANT_READ_URI_PERMISSION);

                }
            }

            if (videoIntent.resolveActivity(getApplicationContext().getPackageManager()) != null) {
                if (mediaFile != null) {
                    videoIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0);
                    startActivityForResult(videoIntent, MultimediaOptionFragment.REQUEST_CODE_CAPTURE_VIDEO_ACTIVITY);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public Uri getCurrentImageUri() {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_" + ".jpeg";
        profilePhotoFile = FileClientService.getFilePath(imageFileName, getApplicationContext(), "image/jpeg");
        imageUri = ALFileProvider.getUriForFile(this, Utils.getMetaDataValue(this, MobiComKitConstants.PACKAGE_NAME) + ".applozic.provider", profilePhotoFile);
        return imageUri;
    }


    public void processGalleryPhotoSelection() {
        if (Utils.hasMarshmallow() && PermissionsUtils.checkSelfForStoragePermission(this)) {
            applozicPermission.requestStoragePermissionsForProfilePhoto();
        } else {
            Intent getContentIntent = new Intent(Intent.ACTION_PICK,
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(getContentIntent, ProfileFragment.REQUEST_CODE_ATTACH_PHOTO);
        }
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        this.searchTerm = query;
        return false;
    }

    @Override
    public boolean onQueryTextChange(String query) {
        this.searchTerm = query;
        if (getSearchListFragment() != null) {
            getSearchListFragment().onQueryTextChange(query);
        }
        return true;
    }

    public SearchListFragment getSearchListFragment() {
        return searchListFragment;
    }

    public void setSearchListFragment(SearchListFragment searchListFragment) {
        this.searchListFragment = searchListFragment;
    }


    @Override
    protected void onDestroy() {
        try {
            super.onDestroy();
            isActivityDestroyed = true;

            if (mobiComKitBroadcastReceiver != null) {
                LocalBroadcastManager.getInstance(this).unregisterReceiver(mobiComKitBroadcastReceiver);
            }
            if (connectivityReceiver != null) {
                unregisterReceiver(connectivityReceiver);
            }
            if (accountStatusAsyncTask != null) {
                accountStatusAsyncTask.cancel(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClickOnMessageReply(Message message) {
        if (message != null && conversation != null) {
            conversation.onClickOnMessageReply(message);
        }
    }

    @Override
    public boolean isPermissionGranted() {
        return !PermissionsUtils.checkSelfForStoragePermission(this);
    }

    @Override
    public void checkPermission(ALStoragePermission storagePermission) {
        PermissionsUtils.requestPermissions(this, PermissionsUtils.PERMISSIONS_STORAGE, PermissionsUtils.REQUEST_STORAGE);
        this.alStoragePermission = storagePermission;
    }

    private class SyncMessagesAsyncTask extends AlAsyncTask<Void, Void> {
        MobiComMessageService messageService;

        public SyncMessagesAsyncTask(Context context) {
            messageService = new MobiComMessageService(context, MessageWorker.class);
        }

        protected Void doInBackground() {
            messageService.syncMessages();
            return null;
        }
    }

    @Override
    public void hideSubtitleAndProfilePic() {
        animateToolbarTitle();
        if (toolbarSubtitle != null) {
            toolbarSubtitle.setVisibility(View.GONE);
        }
        if (conversationContactPhoto != null) {
            conversationContactPhoto.setVisibility(View.GONE);
        }
    }

    public class SyncAccountStatusAsyncTask extends AlAsyncTask<Void, Boolean> {
        Context context;
        RegisterUserClientService registerUserClientService;
        String loggedInUserId;
        ApplozicClient applozicClient;
        WeakReference<Snackbar> snackBarWeakReference;
        WeakReference<LinearLayout> linearLayoutWeakReference;

        public SyncAccountStatusAsyncTask(Context context, LinearLayout linearLayout, Snackbar snackbar) {
            this.context = context;
            this.registerUserClientService = new RegisterUserClientService(context);
            this.linearLayoutWeakReference = new WeakReference<LinearLayout>(linearLayout);
            this.snackBarWeakReference = new WeakReference<Snackbar>(snackbar);
            this.applozicClient = ApplozicClient.getInstance(context);
            this.loggedInUserId = MobiComUserPreference.getInstance(context).getUserId();
        }

        @Override
        protected Boolean doInBackground() {
            User applozicUser = new User();
            applozicUser.setUserId(loggedInUserId);
            try {
                registerUserClientService.updateRegisteredAccount(applozicUser);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return true;
        }


        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if (applozicClient.isAccountClosed() || applozicClient.isNotAllowed()) {
                LinearLayout linearLayout = null;
                Snackbar snackbar = null;
                if (snackBarWeakReference != null) {
                    snackbar = snackBarWeakReference.get();
                }
                if (linearLayoutWeakReference != null) {
                    linearLayout = linearLayoutWeakReference.get();
                }
                if (snackbar != null && linearLayout != null) {
                    snackbar = Snackbar.make(linearLayout, applozicClient.isAccountClosed() ?
                                    R.string.applozic_account_closed : R.string.applozic_free_version_not_allowed_on_release_build,
                            Snackbar.LENGTH_INDEFINITE);
                    snackbar.show();
                }
            }
        }
    }
}
