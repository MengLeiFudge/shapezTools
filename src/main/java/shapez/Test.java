package shapez;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import shapez.calculate.SimpleShape;

/**
 * @author MengLeiFudge
 */
public class Test {
    private static final Logger logger = LoggerFactory.getLogger(Test.class);

    public void process() {
        long time1 = System.nanoTime();
        for (int kk = 0; kk < 1000; kk++) {
            for (int i = 0; i < 65535; i++) {
                id = i;
                //  leftSideBC();
                // assert leftSideAX().equals(leftSideBC());
            }
        }
        long time2 = System.nanoTime();
        for (int kk = 0; kk < 1000; kk++) {
            for (int i = 0; i < 65535; i++) {
                id = i;
                //leftSideAX();
            }
        }
        long time3 = System.nanoTime();
        for (int kk = 0; kk < 1000; kk++) {
            for (int i = 0; i < 65535; i++) {
                id = i;
                // leftSideBC();
            }
        }
        long time4 = System.nanoTime();
        for (int kk = 0; kk < 1000; kk++) {
            for (int i = 0; i < 65535; i++) {
                id = i;
                //leftSideAX();
            }
        }
        long time5 = System.nanoTime();
        logger.info("leftSide1: " + (time2 - time1));
        logger.info("leftSide2: " + (time3 - time2));
        logger.info("leftSide1: " + (time4 - time3));
        logger.info("leftSide2: " + (time5 - time4));
    }

    private int id;


}
