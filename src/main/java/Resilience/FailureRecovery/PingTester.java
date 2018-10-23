package Resilience.FailureRecovery;

import amazonaurora.core.parser.HttpCore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class PingTester {

    private PingTester(){}

    private static Logger logger = LoggerFactory.getLogger(PingTester.class);


    public static boolean performWebsiteTest(String root) {
        try {
            if (!HttpCore.pingTest(root)) {
                logger.error(" FAILED PING TEST !!! CRITICAL WEBSITE ISNT UP AND RUNNING");
                System.out.println(" FAILED PING TEST !!! CRITICAL WEBSITE ISNT UP AND RUNNING ");
                return true;
            }
        } catch (IOException io) {
            logger.error(io.getMessage() + "Failed Ping Test . Make Sure the Website is up and running");
        }
        return false;
    }
}
