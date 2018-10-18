package amazonaurora.core.parser;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

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

    public static String extractFacebookOGData(Document doc){
        Elements elements = doc.getElementsByTag("meta");
        if(elements.attr("name").contentEquals("description")){
            String content = elements.attr("content");
            return content;
    }

}
