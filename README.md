# Solr based Filesystem indexer

The idea behind this was to be able to search large filesystems and multiple 
drives quickly for files. In future, be able to find duplicates. Every time I 
needed to check if I already have a file I am downloading I would have to use 
the slow Windows search or the `find` command. After trial and error I finally 
settled on [Apache Solr](http://lucene.apache.org/solr/) as the search tool. 
It's fast and easy to use.

Warning: This is a good example of my lack of understanding of programming and 
Java in general. Use at your own risk.

### Installation and Usage
- Install [Apache Solr](http://www.apache.org/dyn/closer.lua/lucene/solr/6.4.0)
- create an index as below and name your collection `files`. You can name it 
anything but that's default in code.

|Field| Type | Options| Default|
|-----|:----:|:------:|-------:|
|id|string|primary key| N.A.|
|path|string|indexed,stored,required|N.A.|
|size|double|stored|0.00|

- Download source and build like any Java maven project and use the fat jar 

`dedup-xx-SNAPSHOT-jar-with-dependencies.jar [options]` or
- Index a folder or drive assuming your collection is called `files` with

`dedup-xx-SNAPSHOT-jar-with-dependencies.jar -index C:\`

- ..or specify a collection URL with optional -url argument

`dedup-xx-SNAPSHOT-jar-with-dependencies.jar -index C:\ -url http://localhost:8983/solr/mycollection`

### Searching

 You can use the Solr admin interface for searching. Visit http://localhost:8983/solr Select your collection from the left side drop-down menu. Then select "Query"
 
 You will be using `q` field. Hit Execute to test with `*:*` and you will see 10 results.
 
 Refer to Solr guide on queries. You can search normally like you would in Windows Explorer but advanced query options are also available in the guide.
 
 ### Plans
 - See Issues.