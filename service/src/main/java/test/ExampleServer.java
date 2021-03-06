package test;

import com.google.common.collect.ImmutableList;
import com.google.inject.Module;
import com.twitter.common.quantity.Amount;
import com.twitter.common.quantity.Time;
import com.twitter.common.zookeeper.Group;
import com.twitter.common.zookeeper.ServerSetImpl;
import com.twitter.common.zookeeper.ZooKeeperClient;
import com.twitter.finagle.Thrift;
import com.twitter.finatra.http.AbstractHttpServer;
import com.twitter.finatra.http.filters.CommonFilters;
import com.twitter.finatra.http.routing.HttpRouter;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.util.Collection;
import java.util.HashMap;

/**
 * Created by msrivastava on 5/31/17.
 */
public class ExampleServer extends AbstractHttpServer {

    @Override
    public boolean disableAdminHttpServer() { return true;}

    @Override
    public String defaultFinatraHttpPort() {
        try {
            ServerSocket socket = new ServerSocket(0);
            socket.close();
            return ":"+socket.getLocalPort();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public Integer getRandomPort() {
        try {
            ServerSocket socket = new ServerSocket(0);
            socket.close();
            return socket.getLocalPort();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public Collection<Module> javaModules() {
        return ImmutableList.of(new DefaultModule());
    }

    @Override
    public void configureHttp(HttpRouter httpRouter) {
        httpRouter
                .filter(CommonFilters.class)
                .add(ExampleAnnotatedControllerJava.class)
                .add(ExampleControllerJava.class);

        FakeService service = this.injector().instance(FakeService.class);
        String path = "/test/example";
        InetSocketAddress serverHost = new java.net.InetSocketAddress(getRandomPort());

        Thrift.server().serveIface(serverHost,new ServiceIfaceImpl(service));
        //Thrift.server().serveIface("localhost:1234",new ServiceIfaceImpl(service));

        ZooKeeperClient zooKeeperClient
                = new ZooKeeperClient(Amount.of(10, Time.SECONDS),new InetSocketAddress("localhost", 2181));
        try {
            new ServerSetImpl(zooKeeperClient,path).join(serverHost,new HashMap<>(),0);
        } catch (Group.JoinException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
