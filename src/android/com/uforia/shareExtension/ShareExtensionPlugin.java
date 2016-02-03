package com.uforia.appList;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Environment;

import org.apache.cordova.CordovaActivity;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * AppList plugin lists all application installed on android device.
 * <p/>
 * Created by Uros Avramovic on 12/18/2014.
 * Requires following permissions:
 * <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
 */
public class AppListPlugin extends CordovaPlugin {

    private CallbackContext CallbackContext = null;
    private String TempDirPath = null;


    /**
     * Executes the request and returns PluginResult.
     *
     * @param action          The action to execute.
     * @param args            JSONArray of arguments for the plugin.
     * @param callbackContext The callback id used when calling back into JavaScript.
     * @return A PluginResult object with a status and message.
     */
    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        createTempDir();
        CallbackContext = callbackContext;
        if (action.equals("listApps")) {
            if (args.length() > 2) {
                CallbackContext.sendPluginResult(new PluginResult(PluginResult.Status.INVALID_ACTION));
            } else {
                try {
                    ArrayList<App> apps = listInstalledApps(args.getBoolean(0), args.getJSONArray(1));

                    executeCallback(apps);
                } catch (JSONException e) {
                    e.printStackTrace();
                    CallbackContext.sendPluginResult(new PluginResult(PluginResult.Status.JSON_EXCEPTION));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        } else if (action.equals("launchApp")) {
            launchApplication(args.getString(0));
        }
        return false;
    }

    private ArrayList<App> listInstalledApps(Boolean includeSystem, JSONArray ExcludeList) throws JSONException, FileNotFoundException, InterruptedException {
        final ArrayList<App> res = new ArrayList<App>();
        final PackageManager packageManager = ((CordovaActivity) this.cordova.getActivity()).getPackageManager();
        final List appList = packageManager.getInstalledPackages(0);

        for (int i = 0; i < appList.size(); i++) {
            final PackageInfo p = (PackageInfo) appList.get(i);

            if ((ApplicationInfo.FLAG_SYSTEM & p.applicationInfo.flags) == 1 && (!includeSystem) || ExcludeList.toString().contains(p.packageName) || packageManager.getLaunchIntentForPackage(p.packageName) == null) {
                continue;
            }

            final App newApp = new App();

            final Activity activity = this.cordova.getActivity();

            newApp.setName(p.applicationInfo.loadLabel(packageManager).toString());
            newApp.setPackageInfo(p.packageName);

            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        newApp.setIconUrl(newApp.saveAppIcon(p.applicationInfo.loadIcon(((CordovaActivity) activity).getPackageManager()), newApp.getName()));
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }

                }
            });

            t.start();
            t.join();
            res.add(newApp);
        }

        return res;
    }

    void createTempDir() {
        File appIcons = new File(Environment.getExternalStorageDirectory() + File.separator + "AppListTempIcons");
        appIcons.mkdirs();
        TempDirPath = appIcons.toString();
    }

    void launchApplication(String packageName) {
        Intent LaunchIntent = ((CordovaActivity) this.cordova.getActivity()).getPackageManager().getLaunchIntentForPackage(packageName);
        this.cordova.getActivity().startActivity(LaunchIntent);

        PluginResult result = new PluginResult(PluginResult.Status.OK);
        CallbackContext.sendPluginResult(result);
    }

    void executeCallback(List<App> apps) {
        JSONArray appList = new JSONArray();
        try {
            for (int i = 0; i < apps.size(); i++) {
                appList.put(i, apps.get(i).toJSON());
            }
        } catch (JSONException e) {
            e.printStackTrace();
            CallbackContext.sendPluginResult(new PluginResult(PluginResult.Status.JSON_EXCEPTION));
        }
        PluginResult result = new PluginResult(PluginResult.Status.OK, appList);
        CallbackContext.sendPluginResult(result);
    }

    private class App {
        private String name;
        private String packageInfo;
        private String iconUrl;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPackageInfo() {
            return packageInfo;
        }

        public void setPackageInfo(String packageInfo) {
            this.packageInfo = packageInfo;
        }

        public String getIconUrl() {
            return iconUrl;
        }

        public void setIconUrl(String iconUrl) {
            this.iconUrl = iconUrl;
        }

        public JSONObject toJSON() throws JSONException {
            JSONObject obj = new JSONObject();
            obj.put("name", this.name);
            obj.put("packageInfo", this.packageInfo);
            obj.put("iconUrl", this.iconUrl);

            return obj;
        }

        public String saveAppIcon(Drawable image, String name) throws FileNotFoundException {
            String path = TempDirPath + File.separator + name.replaceAll("\\s+","") + ".png";
            String response;
            File tempIcon = new File(path);

            if(tempIcon.exists()){
                response = path;
            }
            else {
                Bitmap icon = convertToBitmap(image, image.getMinimumWidth(), image.getMinimumHeight());

                OutputStream stream = new FileOutputStream(path);

                icon.compress(Bitmap.CompressFormat.PNG, 80, stream);
                response = path;
            }

            return response;
        }

        public Bitmap convertToBitmap(Drawable drawable, int widthPixels, int heightPixels) {
            Bitmap mutableBitmap = Bitmap.createBitmap(widthPixels, heightPixels, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(mutableBitmap);
            drawable.setBounds(0, 0, widthPixels, heightPixels);
            drawable.draw(canvas);

            return mutableBitmap;
        }
    }
}