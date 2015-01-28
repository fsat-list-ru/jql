package org.fsat.jql.util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * @since Jan 28, 2015
 **/
public class Strings {
	
	private static String[] hex = { "00", "01", "02", "03", "04", "05", "06",  
           "07", "08", "09", "0A", "0B", "0C", "0D", "0E", "0F", "10", "11",  
           "12", "13", "14", "15", "16", "17", "18", "19", "1A", "1B", "1C",  
           "1D", "1E", "1F", "20", "21", "22", "23", "24", "25", "26", "27",  
           "28", "29", "2A", "2B", "2C", "2D", "2E", "2F", "30", "31", "32",  
           "33", "34", "35", "36", "37", "38", "39", "3A", "3B", "3C", "3D",  
           "3E", "3F", "40", "41", "42", "43", "44", "45", "46", "47", "48",  
           "49", "4A", "4B", "4C", "4D", "4E", "4F", "50", "51", "52", "53",  
           "54", "55", "56", "57", "58", "59", "5A", "5B", "5C", "5D", "5E",  
           "5F", "60", "61", "62", "63", "64", "65", "66", "67", "68", "69",  
           "6A", "6B", "6C", "6D", "6E", "6F", "70", "71", "72", "73", "74",  
           "75", "76", "77", "78", "79", "7A", "7B", "7C", "7D", "7E", "7F",  
           "80", "81", "82", "83", "84", "85", "86", "87", "88", "89", "8A",  
           "8B", "8C", "8D", "8E", "8F", "90", "91", "92", "93", "94", "95",  
           "96", "97", "98", "99", "9A", "9B", "9C", "9D", "9E", "9F", "A0",  
           "A1", "A2", "A3", "A4", "A5", "A6", "A7", "A8", "A9", "AA", "AB",  
           "AC", "AD", "AE", "AF", "B0", "B1", "B2", "B3", "B4", "B5", "B6",  
           "B7", "B8", "B9", "BA", "BB", "BC", "BD", "BE", "BF", "C0", "C1",  
           "C2", "C3", "C4", "C5", "C6", "C7", "C8", "C9", "CA", "CB", "CC",  
           "CD", "CE", "CF", "D0", "D1", "D2", "D3", "D4", "D5", "D6", "D7",  
           "D8", "D9", "DA", "DB", "DC", "DD", "DE", "DF", "E0", "E1", "E2",  
           "E3", "E4", "E5", "E6", "E7", "E8", "E9", "EA", "EB", "EC", "ED",  
           "EE", "EF", "F0", "F1", "F2", "F3", "F4", "F5", "F6", "F7", "F8",  
           "F9", "FA", "FB", "FC", "FD", "FE", "FF" };  
	
    private static byte[] val = { 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F,  
        0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F,  
        0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F,  
        0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F,  
        0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x00, 0x01, 0x02,  
        0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09, 0x3F, 0x3F, 0x3F, 0x3F,  
        0x3F, 0x3F, 0x3F, 0x0A, 0x0B, 0x0C, 0x0D, 0x0E, 0x0F, 0x3F, 0x3F,  
        0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F,  
        0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F,  
        0x3F, 0x3F, 0x0A, 0x0B, 0x0C, 0x0D, 0x0E, 0x0F, 0x3F, 0x3F, 0x3F,  
        0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F,  
        0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F,  
        0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F,  
        0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F,  
        0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F,  
        0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F,  
        0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F,  
        0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F,  
        0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F,  
        0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F,  
        0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F,  
        0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F,  
        0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F,  
        0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F };  
    
    /**
     * Decodes the passed UTF-8 String using an algorithm that's compatible with
     * JavaScript's <code>decodeURIComponent</code> function. Returns
     * <code>null</code> if the String is <code>null</code>.
     *
     * @param s The UTF-8 encoded String to be decoded
     * @return the decoded String
     */
    public static String decodeURIComponent(String s) {
      if (s == null) {
        return null;
      }
      
      String result = s.replaceAll("\\+", "%2B");

      try {
        result = URLDecoder.decode(result, "UTF-8");
      } catch (UnsupportedEncodingException e) {
      }

      return result;
    }

    /**
     * Encodes the passed String as UTF-8 using an algorithm that's compatible
     * with JavaScript's <code>encodeURIComponent</code> function. Returns
     * <code>null</code> if the String is <code>null</code>.
     * 
     * @param s The String to be encoded
     * @return the encoded String
     */
    public static String encodeURIComponent(String s) {
      String result = null;

      try {
        result = URLEncoder.encode(s, "UTF-8")
                           .replaceAll("\\+", "%20");
      } catch (UnsupportedEncodingException e) {
        result = s;
      }

      return result;
    }  
    
