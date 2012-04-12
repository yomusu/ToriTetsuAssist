package jp.yom.rosendb;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/***********************
 * 
 * 
 * oudプロパティ
 * 
 * 
 * @author matsumoto
 *
 */
public class OudReader {
	
	
	static class Prop {

		LinkedHashMap<String,Object>	prop = new LinkedHashMap<String,Object>();


		/**************************************
		 * 
		 * 通常のプロパティ
		 * 
		 * @param key
		 * @param value
		 */
		public void put( String key, String value ) {
			prop.put( key, value );
		}

		/***************************************
		 * 
		 * 固定長配列を確保する
		 * 
		 * @param key
		 * @param arrays
		 */
		public void setArrays( String key, Prop[] arrays ) {
			prop.put( key, arrays );
		}

		/*************************************
		 * 
		 * 固定長配列プロパティ
		 * 
		 * @param key
		 * @param index
		 * @param value
		 */
		public void putToArray( String key, int index, Prop value ) {
			Prop[]	array = (Prop[])prop.get( key );
			array[index] = value;
		}

		/**************************************
		 * 
		 * 連想配列プロパティ
		 * 
		 * @param key
		 * @param cokey
		 * @param value
		 */
		public void putToMap( String key, String cokey, Prop value ) {
			LinkedHashMap<String,Prop>	map = (LinkedHashMap<String,Prop>)prop.get( key );
			if( map==null ) {
				map = new LinkedHashMap<String,Prop>();
				prop.put( key, map );
			}
			map.put( cokey, value );
		}

		/***************************************
		 * 
		 * Oudプロパティ
		 * 
		 * @param key
		 * @param value
		 */
		public void put( String key, Prop value ) {
			prop.put( key, value );
		}



		/**************************************
		 * 
		 * 通常のプロパティとして取得する
		 * 
		 * @param key
		 * @return
		 */
		public String getString( String key ) {
			Object	obj = prop.get(key);
			if( obj instanceof String )
				return (String)obj;
			return null;
		}

		/***************************************
		 * 
		 * Oudプロパティとして取得します
		 * 
		 * @param key
		 * @return
		 */
		public Prop getOud( String key ) {
			Object	obj = prop.get(key);
			if( obj instanceof Prop )
				return (Prop)obj;
			return null;
		}

		/***************************************
		 * 
		 * 固定長配列プロパティとして取得します
		 * 
		 * @param key
		 * @return
		 */
		public Prop[] getArray( String key ) {
			Object	obj = prop.get(key);
			if( obj instanceof Prop[] )
				return (Prop[])obj;
			return null;
		}

		/***************************************
		 * 
		 * 連想配列形式プロパティとして、
		 * さらに指定した子プロパティを取得します
		 * 
		 * @param key
		 * @return
		 */
		public Prop getFromMap( String key, String cokey ) {
			Object	obj = prop.get(key);
			if( obj instanceof LinkedHashMap )
				return ((LinkedHashMap<String,Prop>)obj).get(cokey);
			return null;
		}

		/***************************************
		 * 
		 * 連想配列形式プロパティの全ての子プロパティのIDを取得します
		 * 
		 * @param key
		 * @return
		 */
		public Set<String> getKeySetFromMap( String key, String cokey ) {
			Object	obj = prop.get(key);
			if( obj instanceof LinkedHashMap )
				return ((LinkedHashMap<String,Prop>)obj).keySet();
			return null;
		}


