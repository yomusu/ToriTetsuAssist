package jp.yom.rosendb;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import jp.yom.rosendb.DiaTrainInfo.DiaKey;
import jp.yom.rosendb.DiaTrainInfo.StopInfo;
import jp.yom.rosendb.OudReader.OudNode;
import jp.yom.rosendb.OudReader.OudParseException;


/****************************************
 * 
 * 
 * 
 * 路線データベース
 * 
 * 
 * @author matsumoto
 *
 */
public class RosenDatabase {
	
	
	//==================================================
	// 定数宣言
	//==================================================
	
	public enum Houkou {
		/** 下り */
		KUDARI,
		/** 上り */
		NOBORI,
	}
	
	
	//==================================================
	// クラス宣言
	//==================================================
	
	static public class Eki {
		
		/** 駅ID */
		final public int		id;
		/** プロパティ */
		private OudNode		prop;
		
		public Eki( int id, OudNode prop ) {
			this.prop = prop;
			this.id = id;
		}
		
		/** 駅の表示用テキスト */
		public String getEkimei() { return prop.getString("Ekimei"); }
		
	}
	
	
	static class Ressyasyubetsu {
		String	syubetsumei;
		String	ryakusyou;
	}
	
	
	
	/**********************************
	 * 
	 * これっているのか？
	 * 
	 * @author Yomusu
	 *
	 */
	public interface RosenIterator {
		
		/** 列車情報を取得 */
		public DiaTrainInfo getTrainInfo();
		
		/** 終点かどうか */
		public boolean hasNext();
		
		/** 次の駅へ行く */
		public StationInfo next();
		
	}
	
	
	static public class StationInfo {
		
		public Eki		eki;
		TrainTime	arriveTime;
		TrainTime	leaveTime;
		
		public StopInfo	stopInfo;
	}
	
	
	
	//==================================================
	// メンバ変数の宣言
	//==================================================
	
	/** ダイアの名前マップ */
	private final HashMap<Integer,String>	diaNameMap = new HashMap<Integer,String>();
	
	/** ダイヤマップ */
	private HashMap<DiaKey,DiaTrainInfo>	diaMap = new HashMap<DiaKey,DiaTrainInfo>();
	
	/** マスターデータ */
	private	OudNode	rosenProp;
	
	/** 駅データ */
	Eki[]	stations;
	
	
	
	/**************************************************
	 * 
	 * 
	 * 
	 * @param diaID
	 * @return
	 */
	public DiaKey[] correctDiaKey( int diaID ) {
		
		ArrayList<DiaKey>	result = new ArrayList<DiaKey>();
		
		for( DiaKey key : diaMap.keySet() ) {
			if( key.getDiaID()==diaID )
				result.add( key );
		}
		
		return result.toArray( new DiaKey[0] );
	}
	
	/*************************************************
	 * 
	 * 
	 * 列車運行イテレータを作成して返します
	 * 
	 * @param diaID
	 * @param dir
	 * @param ressyaid
	 * @return
	 */
	public RosenIterator getTransiter( DiaKey diaKey ) {
		
		// 列車情報
		final DiaTrainInfo	train = diaMap.get( diaKey );
		
		final Iterator<StopInfo>	it = train.getStopInfoIter();
		
		return new RosenIterator() {
			
			@Override
			public DiaTrainInfo getTrainInfo() {
				return train;
			}
			
			@Override
			public boolean hasNext() {
				return it.hasNext();
			}
			
			@Override
			public StationInfo next() {
				
				StationInfo	s = new StationInfo();
				s.stopInfo = it.next();
				// 駅
				s.eki = stations[s.stopInfo.ekiID];
				
				return s;
			}
			
		};
	}
	
	
	
	/************************************
	 * 
	 * Oudファイルからデータベースを構築する
	 */
	public void build( BufferedReader reader ) throws IOException, OudParseException {
		
		OudNode	oud = OudReader.parseOudBlock( reader );
		
		rosenProp = oud.getOud("Rosen");
		
		
		//----------------------------
		// 駅情報作成
		{
			OudNode[]	props = rosenProp.getArray("Eki");
			stations = new Eki[props.length];
			for( int i=0; i<props.length; i++ )
				stations[i] = new Eki( i, props[i] );
		}
		
		//----------------------------
		// ダイヤ情報作成
		OudNode[]	diaProps = rosenProp.getArray("Dia");
		for( int diaID=0; diaID<diaProps.length; diaID++ ) {
			
			//------------------------
			// 各列車の停車情報を作成
			
			// のぼり
			{
				OudNode[]	conts = diaProps[diaID].getFromMap("Ressya","Nobori").getArray("RessyaCont");
				for( int n=0; n<conts.length; n++ ) {
					DiaKey	key = new DiaKey( diaID, Houkou.NOBORI, n );
					diaMap.put( key, new DiaTrainInfo( key, conts[n] ) );
				}
			}

			// くだり
			{
				OudNode[]	conts = diaProps[diaID].getFromMap("Ressya","Kudari").getArray("RessyaCont");
				for( int n=0; n<conts.length; n++ ) {
					DiaKey	key = new DiaKey( diaID, Houkou.KUDARI, n );
					diaMap.put( key, new DiaTrainInfo( key, conts[n] ) );
				}
			}
		}
	}
	
}
