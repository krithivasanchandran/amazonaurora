package aurora.rest;

import amazonaurora.core.parser.JsoupDomService;
import org.jsoup.nodes.Document;

public class RetryLogic {

    public static Document retry(Document docs,final String seedUrl,final String useragent){

        if(docs == null){

            short p = 0;
            while(p < CrawlContract.retryRequest){
                docs = JsoupDomService.JsoupExtractor(seedUrl,useragent);
                if (docs == null) {
                    p++;
                    continue;
                }else{
                    break;
                }
            }
        }
        return docs;
    }

}
