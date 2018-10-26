package common.aurora;

import amazonaurora.core.parser.FacebookOpenGraphMetaExtractor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EmailExtractor {

    private static Logger logger = LoggerFactory.getLogger(EmailExtractor.class);

    private EmailExtractor(){}

    public static String EmailFinder(String htmlbody, String urlReference) throws IOException {

        /*
         * Flexible email Address Extraction of pattern abc@smaple.com or a.asd@wample.com
         */

        final String EMAILREGEX = "([\\w\\-]([\\.\\w])+[\\w]+@([\\w\\-]+\\.)+[A-Za-z]{2,4})";
        logger.info("Email Regular Expression is " + EMAILREGEX + EmailExtractor.class.getName());
        Pattern p = Pattern.compile(EMAILREGEX);
        Matcher m = p.matcher(htmlbody);
        StringBuilder emailList = new StringBuilder();

        while (m.find()) {

            String email = m.group();
            logger.info(" Email Extractor Service - Email -->" + email + "," + EmailExtractor.class.getName());
            System.out.println(" Email Address :::  " + email);
            emailList.append(email).append(" ").append(",");
            logger.info("Website URL the email is extracted in " + urlReference);
            System.out.println(" Website URL :::  " + urlReference);
            }
            return emailList.toString();
        }
    }