		/************************************************
		 * 
		 * 
		 * Oudファイル形式で出力してます
		 * 
		 */
		public String toString() {

			StringBuilder	buf = new StringBuilder();

			for( String key : prop.keySet() ) {

				Object	obj = prop.get(key);

				//---------------------
				// 通常のプロパティ
				if( obj instanceof String )
					buf.append(key).append("=").append(obj).append("\n");

				//---------------------
				// Oudプロパティ
				if( obj instanceof Prop ) {
					buf.append(key).append(".").append("\n");
					buf.append( ((OudReader)obj).toString() );
					buf.append(".").append("\n");
				}

				//---------------------
				// 固定長配列プロパティ
				if( obj instanceof Prop[] ) {

					Prop[]	array = (Prop[])obj;
					buf.append(key).append("[]=").append( ((Prop[])obj).length ).append("\n");

					for( int i=0; i<array.length; i++ ) {
						Prop oud = array[i];
						buf.append(key).append("[").append(i).append("].\n");
						buf.append( oud );
						buf.append(".").append("\n");
					}
				}

				//---------------------
				// 連想配列プロパティ
				if( obj instanceof LinkedHashMap ) {

					LinkedHashMap<String,OudReader>	map = (LinkedHashMap)obj;

					for( String cokey : map.keySet() ) {
						buf.append(key).append("[").append(cokey).append("].\n");
						buf.append( map.get(cokey) );
						buf.append(".").append("\n");
					}
				}
			}

			return buf.toString();
		}
	}
	
	
	//==================================================================
	// staticメソッドの宣言
	//==================================================================
	
	/***********************************************
	 * 
	 * 
	 * Oudファイルの解析を行います
	 * 
	 * @param in
	 * @return
	 * @throws IOException
	 * @throws OudParseException
	 */
	static public Prop parseOudBlock( BufferedReader in ) throws IOException,OudParseException {
		
		Prop	oud = new Prop();
		
		
		// ノーマルプロパティ：<key>=<value>
		final Pattern	p1 = Pattern.compile("^(\\w+)=(.*)$");
		// Oudプロパティ：<key>=.
		final Pattern	p2 = Pattern.compile("^(\\w+)\\.$");
		
		// 配列の宣言：<key>[]=<n>
		final Pattern	p3 = Pattern.compile("^(\\w+)\\[\\]=(\\d+)$");
		// <key>[<n>]=.
		final Pattern	p4 = Pattern.compile("^(\\w+)\\[(\\d+)\\]\\.$");
		
		// 連想配列プロパティ：<key>[<cokey>].
		final Pattern	p6 = Pattern.compile("^(\\w+)\\[(\\w+)\\]\\.$");
		
		// .
		final Pattern	endPattern = Pattern.compile("^\\.$");
		
		
		
		while( true ) {
			
			String	line = in.readLine();
			if( line ==null )
				break;
			
			// プロパティのセット
			{
				Matcher	m = p1.matcher(line);
				if( m.find() ) {
					String	key = m.group(1);
					oud.put( key, m.group(2) );
					continue;
				}
			}

			// Oud
			{
				Matcher	m = p2.matcher(line);
				if( m.find() ) {
					String	key = m.group(1);
					Prop	child = parseOudBlock( in );
					oud.put( key, child );
					continue;
				}
			}

			// 配列宣言
			{
				Matcher	m = p3.matcher(line);
				if( m.find() ) {
					String	key = m.group(1);
					int	count = Integer.parseInt( m.group(2) );
					oud.setArrays( key, new Prop[count] );
					continue;
				}
			}

			// 配列のOud
			{
				Matcher	m = p4.matcher(line);
				if( m.find() ) {
					String	key = m.group(1);
					int	index = Integer.parseInt( m.group(2) );
					Prop	child = parseOudBlock( in );
					oud.putToArray( key, index, child );
					continue;
				}
			}

			// マッププロパティ
			{
				Matcher	m = p6.matcher(line);
				if( m.find() ) {
					String	key = m.group(1);
					String	cokey = m.group(2);
					Prop	child = parseOudBlock( in );
					oud.putToMap( key, cokey, child );
					continue;
				}
			}
			
			// Endマーク
			{
				Matcher	m = endPattern.matcher(line);
				if( m.find() )
					return oud;
			}
			
			// 解析エラー
			throw new OudParseException(line);
		}
		
		return oud;
	}
	
	
	/*************************************************
	 * 
	 * Oudファイルの解析エラー
	 * 
	 * @author matsumoto
	 *
	 */
	static public class OudParseException extends Exception {
		
		public OudParseException( String mes ) {
			super(mes);
		}
	}
	
}
