package challenge24.y2014.finals.b;

import java.util.Scanner;

/**
 * Created by ab on 5/4/14.
 */
public class Temp {
	public static void main(String[] args) {
//		for (int i = 1; i < 10; ++i) {
//			System.out.println(i + " " + Integer.toBinaryString('0' + i));
//		}
		System.out.println(Integer.toHexString('a'));
		Scanner in = new Scanner(" #f) #t) #t) #f) #f) #f) #f)");
		while (in.hasNext()) {
			String s = in.nextLine();

			String num = "";
			for (char c : s.toCharArray()) {
				if (c == 't') {
					num += "1";
				} else if (c == 'f') {
					num += "0";
				}
			}

			int a = Integer.parseInt(num, 2);
			System.out.println((char) a);
		}
		in.close();
	}
}
