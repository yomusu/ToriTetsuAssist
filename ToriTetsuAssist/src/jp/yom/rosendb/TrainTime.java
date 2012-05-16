package jp.yom.rosendb;


/***********************************************
 * 
 * 
 * 時刻表向け時刻クラス
 * 
 * 
 * @author Yomusu
 *
 */
public class TrainTime implements Comparable<TrainTime> {

	int	hour;
	int	minute;
	int	second;
	
	public TrainTime( int h, int m, int s ) {
		hour = h;
		minute = m;
		second = s;
	}
	
	public TrainTime( String hhmm ) {
		int	n = Integer.parseInt( hhmm );
		
		hour = n / 100;
		minute = n % 100;
		second = 0;
	}
	
	@Override
	public String toString() {
		return String.format("%02d:%02d:%02d", hour, minute, second );
	}
	
	private int toSecond() {
		return (hour*60*60) + (minute*60) + second;
	}
	
	@Override
	public int hashCode() {
		return toSecond();
	}
	
	@Override
	public boolean equals( Object obj ) {
		
		if( obj instanceof TrainTime ) {
			TrainTime	d = (TrainTime)obj;
			return toSecond() == d.toSecond();
		}
		return false;
	}

	@Override
	public int compareTo(TrainTime d) {
		return Integer.signum( toSecond() - d.toSecond() );
	}
}
