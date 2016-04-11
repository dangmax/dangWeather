package com.example.dangweather.activity;

import com.example.dangweather.R;
import com.example.dangweather.service.AutoUpdateService;
import com.example.dangweather.util.HttpCallBackListener;
import com.example.dangweather.util.HttpUtil;
import com.example.dangweather.util.Utility;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class WeatherActivity extends Activity implements OnClickListener {
	private LinearLayout weatherInfoLayout;
	
	/*
	 * city name
	 */
	private TextView cityName;
	
	/*
	 * publish time
	 */
	private TextView publishTime;
	
	/*
	 * weather desp
	 */
	private TextView weatherDesp;
	
	/*
	 * temp1
	 */
	private TextView temp1;
	
	/*
	 * temp2
	 */
	private TextView temp2;
	
	/*
	 * current Date
	 */
	private TextView currentDate;
	
	/*
	 * switch city Button 
	 */
	private Button  switchCity;
	
	/*
	 * refresh weather Button 
	 */
	private Button  refreshWeather;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.weather_layout);
		//��ʼ�������ؼ�
		weatherInfoLayout = (LinearLayout) findViewById(R.id.weather_info_layout);
		cityName = (TextView) findViewById(R.id.city_name);
		publishTime  = (TextView) findViewById(R.id.publish_text);
		weatherDesp  = (TextView) findViewById(R.id.weather_desp);
		temp1        = (TextView) findViewById(R.id.temp1);
		temp2        = (TextView) findViewById(R.id.temp2);
		currentDate  = (TextView) findViewById(R.id.current_date);
		switchCity   = (Button) findViewById(R.id.switch_city);
		refreshWeather   = (Button) findViewById(R.id.refresh_weather);
		String countyCode = getIntent().getStringExtra("county_code");
		if(!TextUtils.isEmpty(countyCode)){
			//���ؼ�����
			publishTime.setText("ͬ���С�������");
			weatherInfoLayout.setVisibility(View.INVISIBLE);
			cityName.setVisibility(View.INVISIBLE);
			queryWeatherCode(countyCode);
		}else{
			//û���ص�ʱ���ֱ����ʾ��������
			showWeather();
		}
		switchCity.setOnClickListener(this);
		refreshWeather.setOnClickListener(this);
	}
/**
 * ��ѯ�ض�Ӧ����������
 * @param countyCode
 */
	private void queryWeatherCode(String countyCode) {
		String address = "http://www.weather.com.cn/data/list3/city" + countyCode + ".xml";
		queryFromServer(address,"countyCode");
	}


	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.switch_city:
			Intent intent = new Intent(this,ChooseAreaActivity.class);
			intent.putExtra("from_weather_activity", true);
			startActivity(intent);
			finish();
			break;
		case R.id.refresh_weather:
			publishTime.setText("ͬ���С�������");
			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
			String weatherCode = prefs.getString("weather_code", "");
			if(!TextUtils.isEmpty(weatherCode)){
				queryWeatherInfo(weatherCode);
			}
			
			break;
		default:
			break;
		}
	}

	/**
	 * �����������ţ���ѯ������Ϣ
	 * @param weatherCode
	 */
	private void queryWeatherInfo(String weatherCode) {
		String address = "http://www.weather.com.cn/data/cityinfo/" + weatherCode + ".html"; 
		queryFromServer(address, "weatherCode"); 
	}
	
	/**
	 * ���ݵ�ַ������ȥ�������˲�ѯ��������or������Ϣ
	 * @param address
	 * @param string
	 */
	private void queryFromServer(final String address, final String type) {
		HttpUtil.sendHttpRequest(address, new HttpCallBackListener() {
			
			@Override
			public void onFinish(String content) {
				if(type.equals("countyCode")){
					if(!TextUtils.isEmpty(content)){
						//�ӷ��������н�������������
						String[] array = content.split("\\|");
						if(array!=null&&array.length==2){
							String weatherCode = array[1];
							queryWeatherInfo(weatherCode);
						}
					}
				}else if(type.equals("weatherCode")){
					Utility.handleWeatherResponse(WeatherActivity.this, content);
					runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							showWeather();
						}
					});
					
				}
			}
			
			@Override
			public void onError(Exception e) {
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						publishTime.setText("ͬ��ʧ��!");
					}
				});
			}
		});
	}
	
	/**
	 * �� SharedPreferences�ж�ȡ�洢��������Ϣ
	 */
	private void showWeather() {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		cityName.setText(prefs.getString("city_name", ""));
		temp1.setText(prefs.getString("temp1", ""));
		temp2.setText(prefs.getString("temp2", ""));
		weatherDesp.setText(prefs.getString("weather_desp", ""));
		publishTime.setText("����"+prefs.getString("publish_time", "")+"����");
		currentDate.setText(prefs.getString("current_date", ""));
		weatherInfoLayout.setVisibility(View.VISIBLE);
		cityName.setVisibility(View.VISIBLE);
		Intent intent = new Intent(this, AutoUpdateService.class);
		startService(intent);
	}
}

