package m112.di.uoa.gr;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

/**
 * @author alexpap
 */
@RunWith(Parameterized.class) public class AprioriCLITest {

    String argsLine;

    public AprioriCLITest(String args) {
        argsLine = args;
    }

    @org.junit.Before public void setUp() throws Exception {

    }

    @org.junit.After public void tearDown() throws Exception {

    }

    @org.junit.Test public void testMain() throws Exception {

        String[] split = argsLine.split(" ");
        AprioriCLI.main(split);
    }

    @Parameterized.Parameters public static Collection inputParameters(){
        return Arrays.asList(new Object[]{
            "--min-support 0.01 --min-confidence 0.5 --input ml_100k",
            "--min-support 0.05 --min-confidence 0.5 --input ml_100k",
            "--min-support 0.10 --min-confidence 0.5 --input ml_100k",
            "--min-support 0.20 --min-confidence 0.5 --input ml_100k",
            "--min-support 0.30 --min-confidence 0.5 --input ml_100k",
            "--min-support 0.40 --min-confidence 0.5 --input ml_100k",
            "--min-support 0.50 --min-confidence 0.5 --input ml_100k",
            "--min-support 0.01 --min-confidence 0.5 --input ml_1m",
            "--min-support 0.05 --min-confidence 0.5 --input ml_1m",
            "--min-support 0.10 --min-confidence 0.5 --input ml_1m",
            "--min-support 0.20 --min-confidence 0.5 --input ml_1m",
            "--min-support 0.30 --min-confidence 0.5 --input ml_1m",
            "--min-support 0.40 --min-confidence 0.5 --input ml_1m",
            "--min-support 0.50 --min-confidence 0.5 --input ml_1m",
            "--min-support 0.01 --min-confidence 0.5 --input ml_10m",
            "--min-support 0.05 --min-confidence 0.5 --input ml_10m",
            "--min-support 0.10 --min-confidence 0.5 --input ml_10m",
            "--min-support 0.20 --min-confidence 0.5 --input ml_10m",
            "--min-support 0.30 --min-confidence 0.5 --input ml_10m",
            "--min-support 0.40 --min-confidence 0.5 --input ml_10m",
            "--min-support 0.50 --min-confidence 0.5 --input ml_10m",
        });
    }
}
