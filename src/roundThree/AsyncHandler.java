package roundThree;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * 同步与异步
 *
 *        通常同步意味着一个任务的某个处理过程会对多个线程在用串行化处理，而异步则意味着某个处理过程可以允许多个线程同时处理。
 *
 *        异步通常代表着更好的性能，因为它很大程度上依赖于缓冲，是典型的使用空间换时间的做法，例如在计算机当中，高速缓存作为cpu和磁盘io之间的缓冲地带协调cpu高速计算能力和磁盘的低速读写能力。
 *
 * volatile
 *
 *        应用场景：检查一个应用执行关闭或中断状态。因为此关键字拒绝了虚拟对一个变量多次赋值时的优化从而保证了虚拟机一定会检查被该关键字修饰的变量的状态变化。
 *
 * CountDownLatch
 *
 *        应用场景：控制在一组线程操作执行完成之前当前线程一直处于等待。例如在主线程中执行await()方法阻塞主线程，在工作线程执行完逻辑后执行countDown()方法。
 *
 * 本文示例场景：
 *
 *        1，从控制台发送消息到消息服务器(由一个队列模拟)。
 *
 *        2，将消息队列写入到文件(对写文件的操作设置延时以模拟性能瓶颈)。
 *
 *        3，消息服务器作为控制台和文件写入之间的缓冲区。
 */
public class AsyncHandler {

    /**
     * 控制资源释放.
     */
    private CountDownLatch latch;

    /**
     * 处理完成标识.
     */
    private volatile boolean handleFinish;

    /**
     * 消息写入本地文件完成.
     */
    private volatile boolean sendFinish;

    /**
     * 阻塞队列.
     */
    private BlockingQueue<String> queue;

    private BufferedWriter bw;

    public AsyncHandler(CountDownLatch latch) {
        this.latch = latch;
        /**
         * 使用链表实现.
         */
        queue = new LinkedBlockingQueue<String>();
        File file = new File("./resource/out/crawler01.txt");
        try {
            bw = new BufferedWriter(new FileWriter(file));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void handle() {
        // 模拟性能瓶颈的执行过程,3s处理一条消息.
        new Thread() {
            public void run() {
                while (!handleFinish) {
                    try {
                        TimeUnit.SECONDS.sleep(3);
                    } catch (InterruptedException e1) {
                        // 不做处理.
                    }
                    String s = queue.peek();
                    if (s != null) {
                        queue.poll();
                        try {
                            bw.write(s);
                            bw.newLine();
                        } catch (IOException e) {
                        }
                    }
                    // 若队列为空并且消息发送完成.
                    if (queue.isEmpty() && sendFinish) {
                        // 计数器1->0
                        latch.countDown();
                        // 让处理过程结束.
                        handleFinish = true;
                        break;
                    }
                }
            }

        }.start();

    }

    /**
     *
     * <pre>
     * 给出消息发送完成的标识.
     * </pre>
     *
     */
    public void sendFinish() {
        sendFinish = true;
    }

    /**
     *
     * <pre>
     * 资源释放.
     * </pre>
     *
     */
    public void release() {
        System.out.println("release!");
        if (bw != null) {
            try {
                bw.close();
            } catch (IOException e) {
                // TODO 打印日志.
            }
        }
        //其实使用queue = null就够了.
        if (queue != null) {
            queue.clear();
            queue = null;
        }
    }

    /**
     *
     * <pre>
     * 往队列发送消息.
     * </pre>
     *
     * @param text
     */
    public void sendMsg(String text) {
        if (text != null && !text.isEmpty()) {
            queue.add(text);
        }
    }

    public static void main(String[] args) {
        CountDownLatch latch = new CountDownLatch(1);
        AsyncHandler handler = new AsyncHandler(latch);
        handler.handle();

        // 做一次检查.
        Scanner scanner = new Scanner(System.in);
        while (true) {
            String text = scanner.next();
            // 若用户选择退出.
            if ("exit".equals(text)) {
                // 表示消息已经发送完成.
                handler.sendFinish();
                break;
            }
            handler.sendMsg(text);
        }

        try {
            // 阻塞主线程等待消息写入到本地文件完成.
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // 释放资源 文件流,队列.
        handler.release();
        // 关闭控制台输入.
        scanner.close();
    }

}
