package com.languagedetection;

import common.aurora.FillerWords;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.stream.Stream;

public final class TextNormalizer {
    private TextNormalizer(){}

     public static class Normalizer{

        private Normalizer(){}{}

        private static final Logger LOGGER = LoggerFactory.getLogger(TextNormalizer.class);
        private static final String REGEX_SPACE = "\\s+";
        private static final String REGEX_ALPHANUM = "[^-\\dA-Za-z ]";

        public static String getWords(final String sentence) {

            String[] splitter = sentence.split(REGEX_SPACE);
            StringBuilder wordBuilder = new StringBuilder();

            for(String s: splitter){
                String s_l = s.toLowerCase().replaceAll(REGEX_ALPHANUM, "");

                /*
                 * Not a Filler Word.
                 */
                if(!FillerWords.match(s_l)){
                    wordBuilder.append(s_l).append(" ");
                }
            }
            LOGGER.info("Normalized Text is " + wordBuilder.toString() + TextNormalizer.class.getName());

        return wordBuilder.toString();
        }

    }
}
