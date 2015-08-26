[![Build Status](https://travis-ci.org/marccarre/xml-stream-filter.png?branch=master)](https://travis-ci.org/marccarre/xml-stream-filter) [![Coverage Status](https://coveralls.io/repos/marccarre/xml-stream-filter/badge.png)](https://coveralls.io/r/marccarre/xml-stream-filter)

xml-stream-filter - a Java and command line utility to:

  - stream-process large XML files, and
  - when specified elements are detected:
    - convert these to DOM trees
    - filter them according to the specified XPath predicate
    - transform them according to the specified XPath transformation.
  - based on:
    -  the `javax.xml.*` API and 
    - `aalto-xml` [known](https://github.com/eishay/jvm-serializers/wiki) to be a very fast implementation.
