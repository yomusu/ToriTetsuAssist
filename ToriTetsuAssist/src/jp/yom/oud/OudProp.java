package jp.yom.oud;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.net.URL;
import java.util.LinkedHashMap;
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
public class OudProp {
	
	
	LinkedHashMap<String,Object>	prop = new LinkedHashMap<String,Object>();
	
	
	/**************************************
	 * 
	 * 通常のプロパティ
	 * 
	 * @param key
	 * @param value
	 */
	public void setProperty( String key, String value ) {
		prop.put( key, value );
	}
	
	/***************************************
	 * 
	 * 固定長配列を確保する
	 * 
	 * @param key
	 * @param arrays
	 */
	public void setArrays( String key, OudProp[] arrays ) {
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
	public void setProperty( String key, int index, OudProp value ) {
		OudProp[]	array = (OudProp[])prop.get( key );
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
	public void setProperty( String key, String cokey, OudProp value ) {
		LinkedHashMap<String,OudProp>	map = (LinkedHashMap)prop.get( key );
		if( map==null ) {
			map = new LinkedHashMap<String,OudProp>();
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
	public void setProperty( String key, OudProp value ) {
		prop.put( key, value );
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
			if( obj instanceof OudProp ) {
				buf.append(key).append(".").append("\n");
				buf.append( ((OudProp)obj).toString() );
				buf.append(".").append("\n");
			}
			
			//---------------------
			// 固定長配列プロパティ
			if( obj instanceof OudProp[] ) {
				
				OudProp[]	array = (OudProp[])obj;
				buf.append(key).append("[]=").append( ((OudProp[])obj).length ).append("\n");
				
				for( int i=0; i<array.length; i++ ) {
					OudProp oud = array[i];
					buf.append(key).append("[").append(i).append("].\n");
					buf.append( oud );
					buf.append(".").append("\n");
				}
			}
			
			//---------------------
			// 連想配列プロパティ(途中)
			if( obj instanceof LinkedHashMap ) {
				
				LinkedHashMap<String,OudProp>	map = (LinkedHashMap)obj;
				
				for( String cokey : map.keySet() ) {
					buf.append(key).append("[").append(cokey).append("].\n");
					buf.append( map.get(cokey) );
					buf.append(".").append("\n");
				}
			}
		}
		
		return buf.toString();
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
	static public OudProp parseOudBlock( BufferedReader in ) throws IOException,OudParseException {
		
		OudProp	oud = new OudProp();
		
		
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
					oud.setProperty( key, m.group(2) );
					continue;
				}
			}

			// Oud
			{
				Matcher	m = p2.matcher(line);
				if( m.find() ) {
					String	key = m.group(1);
					OudProp	child = parseOudBlock( in );
					oud.setProperty( key, child );
					continue;
				}
			}

			// 配列宣言
			{
				Matcher	m = p3.matcher(line);
				if( m.find() ) {
					String	key = m.group(1);
					int	count = Integer.parseInt( m.group(2) );
					oud.setArrays( key, new OudProp[count] );
					continue;
				}
			}

			// 配列のOud
			{
				Matcher	m = p4.matcher(line);
				if( m.find() ) {
					String	key = m.group(1);
					int	index = Integer.parseInt( m.group(2) );
					OudProp	child = parseOudBlock( in );
					oud.setProperty( key, index, child );
					continue;
				}
			}

			// マッププロパティ
			{
				Matcher	m = p6.matcher(line);
				if( m.find() ) {
					String	key = m.group(1);
					String	cokey = m.group(2);
					OudProp	child = parseOudBlock( in );
					oud.setProperty( key, cokey, child );
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
	static class OudParseException extends Exception {
		
		public OudParseException( String mes ) {
			super(mes);
		}
	}
	
	
	
	static public void main( String[] args ) {
		
		URL	url = OudProp.class.getResource("test.oud");
		LineNumberReader	lineReader = null;
		
		try {
			
			InputStream	in = url.openStream();
			
			lineReader = new LineNumberReader( new InputStreamReader(in,"MS932") );
			OudProp	oud = parseOudBlock( lineReader );
			
			System.out.println( oud );
			
		} catch( OudParseException e ) {
			
			if( lineReader!=null ) {
				System.out.println( "解析エラー(行"+lineReader.getLineNumber()+"):"+e.getMessage() );
			}
			
		} catch( IOException e ) {
			
			e.printStackTrace();
			
		} finally {
			
			if( lineReader!=null ) {
				try {
					lineReader.close();
				} catch( IOException e ){}
			}
		}
	}
}
