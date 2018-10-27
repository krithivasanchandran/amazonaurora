package amazonaurora.core.parser;

import Duplicate.metadata.OnExitStrategy;
import MemoryListener.MemoryNotifier;
import aurora.rest.CrawlContract;
import aurora.rest.RetryLogic;
import com.languagedetection.TextNormalizer;
import common.aurora.*;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Set;

/*
 * Parsing of home page and child Pages - Ranking Score calculation based on few criterias.
 * Seed URL -> Root Url.
 */

public class HomePageHTMLService {
    private static Logger logger = LoggerFactory.getLogger(HomePageHTMLService.class);
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

            /*****************************
             * Validate for robots.txt
             ****************************/
            String robotsChecker = seedUrl.endsWith("/") ? seedUrl.concat("robots.txt") : seedUrl.concat("/robots.txt");
            boolean isRobotsExists = HttpCore.pingTest(robotsChecker);
            logger.info("Robots Checker - Root URL " + robotsChecker + "," + isRobotsExists + "," + HomePageHTMLService.class.getName());

            Rankscore += isRobotsExists ? (short)10 : (short)5;

            /******************************
             * Validate for sitemap.xml
             *****************************/

            String sitemap = seedUrl.endsWith("/") ? seedUrl.concat("sitemap.xml") : seedUrl.concat("/sitemap.xml");
            boolean isSitemap = HttpCore.pingTest(sitemap);
            logger.info("Sitemap - RootURL Sitemap " + sitemap + " ,Does Sitemap exists --> " + isSitemap + "," + HomePageHTMLService.class.getName());
            sitemap = null;

            Rankscore += isSitemap ? (short)10 : (short)5;


            /************************************************************************
             * Check Outgoing Links - Count - High Quality Links - Within Webpage
             ************************************************************************/

            Set<String> outgoingLinks = LinkExtractor.extractOutgoingLinks(document,seedUrl);
            /*
             * Serialize and save object - Outgoing Links.
             */
         // ---> Need to check in ubuntu file system   HotRestartManager.persistchildLinks(outgoingLinks);
            short totalOutLinks = (short)outgoingLinks.size();
            logger.info("Total Outbound Links " + totalOutLinks + "," + HomePageHTMLService.class.getName());

            Rankscore += (totalOutLinks > 0) ? (short)10 : (short)5;

            /***********************************************************************
             * Home Page - Extract Phone , Address and Email Id
             **********************************************************************/
            String bodytext = TextNormalizer.Normalizer.getWords(document.body().text().trim());
            bodytext = (bodytext.length() < 5000) ? bodytext.trim() : bodytext.substring(0,5000);

            String phoneNumberList = PhoneNumberExtractor.extractPhoneNumber(bodytext);
            StringBuilder contactNumbersList = new StringBuilder();

            if(phoneNumberList != null && !(phoneNumberList.isEmpty())){
                    logger.info("Contact Telephone Numbers in homepage - Writing it to StringBuilder " + phoneNumberList);
                    contactNumbersList.append(phoneNumberList).append(",");
            }
            String emailList = EmailExtractor.EmailFinder(bodytext,seedUrl);


            MemoryNotifier.printRuntimeMemory();
            /************************************************************************************
             * Link Discovery of Contact Us Page. - Find different flavors of Contact US Page
             * The loop executes for only one contactUs per homepage
             ************************************************************************************/

             String[] contacts = {"contactus","contact","contact-us"};
             boolean checkContactsOnEnter = false;

             for(int i=0;i<contacts.length;i++){
                 for(String s : outgoingLinks){
                     if(s.contains(contacts[i])){
                         //Check for Contact US Page Exists.
                         logger.info(" ContactUs Page - Discovery Link Exists !! Now Crawling");
                         Document doc = JsoupDomService.JsoupExtractor(s,useragent);

                         if(doc != null){
                             //Extract Phone number -
                             String contactsPhone = PhoneNumberExtractor.extractPhoneNumber(doc.text());

                             /*
                              * EmailList Extraction from Contact Us Page
                              */
                             emailList = emailList.concat(EmailExtractor.EmailFinder(doc.text(),s));
                             logger.info(" List of Emails -->  " + emailList + " ," + HomePageHTMLService.class.getName() +", TimeStamp -> " + contactsPhone +" --> " + GetTimeStamp.getCurrentTimeStamp().toString());

                                 logger.info("Contact Telephone Numbers in "+ s+"- Writing it to StringBuilder " + HomePageHTMLService.class.getName());
                                 contactNumbersList.append(contactsPhone);

                             checkContactsOnEnter = true;
                             break;
                         }
                     }
                 }
             }


             if(!checkContactsOnEnter) {
                 logger.info("Looks like there is no Contact Us Page from the home page " + HomePageHTMLService.class.getName());
                 /*
                  * Construct ContactUs - URL and find if it works.
                  */
                 for (int i = 0; i < contacts.length; i++) {
                     String temp = seedUrl.endsWith("/") ? seedUrl.concat(contacts[i]) : seedUrl.concat("/".concat(contacts[i]));

                     Document doc_1 = JsoupDomService.JsoupExtractor(temp, useragent);

                     if (doc_1 != null) {
                         logger.info(" ContactUs Page - Discovery Link Exists !! Now Crawling");

                         //Extract Phone number -
                         String contactsPhone = PhoneNumberExtractor.extractPhoneNumber(doc_1.text());

                         /*
                          * EmailList Extraction from Contact Us Page
                          */
                         emailList = emailList.concat(EmailExtractor.EmailFinder(doc_1.text(), temp));
                         logger.info(" List of Emails -->  " + emailList + " ," + HomePageHTMLService.class.getName() + ", TimeStamp -> " + contactsPhone + " --> " + GetTimeStamp.getCurrentTimeStamp().toString());

                         logger.info("Contact Telephone Numbers in " + temp + "- Writing it to StringBuilder " + HomePageHTMLService.class.getName());
                         contactNumbersList.append(contactsPhone);

                     }
                 }
             }

             /*************************************************
              * Parse URLS with depth 1 -> 1 hop from home page
              * I don't want to choke their servers.
              *************************************************/
              CrawlDepthFactor1.crawlAtDepthFactor1(outgoingLinks);


            /*
             * Named Entity Recognition - Location Identification -> Address Extraction
             */

            String cityStateZip = AddressParser.extractAddressParts(bodytext);
            String[] parts = cityStateZip.split("\\s+");
            for(String str : parts){
                if(AddressParser.namedEntityLocationRecognition(str.toLowerCase().trim())){
                   String stateName =  USAStateAbbreviations.getPOFullName(str.toLowerCase().trim());
                    cityStateZip = cityStateZip.concat(stateName);
                }
            }
            logger.info("Address Extraction Results : " + cityStateZip + HomePageHTMLService.class.getName());

            MemoryNotifier.printRuntimeMemory();

            // Write the values to the database
            logger.info("The ranker value is ----> " + HomePageHTMLService.Rankscore);
            Rankscore = 0;

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
}
