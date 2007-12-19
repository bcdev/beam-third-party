package org.esa.beam.processor;

import java.io.File;

/**
 * Created by Marco Peters.
 *
 * @author Marco Peters
 * @version $Revision:$ $Date:$
 */
public class MerisVegTestConfig {
    public static  String testFileBaseDirPath = "src/test/resources/org/esa/beam/processor/";

    static {
        if (!new File(testFileBaseDirPath).exists()) {
            testFileBaseDirPath = "beam-meris-veg/" + testFileBaseDirPath;
            if (!new File(testFileBaseDirPath).exists()) {
                throw new IllegalStateException("not found: testFileBaseDirPath = " + testFileBaseDirPath);
            }
        }

    }

}
