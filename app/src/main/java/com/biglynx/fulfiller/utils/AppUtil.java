package com.biglynx.fulfiller.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Build;
import android.os.Build.VERSION;
import android.preference.PreferenceManager;
import android.provider.Settings.Secure;
import android.support.v4.BuildConfig;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import com.biglynx.fulfiller.R;
import com.biglynx.fulfiller.ui.EditProfileActivity;
import com.biglynx.fulfiller.ui.InitialScreen;
import com.google.android.gms.vision.barcode.Barcode;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.TimeZone;
import java.util.UUID;

import okhttp3.ResponseBody;


public class AppUtil {
    public static final String APP_VERSION = "1.0-alpha-1";
    public static final String CACHE_DIR_NAME = "__vimeo_v_cache";
    public static String OS_NAME = null;
    public static String OS_VERSION = null;
    public static String PHONE_UNIQUE_ID = null;
    public static final String TAG = "Utils";
    public static Context context;
    private static boolean hadLoginSession;
    public static boolean notifySettings;
    private static ProgressDialog progressDialog;

    static {
        OS_NAME = "Android";
        OS_VERSION = VERSION.RELEASE;
    }

    public static String milliSecondsToTimer(long milliseconds) {
        String finalTimerString = BuildConfig.FLAVOR;
        String secondsString = BuildConfig.FLAVOR;
        int hours = (int) (milliseconds / 3600000);
        int minutes = ((int) (milliseconds % 3600000)) / 60000;
        int seconds = (int) (((milliseconds % 3600000) % 60000) / 1000);
        if (hours > 0) {
            finalTimerString = hours + ":";
        }
        if (seconds < 10) {
            secondsString = "0" + seconds;
        } else {
            secondsString = BuildConfig.FLAVOR + seconds;
        }
        return finalTimerString + minutes + ":" + secondsString;
    }

    /**
     * @method : printKeyHash
     * @description : this method is get hashkey for twitter authentication
     */
    public static String printKeyHash(Activity context) {
        PackageInfo packageInfo;
        String key = null;
        try {
            //getting application package name, as defined in manifest
            String packageName = context.getApplicationContext().getPackageName();

            //Retriving package info
            packageInfo = context.getPackageManager().getPackageInfo(packageName,
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : packageInfo.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                key = new String(Base64.encode(md.digest(), 0));
                Log.e("Key Hash=", "keyhash is " + key);
            }
        } catch (PackageManager.NameNotFoundException e1) {
            Log.e("Name not found", e1.toString());
        } catch (NoSuchAlgorithmException e) {
            Log.e("No such an algorithm", e.toString());
        } catch (Exception e) {
            Log.e("Exception", e.toString());
        }
        return key;
    }

    public static void SlideUP(View view, Context context) {
        view.startAnimation(AnimationUtils.loadAnimation(context,
                R.anim.slid_up));
    }

    public static void SlideDown(View view, Context context) {
        view.startAnimation(AnimationUtils.loadAnimation(context,
                R.anim.slid_down));
    }

    public static int getProgressPercentage(long currentDuration, long totalDuration) {
        Double percentage = Double.valueOf(0.0d);
        return Double.valueOf((((double) ((long) ((int) (currentDuration / 1000)))) / ((double) ((long) ((int) (totalDuration / 1000))))) * 100.0d).intValue();
    }


    public static void toast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public static String uniqueID() {
        return UUID.randomUUID().toString();
    }

    public static void initializeApp(Context ctx) {
        PHONE_UNIQUE_ID = Secure.getString(ctx.getContentResolver(), "android_id");
    }

    public static String getPhoneUniqueID(Context ctx) {
        if (PHONE_UNIQUE_ID == null) {
            PHONE_UNIQUE_ID = Secure.getString(ctx.getContentResolver(), "android_id");
        }
        return PHONE_UNIQUE_ID;
    }

