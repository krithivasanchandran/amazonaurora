package common.aurora;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/*
 * Making it ReadOnly HashMap --> Unmodifiable Collections <-- - To Make it take less memory .
 */

public class USAStateAbbreviations {

    private static final Map<String,String> statesUSA = new HashMap<String,String>(51);

    private USAStateAbbreviations(){}

    static{
        statesUSA.put("al","alabama");
        statesUSA.put("ak","alaska");
        statesUSA.put("az","arizona");
        statesUSA.put("ar","arkansas");
        statesUSA.put("ca","california");
        statesUSA.put("co","colorado");
        statesUSA.put("ct","connecticut");
        statesUSA.put("de","delaware");
        statesUSA.put("dc","district of columbia");
        statesUSA.put("fl","florida");
        statesUSA.put("ga","georgia");
        statesUSA.put("hi","hawaii");
        statesUSA.put("id","idaho");
        statesUSA.put("il","illinois");
        statesUSA.put("in","indiana");
        statesUSA.put("ia","iowa");
        statesUSA.put("ks","kansas");
        statesUSA.put("ky","kentucky");
        statesUSA.put("la","louisiana");
        statesUSA.put("me","maine");
        statesUSA.put("md","maryland");
        statesUSA.put("ma","massachusetts");
        statesUSA.put("mi","michigan");
        statesUSA.put("mn","minnesota");
        statesUSA.put("ms","mississippi");
        statesUSA.put("mo","missouri");
        statesUSA.put("mt","montana");
        statesUSA.put("ne","nebraska");
        statesUSA.put("nv","nevada");
        statesUSA.put("nh","new hampshire");
        statesUSA.put("nj","new jersey");
        statesUSA.put("nm","new mexico");
        statesUSA.put("ny","new york");
        statesUSA.put("nc","north carolina");
        statesUSA.put("nd","north dakota");
        statesUSA.put("oh","ohio");
        statesUSA.put("ok","oklahoma");
        statesUSA.put("or","oregon");
        statesUSA.put("pa","pennsylvania");
        statesUSA.put("ri","rhode island");
        statesUSA.put("sc","south carolina");
        statesUSA.put("sd","south dakota");
        statesUSA.put("tn","tennessee");
        statesUSA.put("tx","texas");
        statesUSA.put("ut","utah");
        statesUSA.put("vt","vermont");
        statesUSA.put("va","virginia");
        statesUSA.put("wa","washington");
        statesUSA.put("wv","west virginia");
        statesUSA.put("wi","wisconsin");
        statesUSA.put("wy","wyoming");

        // Immutable Read Only Map - Thread Safe Implementation.
        Collections.unmodifiableMap(statesUSA);
    }

    public static boolean containPOCodeKey(String abbreviatedPostalCode){
        return statesUSA.containsKey(abbreviatedPostalCode);
    }

    public static String getPOFullName(String abbreviatePostalCode){
        return statesUSA.get(abbreviatePostalCode);
    }


}
