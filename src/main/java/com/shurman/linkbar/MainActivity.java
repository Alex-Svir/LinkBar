package com.shurman.linkbar;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.lang.ref.WeakReference;

public class MainActivity extends AppCompatActivity {
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ToggleSet toggles = new ToggleSet(
                this,
                AppLinkKit.createAppLinkKitsFromPackagesNames(
                        this,
                        AppPreferences.getBarPackagesNamesArray(this)
                )
        );

        ListView packagesList = findViewById(R.id.lv_packs);
        packagesList.setOnItemClickListener((parent, view, position, id) -> {
            PackagesListAdapter.Entry entry =
                    (PackagesListAdapter.Entry) parent.getItemAtPosition(position);
            toggles.changeSelectedButton(entry.getPackage(), entry.getIcon());
        });

        toggles.setOnStateChangedListener(new ToggleSet.OnStateChangeListener() {
            @Override
            public void onSelectedIconChanged(int selectedId) {
                if (selectedId != ToggleSet.NOTHING_SELECTED_ID &&
                                        packagesList.getCount() == 0) {
                    runPackagesListLoading();
                }
            }

            @Override
            public void onLinksSetChanged(String[] packagesNamesArray) {
                AppPreferences.saveBarPackagesNamesArray(
                        MainActivity.this,
                        packagesNamesArray
                );
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public void clickMenuShow(MenuItem item) {
        showLinkBar();
    }

    public void clickMenuHide(MenuItem item) {
        hideLinkBar();
    }

    public void clickMenuConfig(MenuItem item) {
        runPackagesListLoading();
    }

    public void clickFab(View view) {
        showLinkBar();
    }

    private void showLinkBar() {
        LinkNotification.clear(this);
        AppLinkKit[] appLinkKits = AppLinkKit.createAppLinkKitsFromPackagesNames(
                this,
                AppPreferences.getBarPackagesNamesArray(this)
        );
        LinkNotification.show(this, appLinkKits);
    }

    private void hideLinkBar() {
        LinkNotification.clear(this);
    }

    private void runPackagesListLoading() {
        AppsLoadHandler handler = new AppsLoadHandler(this);
        ((ProgressBar) findViewById(R.id.progress_bar)).setVisibility(ProgressBar.VISIBLE);
        new Thread(() -> {
            Message msg = new Message();
            msg.obj = new PackagesListAdapter(MainActivity.this);
            handler.sendMessage(msg);
        }).start();
    }

    private void onAppsListLoaded(Object obj) {
        ((ProgressBar) findViewById(R.id.progress_bar)).setVisibility(ProgressBar.INVISIBLE);
        ((ListView) findViewById(R.id.lv_packs)).setAdapter((PackagesListAdapter) obj);
    }

    private static class AppsLoadHandler extends Handler {
        private final WeakReference<MainActivity> activity;

        public AppsLoadHandler(MainActivity activity) {
            super();
            this.activity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            MainActivity activity = this.activity.get();
            if (activity != null)
                activity.onAppsListLoaded(msg.obj);
        }
    }
}
