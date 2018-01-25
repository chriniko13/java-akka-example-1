package com.chriniko.examples.fourth.actor;

import akka.actor.AbstractActor;
import akka.actor.Props;
import akka.actor.Status;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.pf.ReceiveBuilder;
import com.chriniko.examples.fourth.message.Order;
import com.chriniko.examples.fourth.message.UnderHeavyWorkload;

import java.util.ArrayList;
import java.util.List;

public class BasicWaiter extends AbstractActor {

    private final LoggingAdapter log = Logging.getLogger(context().system(), this);

    private static final int ORDERS_UNDER_PROCESSING_THRESHOLD = 2;
    private final List<Order> ordersUnderProcessing = new ArrayList<>();

    private final Receive notAvailableForService = ReceiveBuilder
            .create()
            .match(Order.class,
                    order -> {
                        log.info("could not handle another order (too much workload), reporting to chief = "
                                + getContext().sender().path()
                                + ", order = "
                                + order + "\n");
                        getContext().sender().tell(new UnderHeavyWorkload(order), getContext().self());
                    })
            .matchAny(msg -> sender().tell(new Status.Failure(new IllegalStateException("unknown message")), self()))
            .build();

    private final Receive availableForService = ReceiveBuilder
            .create()
            .match(Order.class,
                    order -> {
                        log.info("incoming order = " + order + "\n");

                        if (ordersUnderProcessing.size() == ORDERS_UNDER_PROCESSING_THRESHOLD) {
                            getContext().become(notAvailableForService);
                        }
                        ordersUnderProcessing.add(order);

                    })
            .matchAny(msg -> sender().tell(new Status.Failure(new IllegalStateException("unknown message")), self()))
            .build();

    public static Props props() {
        return Props.create(BasicWaiter.class);
    }

    @Override
    public Receive createReceive() {
        return availableForService;
    }
}
