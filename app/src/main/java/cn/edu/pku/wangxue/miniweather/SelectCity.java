package cn.edu.pku.wangxue.miniweather;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import cn.edu.pku.wangxue.app.MyApplication;
import cn.edu.pku.wangxue.bean.City;
import cn.edu.pku.wangxue.miniweather.R;

public class SelectCity extends Activity implements View.OnClickListener {

    private ImageView mBackBtn;

    private MyApplication App;
    private List<City> data;
    private String SelectedId;
    private TextView cityName;
    private TextView selectcity;
    private ListView listView;

    ArrayList<String> city = new ArrayList<String>();
    ArrayList<String> cityId = new ArrayList<String>();
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_city);
        mBackBtn = (ImageView) findViewById(R.id.title_back);
        mBackBtn.setOnClickListener(this);

        App = (MyApplication)getApplication();
        data = App.getCityList();
        int i = 0;
        while (i < data.size()){
            city.add(data.get(i).getCity().toString());
            cityId.add(data.get(i++).getNumber().toString());
        }
        listView = (ListView) findViewById(R.id.city_list);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(SelectCity.this,android.R.layout.simple_list_item_1,city);
        listView.setAdapter(adapter);

        cityName = (TextView)findViewById(R.id.title_city_name);
        selectcity = (TextView)findViewById(R.id.title_city_name);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){

            public void onItemClick(AdapterView<?>adapterView, View view,int i,long l){
                Toast.makeText(SelectCity.this,"你单击了："+ city.get(i),Toast.LENGTH_SHORT).show();
                SelectedId = cityId.get(i);
              //  selectcity.setText("选择城市："+city.get(i));
            }
        });

    }
    @Override
    public void onClick(View view) {
            switch (view.getId()) {
                case R.id.title_back:
                    Intent i = new Intent();
                    i.putExtra("cityCode", SelectedId);
                    setResult(RESULT_OK, i);

                    SharedPreferences mySharedPreferences = getSharedPreferences("config",Activity.MODE_PRIVATE);
                    SharedPreferences.Editor editor = mySharedPreferences.edit();
                    Log.d("myWeather",SelectedId);
                    editor.putString("main_city_code",SelectedId);
                    editor.commit();

                    finish();
                    break;
                default:
                    break;
            }

    }
}
