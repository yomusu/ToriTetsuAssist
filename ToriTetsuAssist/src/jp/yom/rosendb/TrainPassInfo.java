package jp.yom.rosendb;

import jp.yom.rosendb.RosenDatabase.Houkou;


/*****************************************
 * 
 * 
 * 列車通過情報
 * 
 * @author Yomusu
 *
 */
public class TrainPassInfo {
	
	//========================================
	// メンバ変数の宣言
	//========================================
	
	/** 上り、下り */
	public Houkou	direction;
	
	/** 列車名 */
	public String	trainName;
	
	
	/** 通過時間(by計算) */
	public TrainTime	passTime;
	
	
	/** 出発駅出発時刻 */
	public TrainTime	timeLeaveOff;
	
	/** 到着駅到着時刻 */
	public TrainTime	timeArriveOn;
	
	/** 出発駅 */
	public String	stationFrom;
	
	/** 到着駅 */
	public String	stationTo;
}
