package jp.yom.rosendb;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jp.yom.rosendb.OudReader.OudNode;
import jp.yom.rosendb.RosenDatabase.Houkou;


/**********************************
 * 
 * 
 * ダイヤの列車一本の情報
 * 
 * ・列車情報
 * ・運行スケジュール
 * 
 * 
 */

public class DiaTrainInfo {
	
	
	
	/***************************************
	 * 
	 * 
	 * ダイヤ上の列車一本情報を識別するID
	 * 
	 * @author Yomusu
	 *
	 */
	static public class DiaKey {
		
		final private int		diaID;
		final private Houkou	houkou;
		final private int		ressyaID;
		
		DiaKey( int diaID, Houkou dir, int ressyaID ) {
			this.diaID = diaID;
			this.houkou = dir;
			this.ressyaID = ressyaID;
		}
		
		/** ダイヤIDを取得 */
		public int getDiaID() { return diaID; }
		
		/** 方向を取得 */
		public Houkou getHoukou() { return houkou; }
		
		/** 列車IDを取得 */
		public int getRessyaID() { return ressyaID; }

		@Override
		public boolean equals(Object o) {
			if( o instanceof DiaKey ) {
				DiaKey	d = (DiaKey)o;
				return (this.diaID==d.diaID) && (this.houkou==d.houkou) && (this.ressyaID==d.ressyaID);
			}
			return super.equals(o);
		}

		@Override
		public int hashCode() {
			return (diaID * 10000) + ressyaID + houkou.hashCode();
		}
		
		
	}
	
	
	
	
	static final private Pattern	jikokuPattern = Pattern.compile("^([0-9]);(\\d*)/(\\d*)$|^([0-9]);(\\d+)$|^([0-9])$" );
	
	
	/** ダイヤ識別キー */
	final public DiaKey	key;
	
	/** 元データ */
	private OudNode	prop;
	
	/** 列車種別のID */
	int	resshaID;
	
	/** 備考 */
	String	bikou;
	
	/** 表示用列車名(独自&Lazy) */
	private String	resshaText;
	
	
	/** 停車情報 */
	ArrayList<StopInfo>	stopInfoList = new ArrayList<StopInfo>();
	
	
	public DiaTrainInfo( DiaKey key, OudNode src ) {
		
		this.key = key;
		this.prop = src;
		
		// 列車種別ID
		resshaID = Integer.parseInt( prop.getString("Syubetsu") );
		
		// 駅時刻の解析
		String[]	strs = prop.getString("EkiJikoku").split(",");
		
		for( int i=0; i<strs.length; i++ ) {
			
			String s = strs[i];
			
			Matcher	m = jikokuPattern.matcher( s );
			if( m.find() ) {

				StopInfo	info = null;

				if( m.group(1)!=null ) {
					
					// 1;525/526 --全てセットされている
					// 1;525/    --終点
					// 1;   /526 --始発(使われていない？)
					int	type = Integer.parseInt( m.group(1) );
					TrainTime	t1 = m.group(2).isEmpty() ? null : new TrainTime(m.group(2));
					TrainTime	t2 = m.group(3).isEmpty() ? null : new TrainTime(m.group(3));
					info = new StopInfo( i, type, t1, t2 );

				} else if( m.group(4)!=null ) {
					
					// 1;525--出発を省略
					info = new StopInfo( i, Integer.parseInt( m.group(4) ), new TrainTime( m.group(5) ) );

				} else if( m.group(6)!=null ) {

					// 1--取り扱いのみ
					info = new StopInfo( i, Integer.parseInt( m.group(6) ) );
				}

				stopInfoList.add( info );

			} else {

				// unknown data
			//	stopInfoList.add( null );

			}
		}
	}
	
	/*******************************************
	 * 
	 * 列車名表示用テキストを取得する
	 * 
	 * @return
	 */
	public String getResshaText() {
		
		if( resshaText==null ) {
			String	bango = prop.getString("Ressyabangou");
			String	name  = prop.getString("Ressyamei");
			String	gousuu= prop.getString("Gousuu");

			if( name==null )
				return bango;

			StringBuilder	buf = new StringBuilder(bango).append(":").append(name);
			if( gousuu!=null )
				buf.append(gousuu).append("号");

			resshaText = buf.toString();
		}

		return resshaText;
	}
	
	/*******************************************
	 * 
	 * 停車情報イテレータを取得する
	 * 
	 * @return
	 */
	public Iterator<StopInfo> getStopInfoIter() {
		
		return stopInfoList.iterator();
	}
	
	public String toString() {
		
		StringBuilder	buf = new StringBuilder();
		
		for( StopInfo s : stopInfoList ) {
			if( s!=null ) {
				buf.append( s ).append("\n");
			} else {
				buf.append("null").append("\n");
			}
		}
		return buf.toString();
	}
	
	
	
	/************************************
	 * 
	 * 
	 * 駅に停車/通過情報
	 * 
	 * 終点を表現すべきかどうか
	 * 
	 * @author matsumoto
	 *
	 */
	static public class StopInfo {
		
		/** 駅ID */
		public final int	ekiID;
		
		/** 取り扱い */
		int	type;
		/** 出発時刻 */
		private TrainTime	leaveTime;
		/** 到着時刻 */
		private TrainTime	arriveTime;
		
		StopInfo( int ekiID, int type, TrainTime arrive, TrainTime leave ) {
			this.ekiID = ekiID;
			this.type = type;
			this.arriveTime = arrive;
			this.leaveTime = leave;
		}
		
		StopInfo( int ekiID, int type, TrainTime arrive ) {
			this.ekiID = ekiID;
			this.type = type;
			this.arriveTime = arrive;
			this.leaveTime = null;
		}
		
		StopInfo( int ekiID, int type ) {
			this.ekiID = ekiID;
			this.type = type;
			this.arriveTime = null;
			this.leaveTime = null;
		}
		
		public String toString() {
			StringBuilder	buf = new StringBuilder();
			buf.append( type ).append(";");
			buf.append( arriveTime ).append("/").append( leaveTime );
			return buf.toString();
		}
		
		public TrainTime getArriveTime() {  return arriveTime; }
		
	}

}
