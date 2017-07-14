package www.gjw.http.demo;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;

public class Main {
	private static Logger logger = Logger.getLogger(Main.class.getName());
	public static void main(String[] args) {
        // URL�б�����
        String[] urisToGet = {
                "http://192.168.1.19:8080/sszjrrtf/",
                "http://192.168.1.19:8080/sszm/",
                "http://192.168.1.19:8080/error/"
               	};

        long start = System.currentTimeMillis();
        try {
            int pagecount = urisToGet.length;
            logger.info("����������"+pagecount);
            ExecutorService executors = Executors.newFixedThreadPool(pagecount);
            CountDownLatch countDownLatch = new CountDownLatch(pagecount);
            for (int i = 0; i < pagecount; i++) {
                // �����߳�ץȡ
                executors.execute(new Task(urisToGet[i], countDownLatch));
            }
            countDownLatch.await();
            executors.shutdown();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            System.out.println("�߳�" + Thread.currentThread().getName() + ","
                    + System.currentTimeMillis() + ", �����߳�����ɣ���ʼ������һ����");
        }

        long end = System.currentTimeMillis();
        System.out.println("consume -> " + (end - start));
    }
}
