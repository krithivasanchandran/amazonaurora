package aurora.rest;

import org.springframework.http.ResponseEntity;

import java.util.concurrent.atomic.AtomicBoolean;

public interface CrawlContract {
    /*
     *Prevention of Ddos Attack
     * Reduced the TotalChildCrawlURLs from 15 to 8
     * to prevent non blocking of resources.
     */
    short totalCrawlSeeds = 8;

    /*
     * Initiates Crawl based on the given Seed URL - Invoked from Master Node.
     */
    public ResponseEntity initiateCrawl(String seed);

    /* Critical - To Avoid OverThrottling the Servers
     * CrawlDepth - Factor of 2 hops from home page
     * Security Feature to Avoid Mass Bots Ddos Attack
     */

    short depthfactor = 1;

    /*
     * API Rate Limiting - 1 Minute SLA / 1 Request
     * 60 seconds - 1 request / 3 seconds - 20 Requests
     * Security Enforce Rule - API Rate Limiter = 60000
     */
    int apiRateLimiter = 30000;

    /*
     *
     * Politeness Delay between Subsequent Requests.- 3 Seconds - We dont
     * Choke the Server - implications will lead to IP Banning and
     * Subnet IP Address BlackListing.
     */
     long politenessDelayBetweenSubesquentRequests = 1500;

     /*
      * Retry - 2 Times on Failure
      */
     short retryRequest = 2;

     /*
      * GraceFul shutdown AtomicBoolean - Thread Safe - Highly Concurrent
      * No Shared Memory - Writes directly to the block storage.
      */
     AtomicBoolean isShutDown = new AtomicBoolean(false);
}
