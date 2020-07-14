package com.example.myfood;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.map.ItemizedOverlay;
import com.baidu.mapapi.map.LocationData;
import com.baidu.mapapi.map.MapController;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationOverlay;
import com.baidu.mapapi.map.OverlayItem;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.example.utils.myapplication;

public class MapActivity extends Activity {
	private myapplication myapplication1;
	BMapManager mBMapMan = null;
	MapView mMapView = null;
	MapController mMapController = null;
	private LocationClient locationClient = null;
	private static final int UPDATE_TIME = 5000;
	private static int LOCATION_COUTNS = 0;
	public Double nLatitude; // 经度 给gps定位用
	public Double nLontitude; // 纬度 给gps定位用
	private Bundle Bundle1;// 传值
	private Button button;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		mBMapMan = new BMapManager(getApplication());
		mBMapMan.init("CB37734DDF9B8D0CEE7DC34B55D9581D5E5A3765", null);
		// 注意：请在试用setContentView前初始化BMapManager对象，否则会报错,这句话太经典了
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map);
		Bundle1 = this.getIntent().getExtras();
		myapplication1 = (myapplication) getApplication();
		myapplication1.getInstance().addActivity(this);
		mMapView = (MapView) findViewById(R.id.bmapsView);
		mMapView.setBuiltInZoomControls(true);
		// 设置启用内置的缩放控件
		mMapController = mMapView.getController();
		// 得到mMapView的控制权,可以用它控制和驱动平移和缩放
		button = (Button) findViewById(R.map.button1);
		button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				gpsdw();// 显示gps定位
			}
		});
		ptdw();// 给出酒店坐标
		// gpsdw();
	}

	/**
	 * 给出酒店坐标
	 */
	public void ptdw() {
		String[] spStr = Bundle1.getStringArrayList("map").get(0).toString()
				.split(",");
		GeoPoint point = new GeoPoint(

		(int) (Double.parseDouble(spStr[1]) * 1E6),
				(int) (Double.parseDouble(spStr[0]) * 1E6));
		// 用给定的经纬度构造一个GeoPoint，单位是微度 (度 * 1E6)
		Drawable marker = getResources().getDrawable(R.drawable.icon_marka); // 得到需要标在地图上的资源
		mMapView.getOverlays().add(
				new OverItemT(marker, MapActivity.this, Bundle1
						.getStringArrayList("map"))); // 添加ItemizedOverlay实例到mMapView
		mMapView.refresh();
		mMapController.setCenter(point);// 设置地图中心点
		mMapController.setZoom(12);// 设置地图zoom级别

	}

	/**
	 * 显示gps定位
	 */
	public void gpsdw() {
		locationClient = new LocationClient(this);
		// 设置定位条件
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true); // 是否打开GPS
		option.setCoorType("bd09ll"); // 设置返回值的坐标类型。
		option.setPriority(LocationClientOption.NetWorkFirst); // 设置定位优先级
		option.setProdName("LocationDemo"); // 设置产品线名称。强烈建议您使用自定义的产品线名称，方便我们以后为您提供更高效准确的定位服务。
		option.setScanSpan(UPDATE_TIME); // 设置定时定位的时间间隔。单位毫秒
		locationClient.setLocOption(option);

		// 注册位置监听器
		locationClient.registerLocationListener(new BDLocationListener() {

			@Override
			public void onReceiveLocation(BDLocation location) {
				// TODO Auto-generated method stub
				if (location == null) {
					return;
				}
				/*
				 * StringBuffer sb = new StringBuffer(256);
				 * sb.append("Time : "); sb.append(location.getTime());
				 * sb.append("\nError code : ");
				 * sb.append(location.getLocType()); sb.append("\nLatitude : ");
				 * sb.append(location.getLatitude());
				 * sb.append("\nLontitude : ");
				 * sb.append(location.getLongitude()); sb.append("\nRadius : ");
				 * sb.append(location.getRadius()); if (location.getLocType() ==
				 * BDLocation.TypeGpsLocation) { sb.append("\nSpeed : ");
				 * sb.append(location.getSpeed()); sb.append("\nSatellite : ");
				 * sb.append(location.getSatelliteNumber()); } else if
				 * (location.getLocType() == BDLocation.TypeNetWorkLocation) {
				 * sb.append("\nAddress : "); sb.append(location.getAddrStr());
				 * } LOCATION_COUTNS++; sb.append("\n检查位置更新次数：");
				 * sb.append(String.valueOf(LOCATION_COUTNS));
				 * Toast.makeText(MapActivity.this, sb.toString(),
				 * Toast.LENGTH_SHORT).show(); 这个是用来做测试的，现在不用来显示了
				 */
				nLatitude = location.getLatitude();
				nLontitude = location.getLongitude();
				MyLocationOverlay myLocationOverlay = new MyLocationOverlay(
						mMapView);
				LocationData locData = new LocationData();
				// 手动将位置源置为天安门，在实际应用中，请使用百度定位SDK获取位置信息，要在SDK中显示一个位置，需要
				// 使用百度经纬度坐标（bd09ll）
				locData.latitude = nLatitude;
				locData.longitude = nLontitude;
				locData.direction = 2.0f;
				myLocationOverlay.setData(locData);
				mMapView.getOverlays().add(myLocationOverlay);
				mMapView.refresh();
				mMapController.animateTo(new GeoPoint((int) (nLatitude * 1e6),
						(int) (nLontitude * 1e6)));
				locationClient.stop();
			}

			@Override
			public void onReceivePoi(BDLocation location) {
			}

		});

		locationClient.start();
		locationClient.requestLocation();
	}

	@Override
	protected void onDestroy() {
		mMapView.destroy();
		if (mBMapMan != null) {
			mBMapMan.destroy();
			mBMapMan = null;
		}
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		mMapView.onPause();
		if (mBMapMan != null) {
			mBMapMan.stop();
		}
		super.onPause();
	}

	@Override
	protected void onResume() {
		mMapView.onResume();
		if (mBMapMan != null) {
			mBMapMan.start();
		}
		super.onResume();
	}

	public class OverItemT extends ItemizedOverlay<OverlayItem> {
		private List<OverlayItem> GeoList = new ArrayList<OverlayItem>();
		private Context mContext;
		private double mLat1 = 39.915;
		private double mLon1 = 116.404;
		private ArrayList<String> poString;

		public OverItemT(Drawable marker, Context context, ArrayList<String> url) {
			super(marker);

			this.mContext = context;
			poString = url;
			try {
				for (int i = 0; i < poString.size(); i++) {
					String[] spStr = poString.get(i).toString().split(",");
					GeoPoint p1 = new GeoPoint(
							(int) (Double.valueOf(spStr[1]) * 1E6),
							(int) (Double.valueOf(spStr[0]) * 1E6));
					GeoList.add(new OverlayItem(p1, String.valueOf(i), spStr[2]
							+ spStr[3]));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			// 用给定的经纬度构造GeoPoint，单位是微度 (度 * 1E6)
			// GeoPoint p1 = new GeoPoint((int) (mLat1 * 1E6), (int) (mLon1 *
			// 1E6));
			// GeoList.add(new OverlayItem(p1, "P1", "point1"));

			populate(); // createItem(int)方法构造item。一旦有了数据，在调用其它方法前，首先调用这个方法
		}

		@Override
		protected OverlayItem createItem(int i) {
			return GeoList.get(i);
		}

		@Override
		public int size() {
			return GeoList.size();
		}

		@Override
		// 处理当点击事件
		protected boolean onTap(int i) {
			final String[] spStr = poString.get(i).toString().split(",");
			new AlertDialog.Builder(MapActivity.this)
					.setTitle(spStr[2])
					.setMessage(spStr[3])
					.setIcon(android.R.drawable.ic_dialog_info)
					.setNegativeButton("点餐",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface arg0,
										int arg1) {
									// 数据获取
									myapplication1.setdiqu(spStr[2].toString()); // 传递全局地区选择变量
									Intent Intent1 = new Intent();
									Intent1.setClass(MapActivity.this,
											DiancaiActivity.class);
									Bundle Bundle1 = new Bundle();
									Bundle1.putString("tg", "1");
									Intent1.putExtras(Bundle1);
									startActivity(Intent1);
									finish();
									overridePendingTransition(
											R.anim.in_from_right,
											R.anim.out_to_left);

								}
							}).show();
			// Toast.makeText(this.mContext, GeoList.get(i).getSnippet(),
			// Toast.LENGTH_SHORT).show();
			return true;
		}
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
		finish();
	}
}
