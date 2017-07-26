package www.gjw.concurrent.test;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 资源
 * @author gjw-pc
 */
public class Source {
	static Source source = new Source();
	
	public static Source getInstance() {
		return source;
	}
	
	private final ReentrantLock lock = new ReentrantLock();
	
	public void doSomeThing() {
		System.out.println("AAA="+lock.getQueueLength());
		lock.lock();
		System.out.println("获得锁");
		System.out.println("BBB="+lock.getQueueLength());
		//如果已经被lock，则立即返回false不会等待，达到忽略操作的效果 
		try {
			System.out.println(Thread.currentThread().getName()+" 进入该方法");
			System.out.println(Thread.currentThread().getName()+" do something");
			System.out.println(Thread.currentThread().getName()+" 离开该方法");
		} finally {
			lock.unlock();
		}
	}
}
