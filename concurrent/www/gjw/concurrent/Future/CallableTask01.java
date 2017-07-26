package www.gjw.concurrent.Future;

import java.util.Random;
import java.util.concurrent.Callable;

public class CallableTask01 implements Callable<Integer>{
	private int a;
	private int b;
	
	public CallableTask01(int a,int b) {
		this.a = a;
		this.b = b;
	}

	@Override
	public Integer call() throws Exception {
		System.out.println(Thread.currentThread().getName()+" ¿ªÊ¼");
		int r = 0;
		for (int i = a; i <= b; i++) {
			System.out.println(i+"--"+r);
			r=r+i;
		}
        Thread.sleep(new Random().nextInt(11)*1000);
        System.out.println(Thread.currentThread().getName()+" ½áÊø="+r);
        return r;
	}
}
