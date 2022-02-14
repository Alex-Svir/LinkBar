package com.shurman.linkbar;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.widget.LinearLayout;

import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.content.res.ResourcesCompat;

public class ToggleIcon extends AppCompatImageView {
    private boolean _checked = false;

    public ToggleIcon(Context context) {
        super(context);
    }

    public ToggleIcon(Context context, int id, Drawable icon, String tag) {
        super(context);

        setId(id);
        Resources res = getResources();
        int side = res.getDimensionPixelSize(R.dimen.toggle_icon_size);
        int margin = res.getDimensionPixelSize(R.dimen.toggle_icon_margin);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(side, side, 1.0f);
        params.leftMargin = margin;
        params.rightMargin = margin;
        setLayoutParams(params);
        int pad_tlr = res.getDimensionPixelSize(R.dimen.toggle_icon_padding_tlr);
        setPadding(pad_tlr, pad_tlr, pad_tlr, res.getDimensionPixelSize(R.dimen.toggle_icon_padding_b));
        setImageDrawable(icon);
        setBackground(AppCompatResources.getDrawable(context, R.color.toggle_icon_off));
        setTag(tag);
    }

    public void setChecked(boolean checked) {
        _checked = checked;
        setBackground(ResourcesCompat.getDrawable(
                getResources(),
                checked ? R.color.toggle_icon_on : R.color.toggle_icon_off,
                null
        ));
        refreshDrawableState();
    }

    public boolean isChecked() {
        return _checked;
    }

    public void toggle() {
        setChecked(!_checked);
    }
}
