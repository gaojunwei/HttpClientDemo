package www.gjw.concurrent.ReentrantLock;


/**
 * ��Դ �ǵ���ģʽ
 * @author gjw-pc
 */
public class Task01 implements Runnable {
	@Override
	public void run() {
		Source source = new Source();
		System.out.println(Thread.currentThread().getName()+"-ʵ����Source��"+source);
		source.doSomeThing();
	}
}
