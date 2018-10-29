package restcontroller.implementation;

import Resilience.FailureRecovery.PingTester;
import amazonaurora.core.parser.HomePageHTMLService;
import amazonaurora.core.parser.JsoupDomService;
import amazonaurora.core.parser.PhoneNumberExtractor;
import common.aurora.EmailExtractor;
import common.aurora.GetTimeStamp;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Set;

/************************************************************************************
 * Link Discovery of Contact Us Page. - Find different flavors of Contact US Page
 * The loop executes for only one contactUs per homepage
 ************************************************************************************/

public class LinkDiscovery {

    private static Logger logger = LoggerFactory.getLogger(PingTester.class);

    private LinkDiscovery(){}

    public static void discoverContactUsPage(String seedUrl,Set<String> outgoingLinks,String useragent,final StringBuilder emailList,final StringBuilder contactNumbersList) throws IOException {

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
                        emailList.append(EmailExtractor.EmailFinder(doc.text(),s));
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
                    emailList.append(EmailExtractor.EmailFinder(doc_1.text(),temp));
                    logger.info(" List of Emails -->  " + emailList + " ," + HomePageHTMLService.class.getName() + ", TimeStamp -> " + contactsPhone + " --> " + GetTimeStamp.getCurrentTimeStamp().toString());

                    logger.info("Contact Telephone Numbers in " + temp + "- Writing it to StringBuilder " + HomePageHTMLService.class.getName());
                    contactNumbersList.append(contactsPhone);

                }
            }
        }
    }
}
