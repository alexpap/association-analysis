package m112.di.uoa.gr;

import com.google.gson.Gson;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.zip.ZipFile;

/**
 * Loads the ratings.csv, movies.csv from the latest movieLens dataset.
 * @link http://grouplens.org/datasets/movielens/
 * Ignores ratings and creates for each user a basket of movies.
 *
 * e.g. ratings.csv
 * userId,movieId,rating,timestamp
 * -------------------------------
 * <userId, *movieId>
 * +++++++++++++++++++++++++++++++
 * e.g. movies.csv
 * movieId,title,genres
 * ------------------------------
 * <movieId, title>
 *
 * @author alexpap
 */
public class MovieLensLatestDataSet {

    private static final Logger log = Logger.getLogger(MovieLensLatestDataSet.class);
    private static int nitem = 0, nbasket = 0;

    /**
     * Pre-process latest movieLens data gine as @zipPath and 
     * outputs items.json and  basket.json under @outputPath.
     *
     * @param zipPath       - movieLens tarball path
     * @param outputPath    - expected output dir
     * @return The generated 1-itemset without support
     */
    public static HashMap<Integer, int[]> preProcess(String zipPath, String outputPath) {

        try{
            // loading items (movies.csv)
            ZipFile zipFile = new ZipFile(zipPath);
            String parent = zipPath.substring(zipPath.lastIndexOf("/") + 1).replace(".zip","/");
            InputStream inputStream =
                zipFile.getInputStream(zipFile.getEntry(parent.concat("movies.csv")));
            if (inputStream == null){

                throw new RuntimeException("Unable to locate movielens movies.");
            }
            // prepare output
            Files.createDirectories(Paths.get(outputPath));
            FileOutputStream outputStream =
                new FileOutputStream(outputPath.concat("/item.json"));
            if (outputPath == null){

                throw new RuntimeException("Unable to create movies.json.");
            }
            try {

                Gson gson = new Gson();
                String line = "";
                StringTokenizer tokenizer;
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                reader.readLine(); // skip header

                while ((line = reader.readLine()) != null) {

                    tokenizer = new StringTokenizer(line,",", false);
                    String[] item = {tokenizer.nextToken(), tokenizer.nextToken()};
                    outputStream.write(gson.toJson(item).getBytes());
                    outputStream.write("\n".getBytes());
                    ++nitem;
                }

            }finally{

                try { if (inputStream != null) {inputStream.close();}
                } catch (Throwable ignore){}
                try { if (outputStream != null) {outputStream.close();}
                } catch (Throwable ignore){}
            }
        }catch (Exception ex){

            throw new RuntimeException("Error occured", ex);
        }

        try{
            // loading ratings.csv in order to create baskets and first itemset
            HashMap<Integer, int[]> firstItemset = new HashMap<Integer, int[]>(nitem);
            ZipFile zipFile = new ZipFile(zipPath);
            String parent = zipPath.substring(zipPath.lastIndexOf("/") + 1).replace(".zip","/");
            InputStream inputStream = zipFile.getInputStream(zipFile.getEntry( parent.concat("ratings.csv")));
            if (inputStream == null){

                throw new RuntimeException("Unable to locate movielens u.data.");
            }
            FileOutputStream outputStream =
                new FileOutputStream(outputPath.concat("/basket.json"));
            if (outputPath == null){

                throw new RuntimeException("Unable to create movies.json.");
            }
            try {
                Map<Integer, Set<Integer>> baskets = new HashMap<Integer, Set<Integer>>();
                String line = "";
                StringTokenizer tokenizer;
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                reader.readLine(); // skip header

                while ((line = reader.readLine()) != null) {

                    tokenizer = new StringTokenizer(line, ",", false);
                    Integer ukey = Integer.valueOf(tokenizer.nextToken());
                    Integer ikey = Integer.valueOf(tokenizer.nextToken());

                    // add new item into the user basket
                    Set<Integer> ubasket = baskets.get(ukey);
                    if (ubasket == null){

                        baskets.put(ukey, ubasket = new HashSet<Integer>());
                    }
                    ubasket.add(ikey);

                    // update counters
                    int[] counter = firstItemset.get(ikey);
                    if (counter == null){

                        firstItemset.put(ikey, new int[]{1});
                    } else {
                        counter[0]++;
                    }
                    nbasket++;
                }
                // output each basket
                Gson gson = new Gson();
                for (Map.Entry<Integer, Set<Integer>> entry : baskets.entrySet()) {
                    outputStream.write(gson.toJson(entry).getBytes());
                    outputStream.write("\n".getBytes());
                }

                return firstItemset;           
            }finally{

                try { if (inputStream != null) {inputStream.close();}
                } catch (Throwable ignore){}
                try { if (outputStream != null) {outputStream.close();}
                } catch (Throwable ignore){}
            }
        }catch (Exception ex){

            throw new RuntimeException("Error occured", ex);
        }
    }

    public static int getNitem() {
        return nitem;
    }

    public static int getNbasket() {
        return nbasket;
    }

    public static void getMoviesTitles(String zipFile){

    }

    public static void main(String[] args){
        String path =
            MovieLensLatestDataSet.class.getClassLoader().getResource("ml-latest-small.zip").getPath();
//            MovieLensDataSet.class.getClassLoader().getResource("ml-latest.zip").getPath();
        HashMap<Integer, int[]> firstItemset = MovieLensLatestDataSet.preProcess(path, "/tmp/");
        for (Map.Entry<Integer, int[]> entry : firstItemset.entrySet()) {
            log.info(entry.getKey() + " <- " + entry.getValue()[0]);
        }
    }
}
