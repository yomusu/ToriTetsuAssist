package jp.yom.toritetsuassist;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import jp.yom.rosendb.OudReader.OudParseException;
import jp.yom.rosendb.RosenDatabase;
import jp.yom.rosendb.RosenDatabase.Houkou;
import jp.yom.rosendb.RosenDatabase.RosenIterator;
import jp.yom.rosendb.RosenDatabase.StationInfo;
import jp.yom.rosendb.TrainPassInfo;
import jp.yom.rosendb.TrainTime;
import android.app.TabActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

public class ToriTetsuAssistActivity extends TabActivity {
	
	
	
	private ArrayList<TrainPassInfo>	trainInfoList = new ArrayList<TrainPassInfo>();
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		
		
		// タブのセットアップ
		TabHost	tabHost = getTabHost();
		
		
		
		//--------------------------------------
		// tabContentのセットアップ
		
		// 一覧画面
		ListView	trainListView = new ListView( getApplicationContext() );
		trainListView.setAdapter( new TrainAdapter() );
		trainListView.setId(0);
		
		FrameLayout	tabContent = tabHost.getTabContentView();
		tabContent.addView( trainListView );
		
		
		//---------------------------------------
		// タブのセットアップ
		
		TabSpec	listSpec = tabHost.newTabSpec("list");
		listSpec.setIndicator("一覧");
		listSpec.setContent( 0 );
		tabHost.addTab( listSpec );
		
		// リスト形式
		TabSpec	diaSpec = tabHost.newTabSpec("dia");
		diaSpec.setIndicator("ダイア");
		diaSpec.setContent(R.id.tab2);
		tabHost.addTab( diaSpec );
		
		
		//--------------------------------------
		// テストデータのセットアップ
		
		RosenDatabase	rosen = new RosenDatabase();
		
		try {
			// データの読み込み
			InputStream	in = getClass().getResource("test.oud").openStream();
			rosen.build( new BufferedReader( new InputStreamReader(in,"MS932")) );
			
			// 使用ダイヤは固定
			int	diaID = 0;
			
			// そのダイヤの全列車に対して指定された駅の到着時間を求める
			RosenIterator	it = rosen.getTransiter( diaID, Houkou.NOBORI, 0 );
			
			while( it.hasNext() ) {
				
				StationInfo	s = it.next();
				
				TrainPassInfo	info = new TrainPassInfo();
				
				info.direction = Houkou.NOBORI;
				info.trainName = s.train.getResshaText();
				info.passTime = s.stopInfo.getArriveTime();
				info.timeLeaveOff = s.stopInfo.getArriveTime();
				info.stationFrom = s.eki.getEkimei();
				
				trainInfoList.add( info );
			}
			
			
		} catch( OudParseException e ) {
			
		}catch (IOException e) {

		}
	}
	
	
	
	
	/*************************************************
	 * 
	 * 
	 * 列車情報の一覧を表示するアダプタ
	 * 
	 * 
	 * @author Yomusu
	 *
	 */
	class TrainAdapter extends BaseAdapter {

		public int getCount() {
			return trainInfoList.size();
		}

		public Object getItem(int position) {
			return trainInfoList.get(position);
		}

		@Override
		public int getViewTypeCount() {
			return 1;
		}

		@Override
		public boolean hasStableIds() {
			return true;
		}

		public long getItemId(int position) {
			return position;
		}

		@Override
		public int getItemViewType(int position) {
			return 0;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			
			if( convertView==null ) {
				
				View	v = getLayoutInflater().inflate( R.layout.trainpass_normal, null );
				
				ItemView	item = new ItemView();
				
				item.direction = (TextView)v.findViewById(R.id.direction);
				item.trainName = (TextView)v.findViewById(R.id.trainName);
				item.passTime = (TextView)v.findViewById(R.id.passTime);
				item.leavedTime = (TextView)v.findViewById(R.id.leavedTime );
				item.station = (TextView)v.findViewById(R.id.station );
				
				v.setTag( item );
				convertView = v;
			}
			
			ItemView	item = (ItemView)convertView.getTag();
			
			TrainPassInfo	pass = trainInfoList.get(position);
			item.direction.setText( pass.direction.toString() );
			item.trainName.setText( pass.trainName );
			item.passTime.setText( (pass.passTime!=null) ? pass.passTime.toString() : "--" );
			item.leavedTime.setText( (pass.timeLeaveOff!=null) ? pass.timeLeaveOff.toString() : "--" );
			item.station.setText( pass.stationFrom );
			
			return convertView;
		}
		
	}
	
	static class ItemView {
		
		TextView	direction;
		TextView	trainName;
		TextView	passTime;
		TextView	leavedTime;
		TextView	station;
	}

}