package com.kiss.rssiservice;

import java.util.ArrayList;
import java.util.List;

import com.example.rssiservice.R;

import android.app.ListActivity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.wifi.ScanResult;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

public class MainBkActivity extends ListActivity {
    private WifiService s;

    private ArrayAdapter<ScanResult> adapter;
    private List<ScanResult> wordList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        wordList = new ArrayList<ScanResult>();
        adapter = new ArrayAdapter<ScanResult>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1,
                wordList);

        setListAdapter(adapter);
    }

    private ServiceConnection mConnection = new ServiceConnection() {

        public void onServiceConnected(ComponentName className, IBinder binder) {
            WifiService.WifiBinder b = (WifiService.WifiBinder) binder;
            s = b.getService();
            Toast.makeText(MainBkActivity.this, "set s = WifiService", Toast.LENGTH_SHORT)
                    .show();
        }

        public void onServiceDisconnected(ComponentName className) {
            s = null;
        }
    };

    @Override
    protected void onResume() {
        Log.d("TungLyLog", "onResume");
        super.onResume();
        Intent intent = new Intent(this, WifiService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
//        startService(intent);
    }

    @Override
    protected void onPause() {
        Log.d("TungLyLog", "onPause");
        super.onPause();
        unbindService(mConnection);
    }

    @Override
    public void onDestroy() {
        Log.d("TungLyLog", "onDestroy");
        super.onDestroy();
        stopService(new Intent(getBaseContext(), WifiService.class));
    }

    public void onClick(View view) {
        Log.d("TungLyLog", "onClick");

        if (s != null) {
            Toast.makeText(this,
                    "Number of elements" + s.getListWifis().size(),
                    Toast.LENGTH_SHORT).show();
            wordList.clear();
            wordList.addAll(s.getListWifis());
            adapter.notifyDataSetChanged();
        }
    }
}