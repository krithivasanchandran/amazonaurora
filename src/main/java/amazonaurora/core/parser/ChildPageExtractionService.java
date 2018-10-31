package amazonaurora.core.parser;

import AuroraMySql.DatabaseManager.AuroraInsertData;
import AuroraMySql.DatabaseManager.DaoManager;
import Duplicate.metadata.DuplicateFinder;
import Duplicate.metadata.OnExitStrategy;
import MemoryListener.MemoryNotifier;
import aurora.rest.CrawlContract;
import com.languagedetection.LanguageDetection;
import com.languagedetection.TextNormalizer;
import common.aurora.KeywordAnalyser;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.Connection;
import java.util.Map;
import java.util.Optional;

public class ChildPageExtractionService {

    private static Logger logger = LoggerFactory.getLogger(HomePageHTMLService.class);

    private ChildPageExtractionService(){}

    /*
     * Applies to Child URL's.
     */

    public static short partialExtraction(Document document, final String seedUrl,short Rankscore) throws IOException {



        /*
         * Perform Language Detection - Optional<T> - Chaining if the document text is null or empty
         * then try to send the entire body text as input.
         */
        String dominantLang = LanguageDetection.InitiateLang(Optional.ofNullable(document.body().text()).orElse(document.wholeText()));
        HomePageHTMLService.avoidingObjects.put(0,dominantLang);

        logger.info("Dominant Language " + dominantLang + ","+ HomePageHTMLService.class.getName());

        Rankscore += dominantLang != null || !(dominantLang.isEmpty()) ? (short)10 : (short)5;

        String bodytext = TextNormalizer.Normalizer.getWords(document.body().text().trim());

        final int homepageLength = bodytext.length();
        Rankscore += homepageLength > 5000 ? (short)10 : (short)5;
        HomePageHTMLService.avoidingObjects.put(1,homepageLength+"");
        /*
         * Body Text has length of 5000 characters and less.
         */
        String neatBodyText = (homepageLength < 5000) ? bodytext : bodytext.substring(0,5000);
        logger.info(" Body Text is less than 5000 characters " + neatBodyText + "," + HomePageHTMLService.class.getName());
        HomePageHTMLService.avoidingObjects.put(2,neatBodyText);

        MemoryNotifier.printRuntimeMemory();


        /**************************************************************************
         *  Keyword Frequency Counter
         **************************************************************************/
        String keywordsCount = KeywordAnalyser.keywordCount(neatBodyText);
        // Wait to this output.
        System.out.println(" Writing keywords to the output " + keywordsCount);
        HomePageHTMLService.avoidingObjects.put(3,keywordsCount);



        String title = document.getElementsByAttribute("title").text().trim();
        String normalizedTitle = TextNormalizer.Normalizer.getWords(title);
        logger.info(" Title Home Page " + normalizedTitle + "," + HomePageHTMLService.class.getName());

        if(normalizedTitle != null || !(normalizedTitle.isEmpty())){
            Rankscore += 10;
        }else{
            Rankscore += 5;
        }
        HomePageHTMLService.avoidingObjects.put(4,normalizedTitle);


        /*
         * Getting meta data Tags about the home page
         */
        String descriptionMetaData = parseMetaData(document);
        logger.info(" Meta Tag ----> " + descriptionMetaData +","+HomePageHTMLService.class.getName());

        Rankscore += descriptionMetaData != null || !(descriptionMetaData.isEmpty()) ? (short) 10 : (short) 5;

        HomePageHTMLService.avoidingObjects.put(5,descriptionMetaData);

        // Open Graph Meta Tags Fetcher
        String ogmetadata = FacebookOpenGraphMetaExtractor.extractFacebookOGData(document);
        logger.info(ogmetadata + "," + HomePageHTMLService.class.getName());

        Rankscore += ogmetadata != null || !(ogmetadata.isEmpty()) ? (short) 10 : (short) 5;
        HomePageHTMLService.avoidingObjects.put(6,ogmetadata);

        /*
         * The highest Order priority is h1 > h2 > h3 > h4 > h5 > h6
         * Working on getting heading tags.
         */
        String[] headings = {"h1","h2","h3","h4","h5","h6"};
        String heading=null;
        for(String str: headings){
            heading = document.body().getElementsByTag("h1").text();
            if(heading == null){
                continue;
            }else{
                logger.info("Heading Tags - Order of Priority - Break Happens if anyone of those h tags exists h1 > h2 > h3 > h4 > h5 > h6 --> " +heading + " ----> classname " + HomePageHTMLService.class.getName());
                break;
            }
        }

        Rankscore += heading != null || !(heading.isEmpty()) ? (short) 10 : (short) 5;
        HomePageHTMLService.avoidingObjects.put(7,heading);

        /* - Change Detection Algorithm
         * Calculate Hash Of Text - MD5 Hash. That takes in Hash of data
         * and generates fixed length value. More Infomation here -
         * https://docs.oracle.com/javase/8/docs/api/java/security/MessageDigest.html
         */
        MemoryNotifier.printRuntimeMemory();

        String hashCode = FacebookOpenGraphMetaExtractor.calculateHash(bodytext);
        logger.info(" Generating HashCode - Text Content " + hashCode  + ", " + HomePageHTMLService.class.getName());

        Rankscore += hashCode != null || !(hashCode.isEmpty()) ? (short) 10 : (short) 5;
        HomePageHTMLService.avoidingObjects.put(8,hashCode);

        Boolean duplicateCheck = DuplicateFinder.submitForDuplicatesCheck(hashCode);
        logger.info("Result of Duplicate Check with MD5 algorithm ---> " + duplicateCheck);

        if(duplicateCheck){
            String str = "Has duplicates within the 16 Pages Crawled from the website.";
            logger.info(str + HomePageHTMLService.class.getName());
        }

        HomePageHTMLService.avoidingObjects.put(9,duplicateCheck.toString());


        MemoryNotifier.printRuntimeMemory();

        return Rankscore;
    }

