package Duplicate.metadata;

import common.aurora.LinkExtractor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.concurrent.atomic.AtomicInteger;

public final class OnExitStrategy {

    /*
     * Security FrequencyCounter Check - Should Execute Only Once
     * During LifeCycle of the Class.
     */
    private static final AtomicInteger securityCounter = new AtomicInteger(0);

    private OnExitStrategy(){}

    private static final String[] filenames = {"/home/krithivasan/successLinks.txt" ,
                                                    "/home/krithivasan/failureLinks.txt",
                                                            "/home/krithivasan/childLinks.ser"};

    private static Logger logger = LoggerFactory.getLogger(OnExitStrategy.class);


    /**************************************
     * Exit Strategy
     *
     * Destroy Files - That was persisted
     * for failure recovery .
     * Destroy as the files get accumulated
     * due to more space.
     * Getting Ready for Next Head Seed URL
     * from the queue and start crawling
     * the home page and child URL's
     *
     ***************************************/

    public static void deleteFiles(){

        securityCounter.getAndIncrement();

        if(securityCounter.get() == 1){
            try{

                for(int k=0;k<filenames.length; k++){

                    File file = new File(filenames[k]);
                    logger.info("Exit Strategy ---> Does the file exists ---> " + file.exists());
                    if(file.exists()){
                        String s = file.delete() ? "File is deleted" : "Delete Operation is failed";
                        logger.info(s + filenames[k] + OnExitStrategy.class.getName());
                    }else{
                        logger.info("Sorry Exit Delete File Failed !! File at this path " + filenames[k] + " Failed ");
                    }
                }
            }catch(Exception e){
                logger.info(e.getMessage());
                logger.info(OnExitStrategy.class.getName());
            }
        }
    }

}
