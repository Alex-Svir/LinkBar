package com.shurman.linkbar;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PackagesListAdapter extends BaseAdapter {
    private final LayoutInflater inflater;
    private final List<Entry> list;

    public PackagesListAdapter(Context context) {
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        list = createAppsList(context);
    }

    private List<Entry> createAppsList(Context context) {
        List<Entry> list = new ArrayList<>();
        PackageManager pm = context.getPackageManager();
        List<ApplicationInfo> ais = pm.getInstalledApplications(PackageManager.GET_META_DATA);
        for (ApplicationInfo ai : ais) {        //      TODO    filters
            if (    ai.enabled &&
                    (ai.flags & (ApplicationInfo.FLAG_SUSPENDED | ApplicationInfo.FLAG_SYSTEM)) == 0
            )
                list.add(new Entry(pm, ai.loadLabel(pm).toString(), ai.packageName));
        }
        Collections.sort(list, (o1, o2) -> o1.getLabel().compareTo(o2.getLabel()));
        return list;
    }

    @Override
    public int getCount() {
        return null==list ? 0 : list.size();
    }

    @Override
    public Object getItem(int position) {
        return null==list ? null : position>=list.size() ? null : list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (null == view)
            view = inflater.inflate(R.layout.item_packs_list,parent,false);
        Entry entry = list.get(position);
        ((TextView) view.findViewById(R.id.line1)).setText(entry.getLabel());
        ((TextView) view.findViewById(R.id.line2)).setText(entry.getPackage());
        ((ImageView) view.findViewById(R.id.icon)).setImageDrawable(entry.getIcon());
        return view;
    }

    public static class Entry extends AppLinkKit {
        private static PackageManager _pm;

        public Entry(PackageManager pm, String label, String pkg) {
            super(label, pkg, null);
            _pm = pm;
        }

        @Override
        public Drawable getIcon() {
            try {
                return null == _icon ? _icon = _pm.getApplicationIcon(_package) : _icon;
            } catch (PackageManager.NameNotFoundException e) {
                return _pm.getDefaultActivityIcon();
            }
        }
    }
}
