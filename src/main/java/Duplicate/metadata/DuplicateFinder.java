package Duplicate.metadata;

/*
 * Duplicate Finder based on MD5 HashKey on the text retreived from the
 * Home Page HTML and 15 links of ChildHTML.
 * Returns True for Duplicate Content Exists.
 */

public class DuplicateFinder {

    private DuplicateFinder(){}

    private static final String[] HashBasedDuplicateChecker = new String[16];

    private static short lastInsertedIndex = 0;

    public static Boolean submitForDuplicatesCheck(String hashMD5Key){

            if(hashMD5Key instanceof String){

                if(lastInsertedIndex == 0){

                    HashBasedDuplicateChecker[lastInsertedIndex] = hashMD5Key;
                }else{

                    short i = 0;
                    while(i < HashBasedDuplicateChecker.length){

                        if(HashBasedDuplicateChecker[i].equalsIgnoreCase(hashMD5Key)) {

                            HashBasedDuplicateChecker[++lastInsertedIndex] = hashMD5Key;
                            return true;
                        }
                        i++;
                    }
                    return false;
                }
            }
            return false;
    }
}
