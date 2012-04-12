package jp.yom.rosendb;

import java.io.BufferedReader;
import java.io.IOException;

import jp.yom.rosendb.OudReader.OudParseException;
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
	
	
	
	
	class TrainTransiter {
		
		int	position = 0;
		
		public TrainTransiter() {
			
			// 始発駅まで早送り
			
		}
		
		/******************************
		 * 
		 * 終点かどうか
		 * 
		 * @return
		 */
		public boolean hasNext() {
			return false;
		}
		
		
		/******************************
		 * 
		 * 次の駅へ行く
		 * 
		 * @return
		 */
		public StationInfo next() {
			
			StationInfo	s = new StationInfo();
			
			s.eki = stations[position];
			
			return s;
		}
		
	}
	
	
	static class StationInfo {
		
		Eki		eki;
		TrainTime	arriveTime;
		TrainTime	leaveTime;
	}
	
	
	//==================================================
	// メンバ変数の宣言
	//==================================================
	
	/** ダイア */
	private Dia[]	dias = new Dia[0];
	
	
	/** マスターデータ */
	private	Prop	rosenProp;
	
	
	Eki[]	stations;
	
	
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
	public TrainTransiter getTransiter( int diaID, Houkou dir, int ressyaid ) {
		return new TrainTransiter();
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
	public void build( BufferedReader reader ) throws IOException, OudParseException {
		
		Prop	oud = OudReader.parseOudBlock( reader );
		
		rosenProp = oud.getOud("Rosen");
		
		Prop[]	props = rosenProp.getArray("Eki");
		
		stations = new Eki[props.length];
		for( int i=0; i<props.length; i++ )
			stations[i] = new Eki( i, props[i] );
		
		
		dias[0] = new Dia( rosenProp.getArray("Dia")[0] );
	}
	
}
