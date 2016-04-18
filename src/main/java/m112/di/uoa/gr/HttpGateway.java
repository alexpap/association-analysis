package m112.di.uoa.gr;

import org.apache.http.ExceptionLogger;
import org.apache.http.impl.nio.bootstrap.HttpServer;
import org.apache.http.impl.nio.bootstrap.ServerBootstrap;
import org.apache.http.impl.nio.reactor.IOReactorConfig;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;


/**
 * Embedded HTTP/1.1 async nio server
 * @author alexpap
 */
public class HttpGateway {
    private static final Logger log = Logger.getLogger(HttpGateway.class);
    private HttpServer server = null;
    private HttpGateway(){}
    private static final HttpGateway instance = new HttpGateway();

    public static HttpGateway getInstance(){ return instance; }

    public void start(){

        if ( server != null) {
            log.debug("Server is running, no action taken.");
            return;
        }
        IOReactorConfig config = IOReactorConfig.custom()
            .setSoTimeout(15000)
            .setTcpNoDelay(true)
            .build();

        server = ServerBootstrap.bootstrap()
            .setListenerPort(9095)
            .setServerInfo("AAGW/1.1")
            .setIOReactorConfig(config)
            .setExceptionLogger(ExceptionLogger.STD_ERR)
            .registerHandler("/static/*", new HttpFileHandler(new File("/static")))
            .create();

        try {
            server.start();
        } catch (IOException e) {
            log.error(e);
            server = null;
        }

    }

    public boolean isUp(){
        return server != null;
    }

    public void stop(){

        if ( server == null ) {
            log.debug("Server is already stoppped, no action taken.");
        }
        server.shutdown(5, TimeUnit.SECONDS);
    }
}
