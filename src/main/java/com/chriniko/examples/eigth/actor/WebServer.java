package com.chriniko.examples.eigth.actor;

import akka.actor.*;
import akka.event.Logging;
import akka.http.javadsl.ConnectHttp;
import akka.http.javadsl.Http;
import akka.http.javadsl.ServerBinding;
import akka.http.javadsl.marshallers.jackson.Jackson;
import akka.http.javadsl.marshalling.Marshaller;
import akka.http.javadsl.model.RequestEntity;
import akka.http.javadsl.model.StatusCodes;
import akka.http.javadsl.server.Route;
import akka.japi.pf.ReceiveBuilder;
import akka.stream.ActorMaterializer;
import akka.stream.Materializer;
import com.chriniko.examples.eigth.domain.Student;
import com.chriniko.examples.eigth.message.GetStudent;
import com.chriniko.examples.eigth.message.StudentResult;
import scala.util.Try;

import java.net.InetSocketAddress;
import java.util.concurrent.CompletionStage;

import static akka.http.javadsl.server.Directives.*;
import static akka.http.javadsl.server.PathMatchers.longSegment;
import static akka.http.javadsl.server.PathMatchers.segment;
import static akka.pattern.PatternsCS.ask;
import static akka.pattern.PatternsCS.pipe;

public class WebServer extends AbstractLoggingActor {

    private final static Marshaller<Student, RequestEntity> studentMarshaller = Jackson.<Student>marshaller();

    // internal protocol
    private static class ServerStarted {
        final String host;
        final int port;

        public ServerStarted(String host, int port) {
            this.host = host;
            this.port = port;
        }
    }

    private static class ServerFailed {
        final Exception cause;

        public ServerFailed(Exception cause) {
            this.cause = cause;
        }
    }

    public static Props props(ActorRef database, String host, int port) {
        return Props.create(WebServer.class, database, host, port);
    }


    private final ActorRef database;
    private final CompletionStage<ServerBinding> bindingCompletionStage;

    public WebServer(ActorRef database, String host, int port) {

        this.database = database;

        // create route...
        final Route route =
                logRequest("request", Logging.InfoLevel(), () ->
                        path(segment("students").slash(longSegment()), (studentId) ->

                                get(() ->
                                        onComplete(lookupStudent(studentId), (Try<StudentResult> result) -> {

                                            if (result.isFailure()) {
                                                return complete(StatusCodes.SERVICE_UNAVAILABLE);
                                            } else {

                                                final StudentResult studentResult = result.get();

                                                if (studentResult.getStudent() != null) {
                                                    return completeOK(studentResult.getStudent(), studentMarshaller);
                                                } else {
                                                    return complete(StatusCodes.NOT_FOUND);
                                                }

                                            }
                                        })
                                )
                        )
                );

        // create materializer...
        Materializer materializer = ActorMaterializer.create(context());

        // get the actor system...
        ActorSystem system = context().system();

        bindingCompletionStage = Http.get(system)
                .bindAndHandle(
                        route.flow(system, materializer),
                        ConnectHttp.toHost(host, port),
                        materializer);


        // starting the http server is async, inform us when it completes, or fails
        pipe(bindingCompletionStage, context().dispatcher()).to(self());

        // add message handling...
        receive(ReceiveBuilder
                .match(Status.Failure.class, failure -> onFailure(failure.cause()))
                .match(ServerBinding.class, this::onStarted)
                .build());
    }

    private void onStarted(ServerBinding binding) {
        final InetSocketAddress address = binding.localAddress();
        log().info("Server started at {}:{}", address.getHostString(), address.getPort());
    }

    private void onFailure(Throwable cause) {
        log().error(cause, "Failed to start webserver");
        throw new RuntimeException(cause);
    }

    private CompletionStage<StudentResult> lookupStudent(long studentId) {
        return ask(database, // actor to ask
                new GetStudent(String.valueOf(studentId)), // message
                500 /* max time in ms to wait before failing */)
                .thenApply(object -> ((StudentResult) object));

    }


    @Override
    public void postStop() {
        // make sure we stop the http server when actor stops
        bindingCompletionStage.thenAccept(ServerBinding::unbind);
    }
}
