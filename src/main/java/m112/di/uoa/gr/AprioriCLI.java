package m112.di.uoa.gr;

import org.apache.commons.cli.*;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import java.util.ArrayList;
import java.util.List;

/**
 * @author alexpap
 */
public class AprioriCLI {

    private static final Logger log = Logger.getLogger(AprioriCLI.class);

    private static Options createOptions(){

        Options options = new Options();
        options.addOption(
            Option.builder()
                .argName("minsupp")
                .hasArg(true)
                .desc("minimum support")
                .longOpt("min-support")
                .required()
                .build()
        );
        options.addOption(
            Option.builder()
                .argName("minconf")
                .hasArg()
                .longOpt("min-confidence")
                .required()
                .build()
        );
        options.addOption(
            Option.builder()
                .argName("input")
                .hasArg()
                .desc("given movieLens input dataset to use")
                .longOpt("input")
                .required()
                .build()
        );
        options.addOption(
            Option.builder()
                .argName("titles")
                .desc("print items title instead of ids")
                .longOpt("export-titles")
                .optionalArg(true)
                .build()
        );
        options.addOption(
            Option.builder()
                .argName("log")
                .desc("log level")
                .longOpt("log")
                .optionalArg(true)
                .build()
        );
        return options;
    }

    public static void main(String[] args){

        double minsupp = -1, minconf = -1;
        MovieLensDatasetType inputType = null;
        boolean exportTitles = false;
        Level level = Level.DEBUG;
        Options options = createOptions();
        CommandLineParser parser = new DefaultParser();
        try{
            log.debug("Parsing ...");
            CommandLine line = parser.parse(options, args);
            minsupp = Double.parseDouble(line.getOptionValue("min-support"));
            if(minsupp < 0.01 || minsupp > 0.5){
                throw new ParseException("Please provide minimum support between [0.1,0.5].");
            }
            minconf = Double.parseDouble(line.getOptionValue("min-confidence"));
            if(minconf < 0.5 || minconf > 0.8){
                throw new ParseException("Please provide minimum support between [0.5,0.8].");
            }
            inputType = MovieLensDatasetType.valueOf(line.getOptionValue("input"));
            if(inputType == null)
                throw new ParseException("Please provide as input one of {ml_100k, ml_1m, ml_10m, ml_latest_small}.");
            exportTitles = line.hasOption("titles");
            level = Level.toLevel(line.getOptionValue("log"), Level.DEBUG);
            if(inputType == null)
                throw new ParseException("Please provide as input one of {info, debug}.");
        }catch(Exception ex){
            log.error(ex);
            new HelpFormatter().printHelp("AprioriCLI", options);
            System.exit(1);
        }
        Logger.getRootLogger().setLevel(level);
        AprioriFrequentItemsetGeneration frequentItemset = new AprioriFrequentItemsetGeneration(minsupp);
        frequentItemset.preprocess(inputType);

        List<AprioriCandidatesHashTree> trees = new ArrayList<AprioriCandidatesHashTree>();

        while (frequentItemset.hasNext()) {

            AprioriCandidatesHashTree tree = frequentItemset.next();
            if (tree.size() > 0) {

                trees.add(tree);
            }

            //iterate over itemset
            while (tree.hasNext()) {

                AprioriItemset itemset = tree.next();
                log.trace(itemset.toString());
            }
        }

        log.trace("\n");
        log.trace("Generating Apriori Association Rules...");

        List<AprioriRule> rules_temp;
        List<AprioriRule> rules_all=new ArrayList();
        AprioriAssociationRulesGeneration rules_gen = new AprioriAssociationRulesGeneration(trees, minconf, rules_all);
        while (rules_gen.hasNext()) {
            rules_temp=rules_gen.next();
            for (int i=0; i<rules_temp.size(); i++) {
                log.trace(rules_temp.get(i).toString());
            }
        }
        /*
        for (int i=0; i<rules_all.size(); i++) {
            log.debug(rules_all.get(i).toString());
        }
        */
    }
}
