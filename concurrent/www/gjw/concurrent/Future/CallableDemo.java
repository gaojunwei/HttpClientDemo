package www.gjw.concurrent.Future;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Callable接口测试
 * 实现多任务计算并将计算结果汇总
 * @author gjw-pc
 */
public class CallableDemo { 
    public static void main(String[] args) { 
        ExecutorService executorService = Executors.newCachedThreadPool(); 
        List<Future<Integer>> resultList = new ArrayList<Future<Integer>>(); 

        Future<Integer> future1 = executorService.submit(new CallableTask01(1,3)); 
        resultList.add(future1);
        Future<Integer> future2 = executorService.submit(new CallableTask01(4,7)); 
        resultList.add(future2);
        Future<Integer> future3 = executorService.submit(new CallableTask01(8,10)); 
        resultList.add(future3);

        System.out.println((1+2+3+4+5+6+7+8+9+10));
        
        int r = 0;
        //遍历任务的结果
        for (Future<Integer> fs : resultList) {
            try {
            	System.out.println("得到结果："+fs.get());
            	r = r + (int)fs.get();//打印各个线程（任务）执行的结果
            } catch (InterruptedException |ExecutionException e) {
            	e.printStackTrace();
            } finally {
                executorService.shutdown();
            }
        }
        System.out.println("计算结果是："+r);
    }
} 