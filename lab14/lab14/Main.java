package lab14;

import lab14lib.Generator;
import lab14lib.GeneratorAudioVisualizer;

public class Main {
	public static void main(String[] args) {
		/** Your code here. */
		Generator generator = new SawToothGenerator(512);
		GeneratorAudioVisualizer gav = new GeneratorAudioVisualizer(generator);
		System.out.println(3 / 5.0);
		gav.drawAndPlay(4096, 1000000);
	}
} 