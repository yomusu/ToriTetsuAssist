package jp.yom.toritetsuassist;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

public class ToriTetsuAssistActivity extends TabActivity {
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		// タブのセットアップ
		TabHost	tabHost = getTabHost();
		
		//---------------------------------------
		// リスト形式
		TabSpec	listSpec = tabHost.newTabSpec("list");
		listSpec.setIndicator("一覧");
		listSpec.setContent(  new Intent( this, TrainInfoListActivity.class ) );
		tabHost.addTab( listSpec );
		
		// リスト形式
		TabSpec	diaSpec = tabHost.newTabSpec("list");
		diaSpec.setIndicator("ダイア");
		diaSpec.setContent(R.id.tab2);
		tabHost.addTab( diaSpec );
	}
}