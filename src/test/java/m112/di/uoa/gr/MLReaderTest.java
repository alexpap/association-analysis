package m112.di.uoa.gr;

import m112.di.uoa.gr.core.AprioriFrequentItemsetGeneration;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.junit.Test;

import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * @author alexpap
 */
public class MLReaderTest {
    private static final Logger log = Logger.getLogger(MLReaderTest.class);

    @Test public void testReader() throws Exception {
        ZipInputStream zipInputStream = new ZipInputStream(
            AprioriFrequentItemsetGeneration.class.getClassLoader()
                .getResourceAsStream("ml-100k.zip"));
        ZipEntry zipEntry = null;
        while((zipEntry = zipInputStream.getNextEntry()) != null){
            log.info(zipEntry.getName());
//            if(zipEntry.getName().equals( "ml-100k/u.item")) IOUtils.copy(zipInputStream, System.out);
        }


    }
}
