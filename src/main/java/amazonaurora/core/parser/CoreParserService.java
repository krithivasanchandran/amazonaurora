package amazonaurora.core.parser;

import common.aurora.GetTimeStamp;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import useragents.rotator.UserAgentsRotator;

import java.io.IOException;
import java.util.Random;

public final class CoreParserService {

    private static Logger logger = LoggerFactory.getLogger(CoreParserService.class);

    /*
     * Avoiding NEW Operator where it creates unwanted Objects.
     * Hence Memory Constraint Option of using Static Classes Everywhere
     * Only One copy per CPU(processor) per Thread exists.
     */
    private CoreParserService(){}

    public static void submitSeed(String seedurl){
        logger.info("Received the Seed URL from Rest Controller" + seedurl);

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

        if(performWebsiteTest(seedurl)){
            logger.error("Ping Test Failed !! Make sure the website is up and running" + "," + CoreParserService.class.getName() + "," +
                    GetTimeStamp.getCurrentTimeStamp().toString());
        }

        logger.info("Proceeding with Extraction as Ping Test Successfull");

        try {
            HomePageHTMLService.homePageCrawl(seedurl,UserAgent);
        } catch (IOException e) {
            logger.error(" IO Exception " + e.getMessage() + "," + CoreParserService.class.getName());
            logger.error(e.getLocalizedMessage() + "," + CoreParserService.class.getName());
        }

    }

    //Generate Random
    public static short generateRandom(){
        return (short) new Random().nextInt(19);
    }

    public static boolean performWebsiteTest(String root) {
        try {
            if (!HttpCore.pingTest(root)) {
                logger.error(" FAILED PING TEST !!! CRITICAL WEBSITE ISNT UP AND RUNNING");
                System.out.println(" FAILED PING TEST !!! CRITICAL WEBSITE ISNT UP AND RUNNING ");
                return true;
            }
        } catch (IOException io) {
            System.out.println(io.getMessage() + " Adding failed URL's to the queue");
        }
        return false;
    }

}
