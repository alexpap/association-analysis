package m112.di.uoa.gr;

import org.apache.commons.cli.*;
import org.apache.commons.lang3.SystemUtils;
import org.apache.log4j.Logger;

import java.io.PrintWriter;
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
        return options;
    }

    public static void main(String[] args){

        double minsupp = -1, minconf = -1;
        MovieLensDatasetType inputType = null;
        boolean exportTitles = false;

        Options options = createOptions();
        CommandLineParser parser = new DefaultParser();
        try{
            log.debug("Parsing ...");
            CommandLine line = parser.parse(options, args);
            minsupp = Double.parseDouble(line.getOptionValue("min-support"));
            if(minsupp < 0.1 && minsupp > 0.5){
                throw new ParseException("Please provide minimum support between [0.1,0.5].");
            }
            minconf = Double.parseDouble(line.getOptionValue("min-confidence"));
            if(minconf < 0.5 && minconf > 0.8){
                throw new ParseException("Please provide minimum support between [0.5,0.8].");
            }
            inputType = MovieLensDatasetType.valueOf(line.getOptionValue("input"));
            log.debug(inputType);

            exportTitles = line.hasOption("titles");
            log.debug(exportTitles);
        }catch(Exception ex){
            log.error(ex);
            new HelpFormatter().printHelp("AprioriCLI", options);
            System.exit(1);
        }

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
                log.info(itemset.toString());
            }
        }

        List rules;
        AprioriAssociationRulesGeneration rules_gen = new AprioriAssociationRulesGeneration(trees, minconf);
        while (rules_gen.hasNext()) {
            rules=rules_gen.next();
            for (int i=0; i<rules.size(); i++) {
                log.debug(rules.get(i));
            }
        }
    }
}
