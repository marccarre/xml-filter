package com.carmatechnologies.utilities.xml;

import com.google.common.io.Resources;
import org.junit.Ignore;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;

import static com.carmatechnologies.utilities.xml.TestingUtilities.streamFor;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

public class XmlStreamFilterCliFactoryTest {

    private static final String USAGE = "usage: java com.carmatechnologies.utilities.xml.XmlStreamFilter [-e <arg>]\n" +
            "       [-f <arg>] [-h] [-i] [-s <arg>] [-t <arg>] [-v]\n" +
            " -e,--element <arg>     Local name of the XML element to detect in the\n" +
            "                        input XML stream and, potentially, select.\n" +
            "                        Example: \"book\".\n" +
            " -f,--file <arg>        [Optional] Define white-list of patterns from\n" +
            "                        file, one per line. XML elements will be selected\n" +
            "                        if the value returned by the provided XPath query\n" +
            "                        is in the white-list. Example: if file contains\n" +
            "                        \"magician\\\\nxquery\\\\n\" and filter is:\n" +
            "                        \"//book/tags/tag/text()\", then \"book\" elements\n" +
            "                        with either a \"magician\" or \"xquery\" tag will be\n" +
            "                        returned.\n" +
            " -h,--help              Print this, i.e. a usage message briefly\n" +
            "                        summarizing the command-line options, then exit.\n" +
            " -i,--indent            [Optional] Indent returned XML elements, for\n" +
            "                        potentially better readability. Default: keep the\n" +
            "                        same formatting and indentation as in the input\n" +
            "                        XML stream.\n" +
            " -s,--select <arg>      XPath query used to select XML elements among the\n" +
            "                        ones detected. Example: \"//book/tags/tag[text() =\n" +
            "                        'magician']\" will select all \"book\" elements with\n" +
            "                        \"magician\" as a \"tag\".\n" +
            " -t,--transform <arg>   [Optional] XPath expression used to transform the\n" +
            "                        selected XML elements. Example:\n" +
            "                        \"//book/title/text()\". Default: the entire XML\n" +
            "                        element will be returned.\n" +
            " -v,--version           Print \"1.0\", i.e. the version number of\n" +
            "                        com.carmatechnologies.utilities.xml.XmlStreamFilte\n" +
            "                        r to the standard output stream. This version\n" +
            "                        number should be included in all bug reports.\n";

    private final OutputStream stdOut = new ByteArrayOutputStream();
    private final OutputStream stdErr = new ByteArrayOutputStream();
    private final PrintWriter stdOutWriter = new PrintWriter(stdOut);
    private final PrintWriter stdErrWriter = new PrintWriter(stdErr);
    private final XmlStreamFilterCliFactory factory = new XmlStreamFilterCliFactory(stdOutWriter, stdErrWriter);

    @Test
    public void simpleFilterUsingXPathToSelectPrintsSelectedXmlToStandardOutput() {
        StreamFilter filter = factory.newStreamFilter(new String[]{"-e", "book", "-s", "//book/tags/tag[text() = 'magician']"});
        assertThat(filter, is(not(nullValue())));
        assertThat(filter, is(instanceOf(XmlStreamFilter.class)));

        InputStream in = streamFor("/books.xml");
        filter.filter(in, stdOut);

        flush();
        assertThat(stdOut.toString(), is("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>" +
                "<book category=\"CHILDREN\">\n" +
                "        <title lang=\"en\">Harry Potter</title>\n" +
                "        <author>J K. Rowling</author>\n" +
                "        <year>2005</year>\n" +
                "        <price>29.99</price>\n" +
                "        <tags>\n" +
                "            <tag>fantasy</tag>\n" +
                "            <tag>magician</tag>\n" +
                "        </tags>\n" +
                "    </book>"));
        assertThat(stdErr.toString(), is(""));
    }

