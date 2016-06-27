package m112.di.uoa.gr;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

/**
 * @author alexpap
 */
@RunWith(Parameterized.class) public class AprioriCLITest {

    double minsupp, minconf;

    public AprioriCLITest(double supp, double conf) {
        minsupp = supp; minconf = conf;
    }

    @org.junit.Before public void setUp() throws Exception {

    }

    @org.junit.After public void tearDown() throws Exception {

    }

    @org.junit.Test public void testMain() throws Exception {

        String[] args = new String[6]; //  --min-support 0.5 --min-confidence 0.8 --input ml_10m
        args[0] = "--min-support";
        args[1] = String.valueOf(minsupp);
        args[2] = "--min-confidence";
        args[3] = String.valueOf(minconf);
        args[4] = "--input";
        args[5] = "ml_10m";
        AprioriCLI.main(args);
    }

    @Parameterized.Parameters public static Collection inputParameters(){
        return Arrays.asList(new Object[][]{
            { 0.5, 0.5},
            { 0.4, 0.5},
            { 0.3, 0.5},
            { 0.2, 0.5},
            { 0.1, 0.5}
        });
    }
}
