package cn.edu.pku.wangxue.miniweather;

import android.util.Log;


        import android.app.Activity;
        import android.content.Intent;
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
        import android.widget.TextClock;
        import android.widget.TextView;
        import android.widget.Toast;

        import java.util.ArrayList;
        import java.util.List;

        import cn.edu.pku.wangxue.app.MyApplication;
        import cn.edu.pku.wangxue.bean.City;


/**
 * Created by crazy on 2016-10-18-0018.
 */

public class SelectCity extends Activity implements View.OnClickListener {

    private ImageView mBackBtn ;
    private ListView mlistView;
    private MyApplication App ;
    private List<City> data ;
    private String SelectedId;
    private TextView cityName ;

    private EditText mSearch;

    private TextView selectcity;
    ArrayList<String> city = new ArrayList<String>();
    ArrayList<String> cityId = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        setContentView(R.layout.select_city);

        mBackBtn = (ImageView) findViewById(R.id.title_back);
        mBackBtn.setOnClickListener(this);

        mSearch = (EditText)findViewById(R.id.search_edit);
        mSearch.addTextChangedListener(mTextWacher);

        App = (MyApplication) getApplication();
        data = App.getCityList();
        int i = 0 ;
        while(i<data.size()){
            city.add(data.get(i).getCity());
            cityId.add(data.get(i).getNumber());
            i++;
        }

        mlistView = (ListView)findViewById(R.id.city_list) ;
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(SelectCity.this,android.R.layout.simple_list_item_1,city);

        mlistView.setAdapter(adapter);

        cityName =(TextView)findViewById(R.id.title_city_name);
        cityName.setText("请选择城市");

        mlistView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?>adapterView,View view,int i,long l){
                Toast.makeText(SelectCity.this, "你选择了："+city.get(i), Toast.LENGTH_SHORT).show();
                SelectedId = cityId.get(i);
                cityName.setText("当前城市："+city.get(i));

            }
        });


    }

    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.title_back :
                Intent i = new Intent();
                i.putExtra("cityCode",SelectedId);
                setResult(RESULT_OK,i);
                finish();
                break;
/*
            case R.id.search_edit :
                String search = mSearch.getText().toString();
                if (search!=null){
                    city.clear();
                    cityId.clear();
                    int j = 0 ;
                    while(j<data.size()){
                        if (data.get(j).getCity().contains(search)){
                            city.add(data.get(j).getCity());
                            cityId.add(data.get(j).getNumber());
                        }
                        j++;
                    }
                }
                break;
*/
            default:
                break;
        }
    }
    TextWatcher mTextWacher = new TextWatcher() {
        private CharSequence temp;
        private int editStart;
        private int editEnd;
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            temp =charSequence ;
            //           Log.d("myapp","beforeTextChanged:"+temp);
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            String search = mSearch.getText().toString();
            if (search!=null){
                city.clear();
                cityId.clear();
                int j = 0 ;
                while(j<data.size()){
                    if (data.get(j).getCity().contains(search)){
                        city.add(data.get(j).getCity());
                        cityId.add(data.get(j).getNumber());
                    }
                    j++;
                }
            }
            Log.d("myapp","onTextChanged:"+charSequence);
        }

        @Override
        public void afterTextChanged(Editable editable) {
            editStart = mSearch.getSelectionStart();
            editEnd = mSearch.getSelectionEnd();
            if(temp.length()>10){
                Toast.makeText(SelectCity.this,"你输入的字数已经超过了限制！",Toast.LENGTH_SHORT).show();
                editable.delete(editStart-1,editEnd);
                int tempSelection = editStart;
                mSearch.setText(editable);
                mSearch.setSelection(tempSelection);
            }
            Log.d("myapp","afterTextChanged:");
        }
    };
/*
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Log.d("tag111","按下了back键   onBackPressed()");
        Log.d("tag111",SelectedId);
        Intent i = new Intent();
        i.putExtra("cityCode",SelectedId);
        setResult(RESULT_OK,i);
        finish();
        return;
    }
 */
}
