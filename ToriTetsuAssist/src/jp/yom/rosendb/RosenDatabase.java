package jp.yom.rosendb;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import jp.yom.rosendb.DiaTrainInfo.DiaKey;
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
	
	
	public Eki getEki( int ekiID ) {
		
		return stations[ekiID];
	}
	
	
	public DiaTrainInfo getDiaTrainInfo( DiaKey diaKey ) {
		
		return diaMap.get( diaKey );
		
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
