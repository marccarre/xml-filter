package com.carmatechnologies.utilities.xml;

import com.google.common.io.Resources;
import org.junit.Test;

import javax.xml.stream.XMLStreamException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
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

    private static final String NEW_LINE = System.getProperty("line.separator");

    private static final String USAGE = "usage: java com.carmatechnologies.utilities.xml.XmlStreamFilter [-e <arg>]" + NEW_LINE +
            "       [-f <arg>] [-h] [-i] [-s <arg>] [-t <arg>] [-v]" + NEW_LINE +
            " -e,--element <arg>     Local name of the XML element to detect in the" + NEW_LINE +
            "                        input XML stream and, potentially, select." + NEW_LINE +
            "                        Example: \"book\"." + NEW_LINE +
            " -f,--file <arg>        [Optional] Define white-list of patterns from" + NEW_LINE +
            "                        file, one per line. XML elements will be selected" + NEW_LINE +
            "                        if the value returned by the provided XPath query" + NEW_LINE +
            "                        is in the white-list. Example: if file contains" + NEW_LINE +
            "                        \"magician\\\\nxquery\\\\n\" and filter is:" + NEW_LINE +
            "                        \"//book/tags/tag/text()\", then \"book\" elements" + NEW_LINE +
            "                        with either a \"magician\" or \"xquery\" tag will be" + NEW_LINE +
            "                        returned." + NEW_LINE +
            " -h,--help              Print this, i.e. a usage message briefly" + NEW_LINE +
            "                        summarizing the command-line options, then exit." + NEW_LINE +
            " -i,--indent            [Optional] Indent returned XML elements, for" + NEW_LINE +
            "                        potentially better readability. Default: keep the" + NEW_LINE +
            "                        same formatting and indentation as in the input" + NEW_LINE +
            "                        XML stream." + NEW_LINE +
            " -s,--select <arg>      XPath query used to select XML elements among the" + NEW_LINE +
            "                        ones detected. Example: \"//book/tags/tag[text() =" + NEW_LINE +
            "                        'magician']\" will select all \"book\" elements with" + NEW_LINE +
            "                        \"magician\" as a \"tag\"." + NEW_LINE +
            " -t,--transform <arg>   [Optional] XPath expression used to transform the" + NEW_LINE +
            "                        selected XML elements. Example:" + NEW_LINE +
            "                        \"//book/title/text()\". Default: the entire XML" + NEW_LINE +
            "                        element will be returned." + NEW_LINE +
            " -v,--version           Print \"1.0\", i.e. the version number of" + NEW_LINE +
            "                        com.carmatechnologies.utilities.xml.XmlStreamFilte" + NEW_LINE +
            "                        r to the standard output stream. This version" + NEW_LINE +
            "                        number should be included in all bug reports." + NEW_LINE;

    private final OutputStream stdOut = new ByteArrayOutputStream();
    private final OutputStream stdErr = new ByteArrayOutputStream();
    private final PrintWriter stdOutWriter = new PrintWriter(stdOut);
    private final PrintWriter stdErrWriter = new PrintWriter(stdErr);
    private final XmlStreamFilterCliFactory factory = new XmlStreamFilterCliFactory(stdOutWriter, stdErrWriter);

    @Test
    public void simpleFilterUsingXPathToSelectPrintsSelectedXmlToStandardOutput() throws XMLStreamException, IOException {
        StreamFilter filter = factory.newStreamFilter(new String[]{"-e", "book", "-s", "//book/tags/tag[text() = 'magician']"});
        assertThat(filter, is(not(nullValue())));
        assertThat(filter, is(instanceOf(XmlStreamFilter.class)));

        InputStream in = streamFor("/books.xml");
        filter.filter(in, stdOut);

        assertThat(stdOut.toString(), is("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>" +
                "<book category=\"CHILDREN\">" + NEW_LINE +
                "        <title lang=\"en\">Harry Potter</title>" + NEW_LINE +
                "        <author>J K. Rowling</author>" + NEW_LINE +
                "        <year>2005</year>" + NEW_LINE +
                "        <price>29.99</price>" + NEW_LINE +
                "        <tags>" + NEW_LINE +
                "            <tag>fantasy</tag>" + NEW_LINE +
                "            <tag>magician</tag>" + NEW_LINE +
                "        </tags>" + NEW_LINE +
                "    </book>"));
        assertThat(stdErr.toString(), is(""));
    }

    @Test
    public void simpleFilterUsingXPathToSelectPrintsSelectedXmlToStandardOutputWithIndentation() throws XMLStreamException, IOException {
        StreamFilter filter = factory.newStreamFilter(new String[]{"-e", "book", "-s", "//book/tags/tag[text() = 'magician']", "-i"});
        assertThat(filter, is(not(nullValue())));
        assertThat(filter, is(instanceOf(XmlStreamFilter.class)));

        InputStream in = streamFor("/books_no_indentation.xml");
        filter.filter(in, stdOut);

        assertThat(stdOut.toString(), is("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>" + NEW_LINE +
                "<book category=\"CHILDREN\">" + NEW_LINE +
                "    <title lang=\"en\">Harry Potter</title>" + NEW_LINE +
                "    <author>J K. Rowling</author>" + NEW_LINE +
                "    <year>2005</year>" + NEW_LINE +
                "    <price>29.99</price>" + NEW_LINE +
                "    <tags>" + NEW_LINE +
                "        <tag>fantasy</tag>" + NEW_LINE +
                "        <tag>magician</tag>" + NEW_LINE +
                "    </tags>" + NEW_LINE +
                "</book>" + NEW_LINE));
        assertThat(stdErr.toString(), is(""));
    }

    @Test
    public void advancedFilterUsingXPathToBothSelectAndTransformAndUsingWhiteListFromFilePrintsSelectedXmlToStandardOutput() throws XMLStreamException, IOException {
        StreamFilter filter = factory.newStreamFilter(new String[]{"-e", "book", "-s", "//book/tags/tag/text()", "-t", "//book/title/text()", "-f", Resources.getResource("white_list.txt").getFile()});
        assertThat(filter, is(not(nullValue())));
        assertThat(filter, is(instanceOf(XmlStreamFilter.class)));

        InputStream in = streamFor("/books.xml");
        filter.filter(in, stdOut);

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
        assertThat(stdOut.toString(), is("1.0" + NEW_LINE));
        assertThat(stdErr.toString(), is(""));
    }

    @Test
    public void printVersionLongArgument() {
        StreamFilter filter = factory.newStreamFilter(new String[]{"--version"});
        assertThat(filter, is(not(nullValue())));
        assertThat(filter, is(instanceOf(NoOpStreamFilter.class)));
        flush();
        assertThat(stdOut.toString(), is("1.0" + NEW_LINE));
        assertThat(stdErr.toString(), is(""));
    }

    @Test
    public void notProvidingAnyArgumentPrintsErrorMessageAndUsage() {
        StreamFilter filter = factory.newStreamFilter(new String[]{});
        assertThat(filter, is(not(nullValue())));
        assertThat(filter, is(instanceOf(NoOpStreamFilter.class)));
        flush();
        assertThat(stdOut.toString(), is(""));
        assertThat(stdErr.toString(), is("Invalid command line arguments: please provide a value for argument \"element\"." + NEW_LINE + USAGE));
    }

    @Test
    public void notProvidingValueToElementArgumentPrintsErrorMessageAndUsage() {
        StreamFilter filter = factory.newStreamFilter(new String[]{"-e"});
        assertThat(filter, is(not(nullValue())));
        assertThat(filter, is(instanceOf(NoOpStreamFilter.class)));
        flush();
        assertThat(stdOut.toString(), is(""));
        assertThat(stdErr.toString(), is("Missing argument for option: e" + NEW_LINE + USAGE));
    }

    @Test
    public void notProvidingSelectQueryPrintsErrorMessageAndUsage() {
        StreamFilter filter = factory.newStreamFilter(new String[]{"-e", "book"});
        assertThat(filter, is(not(nullValue())));
        assertThat(filter, is(instanceOf(NoOpStreamFilter.class)));
        flush();
        assertThat(stdOut.toString(), is(""));
        assertThat(stdErr.toString(), is("Invalid command line arguments: please provide a value for argument \"select\"." + NEW_LINE + USAGE));
    }

    @Test
    public void providingInvalidSelectXPathQueryPrintsErrorMessageAndUsage() {
        StreamFilter filter = factory.newStreamFilter(new String[]{"-e", "book", "-s", "~~~clearly not XPath~~"});
        assertThat(filter, is(not(nullValue())));
        assertThat(filter, is(instanceOf(NoOpStreamFilter.class)));
        flush();
        assertThat(stdOut.toString(), is(""));
        assertThat(stdErr.toString(), is("Invalid command line arguments: Invalid XPath expression \"~~~clearly not XPath~~\" for argument \"select\". Original error: \n" +
                "javax.xml.transform.TransformerException: A location path was expected, but the following token was encountered:  ~~~clearly" + NEW_LINE + USAGE));
    }

    @Test
    public void providingInvalidSelectXPathQueryAlongSideValidWhiteListFilePrintsErrorMessageAndUsage() {
        StreamFilter filter = factory.newStreamFilter(new String[]{"-e", "book", "-s", "~~~clearly not XPath~~", "-f", Resources.getResource("white_list.txt").getFile()});
        assertThat(filter, is(not(nullValue())));
        assertThat(filter, is(instanceOf(NoOpStreamFilter.class)));
        flush();
        assertThat(stdOut.toString(), is(""));
        assertThat(stdErr.toString(), is("Invalid command line arguments: Invalid XPath expression \"~~~clearly not XPath~~\" for argument \"select\". Original error: \n" +
                "javax.xml.transform.TransformerException: A location path was expected, but the following token was encountered:  ~~~clearly" + NEW_LINE + USAGE));
    }

    @Test
    public void providingInvalidSelectXPathTransformQueryPrintsErrorMessageAndUsage() {
        StreamFilter filter = factory.newStreamFilter(new String[]{"-e", "book", "-s", "//book/tags/tag[text() = 'magician']", "-t", "~~~clearly not XPath~~"});
        assertThat(filter, is(not(nullValue())));
        assertThat(filter, is(instanceOf(NoOpStreamFilter.class)));
        flush();
        assertThat(stdOut.toString(), is(""));
        assertThat(stdErr.toString(), is("Invalid command line arguments: Invalid XPath expression \"~~~clearly not XPath~~\" for argument \"transform\". Original error: \n" +
                "javax.xml.transform.TransformerException: A location path was expected, but the following token was encountered:  ~~~clearly" + NEW_LINE + USAGE));
    }

    @Test
    public void providingInvalidWhiteListFilePrintsErrorMessageAndUsage() {
        StreamFilter filter = factory.newStreamFilter(new String[]{"-e", "book", "-s", "//book/tags/tag/text()", "-f", "~/non_existant_white_list.txt"});
        assertThat(filter, is(not(nullValue())));
        assertThat(filter, is(instanceOf(NoOpStreamFilter.class)));
        flush();
        assertThat(stdOut.toString(), is(""));
        assertThat(stdErr.toString(), is("Invalid command line arguments: Failed to read white-list of patterns from file: ~/non_existant_white_list.txt. Original error: \n" +
                "~/non_existant_white_list.txt (No such file or directory)" + NEW_LINE + USAGE));
    }

    private void flush() {
        stdOutWriter.flush();
        stdErrWriter.flush();
    }
}
