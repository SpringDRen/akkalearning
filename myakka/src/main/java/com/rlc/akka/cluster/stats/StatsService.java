package com.rlc.akka.cluster.stats;

import akka.cluster.Cluster;
import akka.cluster.pubsub.DistributedPubSub;
import akka.cluster.pubsub.DistributedPubSubMediator;
import com.rlc.akka.cluster.stats.StatsMessages.StatsJob;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.routing.ConsistentHashingRouter.ConsistentHashableEnvelope;
import akka.routing.FromConfig;

//#service
public class StatsService extends UntypedActor {

    // This router is used both with lookup and deploy of routees. If you
    // have a router with only lookup of routees you can use Props.empty()
    // instead of Props.create(StatsWorker.class).
    ActorRef workerRouter = getContext().actorOf(
            FromConfig.getInstance().props(Props.create(StatsWorker.class)),
            "workerRouter");

    ActorRef mediator =
            DistributedPubSub.get(getContext().system()).mediator();

    public StatsService() {
        mediator.tell(new DistributedPubSubMediator.Subscribe("content", getSelf()),
                getSelf());
    }

    @Override
    public void onReceive(Object message) {
        if (message instanceof StatsJob) {
            StatsJob job = (StatsJob) message;
            if (job.getText().equals("")) {
                unhandled(message);
            } else {
                //广播消息
                //如果使用ClusterSingleton，广播消息只能发送到oldest节点
                Cluster node = Cluster.get(getContext().system());
                mediator.tell(new DistributedPubSubMediator.Publish("content", "1234" + node.selfAddress()),
                        getSelf());
                //show child
                Iterable<ActorRef> it = getContext().getChildren();
                for (ActorRef ref : it) {
                    System.out.println(getSelf() + "-----:" + ref);
                }
                //--
                final String[] words = job.getText().split(" ");
                final ActorRef replyTo = getSender();

                // create actor that collects replies from workers
                ActorRef aggregator = getContext().actorOf(
                        Props.create(StatsAggregator.class, words.length, replyTo));

                // send each word to a worker
                for (String word : words) {
                    workerRouter.tell(new ConsistentHashableEnvelope(word, word),
                            aggregator);
                }
            }

        }
        if (message instanceof String) {
            if(getSelf().equals(getSender())){
                System.out.println("11111");
            }
            System.out.println(message);
        } else {
            unhandled(message);
        }
    }
}

//#service

