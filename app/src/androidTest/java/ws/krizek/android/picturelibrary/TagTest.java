package ws.krizek.android.picturelibrary;

import junit.framework.TestCase;

import ws.krizek.android.picturelibrary.data.Tag;

/**
 * Created by sharp on 3.1.15.
 */
public class TagTest extends TestCase {
    public TagTest(String testName) {
        super(testName);
    }

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testGetTagEquals() {
        Tag t1 = Tag.getTag("t1");
        Tag t2 = Tag.getTag("t1");

        assertEquals(t1, t2);
    }
}
