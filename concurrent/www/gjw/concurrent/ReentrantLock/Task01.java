package www.gjw.concurrent.ReentrantLock;


/**
 * 资源 非单例模式
 * @author gjw-pc
 */
public class Task01 implements Runnable {
	@Override
	public void run() {
		Source source = new Source();
		System.out.println(Thread.currentThread().getName()+"-实例化Source："+source);
		source.doSomeThing();
	}
}
