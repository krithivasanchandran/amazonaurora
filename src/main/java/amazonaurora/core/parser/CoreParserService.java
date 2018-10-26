package amazonaurora.core.parser;

import Duplicate.metadata.OnExitStrategy;
import Resilience.FailureRecovery.HotRestartManager;
import Resilience.FailureRecovery.PingTester;
import common.aurora.GetTimeStamp;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import useragents.rotator.UserAgentsRotator;

import java.io.IOException;
import java.util.Random;
import java.util.Set;

public class CoreParserService {

    private static Logger logger = LoggerFactory.getLogger(CoreParserService.class);

    /*
     * Avoiding NEW Operator where it creates unwanted Objects.
     * Hence Memory Constraint Option of using Static Classes Everywhere
     * Only One copy per CPU(processor) per Thread exists.
     */
    private CoreParserService(){}

    public static void submitSeed(String seedurl){

        logger.info("Received the Seed URL from Rest Controller" + seedurl);

        /*****************************************************************************
         * Failure Proof - Load File if On JVM Crash and Restart to resume Crawling.
         * Checks the bytes in file if present crawls the child URL's.
         *****************************************************************************/


        try{
            Set<String> resumeRestartedSeedURLs = HotRestartManager.hotRestartLoadFile();

            if(resumeRestartedSeedURLs != null || !resumeRestartedSeedURLs.isEmpty()){
                logger.info("Resume crawling from last left point -> Applies only to Child Outgoing Url's");
                CrawlDepthFactor1.crawlAtDepthFactor1(resumeRestartedSeedURLs);
            }else{
                logger.info("Failed to resume crawling as the seed URLs are not present");
            }
        }catch(Exception ex){
            logger.info(ex.getMessage());
            logger.info(ex.getLocalizedMessage());
            logger.info(CoreParserService.class.getName());
        }


        //Generate the UserAgent String
        /*
         * Memory Constraint - Using Singleton to avoid Creation of Unwanted Objects or Objects Copies
         */
        UserAgentsRotator uagent = UserAgentsRotator.singletonRotator();
        short randShort = generateRandom();
        logger.info("Generated Random Number for Fetching User Agent String" + randShort);

        uagent.setPreviousState(randShort);
        final String UserAgent = uagent.userAgentRotator(randShort);
        logger.info("Current User Agent String Pointing is " + UserAgent);

        if(PingTester.performWebsiteTest(seedurl)){
            logger.error("Ping Test Failed !! Make sure the website is up and running" + "," + CoreParserService.class.getName() + "," +
                    GetTimeStamp.getCurrentTimeStamp().toString());
        }

        logger.info("Proceeding with Extraction as Ping Test Successfull");

        try {

            HomePageHTMLService.homePageCrawl(seedurl,UserAgent);

            logger.info("Completed the CRAWLING LIFECYCLE !! NOW DELETING AND MOVING ON");
            OnExitStrategy.deleteFiles();

        } catch (IOException e) {
            logger.error(" IO Exception " + e.getMessage() + "," + CoreParserService.class.getName());
            logger.error(e.getLocalizedMessage() + "," + CoreParserService.class.getName());
        }
    }

    //Generate Random
    public static short generateRandom(){
        return (short) new Random().nextInt(19);
    }

}
