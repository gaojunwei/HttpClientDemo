package www.gjw.http.demo;

import java.util.concurrent.CountDownLatch;

import org.apache.log4j.Logger;

public class Task implements Runnable {
	private static Logger logger = Logger.getLogger(Task.class.getName());
	
    private CountDownLatch countDownLatch;
    private String url;

    public Task(String url, CountDownLatch countDownLatch) {
        this.url = url;
        this.countDownLatch = countDownLatch;
    }

    @Override
    public void run() {
        try {
        	logger.info(Thread.currentThread().getName()+"任务开始");
        	//logger.info(HttpClientUtil.get(url));
        	HttpClientUtil.get(url);
        	logger.info(Thread.currentThread().getName()+"任务结束");
        } finally {
            countDownLatch.countDown();
        }
    }
}
