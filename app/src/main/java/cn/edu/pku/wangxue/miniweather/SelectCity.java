package cn.edu.pku.wangxue.miniweather;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
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

    private EditText search_edit;
    private ImageView mBackBtn;
    private MyApplication App;
    private List<City> data;
    private String SelectedId;
    private TextView cityName;
    private TextView selectcity;
    private ListView listView;

    private List<City> citys = new ArrayList<City>() ;
    ArrayList<String> city = new ArrayList<String>();
    ArrayList<String> cityId = new ArrayList<String>();
    private TextWatcher mTextWatcher = new TextWatcher() {
        private CharSequence temp;
        private int editStart;
        private int editEnd;

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            temp = s;
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            editStart = search_edit.getSelectionStart();
            editEnd = search_edit.getSelectionEnd();
            if(temp.length() > 10){
                Toast.makeText(SelectCity.this, "您输入的字数已经超过了限制！", Toast.LENGTH_SHORT).show();
                s.delete(editStart-1, editEnd);
                int tempSelection = editStart;
                search_edit.setText(s);
                search_edit.setSelection(tempSelection);
            }

            // 过滤符合条件的城市列表
            String filterStr = s.toString();
            for(City c:citys){
                // 简单过滤原则：名字匹配
                if( (!c.getCity().contains(filterStr))){
                    city.remove(c);
                }
            }

        }
    };
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_city);
        search_edit = (EditText) findViewById(R.id.search_edit);
        search_edit.addTextChangedListener(mTextWatcher);
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
