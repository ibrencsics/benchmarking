package org.ib.eval.ribbon;

import io.netty.buffer.ByteBuf;
import io.reactivex.netty.protocol.http.client.HttpClientRequest;
import io.reactivex.netty.protocol.http.client.HttpClientResponse;

import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import rx.Observer;

import com.google.common.collect.Lists;
import com.netflix.ribbon.transport.netty.RibbonTransport;
import com.netflix.ribbon.transport.netty.http.LoadBalancingHttpClient;
import com.netflix.loadbalancer.BaseLoadBalancer;
import com.netflix.loadbalancer.LoadBalancerBuilder;
import com.netflix.loadbalancer.Server;


public class RibbonClient {

    private static Logger logger = LogManager.getLogger(RibbonClient.class);

    public static void main(String[] args) throws Exception {
        call();
    }

    private static void call() throws Exception {
        List<Server> servers = Lists.newArrayList(new Server("localhost:8000"), new Server("localhost:8001"));
        BaseLoadBalancer lb = LoadBalancerBuilder.newBuilder()
            .buildFixedServerListLoadBalancer(servers);

        LoadBalancingHttpClient<ByteBuf, ByteBuf> client = RibbonTransport.newHttpClient(lb);
        final CountDownLatch latch = new CountDownLatch(servers.size());
        Observer<HttpClientResponse<ByteBuf>> observer = new Observer<HttpClientResponse<ByteBuf>>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
            }

            @Override
            public void onNext(HttpClientResponse<ByteBuf> args) {
                latch.countDown();
                logger.debug("Got response: " + args.getStatus());
            }
        };
        for (int i = 0; i < 20/*servers.size()*/; i++) {
//            Thread.sleep(500);
            HttpClientRequest<ByteBuf> request = HttpClientRequest.createGet("/");
            client.submit(request).subscribe(observer);
        }
        latch.await();
        System.out.println(lb.getLoadBalancerStats());
    }
}
