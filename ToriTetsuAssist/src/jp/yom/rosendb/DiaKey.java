package jp.yom.rosendb;

import jp.yom.rosendb.RosenDatabase.Houkou;


/***************************************
 * 
 * 
 * ダイヤ上の列車一本情報を識別するID
 * 
 * @author Yomusu
 *
 */
public class DiaKey {
	
	/** ダイヤID */
	final private int		diaID;
	
	/** 方向 */
	final private Houkou	houkou;
	
	/** 列車ID */
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
