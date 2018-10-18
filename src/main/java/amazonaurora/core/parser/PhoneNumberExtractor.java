package amazonaurora.core.parser;

/*
 * Contact Number Extraction Using - libphonenumber.java - lib
 * More Info Here - https://github.com/googlei18n/libphonenumber
 */

import com.google.i18n.phonenumbers.AsYouTypeFormatter;
import com.google.i18n.phonenumbers.PhoneNumberMatch;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.languagedetection.LanguageDetection;
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

        PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
        Iterator<PhoneNumberMatch> itr = phoneUtil.findNumbers(bodytext, defaultCountry).iterator();
        StringBuilder phonelist = new StringBuilder();

        while(itr.hasNext()){
            PhoneNumberMatch numberMatch = itr.next();
            Phonenumber.PhoneNumber number = numberMatch.number();
            if(phoneUtil.isValidNumber(number)){
                String phoneNumber = (String) bodytext.subSequence(numberMatch.start(),numberMatch.end());
                phonelist.append(phoneNumber!= null ? phoneNumber+"," : "");
            }
        }

        return phonelist.toString();
    }

}
