package com.example.demo003;

import java.nio.channels.spi.SelectorProvider;
import java.util.ArrayList;
import java.util.List;

import com.example.demo003.R.id;
import com.example.demo003.db.CoolWeatherDB;
import com.example.demo003.modle.City;
import com.example.demo003.modle.County;
import com.example.demo003.modle.Province;
import com.example.demo003.util.HttpCallbackListener;
import com.example.demo003.util.HttpUtil;
import com.example.demo003.util.Utility;

import android.app.Activity;
import android.app.ProgressDialog;
import android.app.DownloadManager.Query;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.TextureView;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

public class MainActivity extends Activity {

	protected static final int LEVEL_PROVINCE = 0;
	protected static final int LEVEL_CITY = 0;
	private static final int LEVEL_COUNTY = 0;
	private ProgressDialog progressDialog;
	private TextView titleText;
	private ListView listView;
	private ArrayAdapter<String> adapter;
	private CoolWeatherDB coolWeatherDB;
	private List<String> dataList = new ArrayList<String>();

	private List<Province> provincesList;
	private List<City> cityList;
	private List<County> countyList;

	private Province selectedProvince;
	private City selectedCity;
	private int currenLevel;

	private boolean isFromWeatherActivity;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		/**
		 * P521
		 */
		isFromWeatherActivity = getIntent().getBooleanExtra(
				"from_weather_activity", false);

		/**
		 * £ê £Ð£µ£¶£±´úÂë¡¡
		 */
		Shared();
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		listView = (ListView) findViewById(R.id.list_view);
		titleText = (TextView) findViewById(R.id.title_text);
		adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_expandable_list_item_1, dataList);
		listView.setAdapter(adapter);
		coolWeatherDB = coolWeatherDB.getInstance(this);
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int index,
					long arg3) {
				// TODO Auto-generated method stub
				if (currenLevel == LEVEL_PROVINCE) {
					selectedProvince = provincesList.get(index);
					queryCities();
				} else if (currenLevel == LEVEL_CITY) {
					selectedCity = cityList.get(index);
					queryCounties();
				} else if (currenLevel == LEVEL_COUNTY) {
					String countryCode = countyList.get(index).getCountyCode();
					Intent intent = new Intent(MainActivity.this,
							WeatherActivity.class);
					intent.putExtra("county_code", countryCode);
					startActivity(intent);
					finish();
				}
			}
		});
		queryProvices();
	}

	private void Shared() {
		// TODO Auto-generated method stub
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(this);
		if (prefs.getBoolean("city_selected", false) && isFromWeatherActivity) {
			Intent intent = new Intent(this, WeatherActivity.class);
			startActivity(intent);
			finish();
			return;
		}
	}

	private void queryProvices() {
		// TODO Auto-generated method stub
		provincesList = coolWeatherDB.loadProvinces();
		if (provincesList.size() > 0) {
			dataList.clear();
			for (Province province : provincesList) {
				dataList.add(province.getProvinceName());
			}
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			titleText.setText("China");
			currenLevel = LEVEL_PROVINCE;
		} else {
			queryFromServer(null, "province");
		}
	}

	private void queryCities() {
		// TODO Auto-generated method stub
		cityList = coolWeatherDB.loadCities(selectedProvince.getId());
		if (cityList.size() > 0) {
			dataList.clear();
			for (City city : cityList) {
				dataList.add(city.getCityName());
			}
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			titleText.setText(selectedProvince.getProvinceCode());
			currenLevel = LEVEL_CITY;
		}
	}

	private void queryCounties() {
		// TODO Auto-generated method stub
		countyList = coolWeatherDB.loadCounties(selectedCity.getId());
		if (countyList.size() > 0) {
			dataList.clear();
			for (County county : countyList) {
				dataList.add(county.getCountyName());
			}
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			titleText.setText(selectedCity.getCityName());
			currenLevel = LEVEL_COUNTY;
		} else {
			queryFromServer(selectedCity.getCitycode(), "county");
		}

	}

	private void queryFromServer(final String code, final String type) {
		String address;
		if (!TextUtils.isEmpty(code)) {
			address = "http://www.weather.com.cn/data/list3/city" + code
					+ ".xml";
		} else {
			address = "http://www.weather.com.cn/data/list3/city.xml";

		}
		showProgressDialog();

		HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {

			@Override
			public void onFinish(String respnse) {
				// TODO Auto-generated method stub
				boolean result = false;
				if ("province".equals(type)) {
					result = Utility.handleProvincesResponse(coolWeatherDB,
							respnse);
				} else if ("city".equals(type)) {
					result = Utility.handleCitiesResponse(coolWeatherDB,
							respnse, selectedProvince.getId());
				} else if ("county".equals(type)) {
					result = Utility.handleCountiesResponse(coolWeatherDB,
							respnse, selectedCity.getId());
				}
				if (result) {
					runOnUiThread(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							closeProgressDialog();
							if ("provice".equals(type)) {
								queryProvices();
							} else if ("city".equals(type)) {
								queryCities();
							} else if ("county".equals(type)) {
								queryCounties();
							}
						}

					});
				}

			}

			@Override
			public void onError(Exception e) {
				// TODO Auto-generated method stub
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						closeProgressDialog();
						Toast.makeText(getApplicationContext(),
								"NO Loading...", Toast.LENGTH_SHORT).show();

					}
				});
			}
		});
	}

	private void showProgressDialog() {
		// TODO Auto-generated method stub
		if (progressDialog == null) {
			progressDialog = new ProgressDialog(this);
			progressDialog.setMessage("Loading.....");
			progressDialog.setCanceledOnTouchOutside(false);

		}
		progressDialog.show();
	}

	private void closeProgressDialog() {
		// TODO Auto-generated method stub
		if (progressDialog != null) {
			progressDialog.dismiss();
		}
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		if (currenLevel == LEVEL_COUNTY) {
			queryCounties();

		} else if (currenLevel == LEVEL_CITY) {
			queryProvices();
		} else {
			if (isFromWeatherActivity) {
				Intent intent = new Intent(this, WeatherActivity.class);
				startActivity(intent);
			}
			finish();
		}
	}
}
