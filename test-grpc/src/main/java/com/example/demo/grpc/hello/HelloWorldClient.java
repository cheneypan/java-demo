package com.example.demo.grpc.hello;

import io.grpc.Context;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Created by cheney on 2018/7/13.
 */
public class HelloWorldClient {

    Logger logger = LoggerFactory.getLogger(getClass());

    private final ManagedChannel channel;
    private final GreeterGrpc.GreeterBlockingStub blockingStub;


    public HelloWorldClient(String host,int port){
        channel = ManagedChannelBuilder.forAddress(host,port)
                .usePlaintext()
                .build();

        blockingStub = GreeterGrpc.newBlockingStub(channel);
    }


    public void shutdown() throws InterruptedException {
//        Thread.sleep(1000);
        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
        logger.info("exit ... ...");
    }

    public  void greet(String name){
        HelloRequest request = HelloRequest.newBuilder().setName(name).build();
        HelloReply response = blockingStub.sayHello(request);
        logger.info(response.getMessage());
    }

    public void singleStream(String name) {
        Context.CancellableContext withCancellation = Context.current().withCancellation();
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
                            logger.info(">>>>>> out ....");
                            withCancellation.cancel(Status.CANCELLED.asException());
                            break;
                        }
                    }
                }
            });
            logger.info("end ... ...");
        } catch (Throwable t) {
            withCancellation.cancel(t);
        }
    }

    public void chat(){
        GreeterGrpc.GreeterStub stub =  GreeterGrpc.newStub(channel);
        final CountDownLatch finishLatch = new CountDownLatch(1);
        StreamObserver<HelloRequest> requestStreamObserver = stub.chat(new StreamObserver<HelloReply>(){
            @Override
            public void onNext(HelloReply helloReply) {
                logger.info("Reply >>> " + helloReply.getMessage());
            }

            @Override
            public void onError(Throwable throwable) {
                logger.info("error: " + throwable.getMessage());
                finishLatch.countDown();
            }

            @Override
            public void onCompleted() {
                logger.info("Completed ...");
                finishLatch.countDown();
            }
        });
        Random random = new Random();
        for(int i=0; i<6; i++){
            logger.info("index >>> " + i);
            requestStreamObserver.onNext(HelloRequest.newBuilder().setName("name_" + i).setSex("" + i).build());
            try {
                Thread.sleep(random.nextInt(1000) + 500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (finishLatch.getCount() == 0) {
                return;
            }
        }
        //标识已经写完
        requestStreamObserver.onCompleted();
    }

    public static void main(String[] args) throws InterruptedException {
        HelloWorldClient client = new HelloWorldClient("127.0.0.1",50051);
//        for(int i=0;i<5;i++){
//            client.greet("world_"+i);
//        }
        client.singleStream("cheney");
        client.singleStream("drolly");
//        client.chat();
        client.shutdown();
    }
}
