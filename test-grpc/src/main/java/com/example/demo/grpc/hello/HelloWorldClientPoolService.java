package com.example.demo.grpc.hello;

import io.grpc.Context;
import io.grpc.ManagedChannel;
import io.grpc.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.Iterator;

/**
 * Created by cheney on 2018/7/17.
 */
@Component
public class HelloWorldClientPoolService {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private GRpcChannelPool helloChannelPool;

    @PostConstruct
    public void init(){
        ManagedChannel channel = helloChannelPool.borrowChannel();
//        String[] names = {"cheney", "pan", "drolly", "nero", "abc", "keven", "spoon"};
        String[] names = {"cheney"};
        for (String name : names) {
            new Thread(() ->{
                Context.CancellableContext withCancellation = Context.current().withCancellation();
                GreeterGrpc.GreeterBlockingStub blockingStub = GreeterGrpc.newBlockingStub(channel);
                singleStream(blockingStub, withCancellation, name);
            }).start();
        }
//        new Thread(() -> {
//            try {
//                Thread.sleep(10 * 1000);
//                helloChannelPool.close();
//            } catch (IOException | InterruptedException e) {
//                logger.error(e.getMessage(), e);
//            }
//        }).start();
//        for(int i=0; i<100; i++){
//            int index = i;
//            new Thread(() ->
//                    singleStream(channel, "cheney_" + index)
//            ).start();
//        }
    }

    private void singleStream(GreeterGrpc.GreeterBlockingStub blockingStub, Context.CancellableContext withCancellation, String name) {
//        logger.info("channel state: {}", channel.getState(true));
        logger.info("name = {}", name);
        try {
            withCancellation.run(() -> {
                while (!Context.current().isCancelled()) {
                    int count = 0;
                    Iterator<HelloReply> iterator = blockingStub.singleStream(HelloRequest.newBuilder().setName(name).setSex("1").build());
                    for (Iterator<HelloReply> iterator2 = iterator; iterator.hasNext(); ) {
                        HelloReply helloReply = iterator2.next();
                        logger.info("Get Reploy >>> " + helloReply.getMessage());
                        count++;
                        if (count >= 5) {
                            logger.info(">>>>>> out [name={}]....", name);
                            withCancellation.cancel(Status.CANCELLED.asException());
                            break;
                        }
                    }
                }
            });
            logger.info("[name={}] end ... ...", name);
        } catch (Throwable t) {
            logger.error(t.getMessage() + "[" + name + "] ");
            // reconnect
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
            }
            logger.info("[name={}] reconnect ...", name);
            this.singleStream(blockingStub, withCancellation, name);
        }
    }
}
