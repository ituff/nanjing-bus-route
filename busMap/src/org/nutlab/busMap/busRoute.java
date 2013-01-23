package org.nutlab.busMap;

import android.R.integer;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.nutlab.busMap.R;
import org.nutlab.busMap.BMapApiDemoApp;

import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.GeoPoint;
import com.baidu.mapapi.MKAddrInfo;
import com.baidu.mapapi.MKBusLineResult;
import com.baidu.mapapi.MKDrivingRouteResult;
import com.baidu.mapapi.MKLine;
import com.baidu.mapapi.MKPlanNode;
import com.baidu.mapapi.MKPoiResult;
import com.baidu.mapapi.MKSearch;
import com.baidu.mapapi.MKSearchListener;
import com.baidu.mapapi.MKSuggestionResult;
import com.baidu.mapapi.MKTransitRoutePlan;
import com.baidu.mapapi.MKTransitRouteResult;
import com.baidu.mapapi.MKWalkingRouteResult;
import com.baidu.mapapi.MapActivity;
import com.baidu.mapapi.MapController;
import com.baidu.mapapi.MapView;
import com.baidu.mapapi.TransitOverlay;

public class busRoute extends MapActivity {

	private Button mBtnTransit = null; // 公交搜索
	private ListView busList;// 公交列表
	private ListView routeList;

