package de.saumya.mojo.ruby;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.codehaus.plexus.util.FileUtils;

import de.saumya.mojo.ruby.script.GemScriptFactory;
import de.saumya.mojo.ruby.script.ScriptFactory;

public class EmbeddedLauncherTest extends TestCase {

    public EmbeddedLauncherTest(final String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite(EmbeddedLauncherTest.class);
    }

    private ScriptFactory factory;
    private String        expected;
    private File          home;
    private File          path;

    @Override
    public void setUp() throws Exception {
        final List<String> classpathElements = new ArrayList<String>();
        classpathElements.add(".");

        // setup local rubygems repository
        this.home = new File("target");
        this.path = new File("target/rubygems");
        this.path.mkdirs();
        this.expected = "onetwothree\n" + this.home.getAbsolutePath() + "\n"
                + this.path.getAbsolutePath() + "\n";

        final Logger logger = new NoopLogger();
        // no classrealm
        this.factory = new GemScriptFactory(logger,
                null,
                new File(""),
                classpathElements,
                false,
                this.home,
                this.path);

        // for eclipse
        final File output = new File("target/test-classes");
        if (!output.exists()) {
            FileUtils.copyDirectory(new File("src/test/resources"), output);
        }

    }

    public void testExecution() throws Exception {
        this.factory.newArguments()
                .addArg("target/test-classes/test.rb")
                .addArg("one")
                .addArg("two")
                .addArg("three")
                .execute();
        File f = new File("target/test-classes/test.rb.txt");
        if (!f.exists()) {
            // in this case GEM_HOME was set in system environment
            f = new File("target/test-classes/test.rb-gem.txt");
        }
        assertEquals("onetwothree", FileUtils.fileRead(f)
                .replace("\n", "--n--")
                .replaceFirst("--n--.*", ""));
    }

    public void testExecutionInTarget() throws Exception {
        final File f;
        if (System.getenv("GEM_HOME") != null) {
            f = new File("target/test-classes/test.rb-gem.txt");
        }
        else {
            f = new File("target/test-classes/test.rb.txt");
        }

        f.delete();
        this.factory.newArguments()
                .addArg("test-classes/test.rb")
                .addArg("one")
                .addArg("two")
                .addArg("three")
                .executeIn(new File("target"));
        assertEquals("onetwothree", FileUtils.fileRead(f)
                .replace("\n", "--n--")
                .replaceFirst("--n--.*", ""));
    }

    public void testExecutionWithOutput() throws Exception {
        final File output = new File("target/test-classes/test_with_output.rb.txt");
        output.delete();
        this.factory.newArguments()
                .addArg("target/test-classes/test_with_output.rb")
                .addArg("one")
                .addArg("two")
                .addArg("three")
                .execute(output);
        assertEquals("onetwothree", FileUtils.fileRead(output)
                .replace("\n", "--n--")
                .replaceFirst("--n--.*", ""));
    }

    public void testExecutionWithOutputInTarget() throws Exception {
        final File output = new File("target/test-classes/test_with_output.rb.txt");
        output.delete();
        this.factory.newArguments()
                .addArg("test-classes/test_with_output.rb")
                .addArg("one")
                .addArg("two")
                .addArg("three")
                .executeIn(new File("target"), output);
        assertEquals("onetwothree", FileUtils.fileRead(output)
                .replace("\n", "--n--")
                .replaceFirst("--n--.*", ""));
    }

    public void testScript() throws Exception {
        this.factory.newScriptFromResource("test.rb")
                .addArg("one")
                .addArg("two")
                .addArg("three")
                .execute();
        assertEquals(this.expected,
                     FileUtils.fileRead("target/test-classes/test.rb-gem.txt"));
    }

    public void testScriptInTarget() throws Exception {
        this.factory.newScriptFromResource("test.rb")
                .addArg("one")
                .addArg("two")
                .addArg("three")
                .executeIn(new File("target"));
        assertEquals(this.expected,
                     FileUtils.fileRead("target/test-classes/test.rb-gem.txt"));
    }

    public void testScriptWithOutput() throws Exception {
        final File output = new File("target/test-classes/test_with_output.rb-gem.txt");
        output.delete();
        this.factory.newScriptFromResource("test_with_output.rb")
                .addArg("one")
                .addArg("two")
                .addArg("three")
                .execute(output);
        assertEquals(this.expected, FileUtils.fileRead(output));
    }

    public void testScriptWithOutputInTarget() throws Exception {
        final File output = new File("target/test-classes/test_with_output.rb-gem.txt");
        output.delete();
        this.factory.newScriptFromResource("test_with_output.rb")
                .addArg("one")
                .addArg("two")
                .addArg("three")
                .executeIn(new File("target"), output);
        assertEquals(this.expected, FileUtils.fileRead(output));
    }

    public void testSimpleScriptInTarget() throws Exception {
        final File output = new File("target/simple.txt");
        output.delete();
        this.factory.newScript("File.open('simple.txt', 'w') { |f| f.puts ARGV.join }")
                .addArg("one")
                .addArg("two")
                .addArg("three")
                .executeIn(new File("target"));
        assertEquals("onetwothree\n", FileUtils.fileRead(output));
    }

    public void testSimpleScriptWithOutputInTarget() throws Exception {
        final File output = new File("target/test-classes/simple.txt");
        this.factory.newScript("puts ARGV.join")
                .addArg("one")
                .addArg("two")
                .addArg("three")
                .executeIn(new File("target"), output);
        assertEquals("onetwothree\n", FileUtils.fileRead(output));
    }

    public void testGemHomeAndGemPath() throws Exception {
        final File output = new File("target/test-classes/gem.txt");
        this.factory.newScriptFromResource("META-INF/jruby.home/bin/gem")
                .addArg("env")
                .execute(output);
        final String[] lines = FileUtils.fileRead(output).split("\\n");
        int countDir = 0;
        int countHome = 0;
        int countPath = 0;
        for (final String line : lines) {
            if (line.contains("DIRECTORY: " + this.home.getAbsolutePath())) {
                countDir++;
            }
            if (line.contains(this.home.getAbsolutePath())
                    && !line.contains(this.path.getAbsolutePath())) {
                countHome++;
            }
            if (line.contains(this.path.getAbsolutePath())) {
                countPath++;
            }
        }
        assertEquals(2, countDir);
        assertEquals(3, countHome);
        assertEquals(1, countPath);
    }
}