    public static void store(Context ctx, String key, String value) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(ctx);
        if (settings != null) {
            Editor editor = settings.edit();
            if (editor != null) {
                editor.putString(key, value);
                editor.commit();
            }
        }
    }

    public static String getValue(Context ctx, String key) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(ctx);
        if (settings != null && settings.contains(key)) {
            return settings.getString(key, null);
        }
        return null;
    }

    public static String getAppUniqueID(Context ctx) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(ctx);
        if (settings == null) {
            return null;
        }
        if (settings.contains("FIND_ME_APP_ID")) {
            return settings.getString("FIND_ME_APP_ID", "UNKNOWN");
        }
        Editor editor = settings.edit();
        if (editor == null) {
            return "UNKNOWN";
        }
        String id = uniqueID();
        editor.putString("FIND_ME_APP_ID", id);
        editor.commit();
        return id;
    }

    public String getInstallationId(Context ctx) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(ctx);
        if (settings == null) {
            return "UNKNOWN";
        }
        if (settings.contains("InstallId")) {
            return settings.getString("InstallId", "UNKNOWN");
        }
        Editor editor = settings.edit();
        if (editor == null) {
            return "UNKNOWN";
        }
        String id = "ID" + new Random().nextInt(99999999);
        editor.putString("InstallId", id);
        editor.commit();
        return id;
    }

    public static double distance(double lat1, double lon1, double lat2, double lon2) {
        return (1609.344d * ((60.0d * rad2deg(Math.acos((Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2))) + ((Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2))) * Math.cos(deg2rad(lon1 - lon2)))))) * 1.1515d)) * 1.6d;
    }

    private static double deg2rad(double deg) {
        return (3.141592653589793d * deg) / 180.0d;
    }

    private static double rad2deg(double rad) {
        return (180.0d * rad) / 3.141592653589793d;
    }

    public static void copyStream(InputStream is, OutputStream os) {
        try {
            byte[] bytes = new byte[Barcode.UPC_E];
            while (true) {
                int count = is.read(bytes, 0, Barcode.UPC_E);
                if (count != -1) {
                    os.write(bytes, 0, count);
                } else {
                    return;
                }
            }
        } catch (Exception e) {
        }
    }

    public static boolean hasLoginSession() {
        return hadLoginSession;
    }

    public static void setHasLoginSession(boolean hasLoginSession) {
        hadLoginSession = hasLoginSession;
    }

    public static void showProgressDialog(Context mContext, String msg) {
        try {
            if (progressDialog != null && progressDialog.isShowing())
                return;

            progressDialog = new ProgressDialog(mContext);
            progressDialog.setCancelable(false);
            progressDialog.setMessage(msg);
            progressDialog.show();
        } catch (Exception e) {
        }
    }

    public static void hideProgressDialog() {
        try {
            if (progressDialog != null && progressDialog.isShowing())
                progressDialog.dismiss();
        } catch (Exception e) {
        }
    }

    public static void parseErrorMessage(Context context, String jsonString) {
        if (TextUtils.isEmpty(jsonString)) {
            AppUtil.toast(context, Constants.OOPS_SOMETHING_WENT_WRONG);
            return;
        }
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            String message = jsonObject.optString("Message");
            if (!TextUtils.isEmpty(message)) {
                AppUtil.toast(context, message);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void parseErrorMessage(Context context, ResponseBody responseBody) {

        try {
            if (responseBody == null || TextUtils.isEmpty(responseBody.string())) {
                return;
            }
            try {
                JSONObject jsonObject = new JSONObject(responseBody.string());
                String message = jsonObject.optString("Message");
                if (!TextUtils.isEmpty(message)) {
                    AppUtil.toast(context, message);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    public static String getTwoDecimals(Object d) {
        return String.format("%.2f", d);
    }

    public static boolean ifNotEmpty(String string) {
        if (string != null && string.trim().length() > 0 && !(string.contains("null")))
            return true;
        else
            return false;
    }

    public static String getLocalDateFormat(String dateString) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'kk:mm:ss");
        df.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date result = null;
        try {
            result = df.parse(dateString);

            Calendar cal = Calendar.getInstance();
            cal.setTime(result);
            int month = cal.get(Calendar.DAY_OF_MONTH);
            String suffix = getDayNumberSuffix(month);

            SimpleDateFormat resultDF = new SimpleDateFormat("EEE MMM dd'" + suffix + "' hh:mm a");
            resultDF.setTimeZone(TimeZone.getDefault());

            String format = resultDF.format(result);

            System.out.println(format);
            return format;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        System.out.println(result);
        return " ";
    }

    public static String getDayNumberSuffix(int day) {
        if (day >= 11 && day <= 13) {
            return "th";
        }
        switch (day % 10) {
            case 1:
                return "st";
            case 2:
                return "nd";
            case 3:
                return "rd";
            default:
                return "th";
        }
    }

    public static String formatTodayDate(Date date){
        try {
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            int month = cal.get(Calendar.DAY_OF_MONTH);
            String suffix = getDayNumberSuffix(month);
            SimpleDateFormat displayDateFormat = new SimpleDateFormat("EEE MMM dd'" + suffix + "' hh:mm:ss a yyyy");
            displayDateFormat.setTimeZone(TimeZone.getDefault());
            String result = displayDateFormat.format(date);
            return result;
        }catch (Exception e) {
            e.printStackTrace();
        }
        return " ";
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static void CheckErrorCode(Activity context, int responseCode) {

        //AuthToken expires per an hour.401 error code resembles authToken expiry error.
        // So checking error code and redirecting the user to login.
        if (responseCode == 401) {
            Common.disMissDialog();
            AppPreferences.getInstance(context).setSignInResult(null);
            context.startActivity(new Intent(context, InitialScreen.class));
            context.finishAffinity();
        }
    }

    public static String convertIntoMiles(double kilomtrs) {
        double miles = kilomtrs * 0.6213;
        return String.format(".%2f", miles);
    }

    public static String convertIntoMinutes(String seconds) {
        if (seconds.equals("0") || seconds == null
                || seconds.length() == 0 || seconds.equals("null")) {
            return "0";
        }
        double min = Double.parseDouble(seconds) / 60;
        return String.format(".%2f", min);
    }
}
