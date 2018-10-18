package restcontroller.implementation;

import amazonaurora.core.parser.CoreParserService;
import aurora.rest.CrawlContract;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.web.bind.annotation.*;

import javax.ws.rs.Produces;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/* Primary Class - Heart of Crawler Logic - Exposed as MicroService
 * Rest Controller. Crawl Logic gets Implemented from here.
 */

@RestController
public class CrawlController implements CrawlContract {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

        //Global Variables - Thread Safe - Metrics for this JVM Status
        /*
         * Returns Boolean Value to tell if the JVM is free or not.
         */
        AtomicBoolean isFree = new AtomicBoolean();

        /*
         * Internal critical DDos Check - to avoid flooding the EndServer.
         */
        AtomicInteger requestThreshold = new AtomicInteger();

        /*
         * Service Level Agreement - Accept or Consume Request - 1 Request / 3 minutes interval
         * Setting the Initial Capacity 1 to Save RAM Space - Low Memory FootPrint
         */
        final Map<Long,AtomicInteger> rateLimiter = new HashMap<Long,AtomicInteger>(1);


    //Security Only IP address from that rabbitmq will be able to access it.
    @CrossOrigin(origins = "http://localhost:8080", maxAge = 8000)
    @RequestMapping(value = "/master/startCrawl",method = {RequestMethod.GET})
    @Produces({MediaType.TEXT_PLAIN_VALUE})
    public String initiateCrawl(@RequestParam String url) {

        if(rateLimiter.isEmpty()){
            logger.info("Master Rest initiateCrawl URL submitted from rabbitmq is  ---> " + url);

            rateLimiter.put(System.currentTimeMillis(),new AtomicInteger(1));

            if(url.isEmpty() || url == null){
                return "Seed URL required to Start Crawl";
            }else{
                isFree.set(false);
                CoreParserService.submitSeed(url);
                isFree.set(true);
            }
        }else{
            isFree.set(false);
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
                if(currentTime - l < 180000){
                    logger.warn(" Cannot Invoke Crawler ! Surpassed the Service Level Agreement ");
                    return " Cannot Invoke Crawler ! Surpassed the Service Level Agreement ";
                }else{

                    rateLimiter.clear();
                    rateLimiter.put(System.currentTimeMillis(),new AtomicInteger(1));
                    isFree.set(false);
                    return CoreParserService.submitSeed(url);
                }
            }
        }
        return "It is a dummy one ";
    }

    @CrossOrigin(origins = "IP address", maxAge = 8000)
    @RequestMapping(value = "/master/isfree",method = {RequestMethod.GET})
    @Produces({MediaType.ALL_VALUE})
    public Boolean checkiffree() {
        return isFree.get();
    }
}
