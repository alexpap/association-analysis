package m112.di.uoa.gr;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author alexpap
 */
public class HttpGatewayTest {

    private static final Logger log = Logger.getLogger(HttpGatewayTest.class);
    private HttpGateway gw  = null;

    @Before public void setUp() throws Exception {

        gw = HttpGateway.getInstance();
    }

    @Test public void testServer() throws Exception {

        gw.start();
        assertTrue(gw.isUp());
        gw.stop();
        gw = null;
    }

    @After public void tearDown() throws Exception {

        if (gw != null){
            gw.stop();
        }
    }
}
