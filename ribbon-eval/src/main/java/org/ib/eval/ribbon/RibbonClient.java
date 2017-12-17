package org.ib.eval.ribbon;

import com.netflix.client.config.DefaultClientConfigImpl;
import com.netflix.client.config.IClientConfig;
import com.netflix.client.config.IClientConfigKey;
import com.netflix.config.ConfigurationManager;
import com.netflix.loadbalancer.ILoadBalancer;
import io.netty.buffer.ByteBuf;
import io.reactivex.netty.protocol.http.client.HttpClientRequest;
import io.reactivex.netty.protocol.http.client.HttpClientResponse;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.apache.commons.configuration.Configuration;
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
    private static final int REQ_COUNT = 10;


    public static void main(String[] args) throws Exception {
        call();
    }

    private static void call() throws Exception {
        ConfigurationManager.loadPropertiesFromResources("sample-client.properties");  // 1
//        System.out.println(ConfigurationManager.getConfigInstance().getString("sample-client.ribbon.listOfServers"));

//        Configuration config = ConfigurationManager.getConfigInstance();
//        config.setProperty("client1.niws.client." + IClientConfigKey.Keys.DeploymentContextBasedVipAddresses, "dummy:7001");
//        config.setProperty("client1.niws.client." + IClientConfigKey.Keys.InitializeNFLoadBalancer, "true");
//        config.setProperty("client1.niws.client." + IClientConfigKey.Keys.NFLoadBalancerClassName, DynamicServerListLoadBalancer.class.getName());
//        config.setProperty("client1.niws.client." + IClientConfigKey.Keys.NFLoadBalancerRuleClassName, RoundRobinRule.class.getName());
//        config.setProperty("client1.niws.client." + IClientConfigKey.Keys.NIWSServerListClassName, DiscoveryEnabledNIWSServerList.class.getName());
//        config.setProperty("client1.niws.client." + IClientConfigKey.Keys.NIWSServerListFilterClassName, ZoneAffinityServerListFilter.class.getName());
        IClientConfig clientConfig = IClientConfig.Builder.newBuilder(DefaultClientConfigImpl.class, "sample-client").build();
        System.out.println(clientConfig.getPropertyAsString(IClientConfigKey.Keys.MaxAutoRetries, "def"));
        System.out.println(clientConfig.getPropertyAsString(IClientConfigKey.Keys.ListOfServers, "def"));

//        List<Server> servers = Lists.newArrayList(new Server("localhost:8000"), new Server("localhost:8001"));
        ILoadBalancer lb = LoadBalancerBuilder.newBuilder()
            .withClientConfig(clientConfig)
            .buildLoadBalancerFromConfigWithReflection();
//            .buildFixedServerListLoadBalancer(servers);

        LoadBalancingHttpClient<ByteBuf, ByteBuf> client = RibbonTransport.newHttpClient(lb);
        final CountDownLatch latch = new CountDownLatch(REQ_COUNT);

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
                args.getContent().subscribe(d -> logger.debug(d.toString(StandardCharsets.UTF_8)));
            }
        };

        for (int i = 0; i < REQ_COUNT ; i++) {
            Thread.sleep(500);
            HttpClientRequest<ByteBuf> request = HttpClientRequest.createGet("/" + i);
            client.submit(request).subscribe(observer);
//            logger.debug("Sent {}", i);
        }

        latch.await();
//        logger.debug(lb.getLoadBalancerStats());
    }
}
