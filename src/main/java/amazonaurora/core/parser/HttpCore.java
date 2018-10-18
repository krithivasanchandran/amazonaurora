package amazonaurora.core.parser;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.IOException;

public class HttpCore {

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

            response = httpclient.execute(getRequest);

            //They are temporary and permanent redirects
            if(response.getStatusLine().getStatusCode() == 301 || response.getStatusLine().getStatusCode() == 302) return true;

            //If there is a success 200 then return true
            return (response.getStatusLine().getStatusCode() == 200);
        }catch(Exception ex){
            System.out.println(ex.getMessage());
        }finally{
            if(response!=null){
                response.close();
            }
        }
        return false;
    }

}
