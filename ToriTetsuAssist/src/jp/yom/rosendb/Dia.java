package jp.yom.rosendb;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jp.yom.rosendb.OudReader.Prop;
import jp.yom.rosendb.RosenDatabase.Eki;
import jp.yom.rosendb.RosenDatabase.Houkou;
import jp.yom.rosendb.RosenDatabase.Ressyasyubetsu;

public class Dia {

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
		
		ArrayList<StopInfo>	stopInfoList = new ArrayList<StopInfo>();
		
		
		private TrainSchedule( Prop src ) {
			
			this.prop = src;
			
			// 駅時刻の解析
			String[]	strs = prop.getString("EkiJikoku").split(",");
			
			for( String s : strs ) {
				Pattern	p = Pattern.compile("^([0-9]);(\\d*)/(\\d*)$|^([0-9]);(\\d+)$|^([0-9])$" );
				Matcher	m = p.matcher( s );
				if( m.find() ) {

					StopInfo	info = null;

					if( m.group(1)!=null ) {
						
						// 1;525/526 --全てセットされている
						// 1;525/    --終点
						// 1;   /526 --始発(使われていない？)
						int	type = Integer.parseInt( m.group(1) );
						TrainTime	t1 = m.group(2).isEmpty() ? null : new TrainTime(m.group(2));
						TrainTime	t2 = m.group(3).isEmpty() ? null : new TrainTime(m.group(3));
						info = new StopInfo( type, t1, t2 );

					} else if( m.group(4)!=null ) {
						
						// 1;525--出発を省略
						info = new StopInfo( Integer.parseInt( m.group(4) ), new TrainTime( m.group(5) ) );

					} else if( m.group(6)!=null ) {

						// 1--取り扱いのみ
						info = new StopInfo( Integer.parseInt( m.group(6) ) );
					}

					stopInfoList.add( info );

				} else {

					// unknown data
					stopInfoList.add( null );

				}
			}
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
	static class StopInfo {
		
		/** 取り扱い */
		int	type;
		/** 出発時刻 */
		private TrainTime	leaveTime;
		/** 到着時刻 */
		private TrainTime	arriveTime;
		
		StopInfo( int type, TrainTime arrive, TrainTime leave ) {
			this.type = type;
			this.arriveTime = arrive;
			this.leaveTime = leave;
		}
		
		StopInfo( int type, TrainTime arrive ) {
			this.type = type;
			this.arriveTime = arrive;
			this.leaveTime = null;
		}
		
		StopInfo( int type ) {
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
	}
	
	
	
	
	/** ダイア名 */
	String	diaName;
	
	/** キーに */
//	LinkedHashMap<String,RessyaCont[]>	ressyaMap;
	
	Prop	prop;
	
	TrainSchedule[]	noboriTrasits;
	
	
	public Dia( Prop prop ) {
		
		Prop[]	conts = prop.getFromMap("Ressya","Nobori").getArray("RessyaCont");
		
		noboriTrasits = new TrainSchedule[conts.length];
		
		for( int i=0; i<conts.length; i++ ) {
			noboriTrasits[i] = new TrainSchedule( conts[i] );
		}
		
	}
	

}
