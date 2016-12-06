package cn.edu.pku.wangxue.miniweather;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


import cn.edu.pku.wangxue.bean.TodayWeather;
import cn.edu.pku.wangxue.service.MyService;
import cn.edu.pku.wangxue.util.NetUtil;

import static cn.edu.pku.wangxue.miniweather.R.drawable.biz_plugin_weather_0_50;
import static cn.edu.pku.wangxue.miniweather.R.drawable.biz_plugin_weather_101_150;
import static cn.edu.pku.wangxue.miniweather.R.drawable.biz_plugin_weather_151_200;
import static cn.edu.pku.wangxue.miniweather.R.drawable.biz_plugin_weather_201_300;
import static cn.edu.pku.wangxue.miniweather.R.drawable.biz_plugin_weather_51_100;
import static cn.edu.pku.wangxue.miniweather.R.drawable.biz_plugin_weather_baoxue;
import static cn.edu.pku.wangxue.miniweather.R.drawable.biz_plugin_weather_baoyu;
import static cn.edu.pku.wangxue.miniweather.R.drawable.biz_plugin_weather_dabaoyu;
import static cn.edu.pku.wangxue.miniweather.R.drawable.biz_plugin_weather_daxue;
import static cn.edu.pku.wangxue.miniweather.R.drawable.biz_plugin_weather_dayu;
import static cn.edu.pku.wangxue.miniweather.R.drawable.biz_plugin_weather_duoyun;
import static cn.edu.pku.wangxue.miniweather.R.drawable.biz_plugin_weather_greater_300;
import static cn.edu.pku.wangxue.miniweather.R.drawable.biz_plugin_weather_leizhenyu;
import static cn.edu.pku.wangxue.miniweather.R.drawable.biz_plugin_weather_leizhenyubingbao;
import static cn.edu.pku.wangxue.miniweather.R.drawable.biz_plugin_weather_qing;
import static cn.edu.pku.wangxue.miniweather.R.drawable.biz_plugin_weather_shachenbao;
import static cn.edu.pku.wangxue.miniweather.R.drawable.biz_plugin_weather_tedabaoyu;
import static cn.edu.pku.wangxue.miniweather.R.drawable.biz_plugin_weather_wu;
import static cn.edu.pku.wangxue.miniweather.R.drawable.biz_plugin_weather_xiaoxue;
import static cn.edu.pku.wangxue.miniweather.R.drawable.biz_plugin_weather_xiaoyu;
import static cn.edu.pku.wangxue.miniweather.R.drawable.biz_plugin_weather_yin;
import static cn.edu.pku.wangxue.miniweather.R.drawable.biz_plugin_weather_yujiaxue;
import static cn.edu.pku.wangxue.miniweather.R.drawable.biz_plugin_weather_zhenxue;
import static cn.edu.pku.wangxue.miniweather.R.drawable.biz_plugin_weather_zhenyu;
import static cn.edu.pku.wangxue.miniweather.R.drawable.biz_plugin_weather_zhongxue;
import static cn.edu.pku.wangxue.miniweather.R.drawable.biz_plugin_weather_zhongyu;

/**
 * Created by wx on 2016-09-20-0020.
 */
public class MainActivity extends Activity implements View.OnClickListener{
    private static final int UPDATE_TODAY_WEATHER = 1 ;
    private ImageView mUpdateBtn;
    private ImageView mCitySelect;
    private TextView cityTv,timeTv,humidityTv,weekTv,pmDataTv,pmQualityTv,temperatureTV,climateTv,windTv,city_name_Tv,wenduTV;
    private ImageView weatherImg,pmImg;

