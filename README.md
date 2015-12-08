![Travis CI](https://travis-ci.org/mlucchini/wd-crawler.svg?branch=master)
# crawler

##### Run the provided assembly
```sh
	./crawl <domain-name>
```

##### Run the tests
You may need to [download SBT](http://www.scala-sbt.org/download.html).
```sh
	sbt test
```

##### Compile an assembly
You may need to [download SBT](http://www.scala-sbt.org/download.html).
```sh
	sbt assembly
```

##### Trade-offs
- Poor error-handling strategy
- No retry strategy
- No graphical interface
- Not used the Actor model to encapsulate the mutable shared state in the parallel crawler
- Wrote in Scala and used a few libraries, ended up with an assembly of substantial size
