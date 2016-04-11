package com.example.dangweather.activity;

import java.util.ArrayList;
import java.util.List;

import com.example.dangweather.R;
import com.example.dangweather.db.dangWeatherDB;
import com.example.dangweather.model.City;
import com.example.dangweather.model.County;
import com.example.dangweather.model.Province;
import com.example.dangweather.util.HttpCallBackListener;
import com.example.dangweather.util.HttpUtil;
import com.example.dangweather.util.Utility;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ChooseAreaActivity extends Activity {
		
	public static final int LEVEL_PROVINCE = 0;
	public static final int LEVEL_CITY = 1;
	public static final int LEVEL_COUNTY = 2;
	
	private ProgressDialog progressDialog;
	private TextView titleText;
	private ListView listView;
	private ArrayAdapter<String>  arrayAdapter;
	private dangWeatherDB dangWeatherDB;
	private List<String> dataList = new ArrayList<String>();
	
	private List<Province> provinceList ;
	private List<City> cityList ;
	private List<County> countyList ;
    private Province selectProvince;
    private City     selectCity;
    private int      currentLevel;
    private boolean isFromWeatherActivity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	isFromWeatherActivity = getIntent().getBooleanExtra("from_weather_activity", false);
    	SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
    	//选择了城市并且不是从weatheractivity跳转过来
    	if(prefs.getBoolean("city_selected", false)&&!isFromWeatherActivity){
    		Intent intent = new Intent(this,WeatherActivity.class);
    		startActivity(intent);
    		finish();
    		return;
    	}
    	
    	requestWindowFeature(Window.FEATURE_NO_TITLE);
    	setContentView(R.layout.choose_area);
    	listView = (ListView) findViewById(R.id.list_view);
    	titleText = (TextView) findViewById(R.id.title_text);
    	arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, dataList);
    	listView.setAdapter(arrayAdapter);
    	dangWeatherDB = dangWeatherDB.getInstance(this);
    	listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int index,long arg3) {
					if(currentLevel == LEVEL_PROVINCE){
						selectProvince = provinceList.get(index);
						queryCities();
					}else if(currentLevel == LEVEL_CITY){
						selectCity = cityList.get(index);
						queryCounties();
					}else if(currentLevel == LEVEL_COUNTY){
						String countyCode = countyList.get(index).getCountyCode();
						Intent intent = new Intent(ChooseAreaActivity.this, WeatherActivity.class);
						intent.putExtra("county_code", countyCode);
						startActivity(intent);
						finish();
					}
			}
		});
    	queryProvinces();
    }

    /**
     * 查询省的数据
     */
	private void queryProvinces() {
		provinceList = dangWeatherDB.loadProvince();
		if(provinceList.size()>0){
			dataList.clear();
			for(Province province:provinceList){
				dataList.add(province.getProvinceName());
			}
			arrayAdapter.notifyDataSetChanged();
			listView.setSelection(0);
			titleText.setText("中国");
			currentLevel = LEVEL_PROVINCE;
		}else{
			queryFromServer(null,"province");
		}
	}
    
	/**
	 * 查询城市信息
	 */
	private void queryCities() {
		cityList = dangWeatherDB.loadCities(selectProvince.getId());
		if(cityList.size()>0){
			dataList.clear();
			for(City city:cityList){
				dataList.add(city.getCityName());
			}
			arrayAdapter.notifyDataSetChanged();
			listView.setSelection(0);
			titleText.setText(selectProvince.getProvinceName());
			currentLevel = LEVEL_CITY;
		}else{
			queryFromServer(selectProvince.getProvinceCode(),"city");
		}
	}
    
	/**
	 * 查询县的信息
	 */
	private void queryCounties() {
		countyList = dangWeatherDB.loadCounties(selectCity.getId());
		if(countyList.size()>0){
			dataList.clear();
			for(County county:countyList){
				dataList.add(county.getCountyName());
			}
			arrayAdapter.notifyDataSetChanged();
			listView.setSelection(0);
			titleText.setText(selectCity.getCityName());
			currentLevel = LEVEL_COUNTY;
		}else{
			queryFromServer(selectCity.getCityCode(),"county");
		}
	}

	
	/**
	 * 从服务器上查询省、市、县数据
	 */
	private void queryFromServer(final String cityCode, final String type) {
		String address;
		if(!TextUtils.isEmpty(cityCode)){
			address = "http://www.weather.com.cn/data/list3/city"+cityCode+".xml";
		}else{
			address = "http://www.weather.com.cn/data/list3/city.xml";
		}
		showProgressDialog();
		HttpUtil.sendHttpRequest(address, new HttpCallBackListener() {
			
			@Override
			public void onFinish(String content) {
				boolean result = false;
				if(type.equals("province")){
					result = Utility.handleProvince(dangWeatherDB, content);
				}else if(type.equals("city")){
					result = Utility.handleCities(dangWeatherDB, content,selectProvince.getId());
				}else if(type.equals("county")){
					result = Utility.handleCounties(dangWeatherDB, content,selectCity.getId());
				}
				
				if(result){
					runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							closeProgressDialog(); 
							if("province".equals(type)){ 
								queryProvinces(); 
							}else if("city".equals(type)){
								queryCities(); 
							}else if("county".equals(type)){
								queryCounties(); 
							}
						}
					});
				}
				
			}
			
			@Override
			public void onError(Exception e) {
				runOnUiThread(new  Runnable() {
					public void run() {
						closeProgressDialog();
						Toast.makeText(ChooseAreaActivity.this, "加载失败", Toast.LENGTH_SHORT);
					}
				});
			}
		});
	}
	
	/**
	 * 显示进度对话框
	 */
	private void showProgressDialog(){
		if(progressDialog ==null){
			progressDialog = new ProgressDialog(this);
			progressDialog.setMessage("正在加载....");
			progressDialog.setCanceledOnTouchOutside(false);
		}
		progressDialog.show();
	}
	
	/**
	 * 关闭进度对话框
	 */
	private void closeProgressDialog(){
		if(progressDialog!=null){
			progressDialog.dismiss();
		}
	}
	
	@Override
	public void onBackPressed() {
		if(currentLevel==LEVEL_COUNTY){
			queryCities();
		}else if(currentLevel==LEVEL_CITY){
			queryProvinces();
		}else{
			if(isFromWeatherActivity){
				Intent intent = new Intent(this, WeatherActivity.class);
				startActivity(intent);
			}
			finish();
		}
		
	}
	
	
	
}
