package www.gjw.concurrent.ReentrantLock;

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
		if(lock.getQueueLength()==3)
		{
			System.out.println("中断执行");
			try {
				lock.lockInterruptibly();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		lock.lock();
		System.out.println("获得锁");
		System.out.println("BBB="+lock.getQueueLength());
		//如果已经被lock，则立即返回false不会等待，达到忽略操作的效果 
		try {
			System.out.println(Thread.currentThread().getName()+" 进入该方法");
			System.out.println(Thread.currentThread().getName()+" do something");
			System.out.println(Thread.currentThread().getName()+" 离开该方法");
			doSomeThing02();
		} finally {
			lock.unlock();
			System.out.println("释放锁");
		}
	}
	public void doSomeThing02() {
		lock.lock();//可重入锁（获得几次锁就必须释放几次锁）
		System.out.println("----");
	}
	
}