    public static String parseMetaData(Document doc){
        Elements elements = doc.getElementsByTag("meta");
        StringBuilder metaBuilder = new StringBuilder();
        for(Element r : elements) {
            if (r.attr("name").contains("Description")) {
                metaBuilder.append(r.attr("content"));

            } else if (r.attr("property").contains("Description")) {
                metaBuilder.append(r.attr("content"));

            }else if (r.attr("name").contains("description")) {
                metaBuilder.append(r.attr("content"));

            }else{
                logger.info("None of the meta tags matched !! "+ ChildPageExtractionService.class.getName());
            }
        }
        return metaBuilder.length() > 0 ? metaBuilder.toString() : "No Meta";
    }

    /*ll
     * Compile Time Overloaded Class.
     */

    public static void partialExtraction(Document document, final String seedUrl,final String rootURL) throws IOException {

        short Rankscore=0;
        /*
         * Perform Language Detection - Optional<T> - Chaining if the document text is null or empty
         * then try to send the entire body text as input.
         */
        String dominantLang = LanguageDetection.InitiateLang(document.body().text());
        logger.info("Dominant Language " + dominantLang + ","+ HomePageHTMLService.class.getName());

        Rankscore += dominantLang != null || !(dominantLang.isEmpty()) ? (short)10 : (short)5;

        String bodytext = TextNormalizer.Normalizer.getWords(document.body().text().trim());

        final int homepageLength = bodytext.length();
        Rankscore += homepageLength > 5000 ? (short)10 : (short)5;

        /*
         * Body Text has length of 3000 characters and less.
         */
        String neatBodyText = (homepageLength < 5000) ? bodytext : bodytext.substring(0,5000);
        MemoryNotifier.printRuntimeMemory();


        /**************************************************************************
         *  Keyword Frequency Counter
         **************************************************************************/
        String keywordsCount = KeywordAnalyser.keywordCount(neatBodyText);

        String title = document.getElementsByAttribute("title").text().trim();
        String normalizedTitle = TextNormalizer.Normalizer.getWords(title);

        if(normalizedTitle != null || !(normalizedTitle.isEmpty())){
            Rankscore += 10;
        }else{
            Rankscore += 5;
        }

        /*
         * Getting meta data Tags about the home page
         */
        String descriptionMetaData = parseMetaData(document);
        logger.info(" Meta Tag ----> " + descriptionMetaData +","+HomePageHTMLService.class.getName());

        Rankscore += descriptionMetaData != null || !(descriptionMetaData.isEmpty()) ? (short) 10 : (short) 5;

        // Open Graph Meta Tags Fetcher
        String ogmetadata = FacebookOpenGraphMetaExtractor.extractFacebookOGData(document);
        logger.info(ogmetadata + "," + HomePageHTMLService.class.getName());

        Rankscore += ogmetadata != null || !(ogmetadata.isEmpty()) ? (short) 10 : (short) 5;

        /*
         * The highest Order priority is h1 > h2 > h3 > h4 > h5 > h6
         * Working on getting heading tags.
         */
        String[] headings = {"h1","h2","h3","h4","h5","h6"};
        String heading=null;
        for(String str: headings){
            heading = document.body().getElementsByTag("h1").text();
            if(heading == null){
                continue;
            }else{
                logger.info("Heading Tags - Order of Priority - Break Happens if anyone of those h tags exists h1 > h2 > h3 > h4 > h5 > h6 --> " +heading + " ----> classname " + HomePageHTMLService.class.getName());
                break;
            }
        }

        Rankscore += (heading != null || !(heading.isEmpty())) ? (short) 10 : (short) 5;

        /* - Change Detection Algorithm
         * Calculate Hash Of Text - MD5 Hash. That takes in Hash of data
         * and generates fixed length value. More Infomation here -
         * https://docs.oracle.com/javase/8/docs/api/java/security/MessageDigest.html
         */
        MemoryNotifier.printRuntimeMemory();

        String hashCode = FacebookOpenGraphMetaExtractor.calculateHash(neatBodyText);
        logger.info(" Generating HashCode - Text Content " + hashCode  + ", " + HomePageHTMLService.class.getName());

        Rankscore += hashCode != null || !(hashCode.isEmpty()) ? (short) 10 : (short) 5;

        Boolean duplicateCheck = DuplicateFinder.submitForDuplicatesCheck(hashCode);
        logger.info("Result of Duplicate Check with MD5 algorithm ---> " + duplicateCheck);

        if(duplicateCheck){
            String str = "Has duplicates within the 16 Pages Crawled from the website.";
            logger.info(str + HomePageHTMLService.class.getName());
        }
        MemoryNotifier.printRuntimeMemory();

        //Write to the database.
        Connection childDbConnection = DaoManager.connectJDBCAuroraCloudMySQL();
        if(childDbConnection != null) {

            AuroraInsertData.insertChildWebPageData(childDbConnection,rootURL,seedUrl,dominantLang,
                    neatBodyText,homepageLength,keywordsCount,normalizedTitle,descriptionMetaData,ogmetadata,heading,
                    hashCode,duplicateCheck.toString(),Rankscore);
        }

        /*
         * If the program crashes or is force killed, exit or shutdown hooks would not be fired.
         * A graceful exit would allow the program to finalize writing files, or closing connections, etc.
         */
        if(CrawlContract.isShutDown.get()){
            OnExitStrategy.deleteFiles();
            System.exit(0);
        }
    }

   /* public static void connectJDBCToAWSEC2() {

        System.out.println("----MySQL JDBC Connection Testing -------");

        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("Where is your MySQL JDBC Driver?");
            e.printStackTrace();
            return;
        }

        System.out.println("MySQL JDBC Driver Registered!");
        Connection connection = null;

        try {
            connection = DriverManager.
                    getConnection("jdbc:mysql://" + PUBLIC_DNS + ":3306/dbTest", "remoteu", "password");
        } catch (SQLException e) {
            System.out.println("Connection Failed!:\n" + e.getMessage());
        }

        if (connection != null) {
            System.out.println("SUCCESS!!!! You made it, take control     your database now!");
        } else {
            System.out.println("FAILURE! Failed to make connection!");
        }

    }*/

}
