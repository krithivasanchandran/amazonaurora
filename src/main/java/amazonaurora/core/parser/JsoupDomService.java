package amazonaurora.core.parser;

import com.languagedetection.LanguageDetection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.safety.Cleaner;
import org.jsoup.safety.Whitelist;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

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


            logger.info(" JSoup Extractor Method :: " + " User Agent :: " + useragent + " Timeout Set ::: 2000, referrer :: https://google.com "
            + "follow Redirects : 301 and 302  :: Temporary and Permanent Redirect :: True , Max Body Size : 0, IgnoreContent Type : True" );

            if(document == null || document.text().isEmpty()) return null;

            /*
             * Perform Language Detection - Optional<T> - Chaining if the document text is null or empty
             * then try to send the entire body text as input.
             */
            String dominantLang = LanguageDetection.InitiateLang(Optional.ofNullable(document.body().text()).orElse(document.wholeText()));
            int homepageLength = document.body().text().trim().length();

            /*
             * Body Text has length of 3000 characters and less.
             */
            String bodytext = (homepageLength < 3000) ? document.body().text().trim() : document.body().text().trim().substring(0,3000);
            String title = document.getElementsByAttribute("title").text();

            /*
             * Checks if the webpage has images
             */
             // ---> Still to do.

            /*
             * Getting meta data Tags about the home page
             */
            String descriptionMetaData = parseMetaData(document);

            // Open Graph Meta Tags Fetcher
            String ogmetadata = FacebookOpenGraphMetaExtractor.extractFacebookOGData(document);

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
                    break;
                }
            }

            /* - Change Detection Algorithm
             * Calculate Hash Of Text - MD5 Hash. That takes in Hash of data
             * and generates fixed length value. More Infomation here -
             * https://docs.oracle.com/javase/8/docs/api/java/security/MessageDigest.html
             */

            String hashCode = FacebookOpenGraphMetaExtractor.calculateHash(bodytext);


            /***********************************************************************
             * Check for About Us Page - Extract Phone , Address and Email Id
             **********************************************************************/
            String[] phoneNumberList = PhoneNumberExtractor.extractPhoneNumber(bodytext).split(",");

            /************************************************************************
            * Check Outgoing Links - Count - High Quality Links - Within Webpage
             *********************************************************************/


        }catch(Exception ex){
            logger.error(" Http Exception occurred hence stalling the operation -- Inside the JSOUP Class");
            System.out.println(" Http Exception occurred hence stalling the operation ");
        }
        return null;
    }

    public static String parseMetaData(Document doc){
        Elements elements = doc.getElementsByTag("meta");
        if(elements.attr("name").contentEquals("description")){
            String content = elements.attr("content");
            return content;
        }
        return "No Meta";
    }

    }
