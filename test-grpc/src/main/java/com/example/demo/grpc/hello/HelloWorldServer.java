package com.example.demo.grpc.hello;

import io.grpc.Context;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * gRPC学习记录(四)--官方Demo
 * https://www.jianshu.com/p/39c9eedba2c2
 * Created by cheney on 2018/7/13.
 */
public class HelloWorldServer {

    Logger logger = LoggerFactory.getLogger(getClass());

    private int port = 50051;
    private Server server;

    private void start() throws IOException {
        server = ServerBuilder.forPort(port)
                .addService(new GreeterImpl())
                .build()
                .start();

        logger.info("service start ... ...");

        Runtime.getRuntime().addShutdownHook(new Thread() {

            @Override
            public void run() {

                logger.info("*** shutting down gRPC hello since JVM is shutting down");
                HelloWorldServer.this.stop();
                System.err.println("*** hello shut down");
            }
        });
    }

    private void stop() {
        if (server != null) {
            server.shutdown();
        }
    }

    // block 一直到退出程序
    private void blockUntilShutdown() throws InterruptedException {
        if (server != null) {
            server.awaitTermination();
        }
    }


    public static void main(String[] args) throws IOException, InterruptedException {
        final HelloWorldServer server = new HelloWorldServer();
        server.start();
        server.blockUntilShutdown();
    }


    // 实现 定义一个实现服务接口的类
    private class GreeterImpl extends GreeterGrpc.GreeterImplBase {


        public void sayHello(HelloRequest req, StreamObserver<HelloReply> responseObserver) {
            logger.info("service:"+req.getName());
            HelloReply reply = HelloReply.newBuilder().setMessage(("Hello: " + req.getName())).build();
            responseObserver.onNext(reply);
            responseObserver.onCompleted();
        }

        public void singleStream(HelloRequest req, StreamObserver<HelloReply> responseObserver){
            logger.info("service:" + req.getName());
            for(int i=0; ; i++){
                if(Context.current().isCancelled()){
                    logger.info("[name={}]context current is cancelled .", req.getName());
                    break;
                }
                String message = Context.current().isCancelled() +  " > Hello: " + req.getName() + "_" + i;
                HelloReply reply = HelloReply.newBuilder().setMessage(message).build();
                responseObserver.onNext(reply);
                logger.info("reply: " + message);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                }
            }
//            responseObserver.onCompleted();
        }

        public StreamObserver<HelloRequest> chat(StreamObserver<HelloReply> responseObserver){
            return new StreamObserver<HelloRequest>() {
                @Override
                public void onNext(HelloRequest req) {
                    logger.info("service:"+req.getName());
                    HelloReply reply = HelloReply.newBuilder().setMessage(("Hello: " + req.getName())).build();
                    responseObserver.onNext(reply);
                }

                @Override
                public void onError(Throwable t) {
                    t.printStackTrace();
                    logger.error("Encountered error in routeChat");
                }

                @Override
                public void onCompleted() {
                    responseObserver.onCompleted();
                }
            };
        }
    }
}
