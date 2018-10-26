package amazonaurora.core.parser;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/*
 * Facebook Open Graph - meta Tags Extractor - Write it to DB
 * http://ogp.me/
 * <meta property="og:title" content="Hopefully I win this competition" />
 * <meta property="og:type" content="Type of website" />
 * <meta property="og:url" content="https://www.datacrunk.com/" />
 * <meta property="og:image" content="https://www.datacrunk.com/wp-content/uploads/2011/11/if-java-is-the-engine-then-spring-is-the-fuel-300x258.jpg" />
 * <meta property="og:site_name" content="Krithivasan" />
 * <meta property="og:description" content="Distributed Systems Work Around here" />
 */
public class FacebookOpenGraphMetaExtractor {
    private static Logger logger = LoggerFactory.getLogger(FacebookOpenGraphMetaExtractor.class);

    /*
     * Avoiding NEW Operator where it creates unwanted Objects.
     * Hence Memory Constraint Option of using Static Classes Everywhere
     * Only One copy per CPU(processor) per Thread exists.
     */
    private FacebookOpenGraphMetaExtractor(){}


    public static String extractFacebookOGData(Document doc){
        Elements elements = doc.getElementsByTag("meta");
        final StringBuilder ogbuilder = new StringBuilder();

        for(Element e: elements){
            String keyset = e.attr("property");
            // Still need to work on this one
            switch(keyset){

                case "og:title":
                    ogbuilder.append(" og:title :" + e.attr("content")).append("||");
                    break;
                case "og:type":
                    ogbuilder.append(" og:type :" + e.attr("content")).append("||");
                    break;
                case "og:url":
                    ogbuilder.append(" og:url :" + e.attr("content")).append("||");
                    break;
                case "og:image":
                    ogbuilder.append(" og:image :" + e.attr("content")).append("||");
                    break;
                case "og:site_name":
                    ogbuilder.append(" og:site_name :" + e.attr("content")).append("||");
                    break;
                case "og:description":
                    ogbuilder.append(" og:description :" + e.attr("content")).append("||");
                    break;
                case "og:locale":
                    ogbuilder.append(" og:locale :" + e.attr("content")).append("||");
                    break;
            }
        }
        logger.info("FaceBook Open Graph Contents: " + ogbuilder.toString());

        return ogbuilder.toString();
    }

    /* - Change Detection Algorithm
     * Calculate Hash Of Text - MD5 Hash. That takes in Hash of data
     * and generates fixed length value. More Infomation here -
     * https://docs.oracle.com/javase/8/docs/api/java/security/MessageDigest.html
     */

    public static String calculateHash(String text){
        logger.info("Calculating the Hash from the Body Text :: FacebookOpenGraphMetaExtractor");

        try {
            byte[] messageBytes = text.getBytes("UTF-8");
            MessageDigest md = MessageDigest.getInstance("MD5");

            byte[] hashinbytes = md.digest(messageBytes);

            StringBuffer bytetoStringConv = new StringBuffer();

            for(int i=0; i < hashinbytes.length; ++i){
                bytetoStringConv.append(Integer.toHexString((hashinbytes[i] & 0xFF) | 0x100).substring(1,3));
            }
            return bytetoStringConv.toString();
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            logger.error("No Such Algorithm here :: MD5 Calculate HashFunction Error" + FacebookOpenGraphMetaExtractor.class.getName());
            logger.info(e.getMessage());
            logger.info(FacebookOpenGraphMetaExtractor.class.getName());
        }
        return "";
    }
}
