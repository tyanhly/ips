//package com.kiss.rssiservice;
//
//import android.app.Service;
//import android.content.BroadcastReceiver;
//import android.content.Context;
//import android.content.Intent;
//import android.os.Binder;
//import android.os.IBinder;
//
//import com.kiss.ips.entity.Position;
//
//public class PositionService extends Service {
//    private final IBinder _mBinder = new PositionBinder();
//
//
//    /** Gets called by the system once we have the scan results. */
//    BroadcastReceiver acceleratorReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//        }
//    };
//    
//    /** Called when the service is being created. */
//    @Override
//    public void onCreate() {
//    
//    }
//
//    @Override
//    public int onStartCommand(Intent intent, int flags, int startId) {
//        return Service.START_NOT_STICKY;
//    }
//
//    /** Called when The service is no longer used and is being destroyed */
//    @Override
//    public void onDestroy() {
//
//    }
//
//    @Override
//    public IBinder onBind(Intent arg0) {
//        return _mBinder;
//    }
//
//    public class PositionBinder extends Binder {
//        public PositionService getService() {
//            return PositionService.this;
//        }
//    }
//
//    public Position getCurrentPosition() {
//        /**
//         * @TODO
//         */
//        return new Position();
//    }
//
//}
