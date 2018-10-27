package common.aurora;

import com.languagedetection.TextNormalizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

/*
        ********** KEYWORD DENSITY CHECKER **********
 * Generates the Keywords Used in the HTML body - Generates Key-Value Pair
 * Keywords Count as Value and Keyword as Key in the map - Serializable
 * and stores in Ubuntu Operating Filesystem and gets retrieved only when
 * required for writing back to the Database to save memory.
 * Object.read() and Object.write() method of Serializable Class is used.
 */
public class KeywordAnalyser{

    private static Logger logger = LoggerFactory.getLogger(KeywordAnalyser.class);

    private static final String username = "krithivasan";

    private KeywordAnalyser(){}

    /*
     * Byte takes 8 bits compared to Char taking 16 bits . Hence using byte[] for low memory constrains.
     * Converted Later to String as it takes unwanted CPU cycles.
     */
    private static final Map<String,Integer> keywordMatrix = new HashMap<String,Integer>(100);

    /*
     * Using Parallel Write Only Arrays - Takes less memory
     * Resizable Arrays - AvailableOn Demand to help save memory
     */

    public static String keywordCount(String htmlText) throws IOException {

        StringBuilder keywordBuilder = new StringBuilder();
        if(htmlText.length() < 5000){

            logger.info("Keyword Analyser Method Call Function -> keywordCount entered ," + KeywordAnalyser.class.getName());



            for(String tokenizer: htmlText.split(" ")){

                if(keywordMatrix.containsKey(tokenizer)){
                  int frequency = keywordMatrix.get(tokenizer);
                  frequency = frequency + 1;
                  keywordMatrix.put(tokenizer,frequency);

                }else{
                  keywordMatrix.put(tokenizer,1);
                }
            }

            Set<String> printall = keywordMatrix.keySet();
            for(String s : printall){
                keywordBuilder.append(keywordMatrix.get(s)).append(" --> ").append(s).append(",");
            }

            //Clean Up
            keywordMatrix.clear();

           /*
             * Security - Persist and Save the object in Ubuntu File System
             * In Case JVM Breaks down or Crashes - Object could be de-serialized from the file
             * on startup.
             */
  /*
            File file = null;
            FileOutputStream serializeFile = null;
            ObjectOutputStream outputStream = null;

            try {
                    file = new File("/home/"+username+"/file.txt");
                    file.createNewFile();
                    logger.info("FileName created in path" + "," + "/home/"+username+"/file.txt" + "::");

                    serializeFile = new FileOutputStream("/home/"+username+"/file.txt");

                    outputStream = new ObjectOutputStream(serializeFile);
                    outputStream.writeObject(keywordMatrix);
                    logger.info("Keyword Analyser Matrix Object Written Successfully into file");

            } catch (IOException e) {

                logger.error(e.getMessage() + ", " + KeywordAnalyser.class.getName());

            }finally{

                if(serializeFile!=null){ serializeFile.close(); }
                if(outputStream != null){ outputStream.close(); }

                logger.info("Successfull Closing of FileHandlers in the finally block ," + KeywordAnalyser.class.getName());
            } */

        }
        return (keywordBuilder.length() > 0) ? keywordBuilder.toString() : null;
    }
}
