package cn.edu.pku.wangxue.service;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;


import java.io.Serializable;
import java.util.Timer;
import java.util.TimerTask;

import cn.edu.pku.wangxue.bean.TodayWeather;
import cn.edu.pku.wangxue.util.CommonUtil;

public class MyService extends Service {

    private Timer timer = new Timer();
    // 设置每5s更新一次数据
    private static final int UPDATE_INTERVAL = 5000;
    private String log_tag = "";

    public MyService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                Log.i(log_tag, "service is running...");
                SharedPreferences sharedPreferences = getSharedPreferences("config", MODE_PRIVATE);
                String cityCode = sharedPreferences.getString("main_city_code", "101010100");
                TodayWeather todayWeather = CommonUtil.queryWeather(cityCode);
                // 发送广播
                Intent broadcastIntent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putSerializable("newWeather", (Serializable) todayWeather);
                broadcastIntent.putExtras(bundle);
                broadcastIntent.setAction("Data_Update_Action");
                getBaseContext().sendBroadcast(broadcastIntent);
            }
        }, 0, UPDATE_INTERVAL);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(timer != null){
            timer.cancel();
        }
    }
}
