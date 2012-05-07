package jp.yom.rosendb;

import jp.yom.rosendb.RosenDatabase.Eki;


/******************************************
 * 
 * 
 * 各駅の通過情報を示す
 * 
 * 
 * @author Yomusu
 *
 */
public class EkiPassInfo {
	
	
	/************************************
	 * 
	 * 通過
	 * 
	 * @author Yomusu
	 *
	 */
	static public class EkiThroughInfo extends EkiPassInfo {
		
		public EkiThroughInfo( Eki eki ) {
			super(eki);
			this.arriveTime = null;
			this.leaveTime = null;
		}
	}
	
	/*************************************
	 * 
	 * 停車
	 * 
	 * @author Yomusu
	 *
	 */
	static public class EkiStopInfo extends EkiPassInfo {
		
		public EkiStopInfo( Eki eki, TrainTime arrive, TrainTime leave ) {
			super(eki);
			this.arriveTime = arrive;
			this.leaveTime = leave;
		}
		
		/** 出発を省略系 */
		public EkiStopInfo( Eki eki, TrainTime arriveAndLeave ) {
			super(eki);
			this.arriveTime = arriveAndLeave;
			this.leaveTime = arriveAndLeave;
		}
	}
	
	
	//=================================================================
	// メンバ変数の宣言
	//=================================================================
	
	/** 駅ID */
	public final Eki	eki;
	
	/** 出発時刻 */
	protected TrainTime	leaveTime;
	/** 到着時刻 */
	protected TrainTime	arriveTime;
	
	
	//=================================================================
	// コンストラクタ
	//=================================================================
	
	protected EkiPassInfo( Eki eki ) {
		this.eki = eki;
		
	}
	
	public String toString() {
		StringBuilder	buf = new StringBuilder();
	//	buf.append( type ).append(";");
		buf.append( arriveTime ).append("/").append( leaveTime );
		return buf.toString();
	}
	
	public TrainTime getArriveTime() {  return arriveTime; }
	
	
	
	
	/************************************************
	 * 
	 * 
	 * 駅通過情報専用イテレータ
	 * 
	 * @author Yomusu
	 *
	 */
	static public class EkiPassIterator {
		
		int	pos = 0;
		private final EkiPassInfo[]	infos;
		
		/** 最後の停車駅 */
		EkiStopInfo	lastStopEki = null;
		
		/** 最後の駅のIndex */
		int	shutenIndex;
		
		
		public EkiPassIterator( EkiPassInfo[] infos ) {
			
			this.infos = infos;
			
			// 終点のIndexを求める
			for( int i=infos.length-1; i>=0; i-- ) {
				if( infos[i] instanceof EkiStopInfo ) {
					shutenIndex = i;
					break;
				}
			}
		}
		
		/***********************************
		 * 
		 * 次の駅へ行く
		 * 通過も含めて
		 * 
		 * @return
		 */
		public EkiPassInfo nextEki() {
			EkiPassInfo	result = infos[pos];
			pos++;
			
			// 停車駅ならLastStopを書き換え
			if( result instanceof EkiStopInfo )
				lastStopEki = (EkiStopInfo)result;
			
			return result;
		}
		
		/***********************************
		 * 
		 * 次の停車駅へ行く
		 * 通過はすっ飛ばす
		 * 
		 * @return
		 */
//		public EkiStopInfo nextStopEki() {
//			return null;
//		}
		
		/***********************************
		 * 
		 * 次の停車駅があるかどうか
		 * 
		 * @return
		 */
		public boolean hasNextEki() {
			
			if( pos >= shutenIndex )
				return false;
			
			return false;
		}
		
		
		/************************************
		 * 
		 * 最後に停車した駅を取得する
		 * @return
		 */
		public EkiStopInfo getLastStopEki() {
			return lastStopEki;
		}
	}

}
