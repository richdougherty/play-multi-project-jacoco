import static org.fest.assertions.Assertions.assertThat;
import static play.test.Helpers.contentAsString;
import static play.test.Helpers.contentType;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import org.junit.Test;

import play.mvc.Content;
import play.test.FakeApplication;
import play.test.Helpers;


/**
*
* Simple (JUnit) tests that can call all parts of a play app.
* If you are interested in mocking a whole application, see the wiki for more details.
*
*/
public class ApplicationTest {

    public static File relativeToBaseDir(String path) {
        String baseDir = System.getProperty("play.base.dir");
        if (baseDir == null) {
            throw new NullPointerException("The play.base.dir property must be set so that relative paths can be resolved.");
        }
        return new File(baseDir, path);
    }

    @Test
    public void simpleCheck() {
        int a = 1 + 1;
        assertThat(a).isEqualTo(2);
    }

    @Test
    public void renderTemplate() {
        Content html = views.html.index.render("Your new application is ready.");
        assertThat(contentType(html)).isEqualTo("text/html");
        assertThat(contentAsString(html)).contains("Your new application is ready.");
    }

    @Test
    public void workingDirectoryShouldBeConstant() {
        FakeApplication app = new FakeApplication(relativeToBaseDir("."),
                Helpers.class.getClassLoader(), new HashMap<String, String>(), new ArrayList<String>(), null);
        
        File testData = app.getWrappedApplication().getFile("./test/resources/test-data.txt");
        
        assertThat(testData).isFile();
    }

}
