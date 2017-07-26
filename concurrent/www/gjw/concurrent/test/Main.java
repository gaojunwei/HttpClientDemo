package www.gjw.concurrent.test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
	public static void main(String[] args) {
		ExecutorService eService = Executors.newCachedThreadPool();
		for (int i = 0; i < 5; i++) {
			Task02 task = new Task02();
			eService.submit(task);
		}
		eService.shutdown();
	}
}
