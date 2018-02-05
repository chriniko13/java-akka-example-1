package com.chriniko.examples.ninth.actor;

import akka.actor.AbstractLoggingActor;
import akka.actor.Props;
import akka.japi.pf.ReceiveBuilder;
import com.chriniko.examples.ninth.message.CleanBodyRequest;
import com.chriniko.examples.ninth.message.SearchForUrlRequest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.URL;
import java.net.URLConnection;

/*
    Note: this actor does I/O process, so it would be nice to create it with his own pool executor in order to not block.

 */
public class HttpActor extends AbstractLoggingActor {


    @Override
    public Receive createReceive() {
        return ReceiveBuilder
                .create()
                .match(SearchForUrlRequest.class, msg -> {

                    final String urlToFetchBodyFrom = msg.getUrl();

                    String body = getBody(urlToFetchBodyFrom);

                    sender().tell(new CleanBodyRequest(urlToFetchBodyFrom, body), getSelf());

                })
                .matchAny(msg -> {
                    unhandled(msg);
                })
                .build();
    }


    private String getBody(String urlToFetchBody) {
        try {

            final URL url = new URL(urlToFetchBody);
            final URLConnection conn = url.openConnection();


            try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                 StringWriter sw = new StringWriter()
            ) {

                String inputLine;


                while ((inputLine = br.readLine()) != null) {
                    sw.write(inputLine);
                }

                return sw.toString();
            }

        } catch (IOException error) {

            error.printStackTrace(System.err);
            //getContext().stop(self()); // Note: comment-uncomment depending on your needs.
            throw new RuntimeException(error);
        }

    }

    public static Props props() {
        return Props.create(HttpActor.class);
    }

}
