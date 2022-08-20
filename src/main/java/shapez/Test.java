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
                leftSideBC();
                assert leftSideAX().equals(leftSideBC());
            }
        }
        long time2 = System.nanoTime();
        for (int kk = 0; kk < 1000; kk++) {
            for (int i = 0; i < 65535; i++) {
                id = i;
                leftSideAX();
            }
        }
        long time3 = System.nanoTime();
        for (int kk = 0; kk < 1000; kk++) {
            for (int i = 0; i < 65535; i++) {
                id = i;
                leftSideBC();
            }
        }
        long time4 = System.nanoTime();
        for (int kk = 0; kk < 1000; kk++) {
            for (int i = 0; i < 65535; i++) {
                id = i;
                leftSideAX();
            }
        }
        long time5 = System.nanoTime();
        logger.info("leftSide1: " + (time2 - time1));
        logger.info("leftSide2: " + (time3 - time2));
        logger.info("leftSide1: " + (time4 - time3));
        logger.info("leftSide2: " + (time5 - time4));
    }

    private int id;

    private SimpleShape leftSideBC() {
        //0xCCCC: 1100 1100 1100 1100，取出左半部分
        int id = this.id;
        int ret = 0;
        //层筛选器
        int filter = 0xC;
        while (id != 0) {
            //取出指定层
            int temp = id & filter;
            if (temp > 0) {
                //该层非空，该层直接添加到ret里面，层筛选器上移
                ret |= temp;
                filter <<= 4;
            } else {
                //该层空，原图形砍掉底层
                id >>= 4;
            }
        }
        return new SimpleShape(ret);
    }

    private SimpleShape leftSideAX() {
        int ret = 0;
        //层筛选器
        int filter = 0xC;
        //空层数目
        int q = 0;
        for (int i = 0; i < 4; i++) {
            //取出指定层
            int temp = id & filter;
            if (temp > 0) {
                //该层非空，该层右移空层数后添加到ret里面
                ret |= temp >> q;
            } else {
                //该层空，空层计数增加
                q += 4;
            }
            //层筛选器上移
            filter <<= 4;
        }
        return new SimpleShape(ret);
    }

}
