package common.aurora;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

/*
        ********** KEYWORD DENSITY CHECKER **********
 * Generates the Keywords Used in the HTML body - Generates Key-Value Pair
 * Keywords Count as Value and Keyword as Key in the map - Serializable
 * and stores in Ubuntu Operating Filesystem and gets retrieved only when
 * required for writing back to the Database to save memory.
 * Object.read() and Object.write() method of Serializable Class is used.
 */
public class KeywordAnalyser implements Serializable {

    private static Logger logger = LoggerFactory.getLogger(KeywordAnalyser.class);

    private static final String username = "krithivasan";

    private KeywordAnalyser(){}

    /*
     * Byte takes 8 bits compared to Char taking 16 bits . Hence using byte[] for low memory constrains.
     */
    private static final Map<byte[],Integer> keywordMatrix = new HashMap<byte[],Integer>(100);

    /*
     * Using Parallel Write Only Arrays - Takes less memory
     * Resizable Arrays - AvailableOn Demand to help save memory
     */

    public static Map<byte[],Integer> keywordCount(String htmlText) throws IOException {


        if(htmlText.length() < 5000){

            logger.info("Keyword Analyser Method Call Function -> keywordCount entered ," + KeywordAnalyser.class.getName());

            //Replace 2 white space to Single White Space
            String str = htmlText.replaceAll("\\s{2,}", " ").trim();
            logger.info("After removing more than 2 whitespaces -> " + str);

            StringTokenizer tokenizer = new StringTokenizer(str);

            while(tokenizer.hasMoreTokens()){
                byte[] word = tokenizer.nextToken().trim().getBytes();

                if(keywordMatrix.containsKey(word)){
                  int frequency = keywordMatrix.get(word);
                  frequency = frequency + 1;
                  keywordMatrix.put(word,frequency);

                }else{
                  keywordMatrix.put(word,1);
                }
            }

            /*
             * Security - Persist and Save the object in Ubuntu File System
             * In Case JVM Breaks down or Crashes - Object could be de-serialized from the file
             * on startup.
             */

            File file = null;
            FileOutputStream serializeFile = null;
            ObjectOutputStream outputStream = null;

            try {
                    file = new File("/home/"+username+"/file.txt");
                    file.createNewFile();
                    logger.info("FileName created in path" + "," + "/home/"+username+"/file.txt" + "::")

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
            }

        }
        return !(keywordMatrix.isEmpty()) ? keywordMatrix : null;
    }
}
