package aurora.rest;

public interface CrawlContract {
    /*
     *Prevention of Ddos Attack
     */
    int totalCrawlSeeds = 20;

    /*
     * Initiates Crawl based on the given Seed URL - Subscribed from the RabbitMQ Head Queue
     */
    public String initiateCrawl(String seed);

}
