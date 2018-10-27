package restcontroller.implementation;

import amazonaurora.core.parser.CoreParserService;
import aurora.rest.CrawlContract;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.websocket.server.PathParam;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/* Primary Class - Heart of Crawler Logic - Exposed as MicroService
 * Rest Controller. Crawl Logic gets Implemented from here.
 */

@RestController
@Controller
public class CrawlController implements CrawlContract {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

        /*
         * Internal critical DDos Security Check - to avoid flooding the EndServer.
         */
        AtomicInteger requestThreshold = new AtomicInteger();

        /*
         * Service Level Agreement - Accept or Consume Request - 1 Request / 3 minutes interval
         * Setting the Initial Capacity 1 to Save RAM Space - Low Memory FootPrint
         */
        final Map<Long,AtomicInteger> rateLimiter = new HashMap<Long,AtomicInteger>(1);


    //Security Only IP address from that rabbitmq will be able to access it.
    @CrossOrigin(origins = "http://localhost:8080", maxAge = 8000)
    @RequestMapping(value = "/startCrawl",method = {RequestMethod.GET})
    public void initiateCrawl(@PathParam("url") String url) {

        if(rateLimiter.isEmpty()){
            logger.info("Master Rest initiateCrawl URL submitted from rabbitmq is  ---> " + url);

            rateLimiter.put(System.currentTimeMillis(),new AtomicInteger(1));

            if(url.isEmpty() || url == null){
                logger.error("Seed URL required to Start Crawl . It is empty or null. Seed Url to be in http://www.example.com form");
            }else{
                CoreParserService.submitSeed(url);
            }
        }else{
            /*
             * Check the last service request - Time in milliseconds
             */
            Set<Long> keys = rateLimiter.keySet();
            for(Long l : keys){
                //Get the System Time in milliseconds
                long currentTime = System.currentTimeMillis();

                /*
                 * Security Implementation - Stricter control
                 * Validates if the request is lesser than 3 minutes wait time
                 */
                if(currentTime - l < apiRateLimiter){
                    logger.warn(" Cannot Invoke Crawler ! Surpassed the Service Level Agreement - Please try after 1 minute");
                }else{

                    rateLimiter.clear();
                    rateLimiter.put(System.currentTimeMillis(),new AtomicInteger(1));
                    CoreParserService.submitSeed(url);

                }
            }
        }
    }

    /*
     * Gracefully Shutting down the service - As it should not impact the existing crawl.
     * All the values are written to the database and then the JVM quits.
     */

    @CrossOrigin(origins = "http://localhost:8080", maxAge = 8000)
    @RequestMapping(value = "/shutdown",method = {RequestMethod.GET})
    public void gracefulShutdown() {
        /*
         * Setting the AtomicBoolean Value to true - CrawlContract.java
         */
        isShutDown.set(true);
    }

}
