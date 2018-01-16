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

    private final PartialFunction<Object, BoxedUnit> handleOrders = ReceiveBuilder
            .match(Order.class,
                    order -> {
                        log.info("chief waiter will handle the order = " + order + "\n");
                    })
            .matchAny(msg -> sender().tell(new Status.Failure(new IllegalStateException("unknown message")), self()))
            .build();


    private final PartialFunction<Object, BoxedUnit> sendOrdersToBasicWaiter = ReceiveBuilder
            .match(Order.class,
                    order -> {

                        log.info("propagating order to basic waiter, order = " + order + "\n");

                        ActorRef basicWaiterActor = getChild();

                        basicWaiterActor.tell(order, getContext().self());

                    })
            .match(UnderHeavyWorkload.class,
                    msg -> {

                        log.info("basic waiter = " + getContext().sender().path() + " cannot handle another workload, so chief will handle the other orders." + "\n");
                        getContext().become(handleOrders);

                    })
            .matchAny(msg -> sender().tell(new Status.Failure(new IllegalStateException("unknown message")), self()))
            .build();


    public ChiefWaiter() {

        receive(sendOrdersToBasicWaiter);
    }

    @Override
    public void preStart() {
        log.info("actor is going to start..." + "\n");
    }

    @Override
    public void postStop() {
        log.info("actor is going to stop..." + "\n");
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
