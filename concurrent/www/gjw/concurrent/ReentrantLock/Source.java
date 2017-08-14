package www.gjw.concurrent.ReentrantLock;

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
		if(lock.getQueueLength()==3)
		{
			System.out.println("�ж�ִ��");
			try {
				lock.lockInterruptibly();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		lock.lock();
		System.out.println("�����");
		System.out.println("BBB="+lock.getQueueLength());
		//����Ѿ���lock������������false����ȴ����ﵽ���Բ�����Ч�� 
		try {
			System.out.println(Thread.currentThread().getName()+" ����÷���");
			System.out.println(Thread.currentThread().getName()+" do something");
			System.out.println(Thread.currentThread().getName()+" �뿪�÷���");
			doSomeThing02();
		} finally {
			lock.unlock();
			System.out.println("�ͷ���");
		}
	}
	public void doSomeThing02() {
		lock.lock();//������������ü������ͱ����ͷż�������
		System.out.println("----");
	}
	
}
