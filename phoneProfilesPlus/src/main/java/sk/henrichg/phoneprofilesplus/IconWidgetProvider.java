package sk.henrichg.phoneprofilesplus;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.View;
import android.widget.RemoteViews;

public class IconWidgetProvider extends AppWidgetProvider {

    boolean refreshWidget = true;

    @Override
    public void onUpdate(final Context context, final AppWidgetManager appWidgetManager, final int[] appWidgetIds)
    {
        PPApplication.startHandlerThreadWidget();
        final Handler handler = new Handler(PPApplication.handlerThreadWidget.getLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                String applicationWidgetIconLightness = ApplicationPreferences.applicationWidgetIconLightness(context);
                String applicationWidgetIconColor = ApplicationPreferences.applicationWidgetIconColor(context);
                boolean applicationWidgetIconCustomIconLightness = ApplicationPreferences.applicationWidgetIconCustomIconLightness(context);
                boolean applicationWidgetIconHideProfileName = ApplicationPreferences.applicationWidgetIconHideProfileName(context);
                boolean applicationWidgetIconBackgroundType = ApplicationPreferences.applicationWidgetIconBackgroundType(context);
                String applicationWidgetIconBackgroundColor = ApplicationPreferences.applicationWidgetIconBackgroundColor(context);
                String applicationWidgetIconLightnessB = ApplicationPreferences.applicationWidgetIconLightnessB(context);
                String applicationWidgetIconBackground = ApplicationPreferences.applicationWidgetIconBackground(context);
                boolean applicationWidgetIconShowBorder = ApplicationPreferences.applicationWidgetIconShowBorder(context);
                String applicationWidgetIconLightnessBorder = ApplicationPreferences.applicationWidgetIconLightnessBorder(context);
                boolean applicationWidgetIconRoundedCorners = ApplicationPreferences.applicationWidgetIconRoundedCorners(context);
                String applicationWidgetIconLightnessT = ApplicationPreferences.applicationWidgetIconLightnessT(context);

                int monochromeValue = 0xFF;
                if (applicationWidgetIconLightness.equals("0")) monochromeValue = 0x00;
                if (applicationWidgetIconLightness.equals("25")) monochromeValue = 0x40;
                if (applicationWidgetIconLightness.equals("50")) monochromeValue = 0x80;
                if (applicationWidgetIconLightness.equals("75")) monochromeValue = 0xC0;
                //if (applicationWidgetIconLightness.equals("100")) monochromeValue = 0xFF;

                DataWrapper dataWrapper = new DataWrapper(context,
                        applicationWidgetIconColor.equals("1"),
                        monochromeValue,
                        applicationWidgetIconCustomIconLightness);

                Profile profile = dataWrapper.getActivatedProfile(true, false);

                try {
                    if (!refreshWidget) {
                        String pNameWidget = PPApplication.getWidgetProfileName(context, 1);

                        String pName;
                        if (profile != null)
                            pName = DataWrapper.getProfileNameWithManualIndicatorAsString(profile, true, "", true, false, dataWrapper, false, context);
                        else
                            pName = context.getResources().getString(R.string.profiles_header_profile_name_no_activated);

                        if (pName.equals(pNameWidget)) {
                            PPApplication.logE("IconWidgetProvider.onUpdate", "activated profile NOT changed");
                            return;
                        }
                    }


                    boolean isIconResourceID;
                    String iconIdentifier;
                    String pName;
                    Spannable profileName;
                    if (profile != null) {
                        isIconResourceID = profile.getIsIconResourceID();
                        iconIdentifier = profile.getIconIdentifier();
                        pName = DataWrapper.getProfileNameWithManualIndicatorAsString(profile, false, "", true, true, dataWrapper, false, context);
                        profileName = DataWrapper.getProfileNameWithManualIndicator(profile, false, "", true, true, dataWrapper, false, context);
                    } else {
                        // create empty profile and set icon resource
                        profile = new Profile();
                        profile._name = context.getResources().getString(R.string.profiles_header_profile_name_no_activated);
                        profile._icon = Profile.PROFILE_ICON_DEFAULT + "|1|0|0";

                        profile.generateIconBitmap(context,
                                applicationWidgetIconColor.equals("1"), monochromeValue,
                                applicationWidgetIconCustomIconLightness);
                        isIconResourceID = profile.getIsIconResourceID();
                        iconIdentifier = profile.getIconIdentifier();
                        pName = profile._name;
                        profileName = new SpannableString(profile._name);
                    }

                    PPApplication.setWidgetProfileName(context, 1, pName);

                    // get all IconWidgetProvider widgets in launcher
                    ComponentName thisWidget = new ComponentName(context, IconWidgetProvider.class);
                    int[] allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);

                    for (int widgetId : allWidgetIds) {

                        // prepare view for widget update
                        RemoteViews remoteViews;
                        if (applicationWidgetIconHideProfileName)
                            remoteViews = new RemoteViews(context.getPackageName(), R.layout.icon_widget_no_profile_name);
                        else {
                            if (profile._duration > 0)
                                remoteViews = new RemoteViews(context.getPackageName(), R.layout.icon_widget);
                            else
                                remoteViews = new RemoteViews(context.getPackageName(), R.layout.icon_widget_one_line_text);
                        }

                        // set background
                        int red = 0x00;
                        int green;
                        int blue;
                        if (applicationWidgetIconBackgroundType) {
                            int bgColor = Integer.valueOf(applicationWidgetIconBackgroundColor);
                            red = Color.red(bgColor);
                            green = Color.green(bgColor);
                            blue = Color.blue(bgColor);
                        } else {
                            //if (applicationWidgetIconLightnessB.equals("0")) red = 0x00;
                            if (applicationWidgetIconLightnessB.equals("25")) red = 0x40;
                            if (applicationWidgetIconLightnessB.equals("50")) red = 0x80;
                            if (applicationWidgetIconLightnessB.equals("75")) red = 0xC0;
                            if (applicationWidgetIconLightnessB.equals("100")) red = 0xFF;
                            green = red;
                            blue = red;
                        }
                        int alpha = 0x40;
                        if (applicationWidgetIconBackground.equals("0")) alpha = 0x00;
                        //if (applicationWidgetIconBackground.equals("25")) alpha = 0x40;
                        if (applicationWidgetIconBackground.equals("50")) alpha = 0x80;
                        if (applicationWidgetIconBackground.equals("75")) alpha = 0xC0;
                        if (applicationWidgetIconBackground.equals("100")) alpha = 0xFF;
                        int redBorder = 0xFF;
                        int greenBorder;
                        int blueBorder;
                        if (applicationWidgetIconShowBorder) {
                            if (applicationWidgetIconLightnessBorder.equals("0")) redBorder = 0x00;
                            if (applicationWidgetIconLightnessBorder.equals("25")) redBorder = 0x40;
                            if (applicationWidgetIconLightnessBorder.equals("50")) redBorder = 0x80;
                            if (applicationWidgetIconLightnessBorder.equals("75")) redBorder = 0xC0;
                            //if (applicationWidgetIconLightnessBorder.equals("100")) redBorder = 0xFF;
                        }
                        greenBorder = redBorder;
                        blueBorder = redBorder;
                        if (applicationWidgetIconRoundedCorners) {
                            remoteViews.setViewVisibility(R.id.widget_icon_background, View.VISIBLE);
                            remoteViews.setViewVisibility(R.id.widget_icon_not_rounded_border, View.GONE);
                            if (applicationWidgetIconShowBorder)
                                remoteViews.setViewVisibility(R.id.widget_icon_rounded_border, View.VISIBLE);
                            else
                                remoteViews.setViewVisibility(R.id.widget_icon_rounded_border, View.GONE);
                            remoteViews.setInt(R.id.widget_icon_root, "setBackgroundColor", 0x00000000);
                            remoteViews.setInt(R.id.widget_icon_background, "setColorFilter", Color.argb(0xFF, red, green, blue));
                            //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
                            remoteViews.setInt(R.id.widget_icon_background, "setImageAlpha", alpha);
                            //else
                            //    remoteViews.setInt(R.id.widget_icon_background, "setAlpha", alpha);
                            if (applicationWidgetIconShowBorder)
                                remoteViews.setInt(R.id.widget_icon_rounded_border, "setColorFilter", Color.argb(0xFF, redBorder, greenBorder, blueBorder));
                        } else {
                            remoteViews.setViewVisibility(R.id.widget_icon_background, View.GONE);
                            remoteViews.setViewVisibility(R.id.widget_icon_rounded_border, View.GONE);
                            if (applicationWidgetIconShowBorder)
                                remoteViews.setViewVisibility(R.id.widget_icon_not_rounded_border, View.VISIBLE);
                            else
                                remoteViews.setViewVisibility(R.id.widget_icon_not_rounded_border, View.GONE);
                            remoteViews.setInt(R.id.widget_icon_root, "setBackgroundColor", Color.argb(alpha, red, green, blue));
                            /*remoteViews.setInt(R.id.widget_icon_background, "setColorFilter", 0x00000000);
                            //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
                            remoteViews.setInt(R.id.widget_icon_background, "setImageAlpha", 0);
                            //else
                            //    remoteViews.setInt(R.id.widget_icon_background, "setAlpha", 0);*/
                            if (applicationWidgetIconShowBorder)
                                remoteViews.setInt(R.id.widget_icon_not_rounded_border, "setColorFilter", Color.argb(0xFF, redBorder, greenBorder, blueBorder));
                        }

                        if (isIconResourceID) {
                            if (profile._iconBitmap != null)
                                remoteViews.setImageViewBitmap(R.id.icon_widget_icon, profile._iconBitmap);
                            else {
                                //int iconResource = context.getResources().getIdentifier(iconIdentifier, "drawable", context.getPackageName());
                                int iconResource = Profile.getIconResource(iconIdentifier);
                                remoteViews.setImageViewResource(R.id.icon_widget_icon, iconResource);
                            }
                        } else {
                            remoteViews.setImageViewBitmap(R.id.icon_widget_icon, profile._iconBitmap);
                        }

                        red = 0xFF;
                        if (applicationWidgetIconLightnessT.equals("0")) red = 0x00;
                        if (applicationWidgetIconLightnessT.equals("25")) red = 0x40;
                        if (applicationWidgetIconLightnessT.equals("50")) red = 0x80;
                        if (applicationWidgetIconLightnessT.equals("75")) red = 0xC0;
                        //if (applicationWidgetIconLightnessT.equals("100")) red = 0xFF;
                        green = red;
                        blue = red;
                        remoteViews.setTextColor(R.id.icon_widget_name, Color.argb(0xFF, red, green, blue));

                        if (!applicationWidgetIconHideProfileName)
                            remoteViews.setTextViewText(R.id.icon_widget_name, profileName);

                        // intent for start LauncherActivity on widget click
                        Intent intent = new Intent(context, LauncherActivity.class);
                        // clear all opened activities
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra(PPApplication.EXTRA_STARTUP_SOURCE, PPApplication.STARTUP_SOURCE_WIDGET);
                        PendingIntent pendingIntent = PendingIntent.getActivity(context, 100, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                        remoteViews.setOnClickPendingIntent(R.id.icon_widget_icon, pendingIntent);
                        remoteViews.setOnClickPendingIntent(R.id.icon_widget_name, pendingIntent);

                        // widget update
                        try {
                            appWidgetManager.updateAppWidget(widgetId, remoteViews);
                        } catch (Exception ignored) {}
                    }
                } catch (Exception ignored) {}

                dataWrapper.invalidateDataWrapper();
            }
        });
    }
}
