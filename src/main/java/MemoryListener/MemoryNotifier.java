package MemoryListener;

import Resilience.FailureRecovery.HotRestartManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
 * Memory Used Up Space Calculator in MB. Used for log level analysis.
 * PrintRuntimeMemory() prints the free RAM space left in MegaBytes.
 * Our Jar runs in less than 900 MB Ram.
 */

public class MemoryNotifier {

    private static Logger logger = LoggerFactory.getLogger(MemoryNotifier.class);

    private static final long MEGABYTE = 1024L * 1024L;

    private MemoryNotifier(){}

    public static void printRuntimeMemory() {

        final Runtime runtime = Runtime.getRuntime();

        int numberOfProcessors = runtime.availableProcessors();
        logger.info("Number of processors available to this JVM: " + numberOfProcessors);

        runtime.runFinalization();

        long memory = runtime.totalMemory() - runtime.freeMemory();

        logger.info("Used memory is bytes: " + memory);
        logger.info("Used memory is megabytes: "+ bytesToMegabytes(memory));
    }

    public static long bytesToMegabytes(long bytes) {
        return bytes / MEGABYTE;
    }

}
