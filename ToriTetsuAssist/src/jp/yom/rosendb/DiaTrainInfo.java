package jp.yom.rosendb;

import jp.yom.rosendb.EkiPassInfo.EkiPassIterator;
import jp.yom.rosendb.OudReader.OudNode;


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
	EkiPassInfo[]	stopInfoList;
	
	
	public DiaTrainInfo( DiaKey key, OudNode src, EkiPassInfo[] list ) {
		
		this.key = key;
		this.prop = src;
		
		// 列車種別ID
		resshaID = Integer.parseInt( prop.getString("Syubetsu") );
		
		this.stopInfoList = list;
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
	public EkiPassIterator iterator() {
		
		return new EkiPassIterator( stopInfoList );
	}
	
	public String toString() {
		
		StringBuilder	buf = new StringBuilder();
		
		for( EkiPassInfo s : stopInfoList ) {
			if( s!=null ) {
				buf.append( s ).append("\n");
			} else {
				buf.append("null").append("\n");
			}
		}
		return buf.toString();
	}
	
}