	private MapView mMapView = null; // 地图View
	private MKSearch mSearch = null; // 搜索模块，也可去掉地图模块独立使用
	private MapController mapController;

	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.busroute);

		BMapApiDemoApp app = (BMapApiDemoApp) this.getApplication();
		if (app.mBMapMan == null) {
			app.mBMapMan = new BMapManager(getApplication());
			app.mBMapMan.init(app.mStrKey,
					new BMapApiDemoApp.MyGeneralListener());
		}
		app.mBMapMan.start();
		// 如果使用地图SDK，请初始化地图Activity
		super.initMapActivity(app.mBMapMan);

		mMapView = (MapView) findViewById(R.id.bmapView);
		mMapView.setBuiltInZoomControls(true);
		// 设置在缩放动画过程中也显示overlay,默认为不绘制
		mMapView.setDrawOverlayWhenZooming(true);

		GeoPoint geoPoint = new GeoPoint((int) (32.05000 * 1E6),
				(int) (118.78333 * 1E6));

		// 取得地图控制器对象，用于控制MapView
		mapController = mMapView.getController();
		// 设置地图的中心
		mapController.setCenter(geoPoint);
		// 设置地图默认的缩放级别
		mapController.setZoom(12);

		// 初始化搜索模块，注册事件监听
		mSearch = new MKSearch();
		mSearch.init(app.mBMapMan, new MKSearchListener() {

			@Override
			public void onGetPoiDetailSearchResult(int type, int error) {
			}

			public void onGetDrivingRouteResult(MKDrivingRouteResult res,
					int error) {
				/*
				 * // 错误号可参考MKEvent中的定义 if (error != 0 || res == null) {
				 * Toast.makeText(busRoute.this, "抱歉，未找到结果",
				 * Toast.LENGTH_SHORT).show(); return; } RouteOverlay
				 * routeOverlay = new RouteOverlay(busRoute.this, mMapView); //
				 * 此处仅展示一个方案作为示例
				 * routeOverlay.setData(res.getPlan(0).getRoute(0));
				 * mMapView.getOverlays().clear();
				 * mMapView.getOverlays().add(routeOverlay);
				 * mMapView.invalidate();
				 * 
				 * mMapView.getController().animateTo(res.getStart().pt);
				 */
			}

			public void onGetTransitRouteResult(MKTransitRouteResult res,
					int error) {
				if (error != 0 || res == null) {
					Toast.makeText(busRoute.this, "抱歉，未找到结果",
							Toast.LENGTH_SHORT).show();
					mMapView.setVisibility(View.VISIBLE);
					busList.setVisibility(View.GONE);
					return;
				}
				/*
				 * TransitOverlay routeOverlay = new
				 * TransitOverlay(busRoute.this, mMapView); // 此处仅展示一个方案作为示例
				 * routeOverlay.setData(res.getPlan(0));
				 * mMapView.getOverlays().clear();
				 * mMapView.getOverlays().add(routeOverlay);
				 * mMapView.invalidate();
				 * 
				 * mMapView.getController().animateTo(res.getStart().pt);
				 */

				MyAdapter myAdapter = new MyAdapter(busRoute.this, res);
				busList.setAdapter(myAdapter);

			}

			public void onGetWalkingRouteResult(MKWalkingRouteResult res,
					int error) {
				/*
				 * if (error != 0 || res == null) {
				 * Toast.makeText(busRoute.this, "抱歉，未找到结果",
				 * Toast.LENGTH_SHORT).show(); return; } RouteOverlay
				 * routeOverlay = new RouteOverlay(busRoute.this, mMapView); //
				 * 此处仅展示一个方案作为示例
				 * routeOverlay.setData(res.getPlan(0).getRoute(0));
				 * mMapView.getOverlays().clear();
				 * mMapView.getOverlays().add(routeOverlay);
				 * mMapView.invalidate();
				 * mMapView.getController().animateTo(res.getStart().pt);
				 */

			}

			public void onGetAddrResult(MKAddrInfo res, int error) {
			}

			public void onGetPoiResult(MKPoiResult res, int arg1, int arg2) {

			}

			public void onGetBusDetailResult(MKBusLineResult result, int iError) {
			}

			@Override
			public void onGetSuggestionResult(MKSuggestionResult res, int arg1) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onGetRGCShareUrlResult(String arg0, int arg1) {
				// TODO Auto-generated method stub

			}
		});

		// 设定搜索按钮的响应
		busList = (ListView) findViewById(R.id.bus_list);
		busList.setCacheColorHint(0);
		routeList = (ListView) findViewById(R.id.route_list);
		routeList.setCacheColorHint(0);
		mBtnTransit = (Button) findViewById(R.id.transit);

		OnClickListener clickListener = new OnClickListener() {
			public void onClick(View v) {
				mMapView.setVisibility(View.GONE);
				busList.setVisibility(View.VISIBLE);
				SearchButtonProcess(v);
			}
		};
		mBtnTransit.setOnClickListener(clickListener);
	}

	void SearchButtonProcess(View v) {
		// 处理搜索按钮响应
		EditText editSt = (EditText) findViewById(R.id.start);
		EditText editEn = (EditText) findViewById(R.id.end);

		// 对起点终点的name进行赋值，也可以直接对坐标赋值，赋值坐标则将根据坐标进行搜索
		MKPlanNode stNode = new MKPlanNode();
		stNode.name = editSt.getText().toString();
		MKPlanNode enNode = new MKPlanNode();
		enNode.name = editEn.getText().toString();

		// 实际使用中请对起点终点城市进行正确的设定
		if (mBtnTransit.equals(v)) {
			mSearch.transitSearch("南京", stNode, enNode);
		}
	}

	@Override
	protected void onPause() {
		BMapApiDemoApp app = (BMapApiDemoApp) this.getApplication();
		app.mBMapMan.stop();
		super.onPause();
	}

	@Override
	protected void onResume() {
		BMapApiDemoApp app = (BMapApiDemoApp) this.getApplication();
		app.mBMapMan.start();
		super.onResume();
	}

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}

	// 填充公交列表
	class MyAdapter extends BaseAdapter {
		private MKTransitRouteResult res;
		private LayoutInflater mInflater;
		
		class HolderView {
			public TextView txt;
		}

		public MyAdapter(Context context, MKTransitRouteResult res) {
			this.res = res;
			this.mInflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			return res.getNumPlan();
		}

		@Override
		public Object getItem(int position) {
			return position;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			HolderView holder = null;
			if (convertView == null) {
				holder = new HolderView();
				convertView = mInflater.inflate(R.layout.list_item, null);
				holder.txt = (TextView) convertView
						.findViewById(R.id.list_item_txt);
				convertView.setTag(holder);
			} else {
				holder = (HolderView) convertView.getTag();
			}
			String lineInfo = "";
			// 得到解决方案
			MKTransitRoutePlan routePlan = res.getPlan(position);
			// 公交线路
			MKLine mkLine = routePlan.getLine(0);
			lineInfo += "w>b"
					+ mkLine.getTitle().substring(0,
							mkLine.getTitle().indexOf("("));
			int[] busPosition = new int[routePlan.getNumLines() + 5];
			int[] busOrMetro = new int[routePlan.getNumLines() + 5];
			if (routePlan.getNumLines() > 0) {
				// 循环当前方案公交路线
				for (int i = 1; i < routePlan.getNumLines(); i++) {
					// 公交线路
					mkLine = routePlan.getLine(i);
					busPosition[i] = lineInfo.length() + 1;
					if (mkLine.getTitle().contains("地铁")) {
						busOrMetro[i] = 1;
					} else {
						busOrMetro[i] = 0;
					}
					lineInfo += ">b"
							+ mkLine.getTitle().substring(0,
									mkLine.getTitle().indexOf("("));

				}
			}
			SpannableString spanStr = new SpannableString(lineInfo);
			ImageSpan walk = new ImageSpan(busRoute.this, R.drawable.walk);
			ImageSpan bus = new ImageSpan(busRoute.this, R.drawable.bus);
			spanStr.setSpan(walk, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			spanStr.setSpan(bus, 2, 3, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			for (int i = 1; i < routePlan.getNumLines(); i++) {
				ImageSpan busIcon = new ImageSpan(busRoute.this, R.drawable.bus);
				ImageSpan metroIcon = new ImageSpan(busRoute.this,
						R.drawable.metro);
				if (busOrMetro[i] == 0) {
					spanStr.setSpan(busIcon, busPosition[i],
							busPosition[i] + 1,
							Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				}
				if (busOrMetro[i] == 1) {
					spanStr.setSpan(metroIcon, busPosition[i],
							busPosition[i] + 1,
							Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				}
			}
			holder.txt.setText(spanStr);
			convertView
					.setOnClickListener(new MyListViewOnClick(position, res));
			return convertView;
		}
		class MyListViewOnClick implements OnClickListener {
			private int index;
			private MKTransitRouteResult res;

			public MyListViewOnClick(int index, MKTransitRouteResult res) {
				this.index = index;
				this.res = res;
			}

			@Override
			public void onClick(View arg0) {
				/*
				 * TransitOverlay routeOverlay = new TransitOverlay(busRoute.this,
				 * mMapView); routeOverlay.setData(res.getPlan(index));
				 * mMapView.getOverlays().clear();
				 * mMapView.getOverlays().add(routeOverlay); mMapView.invalidate();
				 * mMapView.getController().animateTo(res.getStart().pt);
				 * busList.setVisibility(View.GONE);
				 * mMapView.setVisibility(View.VISIBLE);
				 */
				/*
				 * Intent intent = new Intent(); Bundle bundle = new Bundle();
				 * intent.setClass(busRoute.this, route.class);
				 * MKTransitRoutePlanParcelabe mkTransitRoutePlanParcelabe = new
				 * MKTransitRoutePlanParcelabe();
				 * mkTransitRoutePlanParcelabe.setMkTransitRoutePlan(res
				 * .getPlan(index)); bundle.putParcelable("route",
				 * mkTransitRoutePlanParcelabe); intent.putExtras(bundle);
				 * //startActivityForResult(intent, 0); startActivity(intent);
				 */

				busList.setVisibility(View.GONE);
				routeList.setVisibility(View.VISIBLE);
				MyAdapter2 myAdapter2 = new MyAdapter2(busRoute.this, res, index);
				routeList.setAdapter(myAdapter2);
			}
		}
	}

	// 填充路径详情
	class MyAdapter2 extends BaseAdapter {
		private MKTransitRouteResult res;
		private LayoutInflater mInflater;
		private int index;
		private MKTransitRoutePlan mkTransitRoutePlan;

		class HolderView {
			public TextView txt;
		}
		
		public MyAdapter2(Context context, MKTransitRouteResult res, int index) {
			this.res = res;
			this.mInflater = LayoutInflater.from(context);
			this.index = index;
			this.mkTransitRoutePlan = res.getPlan(index);
		}

		@Override
		public int getCount() {
			return mkTransitRoutePlan.getNumLines();
		}

		@Override
		public Object getItem(int position) {
			return position;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			HolderView holder = null;
			if (convertView == null) {
				holder = new HolderView();
				convertView = mInflater.inflate(R.layout.list_item, null);
				holder.txt = (TextView) convertView
						.findViewById(R.id.list_item_txt);
				convertView.setTag(holder);
			} else {
				holder = (HolderView) convertView.getTag();
			}
			String lineInfo = "";
			MKLine mkLine = mkTransitRoutePlan.getLine(position);
			if (position == 0) {
				if (mkTransitRoutePlan.getNumLines() == 1) {
					lineInfo += "^起点：" + mkLine.getGetOnStop().name;
					if (mkLine.getTitle().contains("地铁")) {
						lineInfo += "\n$乘坐!" + mkLine.getTitle();
					} else {
						lineInfo += "\n$乘坐@" + mkLine.getTitle();
					}
					lineInfo += "\n&终点：" + mkLine.getGetOffStop().name;
				} else {
					lineInfo += "^起点：" + mkLine.getGetOnStop().name;
					if (mkLine.getTitle().contains("地铁")) {
						lineInfo += "\n$乘坐!" + mkLine.getTitle();
					} else {
						lineInfo += "\n$乘坐@" + mkLine.getTitle();
					}
					lineInfo += "\n~换乘点：" + mkLine.getGetOffStop().name;
				}
			}
			if (position > 0) {
				if (position < mkTransitRoutePlan.getNumLines() - 1) {
					if (mkLine.getTitle().contains("地铁")) {
						lineInfo += "\n$乘坐!" + mkLine.getTitle();
					} else {
						lineInfo += "\n$乘坐@" + mkLine.getTitle();
					}
					lineInfo += "\n~换乘点：" + mkLine.getGetOffStop().name;
				}
				if (position == mkTransitRoutePlan.getNumLines() - 1) {
					if (mkLine.getTitle().contains("地铁")) {
						lineInfo += "\n$乘坐!" + mkLine.getTitle();
					} else {
						lineInfo += "\n$乘坐@" + mkLine.getTitle();
					}
					lineInfo += "\n&终点：" + mkLine.getGetOffStop().name;
				}
			}

			SpannableString spanStr = new SpannableString(lineInfo);
			ImageSpan start = new ImageSpan(busRoute.this, R.drawable.start3);
			ImageSpan end = new ImageSpan(busRoute.this, R.drawable.end3);
			ImageSpan bus = new ImageSpan(busRoute.this, R.drawable.bus);
			ImageSpan change = new ImageSpan(busRoute.this, R.drawable.change);
			ImageSpan go = new ImageSpan(busRoute.this, R.drawable.done);
			ImageSpan metro = new ImageSpan(busRoute.this, R.drawable.metro);
			if (lineInfo.contains("^")) {
				spanStr.setSpan(start, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			}
			if (lineInfo.contains("$")) {
				spanStr.setSpan(go, lineInfo.indexOf("$"),
						lineInfo.indexOf("$") + 1,
						Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			}
			if (lineInfo.contains("@")) {
				spanStr.setSpan(bus, lineInfo.indexOf("@"),
						lineInfo.indexOf("@") + 1,
						Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			}
			if (lineInfo.contains("&")) {
				spanStr.setSpan(end, lineInfo.indexOf("&"),
						lineInfo.indexOf("&") + 1,
						Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			}
			if (lineInfo.contains("~")) {
				spanStr.setSpan(change, lineInfo.indexOf("~"),
						lineInfo.indexOf("~") + 1,
						Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			}
			if (lineInfo.contains("!")) {
				spanStr.setSpan(metro, lineInfo.indexOf("!"),
						lineInfo.indexOf("!") + 1,
						Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			}
			holder.txt.setText(spanStr);
			convertView.setOnClickListener(new MyListViewOnClick2(
					mkTransitRoutePlan));
			return convertView;
		}

		class MyListViewOnClick2 implements OnClickListener {

			private MKTransitRoutePlan plan = null;

			public MyListViewOnClick2(MKTransitRoutePlan plan) {
				this.plan = plan;
			}

			@Override
			public void onClick(View arg0) {

				TransitOverlay routeOverlay = new TransitOverlay(busRoute.this,
						mMapView);
				routeOverlay.setData(plan);
				mMapView.getOverlays().clear();
				mMapView.getOverlays().add(routeOverlay);
				mMapView.invalidate();
				mMapView.getController().animateTo(plan.getStart());
				routeList.setVisibility(View.GONE);
				mMapView.setVisibility(View.VISIBLE);

			}
		}
	}
}