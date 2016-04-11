package com.example.dangweather.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.example.dangweather.db.dangWeatherDB;
import com.example.dangweather.model.City;
import com.example.dangweather.model.County;
import com.example.dangweather.model.Province;

public class Utility {
	
	public synchronized static boolean handleProvince(dangWeatherDB db,String response){
		if(!TextUtils.isEmpty(response)){
			String[] allProvinces = response.split(",");
			if(allProvinces!=null&&allProvinces.length>0){
				for(String province:allProvinces){
					String[] provinceInfo = province.split("\\|");
					Province p = new Province();
					p.setProvinceCode(provinceInfo[0]);
					p.setProvinceName(provinceInfo[1]);
					db.saveProvice(p);
				}
				return true;
			}
		}
		return false;
	}
	
	public static boolean handleCities(dangWeatherDB db, String response, int provinceId) { 
		if (!TextUtils.isEmpty(response)) { 
			String[] allCities = response.split(","); 
			if (allCities != null && allCities.length > 0) { 
				for (String c : allCities) { 
					String[] array = c.split("\\|"); 
					City city = new City();
					city.setCityCode(array[0]); 
					city.setCityName(array[1]); 
					city.setProvinceId(provinceId); 
					db.saveCity(city);
				} 
				return true;
			}
		} 
		return false;
	}
	
	public static boolean handleCounties(dangWeatherDB db,String response, int cityId) {
		if (!TextUtils.isEmpty(response)) {
			String[] allCounties = response.split(",");
			if (allCounties != null && allCounties.length > 0) {
				for (String c : allCounties) { 
					String[] array = c.split("\\|"); 
					County county = new County();
					county.setCountyCode(array[0]); 
					county.setCountyName(array[1]); 
					county.setCityId(cityId); 
					db.saveCounty(county); 
				} 
				return true; 
			}  
		}
		return false;
	}
	
	public static void handleWeatherResponse(Context context,String response){
		JSONObject jsonObject;
		try {
			jsonObject = new JSONObject(response);
			JSONObject weatherInfo = jsonObject.getJSONObject("weatherinfo");
			String cityName = weatherInfo.getString("city");
			String weatherCode = weatherInfo.getString("cityid");
			String temp1      = weatherInfo.getString("temp1");
			String temp2      = weatherInfo.getString("temp2");
			String weatherDesp = weatherInfo.getString("weather");
			String publishTime = weatherInfo.getString("ptime");
			saveWeatherInfo(context,cityName,weatherCode,temp1,temp2,weatherDesp,publishTime);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
	}

	/**
	 * 
	 * @param context
	 * @param cityName
	 * @param weatherCode
	 * @param temp1
	 * @param temp2
	 * @param weatherDesp
	 * @param publishTime
	 * 返回的天气信息存在sharedPreference
	 */
	public static void saveWeatherInfo(Context context, String cityName,
			String weatherCode, String temp1, String temp2, String weatherDesp,
			String publishTime) {
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy年M月d日", Locale.CHINA);
		SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
		editor.putBoolean("city_selected", true);
		editor.putString("city_name", cityName);
		editor.putString("weather_code", weatherCode);
		editor.putString("temp1", temp1);
		editor.putString("temp2", temp2);
		editor.putString("weather_desp", weatherDesp);
		editor.putString("publish_time", publishTime);
		editor.putString("current_date", sdf.format(new Date()));
		editor.commit();
	}
	
}