    @Test
    @Ignore // Transformer currently reads too much when XML isn't indented.
    public void simpleFilterUsingXPathToSelectPrintsSelectedXmlToStandardOutputWithIndentation() {
        StreamFilter filter = factory.newStreamFilter(new String[]{"-e", "book", "-s", "//book/tags/tag[text() = 'magician']", "-i"});
        assertThat(filter, is(not(nullValue())));
        assertThat(filter, is(instanceOf(XmlStreamFilter.class)));

        InputStream in = streamFor("/books_no_indentation.xml");
        filter.filter(in, stdOut);

        flush();
        assertThat(stdOut.toString(), is("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n" +
                "<book category=\"CHILDREN\">\n" +
                "    <title lang=\"en\">Harry Potter</title>\n" +
                "    <author>J K. Rowling</author>\n" +
                "    <year>2005</year>\n" +
                "    <price>29.99</price>\n" +
                "    <tags>\n" +
                "        <tag>fantasy</tag>\n" +
                "        <tag>magician</tag>\n" +
                "    </tags>\n" +
                "</book>\n"));
        assertThat(stdErr.toString(), is(""));
    }

    @Test
    public void advancedFilterUsingXPathToBothSelectAndTransformAndUsingWhiteListFromFilePrintsSelectedXmlToStandardOutput() {
        StreamFilter filter = factory.newStreamFilter(new String[]{"-e", "book", "-s", "//book/tags/tag/text()", "-t", "//book/title/text()", "-f", Resources.getResource("white_list.txt").getFile()});
        assertThat(filter, is(not(nullValue())));
        assertThat(filter, is(instanceOf(XmlStreamFilter.class)));

        InputStream in = streamFor("/books.xml");
        filter.filter(in, stdOut);

        flush();
        assertThat(stdOut.toString(), is("XQuery Kick Start\nLearning XML\n"));
        assertThat(stdErr.toString(), is(""));
    }

    @Test
    public void printHelpShortArgument() {
        StreamFilter filter = factory.newStreamFilter(new String[]{"-h"});
        assertThat(filter, is(not(nullValue())));
        assertThat(filter, is(instanceOf(NoOpStreamFilter.class)));
        flush();
        assertThat(stdOut.toString(), is(USAGE));
        assertThat(stdErr.toString(), is(""));
    }

    @Test
    public void printHelpLongArgument() {
        StreamFilter filter = factory.newStreamFilter(new String[]{"--help"});
        assertThat(filter, is(not(nullValue())));
        assertThat(filter, is(instanceOf(NoOpStreamFilter.class)));
        flush();
        assertThat(stdOut.toString(), is(USAGE));
        assertThat(stdErr.toString(), is(""));
    }

    @Test
    public void printVersionShortArgument() {
        StreamFilter filter = factory.newStreamFilter(new String[]{"-v"});
        assertThat(filter, is(not(nullValue())));
        assertThat(filter, is(instanceOf(NoOpStreamFilter.class)));
        flush();
        assertThat(stdOut.toString(), is("1.0\n"));
        assertThat(stdErr.toString(), is(""));
    }

    @Test
    public void printVersionLongArgument() {
        StreamFilter filter = factory.newStreamFilter(new String[]{"--version"});
        assertThat(filter, is(not(nullValue())));
        assertThat(filter, is(instanceOf(NoOpStreamFilter.class)));
        flush();
        assertThat(stdOut.toString(), is("1.0\n"));
        assertThat(stdErr.toString(), is(""));
    }

    @Test
    public void notProvidingAnyArgumentPrintsErrorMessageAndUsage() {
        StreamFilter filter = factory.newStreamFilter(new String[]{});
        assertThat(filter, is(not(nullValue())));
        assertThat(filter, is(instanceOf(NoOpStreamFilter.class)));
        flush();
        assertThat(stdOut.toString(), is(""));
        assertThat(stdErr.toString(), is("Invalid command line arguments: please provide a value for argument \"element\".\n" + USAGE));
    }

