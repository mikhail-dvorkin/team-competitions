package challenge24.y2014.ec;

import java.io.*;
import java.util.*;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

public class F {
	private static String fileName = F.class.getSimpleName().replaceFirst("_.*", "");
	private static File inFile;
//	int freq = 44100;

	public void run() throws UnsupportedAudioFileException, IOException {
		AudioInputStream is = AudioSystem.getAudioInputStream(inFile);
		long length = is.getFrameLength();
//		System.out.print("Length: " + length + " : " + length * 1.0 / freq);
		int n = (int) length;
		int[] a = new int[n];
		for (int i = 0; i < n; i++) {
			a[i] = is.read();
		}
		is.close();
//		if (is.read() != -1 || a[a.length - 1] == -1) {
//			throw new RuntimeException();
//		}
		int prevPeak = -1;
		Map<Integer, Integer> gapFreq = new TreeMap<>();
		List<Integer> gaps = new ArrayList<>();
		for (int i = 2; i < n; i++) {
			if (a[i - 2] <= a[i - 1] && a[i - 1] > a[i] /*|| a[i - 2] >= a[i - 1] && a[i - 1] < a[i]*/) {
				if (prevPeak >= 0) {
					int gap = i - prevPeak;
//					if (gap < 10) {
//						continue;
//					}
					gaps.add(gap);
//					System.out.println(gap + " " + i);
//					System.out.println(gap);
					gapFreq.put(gap, 1 + gapFreq.getOrDefault(gap, 0));
				}
				prevPeak = i;
			}
		}
//		System.out.println("Gaps: " + gapFreq);
//		System.out.println(gaps);
//		System.out.println(n * 1.0 / freq);
		int w = 10;
		for (int i = w - 1; i < gaps.size(); i++) {
			double sum = 0;
			for (int j = i + 1 - w; j <= i; j++) {
				sum += gaps.get(j);
			}
			System.out.println(("" + sum / w).replace('.', ','));
		}
//		System.out.println(gaps.size());
	}

	public static void main(String[] args) throws IOException, UnsupportedAudioFileException {
		Locale.setDefault(Locale.US);
		for (int t = 0; t <= 0; t++) {
			inFile = new File("PROBLEMSET/input/" + fileName + "/" + t + ".wav");
//			out = new PrintWriter(fileName + t + ".out");
			new F().run();
//			out.close();
		}
	}
}
