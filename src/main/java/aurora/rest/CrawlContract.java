package aurora.rest;

public interface CrawlContract {
    /*
     *Prevention of Ddos Attack
     */
    short totalCrawlSeeds = 20;

    /*
     * Initiates Crawl based on the given Seed URL - Subscribed from the RabbitMQ Head Queue
     */
    public String initiateCrawl(String seed);

    /* Critical - To Avoid OverThrottling the Servers
     * CrawlDepth - Factor of 2 hops from home page
     * Security Feature to Avoid Mass Bots Ddos Attack
     */

    short depthfactor = 1;
}
