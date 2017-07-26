package www.gjw.http.demo;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;

public class Main {
	private static Logger logger = Logger.getLogger(Main.class.getName());
	public static void main(String[] args) {
        // URL列表数组
        String[] urisToGet = {
                "https://www.baidu.com/",
                "http://m.sszjr.com/",
                "https://www.sszjr.com/"
               	};

        long start = System.currentTimeMillis();
        try {
            int pagecount = urisToGet.length;
            logger.info("任务数量："+pagecount);
            ExecutorService executors = Executors.newFixedThreadPool(pagecount);
            CountDownLatch countDownLatch = new CountDownLatch(pagecount);
            for (int i = 0; i < pagecount; i++) {
                // 启动线程抓取
                executors.execute(new Task(urisToGet[i], countDownLatch));
            }
            countDownLatch.await();
            executors.shutdown();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            System.out.println("线程" + Thread.currentThread().getName() + ","
                    + System.currentTimeMillis() + ", 所有线程已完成，开始进入下一步！");
        }

        long end = System.currentTimeMillis();
        System.out.println("consume -> " + (end - start));
    }
}
