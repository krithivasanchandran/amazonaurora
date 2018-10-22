package Resilience.FailureRecovery;

/*
 * JVM Autosaves - Crawl Data - Serializes to File its STATE and
 * Restores the STATE of the jvm after restart.
 */

import amazonaurora.core.parser.CoreParserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Set;

public final class HotRestartManager implements Serializable {

     private static Logger logger = LoggerFactory.getLogger(HotRestartManager.class);

     private HotRestartManager(){}

    /*
     * The JVM finds if these files exists on restart it reads from them
     * and continues it usual operation of crawling if these files are not
     * present in their respective directory.
     */

    private static final String user = "krithivasan";


    // ---> Security Check - Objects are stored to make it non readable by hacker.
    public static void persistchildLinks(Set<String> childLinks){
        try {
            FileOutputStream fout = new FileOutputStream("/home/"+user+"/childLinks.ser");
            ObjectOutputStream oos = new ObjectOutputStream(fout);
            oos.writeObject(childLinks);

        } catch (FileNotFoundException e) {
            logger.info(e.getMessage() + HotRestartManager.class.getName());
            logger.info(e.getLocalizedMessage() + HotRestartManager.class.getName());

        } catch (IOException e) {
            logger.info(e.getMessage() + HotRestartManager.class.getName());
            logger.info(e.getLocalizedMessage() + HotRestartManager.class.getName());
        }

    }

    /*
     * On Start of the initiateCrawl() it checks if the file exists!! If yes it reads from here.
     */

    public static Set<String> hotRestartLoadFile(){
        try{
            FileInputStream fin = new FileInputStream("/home/"+user+"/childLinks.ser");

            int hasContents = 0;
            if((hasContents = fin.available()) > 0){

                ObjectInputStream in = new ObjectInputStream(fin);
                Set<String> childLinks = (Set<String>)in.readObject();
                fin.close();
                in.close();
                return childLinks != null ? childLinks : null;
            }
        } catch (FileNotFoundException e) {
            logger.info(e.getMessage() + HotRestartManager.class.getName());
        } catch (IOException ex) {
            logger.info(ex.getMessage() + HotRestartManager.class.getName());
        } catch (ClassNotFoundException em) {
            logger.info(em.getMessage() + HotRestartManager.class.getName());
        }
        logger.info(" No File Exists in hotRestartLoadFile to retreive the child Links stored on crash" + HotRestartManager.class.getName());
        return null;
    }
}
