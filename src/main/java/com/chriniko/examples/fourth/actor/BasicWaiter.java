package com.chriniko.examples.fourth.actor;

import akka.actor.AbstractActor;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.pf.ReceiveBuilder;
import com.chriniko.examples.fourth.message.Order;
import com.chriniko.examples.fourth.message.UnderHeavyWorkload;
import scala.PartialFunction;
import scala.runtime.BoxedUnit;

import java.util.ArrayList;
import java.util.List;

public class BasicWaiter extends AbstractActor {

    private final LoggingAdapter log = Logging.getLogger(context().system(), this);

    private static final int ORDERS_UNDER_PROCESSING_THRESHOLD = 2;
    private final List<Order> ordersUnderProcessing = new ArrayList<>();

    private final PartialFunction<Object, BoxedUnit> availableForService;
    private final PartialFunction<Object, BoxedUnit> notAvailableForService;

    public BasicWaiter() {

        notAvailableForService = ReceiveBuilder.match(Order.class,
                order -> order.getItems() != null && !order.getItems().isEmpty(),
                order -> {
                    log.info("could not handle incoming order (too much workload), reporting to chief = "
                            + getContext().sender().path()
                            + ", order = "
                            + order);
                    getContext().sender().tell(new UnderHeavyWorkload(order), getContext().self());
                })
                .build();

        availableForService = ReceiveBuilder.match(Order.class,
                order -> order.getItems() != null && !order.getItems().isEmpty(),
                order -> {
                    log.info("incoming order = " + order);

                    if (ordersUnderProcessing.size() >= ORDERS_UNDER_PROCESSING_THRESHOLD) {

                        getContext().become(notAvailableForService);

                    } else {
                        ordersUnderProcessing.add(order);
                    }

                })
                .build();


        receive(availableForService);
    }

    public static Props props() {
        return Props.create(BasicWaiter.class);
    }
}
