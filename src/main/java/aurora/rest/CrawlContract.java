package aurora.rest;

import java.util.concurrent.atomic.AtomicBoolean;

public interface CrawlContract {
    /*
     *Prevention of Ddos Attack
     */
    short totalCrawlSeeds = 15;

    /*
     * Initiates Crawl based on the given Seed URL - Subscribed from the RabbitMQ Head Queue
     */
    public void initiateCrawl(String seed);

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
    int apiRateLimiter = 60000;

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
