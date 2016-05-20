package com.fun.spider;

import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * 公共常量
 * 
 * @author zhenggm<a href="mailto:zhenggm@chsi.com.cn">zhenggm</a>
 * @version $Id$
 */
class Constants {
    public static final int MAX_BUFFER_SIZE = 100;
    public static final int NUM_OF_PRODUCER = 8;
    public static final int NUM_OF_CONSUMER = 10;
}

/**
 * 生成问题ID,以2开头的所有问题
 * 
 * @author zhenggm<a href="mailto:zhenggm@chsi.com.cn">zhenggm</a>
 * @version $Id$
 */
class Task {
    private String id;

    public Task() {
        id = getNum();
    }

    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        return "Task[" + id + "]";
    }

    private String getNum() {
        int num = new Random().nextInt(10);
        int count = 1;
        String id = "2";
        if (num != 0) {
            id += String.valueOf(num);
            while (count++ < 7) {
                id += String.valueOf(new Random().nextInt(10));
            }
        }
        return id;
    }
}

/**
 * 生产者
 * 
 * @author zhenggm<a href="mailto:zhenggm@chsi.com.cn">zhenggm</a>
 * @version $Id$
 */
class Producer implements Runnable {
    private BlockingQueue<Task> buffer;

    public Producer(BlockingQueue<Task> buffer) {
        this.buffer = buffer;
    }

    public void run() {
        while (true) {
            Task task = new Task();
            try {
                buffer.put(task);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }
}

/**
 * 消费者
 * 
 * @author zhenggm<a href="mailto:zhenggm@chsi.com.cn">zhenggm</a>
 * @version $Id$
 */
class Consumer implements Runnable {
    private BlockingQueue<Task> buffer;

    public Consumer(BlockingQueue<Task> buffer) {
        this.buffer = buffer;
    }

    public void run() {
        while (true) {
            try {
                Task task = buffer.take();
                String id = task.getId();
                if (!id.equals("2")) {
                    getContent(id);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    private void getContent(String id) {
        String url = "http://www.zhihu.com/question/" + id;
        try {
            Document dom = Jsoup.connect(url).userAgent("Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)")
                    .timeout(5000).get();
            Elements elems = dom.select("#zh-question-title");
            String title = elems.get(0).text();
            StringBuffer sb = new StringBuffer();
            sb.append("<h4>" + title + "</h4>");
            Elements answers = dom.select(".zm-editable-content");
            int length = answers.size();
            if (length > 0) {
                for (int i = 1; i < length; i++) {
                    Element link = answers.get(i);
                    sb.append(link.html().replaceAll("data-actualsrc", "src"));
                    sb.append("-----------------------------------------<br/>");
                }
                // 写入文件
                // FileWriter.write(sb.toString());
                System.out.println(sb.toString());
            }
        } catch (Exception e) {
            // System.out.println("no  this question Id");
        }
    }
}

public class Spider {
    public static void main(String[] args) {
        BlockingQueue<Task> buffer = new LinkedBlockingQueue<Task>(Constants.MAX_BUFFER_SIZE);
        ExecutorService es = Executors.newFixedThreadPool(Constants.NUM_OF_CONSUMER + Constants.NUM_OF_PRODUCER);
        for (int i = 1; i <= Constants.NUM_OF_PRODUCER; ++i) {
            es.execute(new Producer(buffer));
        }
        for (int i = 1; i <= Constants.NUM_OF_CONSUMER; ++i) {
            es.execute(new Consumer(buffer));
        }
    }
}
