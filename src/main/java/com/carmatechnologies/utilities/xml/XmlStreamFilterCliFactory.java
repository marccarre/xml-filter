package com.carmatechnologies.utilities.xml;

import com.carmatechnologies.utilities.xml.common.Pair;
import com.carmatechnologies.utilities.xml.common.TransformerFactoryImpl;
import com.carmatechnologies.utilities.xml.predicate.XPathPredicate;
import com.carmatechnologies.utilities.xml.predicate.XPathSetPredicate;
import com.carmatechnologies.utilities.xml.transformer.DomTreeToOutputStreamTransformer;
import com.carmatechnologies.utilities.xml.transformer.XPathToOutputStreamTransformer;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Sets;
import com.google.common.io.Files;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.w3c.dom.Node;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;

import static com.google.common.base.Charsets.UTF_8;
import static org.apache.commons.cli.HelpFormatter.DEFAULT_DESC_PAD;
import static org.apache.commons.cli.HelpFormatter.DEFAULT_LEFT_PAD;
import static org.apache.commons.cli.HelpFormatter.DEFAULT_WIDTH;

public class XmlStreamFilterCliFactory {
    private static final String ELEMENT = "element";
    private static final String SELECT = "select";
    private static final String INDENT = "indent";
    private static final String TRANSFORM = "transform";
    private static final String FILE = "file";
    private static final String HELP = "help";
    private static final String VERSION = "version";
    private static final String EMPTY_STRING = "";

    private final PrintWriter stdOutWriter;
    private final PrintWriter stdErrWriter;
    private final Options options;

    public XmlStreamFilterCliFactory() {
        this(new PrintWriter(System.out), new PrintWriter(System.err));
    }

    public XmlStreamFilterCliFactory(final PrintWriter stdOutWriter, final PrintWriter stdErrWriter) {
        this.stdOutWriter = stdOutWriter;
        this.stdErrWriter = stdErrWriter;
        options = getOptions();
    }

    private Options getOptions() {
        final Options options = new Options();

        options.addOption("e", ELEMENT, true, "Local name of the XML element to detect in the input XML stream and, potentially, select. Example: \"book\".");

        options.addOption("s", SELECT, true, "XPath query used to select XML elements among the ones detected. " +
                "Example: \"//book/tags/tag[text() = 'magician']\" will select all \"book\" elements with \"magician\" as a \"tag\".");

        options.addOption("i", INDENT, false, "[Optional] Indent returned XML elements, for potentially better readability. " +
                "Default: keep the same formatting and indentation as in the input XML stream.");

        options.addOption("t", TRANSFORM, true, "[Optional] XPath expression used to transform the selected XML elements. " +
                "Example: \"//book/title/text()\". Default: the entire XML element will be returned.");

        options.addOption("f", FILE, true, "[Optional] Define white-list of patterns from file, one per line. " +
                "XML elements will be selected if the value returned by the provided XPath query is in the white-list. " +
                "Example: if file contains \"magician\\nxquery\\n\" and filter is: \"//book/tags/tag/text()\", " +
                "then \"book\" elements with either a \"magician\" or \"xquery\" tag will be returned.");

        options.addOption("h", HELP, false, "Print this, i.e. a usage message briefly summarizing the command-line options, then exit.");

        options.addOption("v", VERSION, false, "Print \"" + XmlStreamFilter.VERSION + "\", i.e. the version number of " +
                XmlStreamFilter.class.getCanonicalName() + " to the standard output stream. This version number should be included in all bug reports.");

        return options;
    }

    public StreamFilter newStreamFilter(final String[] args) {
        try {
            return createStreamFilter(args);
        } finally {
            stdOutWriter.flush();
            stdErrWriter.flush();
        }
    }