    private TextView[] Otherdatatv , Otherwendutv ,Otherfengtv , Othertypetv;
    private static final int ERROR = -1;
    private static final int REQUEST_CODE = 1;
    private TodayWeather mTodayWeather;
    //viewpager
    private ViewPagerAdapter vpa;
    private ViewPager vp;
    private List<View> Views;
    private Handler mHandler = new Handler(){
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case UPDATE_TODAY_WEATHER :
                    mTodayWeather = (TodayWeather) msg.obj;
                    updateTodayWeather(mTodayWeather);
                    mUpdateBtn.setClickable(true);
                    //停止旋转
                    mUpdateBtn.clearAnimation();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weather_info);
        if (NetUtil.getNetworkState(this) != NetUtil.NETWORN_NONE){
            Log.d("myWeather","网络OK");
            Toast.makeText(MainActivity.this, "网络OK!", Toast.LENGTH_SHORT).show();
        }else
        {
            Log.d("myWeather","网络挂了");
            Toast.makeText(MainActivity.this,"网络挂了！",Toast.LENGTH_LONG).show();
        }
        mUpdateBtn = (ImageView) findViewById(R.id.title_update_btn);
        mUpdateBtn.setOnClickListener(this);
        mCitySelect = (ImageView) findViewById(R.id.title_city_manager);
        mCitySelect.setOnClickListener(this);
        // 在应用启动时，启动"定时更新数据"的service服务
        startService(new Intent(getBaseContext(), MyService.class));
        //initView();

       // initEvent();
        initViewpager();
    }


