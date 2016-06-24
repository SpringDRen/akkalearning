package com.rlc.akka.helloword;

import akka.actor.*;
import scala.concurrent.duration.Duration;
import scala.concurrent.duration.FiniteDuration;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by renlc on 2016/6/22.
 * 定时器测试，消息不可变性测试
 */
public class ScheduleHelloTest {

    static class HelloWorld extends UntypedActor {

        @Override
        public void onReceive(Object message) throws Exception {
            if (message instanceof String) {
                System.out.println(String.valueOf(message));
            }
        }
    }

    static class TellActor extends UntypedActor {

        /**
         * stop消息
         */
        static class Stop{
            //消息不变性测试结果，如果消息为某个自定义的对象，要求其变量均为不可变
            public /*final*/  String info;
//            public final List<String> list;

            public Stop(String info) {
                this.info = info;
            }

//            public Stop(String info,List<String> list) {
//                this.info = info;
//                //把list转换为不可变对象
//                this.list = Collections.unmodifiableList(list);
//            }

            @Override
            public String toString() {
                return super.toString() + "[Stop]" + this.info;
            }
        }

        final ActorRef ref = getContext().actorOf(Props.create(HelloWorld.class), "helloactor");
        //可以取消定时器
        Cancellable cancellable ;

        @Override
        public void onReceive(Object message) throws Exception {
            if (message instanceof String) {
                //初始延迟时间
                FiniteDuration delayTime = Duration.Zero();
                //时间间隔
                FiniteDuration intervalTime = Duration.create(1, TimeUnit.SECONDS);
                //每隔1s发送一次消息
                cancellable = getContext().system().scheduler().schedule(delayTime, intervalTime, () -> ref.tell("haha", ActorRef.noSender()), getContext().dispatcher());
//                getContext().stop(getSelf());
                //直接停止对象，定时器不会随之终止，需要调用cancellable的停止方法，停止system可以停止定时器
            } else if(message instanceof Stop){
                Thread.sleep(1000);
                System.out.println(message);
                System.out.println(cancellable.isCancelled());
                cancellable.cancel();
            } else {
                System.out.println(message);
                unhandled(message);
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        ActorSystem system = ActorSystem.create("hellosytem");
        ActorRef ref = system.actorOf(Props.create(TellActor.class), "tell");
        ref.tell("start", ActorRef.noSender());
        //延迟3s
        Thread.sleep(3000);
        TellActor.Stop stop = new TellActor.Stop("11");
        System.out.println("1:" + stop);
        ref.tell(stop, null);
        stop.info="33";
        System.out.println("2:" + stop);
        stop =  new TellActor.Stop("22");
        System.out.println("3:" + stop);
        //变量不变性测试结果如下：
        //1、消息传递之后，改变消息对象，不会影响Actor的处理
        //2、消息对象里的变量如果发生变化，Actor里的消息会受到影响
        //ref.tell(stop, null);
    }
}
