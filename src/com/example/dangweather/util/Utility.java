package com.example.dangweather.util;

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
	
	
	
}

