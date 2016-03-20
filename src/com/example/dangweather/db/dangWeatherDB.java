package com.example.dangweather.db;

import java.util.ArrayList;
import java.util.List;

import com.example.dangweather.model.City;
import com.example.dangweather.model.County;
import com.example.dangweather.model.Province;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class dangWeatherDB {
	
	public static final String DB_NAME="dangweather";
	
	public static final int VERSION = 1;
	
	private static dangWeatherDB dangWeatherDB;

	private SQLiteDatabase sqLiteDatabase;
	
	private dangWeatherDB(Context context){
		dangWeatherOpenHelper dangWeatherOpenHelper = new dangWeatherOpenHelper(context, DB_NAME, null, VERSION);
		sqLiteDatabase = dangWeatherOpenHelper.getWritableDatabase();
	}

	public synchronized static dangWeatherDB getInstance(Context context){
		if(dangWeatherDB==null){
			dangWeatherDB = new dangWeatherDB(context);
		}
		
		return dangWeatherDB;
	}
	
	public void saveProvice(Province province){
		if(province!=null){
			ContentValues contentValues = new ContentValues();
			contentValues.put("id", province.getId());
			contentValues.put("provinceName", province.getProvinceName());
			contentValues.put("provinceCode", province.getProvinceCode());
			sqLiteDatabase.insert("Province", null, contentValues);
		}
	}
	
	public List<Province> loadProvince(){
		List<Province> list = new ArrayList<>();
		Cursor cursor = sqLiteDatabase.query("Province", null, null, null, null, null, null);
		while(cursor.moveToNext()){
			Province province = new Province();
			String Name = cursor.getString(cursor.getColumnIndex("provinceName"));
			String Code = cursor.getString(cursor.getColumnIndex("provinceCode"));
			int Id = cursor.getInt(cursor.getColumnIndex("id"));
			province.setId(Id);
			province.setProvinceName(Name);
			province.setProvinceCode(Code);
			list.add(province);
		}
		
		return list;
	}
	
	public void saveCity(City city) { 
		if (city != null) { 
			ContentValues values = new ContentValues(); 
			values.put("city_name", city.getCityName()); 
			values.put("city_code", city.getCityCode()); 
			values.put("province_id", city.getProvinceId()); 
			sqLiteDatabase.insert("City", null, values); 
			} 
		}
	
	public List<City> loadCities(int provinceId) { 
		List<City> list = new ArrayList<City>(); 
		Cursor cursor = sqLiteDatabase.query("City", null, "province_id = ?", new String[]{String.valueOf(provinceId)},null,null,null); 
		while(cursor.moveToNext()) { 
			City city = new City(); 
			city.setId(cursor.getInt(cursor.getColumnIndex("id"))); 
			city.setCityName(cursor.getString(cursor .getColumnIndex("city_name"))); 
			city.setCityCode(cursor.getString(cursor .getColumnIndex("city_code"))); 
			city.setProvinceId(provinceId); 
			list.add(city); 
			}
		
		return list;
		}
	
	public void saveCounty(County county) { 
		if (county != null) { 
			ContentValues values = new ContentValues(); 
			values.put("county_name", county.getCountyName()); 
			values.put("county_code", county.getCountyCode()); 
			values.put("city_id", county.getCityId()); 
			sqLiteDatabase.insert("County", null, values); 
			} 
		}
	
	public List<County> loadCounties(int cityId) { 
		List<County> list = new ArrayList<County>(); 
		Cursor cursor = sqLiteDatabase.query("County", null, "city_id = ?", new String[] { String.valueOf(cityId) }, null, null, null); 
		while(cursor.moveToNext()) { 
				County county = new County(); 
				county.setId(cursor.getInt(cursor.getColumnIndex("id"))); 
				county.setCountyName(cursor.getString(cursor .getColumnIndex("county_name"))); 
				county.setCountyCode(cursor.getString(cursor .getColumnIndex("county_code"))); 
				county.setCityId(cityId); 
				list.add(county); 
			}
		
		return list; 
		}

	
}
