package jp.yom.toritetsuassist;

import java.util.ArrayList;

import jp.yom.toritetsuassist.TrainPassInfo.Direction;
import jp.yom.toritetsuassist.TrainPassInfo.TrainTime;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class TrainInfoListActivity extends Activity {
	
	
	private ListView	listView;
	
	private ArrayList<TrainPassInfo>	trainInfoList;
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.train_list);
		
		
		//--------------------------------------
		// テストデータのセットアップ
		trainInfoList = new ArrayList<TrainPassInfo>();
		
		TrainPassInfo	info = new TrainPassInfo();
		info.direction = Direction.KUDARI;
		info.trainName = "あずさ";
		info.passTime = new TrainTime(13, 30, 50);
		info.leavedTime = new TrainTime(13,27,00);
		info.station = "日野";
		
		trainInfoList.add( info );
		
		//--------------------------------------
		// リストビューのセットアップ
		listView = (ListView)findViewById(R.id.listView1);
		listView.setAdapter( new TrainAdapter() );
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
