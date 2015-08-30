[![Build Status](https://travis-ci.org/marccarre/xml-stream-filter.svg?branch=master)](https://travis-ci.org/marccarre/xml-stream-filter) [![Coverage Status](https://coveralls.io/repos/marccarre/xml-stream-filter/badge.svg?branch=master&service=github)](https://coveralls.io/github/marccarre/xml-stream-filter?branch=master)

xml-stream-filter - a Java and command line utility to:

  - stream-process **large XML** and **GZipped XML** files, and
  - when specified elements are detected:
    - convert these to DOM trees
    - filter them according to the specified XPath predicate
    - transform them according to the specified XPath transformation.
  - based on:
    -  the `javax.xml.*` API and 
    - `com.fasterxml.woodstox:woodstox-core` [known](https://github.com/eishay/jvm-serializers/wiki) to be a very fast implementation.
