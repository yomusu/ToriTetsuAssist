package jp.yom.toritetsuassist;


/*****************************************
 * 
 * 
 * 列車通過情報
 * 
 * @author Yomusu
 *
 */
public class TrainPassInfo {
	
	
	enum Direction {
		/** 上り */
		NOBORI,
		/** 下り */
		KUDARI,
	};
	
	
	static class TrainTime {
		
		int	hour;
		int	minute;
		int	second;
		
		public TrainTime( int h, int m, int s ) {
			hour = h;
			minute = m;
			second = s;
		}
		
		public String toString() {
			return String.format("%02d:%02d:%02d", hour, minute, second );
		}
	}
	
	
	//========================================
	// メンバ変数の宣言
	//========================================
	
	/** 上り、下り */
	Direction	direction;
	
	/** 列車名 */
	String	trainName;
	
	/** 通過時間 */
	TrainTime	passTime;
	
	/** 駅出発時刻 */
	TrainTime	leavedTime;
	
	/** 出発駅 */
	String	station;
}
