package com.lvmeng.qm.base.commons.util;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

public class StringUtil {

	public static Integer subNumber(String str) {
		String regEx = "[^0-9]";
		Pattern p = Pattern.compile(regEx);
		Matcher m = p.matcher(str);
		String trim = m.replaceAll("").trim();
		if (StringUtils.isNotBlank(trim)) {
			return Integer.valueOf(trim);
		}
		return null;
	}
//	 public static void main(String[] args) {
//	 String str = "分";
//	 System.out.println(subNumber(str));
//	 System.out.println(str);
//	 }

	public static void main(String[] args) {
		String strA = "E、安全平台类:大数据安全分析类,绿盟云服务类asdf";
		String strB = "E、安全平台类:大数据安全分析类,绿盟云服务类";
		double result = SimilarDegree(strB, strA);
		if (result >= 0.7) {
			System.out.println("相似度很高！" + similarityResult(result) + result);
		} else {
			System.out.println("相似度不高" + similarityResult(result) + result);
		}
		System.out.println();
	}

	/**
	 * 
	 * 相似度转百分比
	 * 
	 */

	public static String similarityResult(double resule) {
		return NumberFormat.getPercentInstance(new Locale("en ", "US ")).format(resule);
	}
	
	public static boolean isContains(List<String> list, String str) {
		 for (String string : list) {
			if (SimilarDegree(string,str) > 0.8) {
				return true;
			}
		 }
		 return false;
	}

	/**
	 * 
	 * 相似度比较
	 * @param strA
	 * @param strB
	 * @return
	 * 
	 */

	public static double SimilarDegree(String strA, String strB) {
		String newStrA = removeSign(strA);
		String newStrB = removeSign(strB);
		int temp = Math.max(newStrA.length(), newStrB.length());
		int temp2 = 0;
		if (newStrA.length() > newStrB.length()) {
			temp2 = longestCommonSubstring(newStrA, newStrB).length();
		}else {
			temp2 = longestCommonSubstring(newStrB, newStrA).length();
		}
		return temp2 * 1.0 / temp;
	}

	private static String removeSign(String str) {
		StringBuffer sb = new StringBuffer();
		for (char item : str.toCharArray())
			if (charReg(item)) {
				// System.out.println("--"+item);
				sb.append(item);
			}
		return sb.toString();
	}

	private static boolean charReg(char charValue) {
		return (charValue >= 0x4E00 && charValue <= 0X9FA5)
				|| (charValue >= 'a' && charValue <= 'z')
				|| (charValue >= 'A' && charValue <= 'Z')
				|| (charValue >= '0' && charValue <= '9');
	}

	private static String longestCommonSubstring(String strA, String strB) {
		char[] chars_strA = strA.toCharArray();
		char[] chars_strB = strB.toCharArray();
		int m = chars_strA.length;
		int n = chars_strB.length;
		int[][] matrix = new int[m + 1][n + 1];
		for (int i = 1; i <= m; i++) {
			for (int j = 1; j <= n; j++) {
				if (chars_strA[i - 1] == chars_strB[j - 1])
					matrix[i][j] = matrix[i - 1][j - 1] + 1;
				else
					matrix[i][j] = Math.max(matrix[i][j - 1], matrix[i - 1][j]);
			}
		}
		char[] result = new char[matrix[m][n]];
		int currentIndex = result.length - 1;
		while (matrix[m][n] != 0) {
			if (matrix[n] == matrix[n - 1])
				n--;
			else if (matrix[m][n] == matrix[m - 1][n])
				m--;
			else {
				result[currentIndex] = chars_strA[m - 1];
				currentIndex--;
				n--;
				m--;
			}
		}
		return new String(result);
	}
}
