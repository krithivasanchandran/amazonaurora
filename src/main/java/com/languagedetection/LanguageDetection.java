package com.languagedetection;

import amazonaurora.core.parser.JsoupDomService;
import com.optimaize.langdetect.DetectedLanguage;
import com.optimaize.langdetect.LanguageDetector;
import com.optimaize.langdetect.LanguageDetectorBuilder;
import com.optimaize.langdetect.ngram.NgramExtractors;
import com.optimaize.langdetect.profiles.LanguageProfile;
import com.optimaize.langdetect.profiles.LanguageProfileReader;
import common.aurora.GetTimeStamp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

public class LanguageDetection {

    private static Logger logger = LoggerFactory.getLogger(LanguageDetection.class);

    public static String InitiateLang(String bodyText){

        logger.info("LanguageDetection.java initializing the constructor");
        try {
            /*
             * Constructor Observable Design Pattern
             */
            LanguageDetector langDetect = warmUp();
            return coreDetection(bodyText,langDetect);

        } catch (IOException e) {
            logger.warn(e.getMessage() + "LanguageDetection.java inside LanguageDetection constructor");
            logger.warn("Error creating Language Detector with profiles built in ");
        }
        return "Failed to Perform Language Detection";
    }

    private static LanguageDetector warmUp() throws IOException {

        //Using BuiltIn Profiles to reduce RAM memory

        List<LanguageProfile> languageProfiles = new LanguageProfileReader().readAllBuiltIn();
        LanguageDetector languageDetector = LanguageDetectorBuilder.create(NgramExtractors.standard())
                .withProfiles(languageProfiles)
                .build();
        return Optional.of(languageDetector).get();
    }

    /*
     * coreDetection - Find the Dominant Language if the home page contains
     * more than one language - Example - 80% English - 20% Polish
     * return English as the Dominant language
     */

    private static String coreDetection(String bodyText,LanguageDetector langDetect) throws IOException {

        List<DetectedLanguage> getProbabilityList = langDetect.getProbabilities(bodyText);
        Collections.sort(getProbabilityList);

        /*
        * Extract the Highest Probability Dominant Language - Sort the Collections and return the highest
        * Dominant Language of the Webpage
         */
        try {
            DetectedLanguage detectedDominantLanguage = getProbabilityList.get(getProbabilityList.size() - 1);
            double dominantProbability = detectedDominantLanguage.getProbability();
            logger.info("Dominant Language Probability is " + dominantProbability + " TimeStamp:: " + GetTimeStamp.getCurrentTimeStamp());
            String dominantRawLanguage = detectedDominantLanguage.getLocale().getLanguage();

            Language languages = Language.valueOf(dominantRawLanguage);
            logger.info("Get the Full Text Language is :: " + languages.getFullTextLanguage());

            return languages.getFullTextLanguage();


        }catch(Exception ex){
            ex.getMessage();
        }
        return "Not Classified Language";
    }



}
