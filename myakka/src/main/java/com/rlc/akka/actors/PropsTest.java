package com.rlc.akka.actors;

import akka.actor.*;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.Creator;

/**
 * Created by renlc on 2016/6/20.
 */
public class PropsTest {

    static class MyActor extends UntypedActor{

        LoggingAdapter log = Logging.getLogger(getContext().system(), this);

        private String actorType;

        public MyActor() {
            super();
        }

        /**
         * 提供静态方法，创建props
         * @param type test
         * @return Props
         */
        public static Props props(final String type){
            return Props.create(MyActor.class,(Creator<MyActor>) () -> new MyActor(type));
        }

        public MyActor(String actorType) {
            super();
            this.actorType = actorType;
        }

        @Override
        public void onReceive(Object o) throws Exception {
            if(o instanceof String){
                Thread.sleep(2000);
                log.info(actorType + ":" + String.valueOf(o));
            } else {
                unhandled(o);
            }
        }
    }

    /**
     * 构造器
     */
    static class MyActorC implements Creator<MyActor> {

        String type;

        public MyActorC(String type){
            this.type = type;
        }

        @Override public MyActor create() {
            return new MyActor(this.type);
        }
    }

    public static void main(String[] args) {
        //无参构造函数
        Props props1 = Props.create(MyActor.class);
        //带参构造函数
        Props props2 = Props.create(MyActor.class, "abcd");
        //使用构造器
        Props props3 = Props.create(new MyActorC("efgh"));
        //使用匿名构造函数
        final String type = "ijkl";
        Props props4 = Props.create(MyActor.class,(Creator<MyActor>) () -> new MyActor(type));

        ActorSystem system = ActorSystem.create("test");
        ActorRef ref1 = system.actorOf(props1, "ref1");
        ActorRef ref2 = system.actorOf(props2, "ref2");
        ActorRef ref3 = system.actorOf(props3, "ref3");
        ActorRef ref4 = system.actorOf(props4, "ref4");
        ActorRef ref5 = system.actorOf(MyActor.props("aaaa"), "ref5");
        ref1.tell("ref1", ref1);
        ref2.tell("ref2", ref2);
        ref3.tell("ref3", ref3);
        ref4.tell("ref4", ref4);
        ref5.tell("ref5", ref5);
        ref5.tell("ref5", ref5);
        System.out.println("终止系统");
        //此操作发生在所有Actor完成任务之后
        system.terminate();
    }
}
