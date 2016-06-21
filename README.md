# IGWEE Wiki-Diff-Webapplication

This web application is the first trial to use the Wikipedia XML export function and
 showing off the difference between each revisions.

In order to use this application, you should export a revision file from wikipedia.
Upload it to the application and review the history.

Good luck.


## Requirements

Java8 JDK with Apache Maven. Internet Connection. A modern browser, tested under
Firefox 38 and Chrome 50.

A service from the [Commission for Modern Austrian History](http://oesterreichische-geschichte.at)

Licensed under BSD-2-Clause, (c) by pepivampir[http://pepivampir.wordpress.com]

## How to compile

You need to be familiar with maven.

1. Compile the igwee-wikidiff. Use "mvn package"

If the compilation is successful, you will get a file called
"igwee-wikidiff-webapp-1.0.0.SNAPSHOT.jar"
in the target directory. Copy it to any folder you want, and then execute it
with "java -jar igwee-wikidiff-webapp-1.0.0.SNAPSHOT.jar"

Afterwards, you should be able to open the browser to the url:
"http://localhost:1414/"
and get the welcome screen.


## Usage

The webapplication offers 5 tabs for 5 different tasks:
1. Upload a Wikipedia XML revision file and download revision file from Wikipedia
1. View each revision as HTML
1. Compare two revisions and view the result of comparison
1. List contributors and their contributions
1. A simple full text search within the revisions

### File upload

First, you must obtain a revision file from the wikipedia website. This application works
with all so-called page files of Semantic Media Wiki. Please refer to Special Page: Export
of WikiPedia on how to download such an XML file.

Click on "Revision File Upload", select the file and click on "upload". The upload process
will take a few seconds to minutes. The reason: After the upload the indexing process will
also take place.

After the process is done, an alert will tell you whether the upload was successful or not.

You can also directly download and ingest the Revision XML file from Wikipedia. Just select
the language code and enter a page title. The page title can also be a page with a namespace
in Wikipedia. So both "Disclaimer" and "Wikipedia:General disclaimer" point to different
pages.

### View revision

Click on the "View Revisions" tab, and you will receive a list of revisions on the left side.
Just click on the ID number, and the revision will be shown on the right panel. Please note
that the rendering from Wiki Markup (the syntax of WikiPedia) is based on the Bliki project:

https://bitbucket.org/axelclk/info.bliki.wiki/wiki/Home

However, since Twitter Bootstrap is used, the font style is different than WikiPedia. This
may be remedied in future release.

### View Diff

Click on "View Diffs", and you can view the difference between two revisions.
On the left side there are two columns of revisions. Please click on the ID of the first
revision you want to compare on the left column, and then click on the ID of the second
revision on the second column. And then click on "GO" to compare the two revisions.

The revisions will be viewed as Wiki Markup source code. There are three tabs available
in the right panel: two show the source code of each revision and the "DIFF" tab for the
difference between the two.

Please note:

If rev1 is "ABCDEF" and rev2 is "ABDCEF" The difference is shown as follows:

AB-C-D+C+EF

-C- is line-through
+C+ will be underlined.

The base algorithm for the comparison is taken from the library:

https://bitbucket.org/cowwoc/google-diff-match-patch/wiki/Home

### Search

A full text search is provided for convenient purpose. Just click on the "Search" tab,
 enter the search term into the field and click on "search".

The search result will be presented by tables of 20 hits pro page. You can use prev and next
button to navigate through each page. Clicking on the ID number of the revision will take
you to the view tab.

The search compabiblities is provided using the Apache Lucene Library.

https://lucene.apache.org/

The search will strip any diacritics or accents as well as punctuations. However, the
words will keep their glyphs, meaning that the cyrillic "р" will not be turned to "r". A search
for "uber" will also find "über".

You can use the lucene query parser syntax to construct more complicated searches. E.g.:

The usual search will be performed in the "revision" field. If you want to search for revisions
of a special user (e.g. "A Special User"), you can use type

`username:"A Special User"`

into the search field, including the quotation signs. Or, if you want to enlist all revisions
in the month February 2005, you can use the following syntax:

`timestamp:[2005-02 TO 2005-03]`

## Known Issues

### When the revision file is too big.

There are two possible consequences, when the revision file is too big. This can happen to
the server or to the client.

When the revision file is too big for the server, then the server will crash. This did not
happen to me, actually. There is a way to control this: Instead of using the RAM based
Lucene one can try to use the File System based Lucene search engine.

When the revision file is too big for the client, i.e. the browser. Then the browser will
crash. However, after restart the browser (and the server) the uploaded revisions should
be available.

## Acknowledgement

The embedded Wiki-Revision file is the revisions of the English Wikipedia page
"Wikipedia:General disclaimer".

This web application is developed with the funding of the
[Commission of Modern Austrian History](http://www.oesterreichische-geschichte.at) at the
[Research Center Digital Humanities of the University of Innsbruck](https://www.uibk.ac.at/forschung/profilbildung/digital_humanities.html.en).

pepivampir
