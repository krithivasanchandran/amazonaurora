package AuroraMySql.DatabaseManager;

import amazonaurora.core.parser.HomePageHTMLService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class AuroraInsertData {
    private static Logger logger = LoggerFactory.getLogger(AuroraInsertData.class);

    private AuroraInsertData(){}

    public static void insertChildWebPageData(Connection childDbConnection, String rooturl, String childpageurl, String dominantLang,
                                                                String neatBodyText,int bodycount, String keywordsCount, String normalizedTitle,String descriptionMetaData,
                                                                String ogmetadata, String heading, String hashCode,String duplicateCheck,
                                                                short Rankscore) {

        Statement statement = null;

        try {

            logger.info("Entering into insertChildWebPageData");

            statement = childDbConnection.createStatement();
            StringBuilder insertQueryBuilder = new StringBuilder();
            insertQueryBuilder.append("INSERT INTO childpagedata (homepageurl,childpageurl,dominantLanguage," +
                    "homepageLength,keywordscount,title,metadata," +
                    "opengraphMetadata,headingTag,hashcode,hasduplicates," +
                    "bodytext,rankscore) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)");

            PreparedStatement preparedStmt = childDbConnection.prepareStatement(insertQueryBuilder.toString());

            /*
             * Emptying the StringBuilder to save memory and to garbage collect it.
             */
            insertQueryBuilder.setLength(0);

            preparedStmt.setString(1, rooturl);
            preparedStmt.setString(2, childpageurl);
            preparedStmt.setString(3, dominantLang);
            preparedStmt.setInt(4, bodycount);
            preparedStmt.setString(5, keywordsCount);
            preparedStmt.setString(6, normalizedTitle);
            preparedStmt.setString(7, descriptionMetaData);
            preparedStmt.setString(8, ogmetadata);
            preparedStmt.setString(9, heading);
            preparedStmt.setString(10, hashCode);
            preparedStmt.setString(11, duplicateCheck);
            preparedStmt.setString(12, neatBodyText);
            preparedStmt.setInt(13, Rankscore);

            // execute the preparedstatement
            preparedStmt.execute();

            /*
             * Closing the handler connections to prevent the memory leak.
             */
            childDbConnection.close();
            preparedStmt.clearParameters();

        } catch (SQLException e) {
            logger.error(e.getMessage());
            logger.error(AuroraInsertData.class.getName());
        }
    }
}
