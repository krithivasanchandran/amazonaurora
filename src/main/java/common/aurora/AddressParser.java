package common.aurora;

import amazonaurora.core.parser.HomePageHTMLService;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*********************************************************
 * Address Extraction from home page or Cantact Us Page
 * if detected apply the
 * Address Parsing Regular Expression for neighbouring
 * text to identify and retrieve the address or city
 * state and zip code.
 * *******************************************************/

public class AddressParser {

    private static Logger logger = LoggerFactory.getLogger(AddressParser.class);

    // Source from -> https://stackoverflow.com/questions/35784962/regex-for-capturing-city-state-zip-from-an-address-string
    private static String addressregularexp = "(\\d*)\\s+((?:[\\w+\\s*-])+)[\\,]\\s+([a-zA-Z]+)\\s+([0-9a-zA-Z]+)";


    private AddressParser(){}

    /* US Locations only supported.
     * Location - Places Recognition - First Level Filtering.
     */
    public static boolean namedEntityLocationRecognition(String word){
        //Replace 2 white space to Single White Space
       return  USAStateAbbreviations.containPOCodeKey(word);
    }

    public static String extractAddressParts(String word){

        Pattern pattern = Pattern.compile(addressregularexp);
        Matcher matcher = pattern.matcher(word);
        StringBuilder addressBuilder = new StringBuilder();

        while(matcher.find()){
          String str = matcher.group();
          addressBuilder.append(str);
        }

        return addressBuilder.toString();
    }
}
