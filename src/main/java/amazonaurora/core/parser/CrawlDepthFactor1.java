package amazonaurora.core.parser;

/*
 * Code - 1 Hop from home page - (Other Languages Supported by the website)- meta data
 * Hash Code Calculation and Keyword Density Calculator.
 */

import Resilience.FailureRecovery.PingTester;
import common.aurora.GetTimeStamp;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import useragents.rotator.UserAgentsRotator;

import java.io.IOException;
import java.util.Set;

public class CrawlDepthFactor1 {

    private static Logger logger = LoggerFactory.getLogger(CrawlDepthFactor1.class);

    private CrawlDepthFactor1(){}

    public static void crawlAtDepthFactor1(Set<String> childlinks,final String parentUrl){

        /***************************************************************************
         *  STREAMS - >  QUERY EXECUTION - CRAWLS - CHILD URL's
         **************************************************************************/
        childlinks.forEach((url) -> {
            submitChildUrls(parentUrl,url);
        });

    }

    private static void submitChildUrls(final String homepageURL,String childurl){
        logger.info("Received the Child URL from the CrawlDepthFactor 1 Class" + childurl);

        //Generate the UserAgent String
        /*
         * Memory Constraint - Using Singleton to avoid Creation of Unwanted Objects or Objects Copies
         */
        final UserAgentsRotator uagent = UserAgentsRotator.singletonRotator();
        short randShort = CoreParserService.generateRandom();
        logger.info("Generated Random Number for Fetching User Agent String" + randShort + "," + CrawlDepthFactor1.class.getName());

        uagent.setPreviousState(randShort);
        final String UserAgent = uagent.userAgentRotator(randShort);
        logger.info("Current User Agent String Pointing is " + UserAgent);


        /* Frequent Ping Test results in IP Banning hence avoided it.

       /* if(PingTester.performWebsiteTest(childurl)){
            logger.error("Ping Test Failed !! Make sure the website is up and running" + "," + CrawlDepthFactor1.class.getName() + "," +
                    GetTimeStamp.getCurrentTimeStamp().toString());
        }*/

        logger.info("Proceeding with Extraction as Ping Test Successfull");

        try {
            Document document = JsoupDomService.JsoupExtractor(childurl,UserAgent);
            if(document != null){
                ChildPageExtractionService.partialExtraction(document,childurl,homepageURL);
            }else{
                logger.error("HTML Document is NULL Hence Crawling Failed.. Make sure the Website is up and running"
                        + "," + CrawlDepthFactor1.class.getName());
            }
        } catch (IOException e) {
            logger.error(" IO Exception " + e.getMessage() + "," + CrawlDepthFactor1.class.getName());
            logger.error(e.getLocalizedMessage() + "," + CrawlDepthFactor1.class.getName());
        }

    }

}
