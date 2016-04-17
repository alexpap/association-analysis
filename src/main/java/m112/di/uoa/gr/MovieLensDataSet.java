package m112.di.uoa.gr;

import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.zip.ZipFile;

/**
 * @author alexpap
 */
public class MovieLensDataSet {

    private static final Logger log = Logger.getLogger(MovieLensDataSet.class);
    private ArrayList<Set<Integer>> baskets = null;
    private ArrayList<String> movies = null;

    public MovieLensDataSet(){}

    public void load(String filePathName){

        if ( movies == null || baskets == null ) {
            baskets = new ArrayList<Set<Integer>>();
            movies = new ArrayList<String>();
        }
        try {
            ZipFile zipFile = new ZipFile(filePathName);
            String parent =
                filePathName.substring(filePathName.lastIndexOf("/") + 1).replace(".zip","/");
            InputStream inputStream = zipFile.getInputStream(zipFile.getEntry( parent + "u.item"));
            try{
                String line = "";
                BufferedReader reader =
                    new BufferedReader(new InputStreamReader(inputStream));
                while( (line=reader.readLine()) != null){
                    String[] split = line.trim().split("|");
                    movies.add(split[2]);
                }
            } finally {
                try {inputStream.close();}catch (Throwable ignore){}
            }
            inputStream = zipFile.getInputStream(zipFile.getEntry(parent + "u.data"));
            try{
                String line = "";
                BufferedReader reader =
                    new BufferedReader(new InputStreamReader(inputStream));
                Set<Integer> umovies;
                while( (line=reader.readLine()) != null){
                    log.info(line);
                    log.info(baskets);
                    String[] split = line.trim().split("\t");
                    Integer uid = Integer.valueOf(split[1]);
                    Integer mid = Integer.valueOf(split[2]);
                    if (uid < baskets.size()) {
                         umovies = baskets.get(uid);
                    } else {
                        umovies = new HashSet<Integer>();
                        baskets.add(umovies);
                    }
                    umovies.add(mid);
                }
            } finally {
                try {inputStream.close();} catch (Throwable ignore){}
            }
        } catch (IOException e) {
            log.error(e);
        }
    }

    public ArrayList<Set<Integer>> getBaskets() {
        return baskets;
    }

    public ArrayList<String> getMovies() {
        return movies;
    }
}
