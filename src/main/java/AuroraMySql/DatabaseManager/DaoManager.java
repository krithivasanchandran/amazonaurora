package AuroraMySql.DatabaseManager;


import amazonaurora.core.parser.HomePageHTMLService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;

public class DaoManager {

    private static final String hostname = "crawldb.c9nylpylkekg.us-east-1.rds.amazonaws.com";
    private static final String port = "3306";
    private static final String dbName = "crawldb";
    private static final String userName = "krithivasan";
    private static final String password = "Springboot1234";

    private static final String jdbcUrl = "jdbc:mysql://" + hostname + ":" +
            port + "/" + dbName + "?user=" + userName + "&password=" + password;

    private static Logger logger = LoggerFactory.getLogger(DaoManager.class);

    private DaoManager() {
    }

    /*
     * Changes brought to the driver class
     * Loading class `com.mysql.jdbc.Driver'. This is deprecated. The new driver class is
     * `com.mysql.cj.jdbc.Driver'. The driver is automatically registered via the SPI and manual
     * loading of the driver class is generally unnecessary.
     *
     */
    public static Connection connectJDBCAuroraCloudMySQL() {

        try {
            System.out.println("Loading driver...");
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("Driver loaded!");

        } catch (ClassNotFoundException e) {
            logger.info("Trouble Initializing the DB Connection Manager");
            logger.info(e.getMessage());
            logger.info(e.getLocalizedMessage());
        }

        logger.info("Successfull MySQL JDBC Driver Registered!");
        Connection connection = null;

        try {
            connection = DriverManager.
                    getConnection(jdbcUrl);
        } catch (SQLException e) {
            logger.info("Connection to Localhost MySQL Instance Failed!:\n" + e.getMessage());
        }

        return connection;
    }

    /*public static void runTestQuery(Connection conn) {
        Statement statement = null;
        try {

            System.out.println("Creating statement...");
            statement = conn.createStatement();
            String sql;
            sql = "SELECT * FROM homepagedata";
            ResultSet rs = statement.executeQuery(sql);
            StringBuilder response = new StringBuilder();
            //STEP 5: Extract data from result set
            while (rs.next()) {
                //Retrieve by column name
                response.append(rs.getInt("crawl_id"));
                response.append(rs.getString("homepageurl"));
                response.append(rs.getString("dominantLanguage"));
                response.append(rs.getString("homepageLength"));
                response.append(rs.getString("keywordscount"));
                response.append(rs.getString("title"));
                response.append(rs.getString("metadata"));

                response.append(rs.getString("opengraphMetadata"));
                response.append(rs.getString("headingTag"));
                response.append(rs.getString("hashcode"));

                response.append(rs.getString("hasduplicates"));
                response.append(rs.getString("robotsExist"));
                response.append(rs.getString("isSitemapExist"));

                response.append(rs.getInt("outgoinglinks"));
                response.append(rs.getString("bodytext"));
                response.append(rs.getString("contactNumbers"));

                response.append(rs.getString("emailList"));
                response.append(rs.getString("addressExtracted"));
                response.append(rs.getInt("rankscore"));
                //Display values
                logger.info(" ******* DB Values ********" + response.toString());


            }
            response.setLength(0);
            //STEP 6: Clean-up environment
            rs.close();
            statement.close();
            conn.close();
        } catch (SQLException se) {
            //Handle errors for JDBC
            se.printStackTrace();
        } catch (Exception e) {
            //Handle errors for Class.forName
            e.printStackTrace();
        } finally {
            //finally block used to close resources
            try {
                if (statement != null)
                    statement.close();
            } catch (SQLException se2) {
            }// nothing we can do
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }//end finally try
        }//end try
    }
*/

    public static void insertHomePageWebDataValuesToAuroraMySQL(Connection conn, String homepageurl, String address,
                                                                String emailList, String phone, String bodytext,
                                                                int totalOutLinks, String isSitemap, String isrobots,
                                                                short Rankscore) {

        Statement statement = null;
        try {

            logger.info("Entering into insertHomePageWebDataValue");
            statement = conn.createStatement();
            StringBuilder insertQueryBuilder = new StringBuilder();
            insertQueryBuilder.append("INSERT INTO homepagedata (homepageurl,dominantLanguage,homepageLength,keywordscount,title," +
                    "metadata,opengraphMetadata,headingTag,hashcode,hasduplicates,robotsExist,isSitemapExist," +
                    "outgoinglinks,bodytext,contactNumbers,emailList,addressExtracted,rankscore) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");

            PreparedStatement preparedStmt = conn.prepareStatement(insertQueryBuilder.toString());

            /*
             * Emptying the StringBuilder to save memory and to garbage collect it.
             */
            insertQueryBuilder.setLength(0);

            preparedStmt.setString(1, homepageurl);
            preparedStmt.setString(2, HomePageHTMLService.avoidingObjects.get(0));
            preparedStmt.setInt(3, Integer.parseInt(HomePageHTMLService.avoidingObjects.get(1)));
            preparedStmt.setString(4, HomePageHTMLService.avoidingObjects.get(3));
            preparedStmt.setString(5, HomePageHTMLService.avoidingObjects.get(4));
            preparedStmt.setString(6, HomePageHTMLService.avoidingObjects.get(5));
            preparedStmt.setString(7, HomePageHTMLService.avoidingObjects.get(6));
            preparedStmt.setString(8, HomePageHTMLService.avoidingObjects.get(7));
            preparedStmt.setString(9, HomePageHTMLService.avoidingObjects.get(8));
            preparedStmt.setString(10, HomePageHTMLService.avoidingObjects.get(9));
            preparedStmt.setString(11, isrobots);
            preparedStmt.setString(12, isSitemap);
            preparedStmt.setInt(13, totalOutLinks);
            preparedStmt.setString(14, HomePageHTMLService.avoidingObjects.get(2));
            preparedStmt.setString(15, phone);
            preparedStmt.setString(16, emailList);
            preparedStmt.setString(17, address);
            preparedStmt.setInt(18, Rankscore);

            // execute the preparedstatement
            preparedStmt.execute();

            conn.close();
        } catch (SQLException e) {
            logger.error(e.getMessage());
            logger.error(DaoManager.class.getName());
        }
    }
}