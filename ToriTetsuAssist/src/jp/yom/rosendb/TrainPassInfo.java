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
	
	/** 通過時間 */
	public TrainTime	passTime;
	
	/** 駅出発時刻 */
	public TrainTime	leavedTime;
	
	/** 出発駅 */
	public String	station;
}
