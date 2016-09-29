package com.example.demo003.db;

import java.util.ArrayList;
import java.util.List;

import com.example.demo003.modle.City;
import com.example.demo003.modle.County;
import com.example.demo003.modle.Province;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class CoolWeatherDB {
	private String PROVICE_NAME = "provice_name";
	private String PROVICE_CODE = "provice_code";
	private String PROVICE = "provice";

	private String CITY_NAME = "city_name";
	private String CITY_CODE = "city_code";
	private String PROVINCE_ID = "province_id";
	private String CITY = "city";

	private String COUNTY_NAME = "county_name";
	private String COUNTY_CODE = "county_code";
	private String CITY_ID = "city_id";
	private String COUNTY = "county";

	// + "id integer primary key autoincrement," + "provice_name text,"
	// + "provice_code text)";
	// public static final String CREATE_CITY = "create table City("
	// + "id integer primary key autoincrement," + "city_name text,"
	// + "city_code text" + "province_id integer)";
	// public static final String CREATE_COUNTY = "create table County("
	// + "id integer primary key autoincrement," + "county_name text,"
	// + "county_code text" + "city_id integer)";

	public static final String DB_NAME = "coo_weather";
	public static final int VERSION = 1;
	private static CoolWeatherDB coolWeatherDB;
	private SQLiteDatabase db;

	private CoolWeatherDB(Context context) {
		CoolWeatherOpenHelper dbHelper = new CoolWeatherOpenHelper(context,
				DB_NAME, null, VERSION);
		db = dbHelper.getWritableDatabase();
	}

	public synchronized static CoolWeatherDB getInstance(Context context) {
		if (coolWeatherDB == null) {
			coolWeatherDB = new CoolWeatherDB(context);
		}
		return coolWeatherDB;
	}

	public void saveProvince(Province province) {
		if (province != null) {
			ContentValues values = new ContentValues();
			values.put(PROVICE_NAME, province.getProvinceName());
			values.put(PROVICE_CODE, province.getProvinceCode());
			db.insert(PROVICE, null, values);
		}
	}

	public List<Province> loadProvinces() {
		List<Province> list = new ArrayList<Province>();
		// Cursor cursor = db.query(PROVICE, null, null, null, null, null,
		// null);
		Cursor cursor = db.query(PROVICE, null, null, null, null, null, null);
		if (cursor.moveToFirst()) {
			do {
				Province province = new Province();
				province.setId(cursor.getInt(cursor.getColumnIndex("id")));
				province.setProvinceName(cursor.getString(cursor
						.getColumnIndex(PROVICE_NAME)));
				province.setProvinceCode(cursor.getString(cursor
						.getColumnIndex(PROVICE_CODE)));
				list.add(province);
			} while (cursor.moveToNext());

		}
		return list;

	}

	public void SaveCity(City city) {
		if (city != null) {
			ContentValues values = new ContentValues();
			values.put(CITY_NAME, city.getCityName());
			values.put(CITY_CODE, city.getCitycode());
			values.put(PROVINCE_ID, city.getProvinceId());
			db.insert(CITY, null, values);
		}
	}

	public List<City> loadCities(int provinceId) {
		List<City> list = new ArrayList<City>();
		Cursor cursor = db.query(CITY, null, "province_id=?",
				new String[] { String.valueOf(provinceId) }, null, null, null);
		if (cursor.moveToFirst()) {
			do {
				City city = new City();
				city.setId(cursor.getInt(cursor.getColumnIndex("id")));
				city.setCityName(cursor.getString(cursor
						.getColumnIndex(CITY_NAME)));
				city.setCitycode(cursor.getString(cursor
						.getColumnIndex(CITY_CODE)));
				city.setProvinceId(provinceId);
				list.add(city);
			} while (cursor.moveToNext());
		}
		return list;

	}

	public void SaveCounty(County county) {
		if (county != null) {
			ContentValues values = new ContentValues();
			values.put(COUNTY_NAME, county.getCountyName());
			values.put(COUNTY_CODE, county.getCountyCode());
			values.put(CITY_ID, county.getCityId());
			db.insert(COUNTY, null, values);
		}
	}

	public List<County> loadCounties(int cityId) {
		List<County> list = new ArrayList<County>();
		Cursor cursor = db.query(COUNTY, null, "cityId=?",
				new String[] { String.valueOf(cityId) }, null, null, null);
		if (cursor.moveToFirst()) {
			do {
				County county = new County();
				county.setCityId(cursor.getInt(cursor.getColumnIndex("id")));
				county.setCountyName(cursor.getString(cursor
						.getColumnIndex(COUNTY_NAME)));
				county.setCountyCode(cursor.getString(cursor
						.getColumnIndex(COUNTY_CODE)));
				county.setCityId(cityId);
				list.add(county);
			} while (cursor.moveToNext());
		}

		return list;
	}

}
