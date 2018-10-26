package amazonaurora.core.parser;

import aurora.rest.CrawlContract;
import com.languagedetection.LanguageDetection;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class HttpCore {

    private static final String username = "krithivasan";
    private static Logger logger = LoggerFactory.getLogger(HttpCore.class);

    /*
     * Avoiding NEW Operator where it creates unwanted Objects.
     * Hence Memory Constraint Option of using Static Classes Everywhere
     * Only One copy per CPU(processor) per Thread exists.
     */
    private HttpCore(){}

    public static boolean pingTest(String url) throws IOException {
        CloseableHttpResponse response=null;
        try{
            CloseableHttpClient httpclient = HttpClients.createSystem();

            RequestConfig requestConfig = RequestConfig.custom()
                                                .setSocketTimeout(4000)
                                                .setConnectTimeout(4000)
                                                .setConnectionRequestTimeout(5000)
                                                .setCircularRedirectsAllowed(false)
                                                .setRedirectsEnabled(false)
                                                .build();

            HttpGet getRequest = new HttpGet(url);
            getRequest.setConfig(requestConfig);

            /*
             * Politeness Delay between Subsequent Requests.- 3 Seconds - We dont
             * Choke the Server - implications will lead to IP Banning and
             * Subnet IP Address BlackListing.
             */
            try{
                Thread.sleep(500);
            }catch(InterruptedException ex){
                logger.error("Interrupted Exception happened when using Thread.sleep(1200)" + JsoupDomService.class.getName());
            }

            response = httpclient.execute(getRequest);


            //They are temporary and permanent redirects
            //If there is a success 200 then return true
            boolean httpStatus = response.getStatusLine().getStatusCode() == 301 ||
                                    response.getStatusLine().getStatusCode() == 302 ||
                                    response.getStatusLine().getStatusCode() == 200;

            logger.info(" HTTP Return Response is " + httpStatus + HttpCore.class.getName());

            File filehandler = null;
            FileWriter writer = null;
            try{
                if(httpStatus){
                    filehandler = new File("/home/"+username+"/successLinks.txt");
                    if(!filehandler.exists()){
                        logger.info("File Create with the pathname " + "/home/"+username+"/successLinks.txt" + HttpCore.class.getName());
                        filehandler.createNewFile();
                    }
                    filehandler.createNewFile();
                    writer = new FileWriter(filehandler);
                    writer.append("Sucess Crawling : " + url);
                    logger.info("Sucess URLs added to the file  ----> " + url);

                }else{
                    filehandler = new File("/home/"+username+"/failureLinks.txt");
                    if(!filehandler.exists()){
                        logger.info("File Created with the pathname " + "/home/"+username+"/failureLinks.txt" + HttpCore.class.getName());
                        filehandler.createNewFile();
                    }
                    writer = new FileWriter(filehandler);
                    writer.append("Failure URL's : " + url);
                    logger.info("Failure URLs added to the file  ----> " + url);
                }
            }catch(IOException ex){
                logger.error(ex.getMessage());
            }finally{
                /*
                 * Closing Handlers to avoid memory leaks and GC pauses
                 */
                if(writer != null){
                    writer.flush();
                    writer.close();
                }
            }

            return httpStatus;

        }catch(Exception ex){
            logger.error(ex.getMessage() + HttpCore.class.getName());
        }finally{
            if(response!=null){
                response.close();
            }

        }
        return false;
    }

}
