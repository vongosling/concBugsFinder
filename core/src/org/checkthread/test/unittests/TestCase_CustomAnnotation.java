package org.checkthread.test.unittests;

import java.util.ArrayList;
import junit.framework.TestCase;

import org.checkthread.main.ICheckThreadError;
import org.checkthread.test.target.customannotation.*;

public class TestCase_CustomAnnotation extends TestCase {
    protected void setUp() {}
    protected void tearDown() {}

    public void testList0() {       
        TestParseHandler handler = TestUtil.parseClassHelper(TestCustomAnnotation.class);
        ArrayList<ICheckThreadError> list = handler.getThreadPolicyErrors();

        // Verify 
        int actualValue = list.size();
        int expectedValue = 1;
        assertEquals(expectedValue,actualValue);
    }
}