//    private void initEvent() {
//        mUpdateBtn.setOnClickListener(this);
//        mCitySelect.setOnClickListener(this);
//    }


    public void onStart(){
        super.onStart();
        TodayWeather todayWeather = null;
        SharedPreferences sharedPreferences = getSharedPreferences("config", MODE_PRIVATE);
        String Default = sharedPreferences.getString("DefaultWeather3","NO_info");
//
        initView();
        Log.d("good",Default);
        if(Default!="NO_info") {
            todayWeather = parseXML(Default);
            if (todayWeather != null) {
                Log.d("myWeather", todayWeather.toString());
                Message msg = new Message();
                msg.what = UPDATE_TODAY_WEATHER;
                msg.obj = todayWeather;
                mHandler.sendMessage(msg);
            }
        }
//
    }

    /*初始化viewpager*/
    private void initViewpager(){
        LayoutInflater inflater = LayoutInflater.from(this);
        Views = new ArrayList<View>();
        //
        Views.add(inflater.inflate(R.layout.page4,null));
        Views.add(inflater.inflate(R.layout.page5,null));
        //
        vpa = new ViewPagerAdapter(Views,this);
        vp = (ViewPager) findViewById(R.id.viewpager);
        vp.setAdapter(vpa);
        //  vp.setOnPageChangeListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.title_city_manager)
        {
            Intent i = new Intent(this, SelectCity.class);
           // startActivity(i);
            startActivityForResult(i,1);
        }

        if (view.getId() == R.id.title_update_btn){

            SharedPreferences sharePreferences = getSharedPreferences("config",MODE_PRIVATE);
            String cityCode = sharePreferences.getString("main_city_code","101010100");
            Log.d("myWeather",cityCode);
            view.setClickable(false);

                queryWeatherCode(cityCode);
                //开始旋转
                rotate();

            if (NetUtil.getNetworkState(this) != NetUtil.NETWORN_NONE){
                Log.d("myWeather","网络OK");
                queryWeatherCode(cityCode);
            }else
            {
                Log.d("myWeather","网络挂了");
                Toast.makeText(MainActivity.this,"网络挂了！",Toast.LENGTH_LONG).show();
            }
        }
    }

    public void rotate(){
        Animation operatingAnim = AnimationUtils.loadAnimation(this, R.anim.rotate);
        LinearInterpolator lin = new LinearInterpolator();
        operatingAnim.setInterpolator(lin);
        if (operatingAnim != null) {
            mUpdateBtn.startAnimation(operatingAnim);
        }
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode == RESULT_OK) {
            String newCityCode= data.getStringExtra("cityCode");
            Log.d("myWeather", "选择的城市代码为  "+newCityCode);
            if (NetUtil.getNetworkState(this) != NetUtil.NETWORN_NONE) {
                Log.d("myWeather", "网络OK");
                if(newCityCode != null) {
                    queryWeatherCode(newCityCode);
                }
            } else {
                Log.d("myWeather", "网络挂了");
                Toast.makeText(MainActivity.this, "网络挂了！", Toast.LENGTH_LONG).show();
            }
        }
    }


    /**
     *
     * @param  cityCode
     */
    private void queryWeatherCode(String cityCode) {
        final String address ="http://wthrcdn.etouch.cn/WeatherApi?citykey="+cityCode;
        Log.d("myWeather",address);
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection con = null ;
                TodayWeather todayWeather = null ;
                try{
                    URL url = new URL(address);
                    con =(HttpURLConnection)url.openConnection();
                    con.setRequestMethod("GET");
                    con.setConnectTimeout(8000);
                    con.setReadTimeout(8000);
                    InputStream in = con.getInputStream();
                    BufferedReader reader = new BufferedReader( new InputStreamReader(in));
                    StringBuilder response = new StringBuilder();
                    String str ;
                    while((str=reader.readLine()) != null){
                        response.append(str);
                        Log.d("myWeather",str);
                    }
                    String responseStr = response.toString();

                    if(responseStr!=null){
                        SharedPreferences mySharedPreferences = getSharedPreferences("config",Activity.MODE_PRIVATE);
                        SharedPreferences.Editor editor = mySharedPreferences.edit();
                        editor.putString("DefaultWeather3",responseStr);
                        editor.commit();
                    }
                    Log.d("myWeather",responseStr);

                    todayWeather = parseXML(responseStr);
                    if(todayWeather != null){
                        Log.d("myWeather",todayWeather.toString());

                        Message msg = new Message();
                        msg.what = UPDATE_TODAY_WEATHER ;
                        msg.obj = todayWeather ;
                        mHandler.sendMessage(msg);
                    }

                }catch (Exception e){
                    e.printStackTrace();
                }finally {
                    if(con != null){
                        con.disconnect();
                    }
                }
            }
        }).start();
    }

    private TodayWeather parseXML(String xmldata) {
        TodayWeather todayWeather = null;
        int fengxiangCount = 0 ;
        int fengliCount = 0 ;
        int dateCount = 0 ;
        int highCount = 0 ;
        int lowCount = 0 ;
        int typeCount = 0 ;
        try {
            XmlPullParserFactory fac = XmlPullParserFactory.newInstance();
            XmlPullParser xmlPullParser = fac.newPullParser();
            xmlPullParser.setInput(new StringReader(xmldata));
            int eventType = xmlPullParser.getEventType();
            Log.d("myWeather","parseXML");
            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    //判断当前事件是否为文档开始事件
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    //判断当前事件是否为标签元素开始事件
                    case XmlPullParser.START_TAG:
                        if (xmlPullParser.getName().equals("resp")) {
                            todayWeather = new TodayWeather();
                        }
                        if (todayWeather != null) {

                            if (xmlPullParser.getName().equals("city")) {
                                eventType = xmlPullParser.next();
                                todayWeather.setCity(xmlPullParser.getText());
                            } else if (xmlPullParser.getName().equals("updatetime")) {
                                eventType = xmlPullParser.next();
                                todayWeather.setUpdatetime(xmlPullParser.getText());
                            } else if (xmlPullParser.getName().equals("shidu")) {
                                eventType = xmlPullParser.next();
                                todayWeather.setShidu(xmlPullParser.getText());
                            } else if (xmlPullParser.getName().equals("wendu")) {
                                eventType = xmlPullParser.next();
                                todayWeather.setWendu(xmlPullParser.getText());
                            } else if (xmlPullParser.getName().equals("pm25")) {
                                eventType = xmlPullParser.next();
                                todayWeather.setPm25(xmlPullParser.getText());
                            } else if (xmlPullParser.getName().equals("quality")) {
                                eventType = xmlPullParser.next();
                                todayWeather.setQuality(xmlPullParser.getText());
                            } else if (xmlPullParser.getName().equals("fengxiang")) {
                                eventType = xmlPullParser.next();
                                todayWeather.setFengxiang(xmlPullParser.getText());
                                fengxiangCount++;
                                switch(fengxiangCount-1){
                                    case 0:
                                        todayWeather.setDate(xmlPullParser.getText());
                                        break;
                                    case 1:
                                        todayWeather.other1.setFengxiang(xmlPullParser.getText());
                                        break;
                                    case 2:
                                        todayWeather.other2.setFengxiang(xmlPullParser.getText());
                                        break;
                                    case 3:
                                        todayWeather.other3.setFengxiang(xmlPullParser.getText());
                                        break;
                                    case 4:
                                        todayWeather.other4.setFengxiang(xmlPullParser.getText());
                                        break;
                                    case 5:
                                        todayWeather.other5.setFengxiang(xmlPullParser.getText());
                                        break;
                                    case 6:
                                        todayWeather.other6.setFengxiang(xmlPullParser.getText());
                                        break;
                                }
                            } else if (xmlPullParser.getName().equals("fengli") && fengliCount == 0) {
                                eventType = xmlPullParser.next();
                                todayWeather.setFengli(xmlPullParser.getText());
                                fengliCount++;
                            } else if (xmlPullParser.getName().equals("date")) {
                                eventType = xmlPullParser.next();
                                todayWeather.setDate(xmlPullParser.getText());
                                dateCount++;
                                switch(dateCount-1){
                                    case 0:
                                        todayWeather.setDate(xmlPullParser.getText());
                                        break;
                                    case 1:
                                        todayWeather.other1.setDate(xmlPullParser.getText());
                                        break;
                                    case 2:
                                        todayWeather.other2.setDate(xmlPullParser.getText());
                                        break;
                                    case 3:
                                        todayWeather.other3.setDate(xmlPullParser.getText());
                                        break;
                                    case 4:
                                        todayWeather.other4.setDate(xmlPullParser.getText());
                                        break;
                                    case 5:
                                        todayWeather.other5.setDate(xmlPullParser.getText());
                                        break;
                                    case 6:
                                        todayWeather.other6.setDate(xmlPullParser.getText());
                                        break;
                                }
                            } else if (xmlPullParser.getName().equals("high")) {
                                eventType = xmlPullParser.next();
                                todayWeather.setHigh(xmlPullParser.getText());
                                highCount++;
                                switch(highCount-1){
                                    case 0:
                                        todayWeather.setHigh(xmlPullParser.getText().substring(2).trim());
                                        break;
                                    case 1:
                                        todayWeather.other1.setHigh(xmlPullParser.getText().substring(2).trim());
                                        break;
                                    case 2:
                                        todayWeather.other2.setHigh(xmlPullParser.getText().substring(2).trim());
                                        break;
                                    case 3:
                                        todayWeather.other3.setHigh(xmlPullParser.getText().substring(2).trim());
                                        break;
                                    case 4:
                                        todayWeather.other4.setHigh(xmlPullParser.getText().substring(2).trim());
                                        break;
                                    case 5:
                                        todayWeather.other5.setHigh(xmlPullParser.getText().substring(2).trim());
                                        break;
                                    case 6:
                                        todayWeather.other6.setHigh(xmlPullParser.getText().substring(2).trim());
                                        break;
                                }
                            } else if (xmlPullParser.getName().equals("low")) {
                                eventType = xmlPullParser.next();
                                todayWeather.setLow(xmlPullParser.getText());
                                lowCount++;
                                switch(lowCount-1){
                                    case 0:
                                        todayWeather.setLow(xmlPullParser.getText().substring(2).trim());
                                        break;
                                    case 1:
                                        todayWeather.other1.setLow(xmlPullParser.getText().substring(2).trim());
                                        break;
                                    case 2:
                                        todayWeather.other2.setLow(xmlPullParser.getText().substring(2).trim());
                                        break;
                                    case 3:
                                        todayWeather.other3.setLow(xmlPullParser.getText().substring(2).trim());
                                        break;
                                    case 4:
                                        todayWeather.other4.setLow(xmlPullParser.getText().substring(2).trim());
                                        break;
                                    case 5:
                                        todayWeather.other5.setLow(xmlPullParser.getText().substring(2).trim());
                                        break;
                                    case 6:
                                        todayWeather.other6.setLow(xmlPullParser.getText().substring(2).trim());
                                        break;
                                }
                            } else if (xmlPullParser.getName().equals("type")) {
                                eventType = xmlPullParser.next();
                                todayWeather.setType(xmlPullParser.getText());
                                typeCount++;
                                switch(typeCount-1){
                                    case 0:
                                        todayWeather.setType(xmlPullParser.getText());
                                        break;
                                    case 1:
                                        todayWeather.other1.setType(xmlPullParser.getText());
                                        break;
                                    case 2:
                                        todayWeather.other2.setType(xmlPullParser.getText());
                                        break;
                                    case 3:
                                        todayWeather.other3.setType(xmlPullParser.getText());
                                        break;
                                    case 4:
                                        todayWeather.other4.setType(xmlPullParser.getText());
                                        break;
                                    case 5:
                                        todayWeather.other5.setType(xmlPullParser.getText());
                                        break;
                                    case 6:
                                        todayWeather.other6.setType(xmlPullParser.getText());
                                        break;
                                }
                            }
                        }
                        break;
                    //判断当前事件是否为标签元素结束事件
                    case XmlPullParser.END_TAG:
                        break;
                }
                //进入下一个元素并触发相应事件
                eventType = xmlPullParser.next();
            }
        }catch (XmlPullParserException e) {
            e.printStackTrace();
        }catch (IOException e) {
            e.printStackTrace();
        }
        return todayWeather ;
    }


    void updateTodayWeather(TodayWeather todayWeather){
        city_name_Tv.setText(todayWeather.getCity()+"天气");
        cityTv.setText(todayWeather.getCity());
        timeTv.setText(todayWeather.getUpdatetime()+"发布");
        wenduTV.setText(todayWeather.getWendu()+"℃");
        humidityTv.setText("湿度："+todayWeather.getShidu());
        pmDataTv.setText(todayWeather.getPm25());

        if(todayWeather.getPm25()!=null) {
            int pm25 = Integer.parseInt(todayWeather.getPm25());

            if (pm25 <= 50) {
                pmImg.setImageResource(biz_plugin_weather_0_50);
            } else if (pm25 <= 100) {
                pmImg.setImageResource(biz_plugin_weather_51_100);
            } else if (pm25 <= 150) {
                pmImg.setImageResource(biz_plugin_weather_101_150);
            } else if (pm25 <= 200) {
                pmImg.setImageResource(biz_plugin_weather_151_200);
            } else if (pm25 <= 300) {
                pmImg.setImageResource(biz_plugin_weather_201_300);
            } else {
                pmImg.setImageResource(biz_plugin_weather_greater_300);
            }
        }
        String wethType =todayWeather.getType();

        switch (wethType){
            case "暴雪":
                weatherImg.setImageResource(biz_plugin_weather_baoxue);
                break;
            case "暴雨":
                weatherImg.setImageResource(biz_plugin_weather_baoyu);
                break;
            case "大暴雨":
                weatherImg.setImageResource(biz_plugin_weather_dabaoyu);
                break;
            case "大雪":
                weatherImg.setImageResource(biz_plugin_weather_daxue);
                break;
            case "大雨":
                weatherImg.setImageResource(biz_plugin_weather_dayu);
                break;
            case "多云":
                weatherImg.setImageResource(biz_plugin_weather_duoyun);
                break;
            case "雷阵雨":
                weatherImg.setImageResource(biz_plugin_weather_leizhenyu);
                break;
            case "雷阵雨冰雹":
                weatherImg.setImageResource(biz_plugin_weather_leizhenyubingbao);
                break;
            case "晴":
                weatherImg.setImageResource(biz_plugin_weather_qing);
                break;
            case "沙尘暴":
                weatherImg.setImageResource(biz_plugin_weather_shachenbao);
                break;
            case "特大暴雨":
                weatherImg.setImageResource(biz_plugin_weather_tedabaoyu);
                break;
            case "雾":
                weatherImg.setImageResource(biz_plugin_weather_wu);
                break;
            case "小雪":
                weatherImg.setImageResource(biz_plugin_weather_xiaoxue);
                break;
            case "小雨":
                weatherImg.setImageResource(biz_plugin_weather_xiaoyu);
                break;
            case "阴":
                weatherImg.setImageResource(biz_plugin_weather_yin);
                break;
            case "雨夹雪":
                weatherImg.setImageResource(biz_plugin_weather_yujiaxue);
                break;
            case "阵雪":
                weatherImg.setImageResource(biz_plugin_weather_zhenxue);
                break;
            case "阵雨":
                weatherImg.setImageResource(biz_plugin_weather_zhenyu);
                break;
            case "中雪":
                weatherImg.setImageResource(biz_plugin_weather_zhongxue);
                break;
            case "中雨":
                weatherImg.setImageResource(biz_plugin_weather_zhongyu);
                break;

        }

        pmQualityTv.setText(todayWeather.getQuality());
        weekTv.setText(todayWeather.getDate());
        temperatureTV.setText(todayWeather.getHigh()+"~"+todayWeather.getLow());
        climateTv.setText(todayWeather.getType());
        windTv.setText("风力："+todayWeather.getFengli());
        Otherdatatv[0].setText(todayWeather.other1.getDate());
        Otherdatatv[1].setText(todayWeather.other2.getDate());
        Otherdatatv[2].setText(todayWeather.other3.getDate());
        Otherdatatv[3].setText(todayWeather.other4.getDate());
        Otherdatatv[4].setText(todayWeather.other5.getDate());
        Otherdatatv[5].setText(todayWeather.other6.getDate());

        Otherwendutv[0].setText(todayWeather.other1.getLow() + "~"+todayWeather.other1.getHigh() );
        Otherwendutv[1].setText(todayWeather.other2.getLow() + "~"+todayWeather.other2.getHigh());
        Otherwendutv[2].setText(todayWeather.other3.getLow() + "~"+todayWeather.other3.getHigh());
        Otherwendutv[3].setText(todayWeather.other4.getLow() + "~"+todayWeather.other4.getHigh());
        Otherwendutv[4].setText(todayWeather.other5.getLow() + "~"+todayWeather.other5.getHigh());
        Otherwendutv[5].setText(todayWeather.other6.getLow() + "~"+todayWeather.other6.getHigh());

        Otherfengtv[0].setText(todayWeather.other1.getFengxiang());
        Otherfengtv[1].setText(todayWeather.other2.getFengxiang());
        Otherfengtv[2].setText(todayWeather.other3.getFengxiang());
        Otherfengtv[3].setText(todayWeather.other4.getFengxiang());
        Otherfengtv[4].setText(todayWeather.other5.getFengxiang());
        Otherfengtv[5].setText(todayWeather.other6.getFengxiang());

        Othertypetv[0].setText(todayWeather.other1.getType());
        Othertypetv[1].setText(todayWeather.other2.getType());
        Othertypetv[2].setText(todayWeather.other3.getType());
        Othertypetv[3].setText(todayWeather.other4.getType());
        Othertypetv[4].setText(todayWeather.other5.getType());
        Othertypetv[5].setText(todayWeather.other6.getType());
        Toast.makeText(MainActivity.this,"更新成功！",Toast.LENGTH_SHORT).show();
    }



    void initView() {
        city_name_Tv = (TextView) findViewById(R.id.title_city_name);
        cityTv = (TextView) findViewById(R.id.city);
        timeTv = (TextView) findViewById(R.id.time);
        wenduTV = (TextView) findViewById(R.id.wendu);
        humidityTv = (TextView) findViewById(R.id.humidity);
        weekTv = (TextView) findViewById(R.id.week_today);
        pmDataTv = (TextView) findViewById(R.id.pm_data);
        pmQualityTv = (TextView) findViewById(R.id.pm2_5_quality);
        pmImg = (ImageView) findViewById(R.id.pm2_5_img);
        temperatureTV = (TextView) findViewById(R.id.temperature);
        climateTv = (TextView) findViewById(R.id.climate);
        windTv = (TextView) findViewById(R.id.wind);
        weatherImg = (ImageView) findViewById(R.id.weather_img);

        Otherdatatv = new TextView[6];
        Otherdatatv[0] = (TextView)Views.get(0).findViewById(R.id.data1);
        Otherdatatv[1] = (TextView)Views.get(0).findViewById(R.id.data2);
        Otherdatatv[2] = (TextView)Views.get(0).findViewById(R.id.data3);
        Otherdatatv[3] = (TextView)Views.get(1).findViewById(R.id.data4);
        Otherdatatv[4] = (TextView)Views.get(1).findViewById(R.id.data5);
        Otherdatatv[5] = (TextView)Views.get(1).findViewById(R.id.data6);


        Othertypetv = new TextView[6];
        Othertypetv[0] = (TextView)Views.get(0).findViewById(R.id.week1);
        Othertypetv[1] = (TextView)Views.get(0).findViewById(R.id.week2);
        Othertypetv[2] = (TextView)Views.get(0).findViewById(R.id.week3);
        Othertypetv[3] = (TextView)Views.get(1).findViewById(R.id.week4);
        Othertypetv[4] = (TextView)Views.get(1).findViewById(R.id.week5);
        Othertypetv[5] = (TextView)Views.get(1).findViewById(R.id.week6);

        Otherfengtv = new TextView[6];
        Otherfengtv[0] = (TextView)Views.get(0).findViewById(R.id.feng1);
        Otherfengtv[1] = (TextView)Views.get(0).findViewById(R.id.feng2);
        Otherfengtv[2] = (TextView)Views.get(0).findViewById(R.id.feng3);
        Otherfengtv[3] = (TextView)Views.get(1).findViewById(R.id.feng4);
        Otherfengtv[4] = (TextView)Views.get(1).findViewById(R.id.feng5);
        Otherfengtv[5] = (TextView)Views.get(1).findViewById(R.id.feng6);

        Otherwendutv = new TextView[6];
        Otherwendutv[0] = (TextView)Views.get(0).findViewById(R.id.wendu1);
        Otherwendutv[1] = (TextView)Views.get(0).findViewById(R.id.wendu2);
        Otherwendutv[2] = (TextView)Views.get(0).findViewById(R.id.wendu3);
        Otherwendutv[3] = (TextView)Views.get(1).findViewById(R.id.wendu4);
        Otherwendutv[4] = (TextView)Views.get(1).findViewById(R.id.wendu5);
        Otherwendutv[5] = (TextView)Views.get(1).findViewById(R.id.wendu6);

        city_name_Tv.setText("N/A");
        cityTv.setText("N/A");
        timeTv.setText("N/A");
        wenduTV.setText("N/A");
        humidityTv.setText("N/A");
        pmDataTv.setText("N/A");
        pmQualityTv.setText("N/A");
        weekTv.setText("N/A");
        temperatureTV.setText("N/A");
        climateTv.setText("N/A");
        windTv.setText("N/A");
    }


}