package com.chriniko.examples.fourth.actor;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.Status;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.pf.ReceiveBuilder;
import com.chriniko.examples.fourth.message.Order;
import com.chriniko.examples.fourth.message.UnderHeavyWorkload;
import scala.PartialFunction;
import scala.runtime.BoxedUnit;

public class ChiefWaiter extends AbstractActor {

    private static final String CHILD_NAME = "basicWaiter";

    private final LoggingAdapter log = Logging.getLogger(context().system(), this);

    private final PartialFunction<Object, BoxedUnit> sendOrdersToBasicWaiter;

    public ChiefWaiter() {

        sendOrdersToBasicWaiter = ReceiveBuilder
                .match(Order.class,
                        order -> order.getItems() != null && !order.getItems().isEmpty(),
                        order -> {

                            log.info("propagating order to basic waiter, order = " + order);

                            ActorRef basicWaiterActor = getChild();

                            basicWaiterActor.tell(order, getContext().self());

                        })
                .match(UnderHeavyWorkload.class,
                        msg -> {

                            log.info("basic waiter cannot handle workload, so chief will handle it.");

                        })
                .matchAny(msg -> sender().tell(new Status.Failure(new IllegalStateException("unknown message")), self()))
                .build();

        receive(sendOrdersToBasicWaiter);
    }

    @Override
    public void preStart() throws Exception {
        log.info("actor is going to start...");
    }

    @Override
    public void postStop() throws Exception {
        log.info("actor is going to stop...");
    }

    private ActorRef getChild() {
        ActorRef basicWaiterActor = getContext().getChild(CHILD_NAME);
        if (basicWaiterActor == null) {
            basicWaiterActor = getContext().actorOf(BasicWaiter.props(), CHILD_NAME);
        }
        return basicWaiterActor;
    }


    public static Props props() {
        return Props.create(ChiefWaiter.class);
    }
}
