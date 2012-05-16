package jp.yom.rosendb;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jp.yom.rosendb.EkiPassInfo.EkiStopInfo;
import jp.yom.rosendb.EkiPassInfo.EkiThroughInfo;
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
	private HashMap<DiaKey,TrainRouteInfo>	diaMap = new HashMap<DiaKey,TrainRouteInfo>();
	
	/** マスターデータ */
	private	OudNode	rosenProp;
	
	/** 駅データ */
	Eki[]	stations;
	
	
	
	/**************************************************
	 * 
	 * 指定したダイヤIDのDiaKeyをすべて取得する
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
	
	
	/*************************************
	 * 
	 * 
	 * ダイヤ情報を取得する
	 * 
	 * @param diaKey
	 * @return
	 */
	public TrainRouteInfo getRouteInfo( DiaKey diaKey ) {
		
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
					
					String[]	jikokus = conts[n].getString("EkiJikoku").split(",");
					
					DiaKey	key = new DiaKey( diaID, Houkou.NOBORI, n );
					
					diaMap.put( key, new TrainRouteInfo( key, conts[n], buildDiaInfo( jikokus ) ) );
				}
			}

			// くだり
			{
				OudNode[]	conts = diaProps[diaID].getFromMap("Ressya","Kudari").getArray("RessyaCont");
				for( int n=0; n<conts.length; n++ ) {
					
					String[]	jikokus = conts[n].getString("EkiJikoku").split(",");
					
					DiaKey	key = new DiaKey( diaID, Houkou.KUDARI, n );
					
					diaMap.put( key, new TrainRouteInfo( key, conts[n], buildDiaInfo( jikokus ) ) );
				}
			}
		}
	}
	
	static final private Pattern	jikokuPattern = Pattern.compile("^([0-9]);(\\d*)/(\\d*)$|^([0-9]);(\\d+)$|^([0-9])$" );
	
	/*************************************************
	 * 
	 * 
	 * 時刻書式の文字列配列からStopInfoのIteratorを作成する
	 * 
	 * 配列は駅の通りに並んでいることが条件
	 * 但し、上り・下りで並び順が逆
	 * 
	 * @param jikokuStrs
	 * @return
	 */
	private EkiPassInfo[] buildDiaInfo( String[] jikokuStrs ) {
		
		ArrayList<EkiPassInfo>	ekiPassList = new ArrayList<EkiPassInfo>();

		// 駅時刻の解析
		for( int i=0; i<jikokuStrs.length; i++ ) {
			
			Eki	eki = getEki(i);
			
			Matcher	m = jikokuPattern.matcher( jikokuStrs[i] );
			if( m.find() ) {

				EkiPassInfo	info = null;

				if( m.group(1)!=null ) {
					
					// 1;525/526 --全てセットされている
					// 1;525/    --終点
					// 1;   /526 --始発(使われていない？)
				//	int	type = Integer.parseInt( m.group(1) );
					TrainTime	t1 = m.group(2).isEmpty() ? null : new TrainTime(m.group(2));
					TrainTime	t2 = m.group(3).isEmpty() ? null : new TrainTime(m.group(3));
					info = new EkiStopInfo( eki, t1, t2 );

				} else if( m.group(4)!=null ) {
					
					// 1;525--出発を省略
					info = new EkiStopInfo( eki, new TrainTime( m.group(5) ) );

				} else if( m.group(6)!=null ) {

					// 1--取り扱いのみ
					info = new EkiThroughInfo( eki );
				}

				ekiPassList.add( info );

			} else {

				// unknown data
			//	stopInfoList.add( null );

			}
		}
		
		return ekiPassList.toArray( new EkiPassInfo[0] );
	}
}
