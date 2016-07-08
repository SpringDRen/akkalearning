package com.rlc.akka.actors;

import static akka.pattern.Patterns.ask;
import static akka.pattern.Patterns.pipe;

import akka.actor.*;
import akka.dispatch.OnSuccess;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.pattern.Patterns;
import scala.Function1;
import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;
import akka.dispatch.Futures;
import akka.dispatch.Mapper;
import akka.util.Timeout;
import scala.runtime.BoxedUnit;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/**
 * Created by renlc on 2016/6/21.
 */
public class SendMessageTest {

    static class ActorTest extends UntypedActor {

        LoggingAdapter log = Logging.getLogger(getContext().system(), this);

        @Override
        public void onReceive(Object o) throws Exception {
            log.info("Receive message....");
            log.info(String.valueOf(o.getClass()));
            log.info(String.valueOf(o));
            //让A延迟1s，测试一下顺序
            if (String.valueOf(getSelf()).indexOf("actorA") > 0) {
                Thread.sleep(1000);
            }
            //actorC 处理消息
            if (String.valueOf(getSelf()).indexOf("actorC") > 0) {
                String msg = String.valueOf(o);
                int ai = msg.indexOf("actorA");
                int bi = msg.indexOf("actorB");
                int i0 = msg.indexOf("actorTT0");
                int i1 = msg.indexOf("actorTT1");
                int i2 = msg.indexOf("actorTT2");
                int i3 = msg.indexOf("actorTT3");
                int i4 = msg.indexOf("actorTT4");
                if (!(ai < bi && bi < i0 && i0 < i1 && i1 < i2 && i2 < i3 && i3 < i4)) {
                    System.out.println("-------------顺序错了");
                }
            }
            String result = "Result:" + String.valueOf(getSelf());
            //使用ask需要返回消息
            getSender().tell(result, getSelf());
        }
    }

    static class Result {
        private StringBuilder builder = new StringBuilder();

        public Result() {
        }

        public void addResult(String str) {
            builder.append(str);
            builder.append(" ");
        }

        @Override
        public String toString() {
            return builder.toString();
        }
    }

    public static void main(String[] args) {
        ActorSystem system = ActorSystem.create("test");
        //Tell: Fire-forget
//        ActorRef tellActor = system.actorOf(Props.create(ActorTest.class), "tellActor");
//        tellActor.tell("test tell", ActorRef.noSender());

        //Ask: Send-And-Receive-Future
        ActorRef actorA = system.actorOf(Props.create(ActorTest.class), "actorA");
        ActorRef actorB = system.actorOf(Props.create(ActorTest.class), "actorB");
        ActorRef actorC = system.actorOf(Props.create(ActorTest.class), "actorC");

        final Timeout t = new Timeout(Duration.create(5, TimeUnit.SECONDS));
        final ArrayList<Future<Object>> futures = new ArrayList<Future<Object>>();
        futures.add(ask(actorA, "request", 1000)); // using 1000ms timeout
        futures.add(ask(actorB, "another request", t)); // using timeout from
        // above

        //按list放入顺序排序
        for (int i = 0; i < 5; i++) {
            ActorRef tac = system.actorOf(Props.create(ActorTest.class), "actorTT" + i);
            futures.add(ask(tac, "test sequ", t));
        }
        //结果是有序的，按照list的放入顺序
        final Future<Iterable<Object>> aggregate = Futures.sequence(futures,
                system.dispatcher());

        //处理结果
        final Future<Result> transformed = aggregate.map(
                new Mapper<Iterable<Object>, Result>() {
                    public Result apply(Iterable<Object> coll) {
                        final Iterator<Object> it = coll.iterator();
                        Result result = new Result();
                        while (it.hasNext()) {
                            String str = String.valueOf(it.next());
                            result.addResult(str);
                        }
                        return result;
                    }
                }, system.dispatcher());
        //将结果发送给C
        //pipe(transformed, system.dispatcher()).to(actorC);
        //或者直接使用回调函数
        transformed.onSuccess(new OnSuccess<Result>(){

            @Override
            public void onSuccess(Result result) throws Throwable {
                System.out.println("/////" + result.toString());
            }
        }, system.dispatcher());

        //可以添加回调
//        future.onComplete(..);
//        future.onFailure(..);
//        future.onSuccess(..);
//        Futures.future(new Callable<Object>() {
//            @Override
//            public Object call() throws Exception {
//                System.out.println("222");
//                return "abcd";
//            }
//        }, system.dispatcher());


        //Future与Actor直接使用
        ActorRef actorD = system.actorOf(Props.create(ActorTest.class), "actorD");
        Timeout timeout = new Timeout(Duration.create(5, "seconds"));
        Future<Object> future = Patterns.ask(actorD, "actorD", timeout);
        try {
            //会阻塞线程
            String result = (String) Await.result(future, timeout.duration());
            System.out.println(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //直接使用，不会阻塞
        System.out.println("开始future");
        Future<String> f = Futures.future(new Callable<String>() {
            public String call() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return "Hello " + "World";
            }
        }, system.dispatcher());
//        f.onSuccess(new PrintResult<String>(), system.dispatcher());
        f.onSuccess(new OnSuccess<String>() {
            @Override
            public void onSuccess(String result) throws Throwable {
                System.out.println("result:" + result);
            }
        }, system.dispatcher());
        System.out.println("不会阻塞");

        system.terminate();
    }

//     static class PrintResult<T> extends OnSuccess<T> {
//        @Override public final void onSuccess(T t) {
//            System.out.println("result:" + t);
//        }
//    }


}
