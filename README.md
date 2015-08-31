[![Build Status](https://travis-ci.org/marccarre/xml-stream-filter.svg?branch=master)](https://travis-ci.org/marccarre/xml-stream-filter) [![Coverage Status](https://coveralls.io/repos/marccarre/xml-stream-filter/badge.svg?branch=master&service=github)](https://coveralls.io/github/marccarre/xml-stream-filter?branch=master)

Introduction
------------

`xml-stream-filter` - a Java and command line utility to:

  - stream-process **large XML** and **GZipped XML** files, and
  - when specified elements are detected:
    - convert these to DOM trees
    - filter them according to the specified XPath predicate
    - transform them according to the specified XPath transformation.
  - based on:
    -  the `javax.xml.*` API and 
    - `com.fasterxml.woodstox:woodstox-core` [known](https://github.com/eishay/jvm-serializers/wiki) to be a very fast implementation.


Usage
-----

The "*thin*" jar is available in Maven Central:

    <dependency>
        <groupId>com.carmatechnologies.utilities</groupId>
        <artifactId>xml-stream-filter</artifactId>
        <version>1.0</version>
    </dependency>

You can also download the "*fat*" jar on [this page](https://github.com/marccarre/xml-stream-filter/releases/tag/v1.0).


Command Line Interface
----------------------

    java -jar com.carmatechnologies.utilities.xml.XmlStreamFilter [-e <arg>] [-f <arg>] [-h] [-i] [-s <arg>] [-t <arg>] [-v] < input.xml|.xml.gz > output.xml|.txt

     -e,--element <arg>     Local name of the XML element to detect in the input XML stream and, potentially, select.
                            Example: "book".

     -f,--file <arg>        [Optional] Define white-list of patterns from file, one per line.
                            XML elements will be selected if the value returned by the provided XPath query is in the white-list.
                            Example:
                            if file contains "magician\nxquery\n" and filter is: "//book/tags/tag/text()",
                            then "book" elements with either a "magician" or "xquery" tag will be returned.

     -h,--help              Print this, i.e. a usage message briefly
                            summarizing the command-line options, then exit.

     -i,--indent            [Optional] Indent returned XML elements, for potentially better readability.
                            Default: keep the same formatting and indentation as in the input XML stream.

     -s,--select <arg>      XPath query used to select XML elements among the ones detected.
                            Example: "//book/tags/tag[text() = 'magician']" will select all "book" elements with "magician" as a "tag".

     -t,--transform <arg>   [Optional] XPath expression used to transform the selected XML elements.
                            Example: "//book/title/text()".
                            Default: the entire XML element will be returned.

     -v,--version           Print "1.0", i.e. the version number of com.carmatechnologies.utilities.xml.XmlStreamFilter
                            to the standard output stream. This version number should be included in all bug reports.