    /**
     * Аналог Javascript escape()
     * @param s
     * @return
     */
    public static String jsEscape(String s) {  
        if (s == null) {  
            return null;  
        }  
        StringBuffer sbuf = new StringBuffer();  
        int len = s.length();  
        for (int i = 0; i < len; i++) {  
            int ch = s.charAt(i);  
            if ('A' <= ch && ch <= 'Z') {  
                sbuf.append((char) ch);  
            } else if ('a' <= ch && ch <= 'z') {  
                sbuf.append((char) ch);  
            } else if ('0' <= ch && ch <= '9') {  
                sbuf.append((char) ch);  
            } else if (ch == '*' || ch == '+' || ch == '-' || ch == '/'  
                    || ch == '_' || ch == '.' || ch == '@') {  
                sbuf.append((char) ch);  
            } else if (ch <= 0x007F) {  
                sbuf.append('%');  
                sbuf.append(hex[ch]);  
            } else {  
                sbuf.append('%');  
                sbuf.append(hex[ch]);  
            }  
        }  
        return sbuf.toString();  
    }  
    
    /**
     * Аналог JavaScript unescape()
     * 
     * @param s
     * @return
     */
    public static String jsUnescape(String s) {  
        if (s == null) {  
            return null;  
        }  
        StringBuffer sbuf = new StringBuffer();  
        int i = 0;  
        int len = s.length();  
        while (i < len) {  
            int ch = s.charAt(i);  
            if ('A' <= ch && ch <= 'Z') {  
                sbuf.append((char) ch);  
            } else if ('a' <= ch && ch <= 'z') {  
                sbuf.append((char) ch);  
            } else if ('0' <= ch && ch <= '9') {  
                sbuf.append((char) ch);  
            } else if (ch == '*' || ch == '+' || ch == '-' || ch == '/'  
                    || ch == '_' || ch == '.' || ch == '@') {  
                sbuf.append((char) ch);  
            } else if (ch == '%') {  
                int cint = 0;  
                if ('u' != s.charAt(i + 1)) {  
                    cint = (cint << 4) | val[s.charAt(i + 1)];  
                    cint = (cint << 4) | val[s.charAt(i + 2)];  
                    i += 2;  
                } else {  
                    cint = (cint << 4) | val[s.charAt(i + 2)];  
                    cint = (cint << 4) | val[s.charAt(i + 3)];  
                    cint = (cint << 4) | val[s.charAt(i + 4)];  
                    cint = (cint << 4) | val[s.charAt(i + 5)];  
                    i += 5;  
                }  
                sbuf.append((char) cint);  
            } else {  
                sbuf.append((char) ch);  
            }  
            i++;  
        }  
        return sbuf.toString();  
    }  

    /**
	 * Разбивка CamelCase строки на список отдельных слов.
	 * 
	 * @param source
	 *            Строка для преобразования
	 * @return
	 */
    public static List<String> splitCamelCase(String source) {
		ArrayList<String> words = new ArrayList<>();
        int length = source.length();
        int i = 0;
        StringBuilder sb = new StringBuilder();
        while (i < length) {
            while (i < length && Character.isUpperCase(source.charAt(i))) {
                sb.append(source.charAt(i));
                ++i;
            }
            while (i < length && !Character.isUpperCase(source.charAt(i))) {
                sb.append(source.charAt(i));
                ++i;
            }
            final String word = sb.toString();
            words.add(word);
            sb.setLength(0);
        }
        return words;
    }

    /**
	 * Преобразование CamelCase строки в under_score формат.
	 * 
	 * @param s
	 *            Строка для преобразования
	 * @return
	 */
    public static String camelToUnder(String s) {
        List<String> words = splitCamelCase(s);
        String[] arr = new String[words.size()];
        int i=0;
        for(String w:words) {
            arr[i]=w.toLowerCase();
            i++;
        }
        return join("_", arr);
    }
    
    /**
     * Преобразовать строку из dot нотации в кэмел кейс. 
     * Например status.id -&gt; statusId
     * @param s
     * @return
     */
    public static String dotToCamel(String s) {
    	StringBuilder retval=new StringBuilder();
    	int i=0;
    	int length=s.length();
    	boolean upper=false;
    	while(i<length) {
    		if(s.charAt(i)=='.') {
    			upper=true;
    		} else {
    			if(upper) {
    				retval.append(Character.toUpperCase(s.charAt(i)));
    				upper=false;
    			} else {
    				retval.append(s.charAt(i));
    			}
    		}
    		++i;
    	}
    	return retval.toString();
    }
    
    

    /**
	 * Объединение строк с использованием разделителя.
	 * 
	 * @param delim
	 *            Разделитель.
	 * @param s
	 *            Массив строк для объединения
	 * @return
	 */
    public static String join(String delim, String[] s) {
        StringBuilder sb = new StringBuilder();
        for(int i = 0 ; i<s.length; i++) {
            sb.append(s[i]);
            if(i+1<s.length) {
                sb.append(delim);
            }
        }
        return sb.toString();
    }
}
