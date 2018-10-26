package amazonaurora.core.parser;

import aurora.rest.CrawlContract;
import com.languagedetection.LanguageDetection;
import common.aurora.LinkExtractor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.safety.Cleaner;
import org.jsoup.safety.Whitelist;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.Random;
import java.util.Set;

public class JsoupDomService {

    private static Logger logger = LoggerFactory.getLogger(JsoupDomService.class);

    /*
     * Avoiding NEW Operator where it creates unwanted Objects.
     * Hence Memory Constraint Option of using Static Classes Everywhere
     * Only One copy per CPU(processor) per Thread exists.
     */
    private JsoupDomService(){}

    public static Document JsoupExtractor(final String seedUrl, final String useragent){

        Document document = null;
        try{

            document = Jsoup.connect(seedUrl)
                                .userAgent(useragent)
                                .timeout(10000)
                                .cookie("CookieName","Crawling for a Challenge")
                                .followRedirects(true)
                                .maxBodySize(0)
                                .ignoreContentType(true)
                                .header("Connection", "keep-alive")
                                .ignoreHttpErrors(true)
                                .get();

            /*
             * Politeness Delay between Subsequent Requests.- 3 Seconds - We dont
             * Choke the Server - implications will lead to IP Banning and
             * Subnet IP Address BlackListing.
             */
            try{
                Thread.sleep(CrawlContract.politenessDelayBetweenSubesquentRequests);
            }catch(InterruptedException ex){
                logger.error("Interrupted Exception happened when using Thread.sleep(3000)" + JsoupDomService.class.getName());
            }


            /*
             * Logging Successfull and Failed URLs to the Ubuntu File Os.
             */

            logger.info(" JSoup Extractor Method :: " + " User Agent :: " + useragent + " Timeout Set ::: 2000, referrer :: https://google.com "
            + "follow Redirects : 301 and 302  :: Temporary and Permanent Redirect :: True , Max Body Size : 0, IgnoreContent Type : True" );

            return document;

        }catch(Exception ex){
            logger.error(" Http Exception occurred hence stalling the operation -- Inside the JSOUP Class");
            System.out.println(" Http Exception occurred hence stalling the operation ");
        }
        logger.error("Document returning null no value in it " + JsoupDomService.class.getName());
        return null;
    }

}