package shapez;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author MengLeiFudge
 */
public class Test {
    private static final Logger logger = LoggerFactory.getLogger(Test.class);

    public void process() {
        logger.trace("trace");
        logger.debug("debug");
        logger.info("info");
        logger.warn("warn");
        logger.error("error");
    }

}
