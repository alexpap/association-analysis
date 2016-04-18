package m112.di.uoa.gr;

import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.zip.ZipFile;

/**
 * Help class to load small movieLens data,
 * Only for Testing.
 * @author alexpap
 */
public class MovieLensDataSet {

    private static final Logger log = Logger.getLogger(MovieLensDataSet.class);
    private static final MovieLensDataSet instance = new MovieLensDataSet();

    private MovieLensDataSet(){}

    public static MovieLensDataSet getInstance() { return instance; }

    public List<String> loadFile(String zipPath, String fileName) {
        try{
            List<String> items = new ArrayList<String>();
            ZipFile zipFile = new ZipFile(zipPath);
            String parent = zipPath.substring(zipPath.lastIndexOf("/") + 1).replace(".zip","/");
            InputStream inputStream = zipFile.getInputStream(zipFile.getEntry( parent + fileName));
            if (inputStream == null){
                throw new RuntimeException("Unable to locate items.");
            }

            try {

                String line = "";
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                while ((line = reader.readLine()) != null) {
                    items.add(line);
                }
                return items;
            }finally{

                try {inputStream.close();}catch (Throwable ignore){}
            }
        }catch (Exception ex){

            log.error(ex);
            throw new RuntimeException("Error occured", ex);
        }
    }
}
