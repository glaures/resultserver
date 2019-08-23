package de.sandkastenliga.resultserver.utils;

import javax.persistence.Transient;

public class StringNormalizer {

	@Transient
	public final static String normalize(String s) {
		s = s.toLowerCase();
		StringBuffer res = new StringBuffer();
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			if (isLetterOrDigit(c)) {
				res.append(c);
			} else {
				res.append("_");
			}
		}
		return res.toString();
	}
	
	private static boolean isLetterOrDigit(char c){
		return ('A' <= c && c <= 'z') || ('0' <= c && c <= '9');
	}
}
