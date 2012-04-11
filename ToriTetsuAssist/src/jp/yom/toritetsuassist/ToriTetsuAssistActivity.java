package jp.yom.toritetsuassist;

import java.util.ArrayList;

import jp.yom.rosendb.RosenDatabase.Houkou;
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
		TrainPassInfo	info = new TrainPassInfo();
		info.direction = Houkou.KUDARI;
		info.trainName = "あずさ";
		info.passTime = new TrainTime(13, 30, 50);
		info.leavedTime = new TrainTime(13,27,00);
		info.station = "日野";
		
		trainInfoList.add( info );
		

	}
	
	
	class TrainAdapter extends BaseAdapter {

		public int getCount() {
			return trainInfoList.size();
		}

		public Object getItem(int position) {
			return trainInfoList.get(position);
		}

		public long getItemId(int position) {
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
			
			item.direction.setText( trainInfoList.get(position).direction.toString() );
			item.trainName.setText( trainInfoList.get(position).trainName );
			item.passTime.setText( trainInfoList.get(position).passTime.toString() );
			item.leavedTime.setText( trainInfoList.get(position).leavedTime.toString() );
			item.station.setText( trainInfoList.get(position).station );
			
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