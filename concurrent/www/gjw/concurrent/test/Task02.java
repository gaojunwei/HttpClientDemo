package www.gjw.concurrent.test;


/**
 * ��Դ ����ģʽ
 * @author gjw-pc
 */
public class Task02 implements Runnable {
	@Override
	public void run() {
		System.out.println(Thread.currentThread().getName()+"-ʵ����Source��"+Source.getInstance());
		Source.getInstance().doSomeThing();
	}
}