    private StreamFilter createStreamFilter(final String[] args) {
        final CommandLine line = parseArguments(args);
        if (line == null) {
            return new NoOpStreamFilter();
        }

        if (line.hasOption(HELP)) {
            printHelp(stdOutWriter);
            return new NoOpStreamFilter();
        }

        if (line.hasOption(VERSION)) {
            printVersion();
            return new NoOpStreamFilter();
        }

        if (!line.hasOption(ELEMENT)) {
            printHelp(messageInvalidArgumentFor(ELEMENT));
            return new NoOpStreamFilter();
        }

        if (!line.hasOption(SELECT)) {
            printHelp(messageInvalidArgumentFor(SELECT));
            return new NoOpStreamFilter();
        }

        final Predicate<Node> filter = line.hasOption(FILE)
                ? getXPathSetPredicate(line.getOptionValue(SELECT), line.getOptionValue(FILE))
                : getXPathPredicate(line.getOptionValue(SELECT));
        if (filter == null) {
            return new NoOpStreamFilter();
        }

        final Function<Pair<Node, OutputStream>, Void> transformer = line.hasOption(TRANSFORM)
                ? getXPathToOutputStreamTransformer(line.getOptionValue(TRANSFORM))
                : getDomTreeToOutputStreamTransformer(line.hasOption(INDENT));
        if (transformer == null) {
            return new NoOpStreamFilter();
        }

        try {
            return new XmlStreamFilter(line.getOptionValue(ELEMENT), filter, transformer);
        } catch (TransformerConfigurationException e) {
            return new NoOpStreamFilter();
        }
    }

    private CommandLine parseArguments(final String[] args) {
        try {
            return new DefaultParser().parse(options, args);
        } catch (ParseException e) {
            printHelp(e.getMessage());
            return null;
        }
    }

    private XPathSetPredicate getXPathSetPredicate(final String xpathQuery, final String filePath) {
        try {
            return new XPathSetPredicate(xpathQuery, Sets.newHashSet(Files.readLines(new File(filePath), UTF_8)));
        } catch (XPathExpressionException e) {
            printHelp(messageInvalidXPathExpression(xpathQuery, SELECT, e));
            return null;
        } catch (IOException e) {
            printHelp(messageInvalidArguments("Failed to read white-list of patterns from file: " + filePath + "." + originalError(e)));
            return null;
        }
    }

    private XPathPredicate getXPathPredicate(final String xpathQuery) {
        try {
            return new XPathPredicate(xpathQuery);
        } catch (XPathExpressionException e) {
            printHelp(messageInvalidXPathExpression(xpathQuery, SELECT, e));
            return null;
        }
    }

    private XPathToOutputStreamTransformer getXPathToOutputStreamTransformer(final String xpathQuery) {
        try {
            return new XPathToOutputStreamTransformer(xpathQuery);
        } catch (XPathExpressionException e) {
            printHelp(messageInvalidXPathExpression(xpathQuery, TRANSFORM, e));
            return null;
        }
    }

    private DomTreeToOutputStreamTransformer getDomTreeToOutputStreamTransformer(final boolean indent) {
        try {
            return indent
                    ? new DomTreeToOutputStreamTransformer(getTransformerConfiguredWithIndentation())
                    : new DomTreeToOutputStreamTransformer();
        } catch (TransformerConfigurationException e) {
            throw new RuntimeException("Failed to create DOM tree to OutputStream tansformer", e);
        }
    }

    private Transformer getTransformerConfiguredWithIndentation() throws TransformerConfigurationException {
        final Transformer transformer = TransformerFactoryImpl.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
        return transformer;
    }

    private void printHelp(final String message) {
        stdErrWriter.println(message);
        printHelp(stdErrWriter);
    }

    private void printHelp(final PrintWriter writer) {
        new HelpFormatter().printHelp(writer, DEFAULT_WIDTH, "java -jar " + XmlStreamFilter.class.getCanonicalName(), null, options, DEFAULT_LEFT_PAD, DEFAULT_DESC_PAD, null, true);
    }

    private void printVersion() {
        stdOutWriter.println(XmlStreamFilter.VERSION);
    }

    private String messageInvalidArgumentFor(final String argument) {
        return messageInvalidArguments("please provide a value for argument \"" + argument + "\".");
    }

    private String messageInvalidArguments(final String message) {
        return "Invalid command line arguments: " + message;
    }

    private String messageInvalidXPathExpression(final String xpathQuery, final String argument, final XPathExpressionException e) {
        return messageInvalidArguments("Invalid XPath expression \"" + xpathQuery + "\" for argument \"" + argument + "\"." + originalError(e));
    }

    private String originalError(final Exception e) {
        if (e == null) {
            return EMPTY_STRING;
        }
        final String message = e.getMessage();
        return ((message == null) || message.isEmpty())
                ? EMPTY_STRING
                : " Original error: \n" + e.getMessage();
    }
}
