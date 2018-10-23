package amazonaurora.core.parser;

/*
 * Contact Number Extraction Using - libphonenumber.java - lib
 * More Info Here - https://github.com/googlei18n/libphonenumber
 */

import com.google.i18n.phonenumbers.PhoneNumberMatch;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;

public class PhoneNumberExtractor {

    private static Logger logger = LoggerFactory.getLogger(PhoneNumberExtractor.class);


    final static String country = "US";
    final static String defaultCountry = null;

    /*
     * Avoiding NEW Operator where it creates unwanted Objects.
     * Hence Memory Constraint Option of using Static Classes Everywhere
     * Only One copy per CPU exists.
     */
    private PhoneNumberExtractor(){}

    /*
     * Parses and Extracts Phonenumbers.
     */
    public static String extractPhoneNumber(String bodytext){

        logger.info("Phone Number Extraction - Default Country is null");

        PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
        Iterator<PhoneNumberMatch> itr = phoneUtil.findNumbers(bodytext, defaultCountry).iterator();
        StringBuilder phonelist = null;

        while(itr.hasNext()){

            PhoneNumberMatch numberMatch = itr.next();
            Phonenumber.PhoneNumber number = numberMatch.number();

            if(phoneUtil.isValidNumber(number)){

                phonelist = new StringBuilder();

                logger.info("Phone number is valid" + "," + PhoneNumberExtractor.class.getName());

                String phoneNumber = (String) bodytext.subSequence(numberMatch.start(),numberMatch.end());

                logger.info("Phone number Extracted is --->" + phoneNumber + "," + PhoneNumberExtractor.class.getName());

                phonelist.append(phoneNumber!= null ? phoneNumber+"," : "");
            }
        }

        logger.info("Total List of phone numbers " + phonelist.toString());
        return phonelist.toString();
    }

}
