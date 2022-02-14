package com.shurman.linkbar;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;

import androidx.appcompat.content.res.AppCompatResources;

public class AppLinkKit {
    private static final String NULL_PACKAGE = "NULL";
    private static final int DEFAULT_ICON = android.R.drawable.ic_menu_myplaces;

    protected String _label, _package;
    protected Drawable _icon;

    public AppLinkKit(String label, String pkg, Drawable icon) {
        _label = label;
        _package = pkg;
        _icon = icon;
    }

    public String getLabel() {
        return _label;
    }

    public String getPackage() {
        return _package;
    }

    public Drawable getIcon() {
        return _icon;
    }

    public static AppLinkKit[] createAppLinkKitsFromPackagesNames(Context context, String[] pkgs) {
        PackageManager pm = context.getPackageManager();
        AppLinkKit[] kits = new AppLinkKit[pkgs.length];
        for (int i=0; i<kits.length; i++) {
            try {
                kits[i] = new AppLinkKit(
                        pm.getApplicationInfo(pkgs[i], PackageManager.GET_META_DATA)
                                        .loadLabel(pm).toString(),
                        pkgs[i],
                        pm.getApplicationIcon(pkgs[i])
                );
            } catch (PackageManager.NameNotFoundException e) {
                kits[i] = new AppLinkKit(
                        context.getString(R.string.app_link_label_empty),
                        NULL_PACKAGE,
                        AppCompatResources.getDrawable(context, DEFAULT_ICON));
            }
        }
        return kits;
    }
}
