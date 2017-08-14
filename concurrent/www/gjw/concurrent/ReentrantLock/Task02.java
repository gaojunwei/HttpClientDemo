package www.gjw.concurrent.ReentrantLock;


/**
 * 资源 单例模式
 * @author gjw-pc
 */
public class Task02 implements Runnable {
	@Override
	public void run() {
		System.out.println(Thread.currentThread().getName()+"-实例化Source："+Source.getInstance());
		Source.getInstance().doSomeThing();
	}
}
