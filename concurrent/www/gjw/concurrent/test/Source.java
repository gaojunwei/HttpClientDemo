package www.gjw.concurrent.test;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * ��Դ
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
		System.out.println("�����");
		System.out.println("BBB="+lock.getQueueLength());
		//����Ѿ���lock������������false����ȴ����ﵽ���Բ�����Ч�� 
		try {
			System.out.println(Thread.currentThread().getName()+" ����÷���");
			System.out.println(Thread.currentThread().getName()+" do something");
			System.out.println(Thread.currentThread().getName()+" �뿪�÷���");
		} finally {
			lock.unlock();
		}
	}
}
