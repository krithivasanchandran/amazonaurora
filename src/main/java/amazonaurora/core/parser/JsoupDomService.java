package amazonaurora.core.parser;

import com.languagedetection.LanguageDetection;
import jdk.jfr.internal.test.WhiteBox;
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

    public static Document JsoupExtractor(final String seedUrl, final String useragent){

        Document document = null;
        try{

            document = Jsoup.connect(seedUrl)
                                .userAgent(useragent)
                                .timeout(10000)
                                .cookie("cookiename","Crawling for a Challenge")
                                .referrer("http://google.com")
                                .followRedirects(true)
                                .maxBodySize(0)
                                .ignoreContentType(true)
                                .get();


            logger.info(" JSoup Extractor Method :: " + " User Agent :: " + useragent + " Timeout Set ::: 2000, referrer :: https://google.com "
            + "follow Redirects : 301 and 302  :: Temporary and Permanent Redirect :: True , Max Body Size : 0, IgnoreContent Type : True" );


            /*
             * Perform Language Detection - Optional<T> - Chaining if the document text is null or empty
             * then try to send the entire body text as input.
             */
            String dominantLang = LanguageDetection.InitiateLang(Optional.ofNullable(document.body().text()).orElse(document.wholeText()));
            int homepageLength = document.body().text().length();
            String title = document.getElementsByAttribute("title").text();

            /*
             * Checks if the webpage has images
             */
             // ---> Still to do.

            /*
             * Getting meta data Tags about the home page
             */
            String descriptionMetaData = parseMetaData(document);



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
        return "No Meta Content with Name Attribute Exists";
    }

    }
