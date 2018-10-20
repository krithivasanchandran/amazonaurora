package amazonaurora.core.parser;

import com.languagedetection.LanguageDetection;
import common.aurora.EmailExtractor;
import common.aurora.GetTimeStamp;
import common.aurora.LinkExtractor;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.text.html.HTML;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.Optional;
import java.util.Set;

public class HomePageHTMLService {
    private static Logger logger = LoggerFactory.getLogger(HomePageHTMLService.class);

    private HomePageHTMLService(){}

    public static void homePageCrawl(final String seedUrl, final String useragent) throws IOException {

        Document document = JsoupDomService.JsoupExtractor(seedUrl,useragent);

        logger.info("Result of first Document Extraction" + HomePageHTMLService.class.getName());
        logger.info(document.text() + GetTimeStamp.getCurrentTimeStamp().toString());

        if(document != null || !(document.text().isEmpty())){

            logger.info("Document object is not null -- Successfull Text Extraction ");

            partialExtraction(document,seedUrl);

            /*****************************
             * Validate for robots.txt
             ****************************/

            String robotsChecker = seedUrl.concat("/robots.txt");
            boolean isRobotsExists = HttpCore.pingTest(robotsChecker);
            logger.info("Robots Checker - Root URL " + robotsChecker + "," + isRobotsExists + "," + HomePageHTMLService.class.getName());
            robotsChecker = null;


            /******************************
             * Validate for sitemap.xml
             *****************************/

            String sitemap = seedUrl.concat("/sitemap.xml");
            boolean isSitemap = HttpCore.pingTest(sitemap);
            logger.info("Sitemap - RootURL Sitemap " + sitemap + " ,Does Sitemap exists --> " + isSitemap + "," + HomePageHTMLService.class.getName());
            sitemap = null;


            /************************************************************************
             * Check Outgoing Links - Count - High Quality Links - Within Webpage
             ************************************************************************/

            Set<String> outgoingLinks = LinkExtractor.extractOutgoingLinks(document,seedUrl);
            short totalOutLinks = (short)outgoingLinks.size();
            logger.info("Total Outbound Links " + totalOutLinks + "," + HomePageHTMLService.class.getName());

            /***********************************************************************
             * Home Page - Extract Phone , Address and Email Id
             **********************************************************************/
            String bodytext = (document.body().text().length() < 5000) ? document.body().text().trim() : document.body().text().trim().substring(0,5000);

            String[] phoneNumberList = PhoneNumberExtractor.extractPhoneNumber(bodytext).split(",");
            String emailList = EmailExtractor.EmailFinder(bodytext,seedUrl);

            StringBuilder contactNumbersList = new StringBuilder();
            for(String t:phoneNumberList){
                logger.info("Contact Telephone Numbers in homepage - Writing it to StringBuilder " + t);
                contactNumbersList.append(t);
            }

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
                             String[] contactsPhone = PhoneNumberExtractor.extractPhoneNumber(doc.text()).split(",");

                             /*
                              * EmailList Extraction from Contact Us Page
                              */
                             emailList = emailList.concat(EmailExtractor.EmailFinder(doc.text(),s));
                             logger.info(" List of Emails -->  " + emailList + " ," + HomePageHTMLService.class.getName() +", TimeStamp -> " + GetTimeStamp.getCurrentTimeStamp().toString());

                             for(String f: contactsPhone){
                                 logger.info("Contact Telephone Numbers in "+ s+"- Writing it to StringBuilder " + f + HomePageHTMLService.class.getName());
                                 contactNumbersList.append(f);
                             }
                             checkContactsOnEnter = true;
                             break;
                         }
                     }
                 }
             }

             if(!checkContactsOnEnter){
                 logger.info("Looks like there is no Contact Us Page from the home page " + HomePageHTMLService.class.getName());
             }

             /*************************************************
              * Parse URLS with depth 1 -> 1 hop from home page
              *************************************************/



            /*
             * Named Entity Recognition - Location Identification -> Address Extraction and then
             * Person Names Identification. - Apache OpenNLP.
             */

        }
    }

    public static String parseMetaData(Document doc){
        Elements elements = doc.getElementsByTag("meta");
        for(Element r : elements){
            if(r.attr("name").contentEquals("description")){
                String content = r.attr("content");
                return content;
            }
        }
        return "No Meta";
    }

    private static void partialExtraction(Document document,final String seedUrl) throws IOException {

        /*
         * Perform Language Detection - Optional<T> - Chaining if the document text is null or empty
         * then try to send the entire body text as input.
         */
        String dominantLang = LanguageDetection.InitiateLang(Optional.ofNullable(document.body().text()).orElse(document.wholeText()));
        logger.info("Dominant Language " + dominantLang + ","+ HomePageHTMLService.class.getName());


        int homepageLength = document.body().text().trim().length();
        logger.info("HomePage Length " + homepageLength + "," + HomePageHTMLService.class.getName());
        /*
         * Body Text has length of 3000 characters and less.
         */
        String bodytext = (homepageLength < 5000) ? document.body().text().trim() : document.body().text().trim().substring(0,5000);
        logger.info(" Body Text is less than 5000 characters " + bodytext + "," + HomePageHTMLService.class.getName());



        String title = document.getElementsByAttribute("title").text();
        logger.info(" Title Home Page " + title + "," + HomePageHTMLService.class.getName());

        /*
         * Getting meta data Tags about the home page
         */
        String descriptionMetaData = parseMetaData(document);
        logger.info(descriptionMetaData +","+HomePageHTMLService.class.getName());

        // Open Graph Meta Tags Fetcher
        String ogmetadata = FacebookOpenGraphMetaExtractor.extractFacebookOGData(document);
        logger.info(ogmetadata + "," + HomePageHTMLService.class.getName());

        /*
         * The highest Order priority is h1 > h2 > h3 > h4 > h5 > h6
         * Working on getting heading tags.
         */
        String[] headings = {"h1","h2","h3","h4","h5","h6"};
        String heading=null;
        for(String str: headings){
            heading = document.body().getElementsByTag("h1").text();
            if(heading == null){
                continue;
            }else{
                logger.info("Heading Tags - Order of Priority - Break Happens if h1 > h2 > h3 > h4 > h5 > h6" + HomePageHTMLService.class.getName());
                break;
            }
        }

        /* - Change Detection Algorithm
         * Calculate Hash Of Text - MD5 Hash. That takes in Hash of data
         * and generates fixed length value. More Infomation here -
         * https://docs.oracle.com/javase/8/docs/api/java/security/MessageDigest.html
         */

        String hashCode = FacebookOpenGraphMetaExtractor.calculateHash(bodytext);
        logger.info(" Generating HashCode - Text Content " + hashCode  + ", " + HomePageHTMLService.class.getName());

    }

}
