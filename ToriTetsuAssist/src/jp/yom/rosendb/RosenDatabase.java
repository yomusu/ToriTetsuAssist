package jp.yom.rosendb;

import java.util.LinkedHashMap;
import java.util.TreeMap;

import jp.yom.rosendb.OudReader.Prop;


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
	
	static class Eki {
		
		/** 駅ID */
		private int		id;
		/** プロパティ */
		private Prop		prop;
		
		public Eki( int id, Prop prop ) {
			this.prop = prop;
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
	 * 
	 * 列車一本の運行スケジュール
	 * 
	 */
	static class TrainSchedule {
		
		Prop	prop;
		
		/** 方向 */
		Houkou	houkou;
		
		/** 列車種別 */
		Ressyasyubetsu	ressha;
		
		/** 列車番号 */
		String	ressyabangou;
		/** 列車名 */
		String	ressyamei;
		/** 備考 */
		String	bikou;
		/** 号数 */
		String	gousuu;
		
		String	jikoku;
		
		
		private TrainSchedule( Prop src ) {
			this.prop = src;
		}
		
		/***************************************
		 * 
		 * 指定した駅の到着時間を返します
		 * 
		 * @param ekiID
		 * @return
		 */
		public TrainTime getTimeArriveOn( int ekiID ) {
			
			return null;
		}
		
		/***************************************
		 * 
		 * 指定した駅の出発時間を返します
		 * 
		 * @param ekiID
		 * @return
		 */
		public TrainTime getTimeLeaveOff( int ekiID ) {
			
			return null;
		}
	}
	
	
	
	static class Dia {
		
		/** ダイア名 */
		String	diaName;
		
		/** キーに */
	//	LinkedHashMap<String,RessyaCont[]>	ressyaMap;
		
		Prop	prop;
		
		TrainSchedule[]	noboriTrasits;
		
		
		public Dia( Prop prop ) {
			
			
			Prop[]	conts = prop.getFromMap("Ressya","Nobori").getArray("RessyaCont");
			
			noboriTrasits = new TrainSchedule[conts.length];
			
			for( int i=0; i<conts.length; i++ )
				noboriTrasits[i] = new TrainSchedule( conts[i] );
			
		}
		
		// 駅間と時刻を指定して、来る順番に並べたRessyaContを返すメソッドとか
		
		public TrainPassInfo[] createTimeTableOf( int eki  ) {
			
			// 指定した駅の上りの時刻表を作成してみるテスト
			TreeMap<TrainTime,TrainSchedule>		treeMap = new TreeMap<TrainTime,TrainSchedule>();
			
			for( TrainSchedule train : noboriTrasits ) {
				TrainTime	t = train.getTimeArriveOn( eki );
				
				treeMap.put( t, train );
			}
			
			return null;
		}
	}
	
	
	
	//==================================================
	// メンバ変数の宣言
	//==================================================
	
	/** ダイア */
	private Dia[]	dias = new Dia[0];
	
	
	/** マスターデータ */
	private	Prop	rosenProp;
	
	
	
	/************************************
	 * 
	 * 駅を駅配列で取得する
	 * 
	 * @return
	 */
	public Eki[] getEkis() {
		
		Prop[]	props = rosenProp.getArray("Eki");
		
		Eki[]	result = new Eki[props.length];
		for( int i=0; i<props.length; i++ )
			result[i] = new Eki( i, props[i] );
		
		return result;
	}
	
	
	/************************************
	 * 
	 * 保持している全てのダイヤを取得
	 * 
	 * @return
	 */
	public Dia[] getDias() {
		return dias;
	}
	
	
	/************************************
	 * 
	 * Oudファイルからデータベースを構築する
	 */
	public void build( Prop oud ) {
		
		rosenProp = oud.getOud("Rosen");
		
	}
	
}
