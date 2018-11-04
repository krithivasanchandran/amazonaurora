package restcontroller.implementation;

import amazonaurora.core.parser.CoreParserService;
import aurora.rest.CrawlContract;
import com.sun.tools.internal.ws.processor.model.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.websocket.server.PathParam;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
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
         * Service Level Agreement - Accept or Consume Request - 1 Request / 30 seconds interval
         * Setting the Initial Capacity 1 to Save RAM Space - Low Memory FootPrint
         */
        final Map<Long,AtomicInteger> rateLimiter = new HashMap<Long,AtomicInteger>(1);


    /*
     * Entry point for submitting SEED URLS for crawling.
     * Accepts URL as the path parameters: eg: http://localhost:8080/startCrawl?url=http://flipkart.com
     */

    @RequestMapping(value = "/startCrawl",method = {RequestMethod.GET})
    public ResponseEntity initiateCrawl(@PathParam("url") String url) {

        if(rateLimiter.isEmpty()){
            logger.info("Master Rest initiateCrawl URL submitted by MasterCrawler is  ---> " + url);

            rateLimiter.put(System.currentTimeMillis(),new AtomicInteger(1));

            if(url.isEmpty() || url == null){
                logger.error("Seed URL required to Start Crawl . It is empty or null. Seed Url to be in http://www.example.com form");
            }else{
                CoreParserService.submitSeed(url);
                return ResponseEntity.status(200).build();
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
                 * Response Code HTTP - 412 Precondition Failed.
                 */
                if(currentTime - l < apiRateLimiter){
                    logger.warn(" Cannot Invoke Crawler ! Surpassed the Service Level Agreement - Please try after 30 seconds");
                    logger.warn("Current time is " + currentTime);
                    logger.warn("Time in the SLA is " + l);
                    logger.warn("Difference is " + (currentTime - l));
                    return ResponseEntity.status(412).build();
                }else{

                    rateLimiter.clear();
                    rateLimiter.put(System.currentTimeMillis(),new AtomicInteger(1));
                    CoreParserService.submitSeed(url);
                    return ResponseEntity.status(200).build();
                }
            }
        }
        return ResponseEntity.status(201).build();
    }

    /*
     * Gracefully Shutting down the service - As it should not impact the existing crawl.
     * All the values are written to the database and then the JVM quits.
     */

    @RequestMapping(value = "/shutdown",method = {RequestMethod.GET})
    public ResponseEntity gracefulShutdown() {
        /*
         * Setting the AtomicBoolean Value to true - CrawlContract.java
         * JVM shuts down only after finish crawling the child URLS.
         */
        isShutDown.set(true);
        return ResponseEntity.status(200).build();
    }

}
