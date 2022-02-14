package com.shurman.linkbar;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.LinearLayout;

public class ToggleSet implements View.OnClickListener {
    public interface OnStateChangeListener {
        void onSelectedIconChanged(int selectedId);
        void onLinksSetChanged(String[] packagesNamesArray);
    }

    public static final int NOTHING_SELECTED_ID = -1;

    private OnStateChangeListener listener;
    private final ToggleIcon[] buttons;
    private int selectedId = NOTHING_SELECTED_ID;
    private String backupPkgName;
    private Drawable backupIcon;

    public ToggleSet(Activity activity, AppLinkKit[] linkKits) {
        //  create icons container
        LinearLayout toggleGroup = activity.findViewById(R.id.toggle_group);
        //  create icons array
        buttons = new ToggleIcon[linkKits.length];
        //  fill icons array and container
        for (int i=0; i<buttons.length; i++) {
            toggleGroup.addView(buttons[i] = new ToggleIcon(
                                                    activity,
                                                    i,
                                                    linkKits[i].getIcon(),
                                                    linkKits[i].getPackage()
                                                )
            );
            buttons[i].setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View view) {
        ToggleIcon tIcon = (ToggleIcon) view;
        tIcon.toggle();
        //  nothing was selected => now 'view' is selected
        if (NOTHING_SELECTED_ID == selectedId) {
            selectedId = view.getId();
        }
        //  'view' was selected => now nothing is selected
        else if (!tIcon.isChecked()) {
            //save changes for ToggleIcon that is being turning unselected
            applyLookOfSelectedIcon();
            selectedId = NOTHING_SELECTED_ID;
        }
        //  different ToggleIcon was selected => selection switched to 'view'
        else {
            discardSelectedIconChanges();
            buttons[selectedId].setChecked(false);
            selectedId = view.getId();
        }
        if (listener != null) listener.onSelectedIconChanged(selectedId);
    }

    public void setOnStateChangedListener(OnStateChangeListener listener) {
        this.listener = listener;
    }

    public void changeSelectedButton(String pkgName, Drawable icon) {
        setNewLookOfSelectedIconAndSaveBackup(pkgName,icon);
    }

    private String[] createPackagesNamesArray() {
        String[] pkgs = new String[buttons.length];
        for (int i=0; i< buttons.length; i++) {
            pkgs[i] = buttons[i].getTag().toString();
        }
        return pkgs;
    }

    private void redrawSelectedIcon(String pkgName, Drawable icon) {
        if (nothingSelected()) return;
        ToggleIcon tIcon = buttons[selectedId];
        if (null != pkgName) tIcon.setTag(pkgName);
        if (null != icon) tIcon.setImageDrawable(icon);
    }

    private void setNewLookOfSelectedIconAndSaveBackup(String pkgName, Drawable icon) {
        if (nothingSelected()) return;
        //  save icon state at the FIRST change after selection when backup is clear
        if (null == backupPkgName && null == backupIcon) {
            ToggleIcon tIcon = buttons[selectedId];
            Object tag = tIcon.getTag();
            if (null != tag)
                backupPkgName = tag.toString();
            backupIcon = tIcon.getDrawable();
        }
        redrawSelectedIcon(pkgName, icon);
    }

    private void applyLookOfSelectedIcon() {
        if (nothingSelected()) return;
        //  if backup is clear - nothing was changed
        if (null == backupPkgName && null == backupIcon) return;
        //  clearing backup and reporting changes
        backupPkgName = null;
        backupIcon = null;
        if (listener != null) listener.onLinksSetChanged(createPackagesNamesArray());
    }

    private void discardSelectedIconChanges() {
        if (nothingSelected()) return;
        //  restore backup
        redrawSelectedIcon(backupPkgName, backupIcon);
        backupPkgName = null;
        backupIcon = null;
    }

    private boolean nothingSelected() {
        return selectedId == NOTHING_SELECTED_ID;
    }
}
