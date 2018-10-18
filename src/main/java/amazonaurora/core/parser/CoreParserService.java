package amazonaurora.core.parser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import useragents.rotator.UserAgentsRotator;

import java.io.IOException;
import java.util.Random;

public final class CoreParserService {

    private static Logger logger = LoggerFactory.getLogger(CoreParserService.class);

    public static String submitSeed(String seedurl){
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
            logger.error("Ping Test Failed !! Make sure the website is up and running");
            return "Ping Test Failed !! Make sure the website is up and running";
        }

        logger.info("Proceeding with Extraction as Ping Test Successfull");

        JsoupDomService.JsoupExtractor(seedurl,UserAgent);
    }

    //Generate Random
    public static short generateRandom(){
        return (short) new Random().nextInt(19);
    }

    private static boolean performWebsiteTest(String root) {
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
