package tianma.ss.spider.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 处理Unicode和String转化的工具类
 * 
 * @author tianma
 *
 */
public class CharacterUtil {

	/**
	 * 将字符串转化为unicode字符串并返回,中文环境不适用
	 * 
	 * @param string
	 * @return
	 */
	public static String string2Unicode(String string) {
		StringBuilder sb = new StringBuilder();
		int len = string.length();
		for (int i = 0; i < len; i++) {
			// 取出每一个字符
			char c = string.charAt(i);
			// 转换为unicode
			sb.append("\\u" + Integer.toHexString(c));
		}
		return sb.toString();
	}

	/**
	 * 将unicode字符串转化为普通字符串并返回,中文环境不适用
	 * 
	 * @param uniStr
	 * @return
	 */
	public static String unicode2String(String uniStr) {
		StringBuilder sb = new StringBuilder();
		String[] hex = uniStr.split("\\\\u");
		int len = hex.length;
		for (int i = 0; i < len; i++) {
			// 转换出每一个代码点
			int data = Integer.parseInt(hex[i], 16);
			// 追加为string
			sb.append((char) data);
		}
		return sb.toString();
	}

	/**
	 * 将普通字符串转化为unicode字符串并返回,中文环境亦适用
	 * 
	 * @param uniStr
	 * @return
	 */
	public static String gbEncoding(String gbStr) {
		StringBuilder sb = new StringBuilder();
		int len = gbStr.length();
		for (int i = 0; i < len; i++) {
			// 取出每一个字符
			char c = gbStr.charAt(i);
			String hex = Integer.toHexString(c);
			if (hex.length() <= 2) {
				hex = "00" + hex;
			}
			// 转换为unicode
			sb.append("\\u" + hex);
		}
		return sb.toString();
	}

	/**
	 * 将unicode字符串转化为普通字符串并返回,中文环境亦适用
	 * 
	 * @param uniStr
	 * @return
	 */
	public static String gbDecoding(String unicode) {
		Pattern pattern = Pattern.compile("(\\\\u(\\p{XDigit}{4}))");
		Matcher matcher = pattern.matcher(unicode);
		char ch;
		while (matcher.find()) {
			ch = (char) Integer.parseInt(matcher.group(2), 16);
			unicode = unicode.replace(matcher.group(1), ch + "");
		}
		return unicode;
	}

}