    @Test
    public void notProvidingValueToElementArgumentPrintsErrorMessageAndUsage() {
        StreamFilter filter = factory.newStreamFilter(new String[]{"-e"});
        assertThat(filter, is(not(nullValue())));
        assertThat(filter, is(instanceOf(NoOpStreamFilter.class)));
        flush();
        assertThat(stdOut.toString(), is(""));
        assertThat(stdErr.toString(), is("Missing argument for option: e\n" + USAGE));
    }

    @Test
    public void notProvidingSelectQueryPrintsErrorMessageAndUsage() {
        StreamFilter filter = factory.newStreamFilter(new String[]{"-e", "book"});
        assertThat(filter, is(not(nullValue())));
        assertThat(filter, is(instanceOf(NoOpStreamFilter.class)));
        flush();
        assertThat(stdOut.toString(), is(""));
        assertThat(stdErr.toString(), is("Invalid command line arguments: please provide a value for argument \"select\".\n" + USAGE));
    }

    @Test
    public void providingInvalidSelectXPathQueryPrintsErrorMessageAndUsage() {
        StreamFilter filter = factory.newStreamFilter(new String[]{"-e", "book", "-s", "~~~clearly not XPath~~"});
        assertThat(filter, is(not(nullValue())));
        assertThat(filter, is(instanceOf(NoOpStreamFilter.class)));
        flush();
        assertThat(stdOut.toString(), is(""));
        assertThat(stdErr.toString(), is("Invalid command line arguments: Invalid XPath expression \"~~~clearly not XPath~~\" for argument \"select\". Original error: \n" +
                "javax.xml.transform.TransformerException: A location path was expected, but the following token was encountered:  ~~~clearly\n" + USAGE));
    }

    @Test
    public void providingInvalidSelectXPathQueryAlongSideValidWhiteListFilePrintsErrorMessageAndUsage() {
        StreamFilter filter = factory.newStreamFilter(new String[]{"-e", "book", "-s", "~~~clearly not XPath~~", "-f", Resources.getResource("white_list.txt").getFile()});
        assertThat(filter, is(not(nullValue())));
        assertThat(filter, is(instanceOf(NoOpStreamFilter.class)));
        flush();
        assertThat(stdOut.toString(), is(""));
        assertThat(stdErr.toString(), is("Invalid command line arguments: Invalid XPath expression \"~~~clearly not XPath~~\" for argument \"select\". Original error: \n" +
                "javax.xml.transform.TransformerException: A location path was expected, but the following token was encountered:  ~~~clearly\n" + USAGE));
    }

    @Test
    public void providingInvalidSelectXPathTransformQueryPrintsErrorMessageAndUsage() {
        StreamFilter filter = factory.newStreamFilter(new String[]{"-e", "book", "-s", "//book/tags/tag[text() = 'magician']", "-t", "~~~clearly not XPath~~"});
        assertThat(filter, is(not(nullValue())));
        assertThat(filter, is(instanceOf(NoOpStreamFilter.class)));
        flush();
        assertThat(stdOut.toString(), is(""));
        assertThat(stdErr.toString(), is("Invalid command line arguments: Invalid XPath expression \"~~~clearly not XPath~~\" for argument \"transform\". Original error: \n" +
                "javax.xml.transform.TransformerException: A location path was expected, but the following token was encountered:  ~~~clearly\n" + USAGE));
    }

    @Test
    public void providingInvalidWhiteListFilePrintsErrorMessageAndUsage() {
        StreamFilter filter = factory.newStreamFilter(new String[]{"-e", "book", "-s", "//book/tags/tag/text()", "-f", "~/non_existant_white_list.txt"});
        assertThat(filter, is(not(nullValue())));
        assertThat(filter, is(instanceOf(NoOpStreamFilter.class)));
        flush();
        assertThat(stdOut.toString(), is(""));
        assertThat(stdErr.toString(), is("Invalid command line arguments: Failed to read white-list of patterns from file: ~/non_existant_white_list.txt. Original error: \n" +
                "~/non_existant_white_list.txt (No such file or directory)\n" + USAGE));
    }

    private void flush() {
        stdOutWriter.flush();
        stdErrWriter.flush();
    }
}
