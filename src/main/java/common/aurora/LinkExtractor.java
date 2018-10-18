package common.aurora;

import com.languagedetection.LanguageDetection;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Queue;
import java.util.Set;

public class LinkExtractor {

    private static Logger logger = LoggerFactory.getLogger(LinkExtractor.class);

    /*
     * Avoiding NEW Operator where it creates unwanted Objects.
     * Hence Memory Constraint Option of using Static Classes Everywhere
     * Only One copy per CPU(processor) per Thread exists.
     */
    private LinkExtractor(){}

    public static Set<String> extractOutgoingLinks(Document document, String rooturl) {

        final Set<String> duplicates = new HashSet<String>();
        Set<String> outgoingSeeds = new HashSet<String>();

        document.select("a").stream().forEach((r1) -> {

            String rawHref = r1.attr("href").toLowerCase();

            /* ************************
             * Filtering Social Links *
             ****************************/
            boolean nomatchesCondition = !rawHref.startsWith("./")
                    && !rawHref.startsWith("//")
                    && !rawHref.startsWith("mailto")
                    && !rawHref.contains("facebook.com")
                    && !rawHref.contains("plus.google.com")
                    && !rawHref.contains("youtube.com")
                    && !rawHref.contains("snapchat.com")
                    && !rawHref.contains("twitter.com")
                    && !rawHref.contains("instagram.com")
                    && !rawHref.contains("pinterest.com")
                    && !rawHref.contains("linkedin.com")
                    && !rawHref.contains("reddit.com")
                    && !rawHref.contains("digg.com")
                    && !rawHref.contains("yahoo.com")
                    && !rawHref.startsWith("javascript");

            /*
             * Validate if href contains http or https
             */
            if((rawHref.startsWith("http://") || rawHref.startsWith("https://")) && nomatchesCondition){

                /*
                 * Duplicate Checks if it contains any Duplicate URLS to prevent wastage of resources.
                 */
                if(!duplicates.contains(rawHref) && (rawHref.startsWith("http://"+rooturl) || rawHref.startsWith("https://"+rooturl))){

                    System.out.println(" Filtered & Adding Extarcted Links -----===================> " + rawHref);
                    duplicates.add(rawHref);
                    outgoingSeeds.add(rawHref);
                }
            }else if (nomatchesCondition) {

                /*
                 * Checks if the URL's Start with /a.html or abc/acd.html are valid ones
                 */
                boolean misnomer = rawHref.startsWith("/")
                        || rawHref.startsWith("?")
                        || rawHref.matches("[a-zA-Z0-9](.*)")
                        || rawHref.endsWith(".html")
                        || rawHref.startsWith("#");

                if (misnomer) {

                    if(rooturl.endsWith("/") && rawHref.matches("[a-zA-Z0-9](.*)")){
                        rawHref = rooturl.concat(rawHref);
                    }else if(rooturl.matches("(.*)[a-zA-Z0-9]") && rawHref.startsWith("/")){
                        rawHref = rooturl.concat(rawHref);
                    }else if(rooturl.endsWith("/") && rawHref.startsWith("/")){
                        String normalizedString = rooturl.substring(0, rooturl.length()-1);
                        rawHref = normalizedString.concat(rawHref);
                    }else if(rooturl.matches("(.*)[a-zA-Z0-9]") && rawHref.matches("[a-zA-Z0-9](.*)")){
                        rawHref = rooturl.concat("/"+rawHref);
                    }else{
                        if(rawHref.startsWith("#")){
                            rawHref = rawHref.substring(1,rawHref.length());
                            rawHref=rooturl.concat(rawHref);
                        }
                    }
                }

                if ((rawHref.startsWith(rooturl)) && !duplicates.contains(rawHref)) {

                    System.out.println(" Filtered Extarcted Links -----> " + rawHref);
                    /*
                     * Second Level URL Filtering - Only Quality Links should get in
                     */
                    if(!shouldVisit(rawHref)){
                        duplicates.add(rawHref);
                        outgoingSeeds.add(rawHref);
                    }
                }
            }else{
                System.out.println(" WARNING !!! None of the URL Extraction Worked Out !!! ");
                System.out.println(" ********* URL NONMATCHED ONES ********\t" + rawHref);
            }
        });

        /*
         * Freeing up Memory Space
         */
        duplicates.clear();

        return outgoingSeeds;
    }

    /*
     * Second Level Filter that filters - audio, video, images, css, javascript
     * and other unsupported file formats.
     */
    private static boolean shouldVisit(String url) {
        String href = url.toLowerCase();
        boolean FILTERS = href.matches("(.*)(css|js|bmp|gif|jpe?g"
                + "JPG|png|tiff?|mid|mp2|mp3|mp4"+
                "|wav|avi|mov|mpeg|ram|m4v|pdf" +
                "|rm|smil|wmv|swf|wma|zip|rar|gz|mailto)(.*)$");

        return FILTERS;
    }
}
