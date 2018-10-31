package amazonaurora.core.parser;

import AuroraMySql.DatabaseManager.DaoManager;
import Duplicate.metadata.OnExitStrategy;
import MemoryListener.MemoryNotifier;
import aurora.rest.CrawlContract;
import aurora.rest.RetryLogic;
import com.languagedetection.TextNormalizer;
import common.aurora.*;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import restcontroller.implementation.LinkDiscovery;

import java.io.IOException;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/*
 * Parsing of home page and child Pages - Ranking Score calculation based on few criterias.
 * Seed URL -> Root Url.
 */

public class HomePageHTMLService {
    private static Logger logger = LoggerFactory.getLogger(HomePageHTMLService.class);
    public static final Map<Integer,String> avoidingObjects = new HashMap<Integer,String>();
    public static short Rankscore = 0;

    private HomePageHTMLService(){}

    public static void homePageCrawl(final String seedUrl, final String useragent) throws IOException {

        MemoryNotifier.printRuntimeMemory();

        Document document = JsoupDomService.JsoupExtractor(seedUrl,useragent);

        if(document == null){
            document = RetryLogic.retry(document,seedUrl,useragent);
        }

        if(document != null || !(document.text().isEmpty())){

            logger.info("Document object is not null -- Successfull Text Extraction ");

            Rankscore = ChildPageExtractionService.partialExtraction(document,seedUrl,Rankscore);

            Rankscore += isRobotsTxtExists(seedUrl) ? (short)10 : (short)5;

            Rankscore += isSiteMapXMLExists(seedUrl) ? (short)10 : (short)5;


            /************************************************************************
             * Check Outgoing Links - Count - High Quality Links - Within Webpage
             ************************************************************************/

            Set<String> outgoingLinks = LinkExtractor.extractOutgoingLinks(document,seedUrl);

            /*
             * Serialize and save object - Outgoing Links.
             */

            short totalOutLinks = (short)outgoingLinks.size();

            logger.info("Total Outbound Links " + totalOutLinks + "," + HomePageHTMLService.class.getName());

            Rankscore += (totalOutLinks > 0) ? (short)10 : (short)5;

            /***********************************************************************
             * Home Page - Extract Phone , Address and Email Id
             **********************************************************************/

            String bodytext = TextNormalizer.Normalizer.getWords(document.body().text().trim());
            bodytext = (bodytext.length() < 5000) ? bodytext.trim() : bodytext.substring(0,5000);

            String phoneNumberList = PhoneNumberExtractor.extractPhoneNumber(bodytext);
            final StringBuilder contactNumbersList = new StringBuilder();

            if(phoneNumberList != null && !(phoneNumberList.isEmpty())){
                    logger.info("Contact Telephone Numbers in homepage - Writing it to StringBuilder " + phoneNumberList);
                    contactNumbersList.append(phoneNumberList).append(",");
            }

            final StringBuilder emailList = new StringBuilder();
            emailList.append(EmailExtractor.EmailFinder(bodytext,seedUrl));


            MemoryNotifier.printRuntimeMemory();

            /*************************************
             * Link Discovery of Contact Us Page.
             *************************************/
            LinkDiscovery.discoverContactUsPage(seedUrl,outgoingLinks,useragent,emailList,contactNumbersList);


             /*************************************************
              * Parse URLS with depth 1 -> 1 hop from home page
              * I don't want to choke their servers.
              *************************************************/
             CrawlDepthFactor1.crawlAtDepthFactor1(outgoingLinks,seedUrl);


            /*
             * Named Entity Recognition - Location Identification -> Address Extraction
             */

            String cityStateZip = AddressParser.extractAddressParts(bodytext);
            StringBuilder addressBuilder = new StringBuilder();
            String[] parts = cityStateZip.split("\\s+");
            for(String str : parts){
                if(AddressParser.namedEntityLocationRecognition(str.toLowerCase().trim())){
                    addressBuilder.append(USAStateAbbreviations.getPOFullName(str.toLowerCase().trim()));
                }
            }
            logger.info("Address Extraction Results : " + addressBuilder.toString() + HomePageHTMLService.class.getName());

            MemoryNotifier.printRuntimeMemory();

            logger.info("The ranker value is ----> " + HomePageHTMLService.Rankscore);

            Connection dbconn = DaoManager.connectJDBCAuroraCloudMySQL();
            if(dbconn != null){

                DaoManager.insertHomePageWebDataValuesToAuroraMySQL(dbconn,seedUrl,addressBuilder.toString(),emailList.toString(),contactNumbersList.toString(),bodytext,totalOutLinks,(isSiteMapXMLExists(seedUrl) ? "true" : "false"),(isRobotsTxtExists(seedUrl) ? "true" : "false"),Rankscore);

                /*
                 *RankScore is a static variable hence reinitializing to 0.
                 * Releasing the memory of StringBuilder for garbage collections.
                 * Making it ready for the next successive Calls.
                 */
                Rankscore = 0;
                addressBuilder.setLength(0);
                emailList.setLength(0);
                contactNumbersList.setLength(0);
                bodytext = null;
                avoidingObjects.clear();

            }else{

                logger.info(" Database connection is null or empty ");
            }
            /*
             * If the program crashes or is force killed, exit or shutdown hooks would not be fired.
             * A graceful exit would allow the program to finalize writing files, or closing connections, etc.
             */
            if(CrawlContract.isShutDown.get()){

                OnExitStrategy.deleteFiles();
                System.exit(0);
            }
        }
    }

    public static boolean isRobotsTxtExists(final String seedUrl) throws IOException {

        /*****************************
         * Validate for robots.txt
         ****************************/
        String robotsChecker = seedUrl.endsWith("/") ? seedUrl.concat("robots.txt") : seedUrl.concat("/robots.txt");
        boolean isRobotsExists = HttpCore.pingTest(robotsChecker);
        logger.info("Robots Checker - Root URL " + robotsChecker + "," + isRobotsExists + "," + HomePageHTMLService.class.getName());

        return isRobotsExists;
    }

    public static boolean isSiteMapXMLExists(final String seedUrl) throws IOException {
            /******************************
             * Validate for sitemap.xml
             *****************************/

        String sitemap = seedUrl.endsWith("/") ? seedUrl.concat("sitemap.xml") : seedUrl.concat("/sitemap.xml");
        boolean isSitemap = HttpCore.pingTest(sitemap);
        logger.info("Sitemap - RootURL Sitemap " + sitemap + " ,Does Sitemap exists --> " + isSitemap + "," + HomePageHTMLService.class.getName());

        return isSitemap;
    }
}
