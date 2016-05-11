# Wikidata FrameNet Aligner

The goal of this project is to align [Wikidata](https://www.wikidata.org) properties with [FrameNet](framenet2.icsi.berkeley.edu) frames and to map property arguments to the corresponding semantic roles, known as Frame Elements (FEs). For instance consider the the property [educated at](https://www.wikidata.org/wiki/Property:P69) which links a student (ARG1) to an educational institution (ARG2). The program uses different kinds of meta data available in the KB and also in FrameNet to identify an alignment as given in the table below:

|Wikidata | FrameNet|
|----------|----------|
|educated at| Eudcation_teaching|
|educated at.ARG1 | Eudcation_teaching.Student|
|educated at.ARG2 | Eudcation_teaching.Institution|

## Running the code

### Setting up Word Embedding

First you need to download the word embedding indicies which can be found [here](https://goo.gl/ysKmQ8). The folder contains two folders:

- leveyDepDisco.zip: word embedding based on dependency graphs (more on information [here](https://levyomer.wordpress.com/2014/04/25/dependency-based-word-embeddings/))
- GoogleNewsDicoIndexCOL.zip: the GoogleNews word embeddings provided by [word2vec](http://word2vec.googlecode.com/).    

Next, unzip these files and update [WordEmbeddingSpace](src/hms/embedding/WordEmbeddingSpace.java) to refer to the location of the containing folders on your disk.

### Setting up the Database
The code uses a relation wikidata database. You can configure your connection in [config.properties](config.properties).
Futhermore, we are using an XML API for accessing FrameNet you should also configure the path to your FrameNet folder by modifying the corresponding entry in [config.properties](config.properties)

### NLP Jar

You also need to add the following [jar](https://goo.gl/KUZevH) to your class path.

### Running an Example

Take a look on the main method in [PropertyFrameMatcher](src/hms/alignment/PropertyFrameMatcher.java) and see an example for running the code to reproduced the results of the AKBC 2016 paper: [Enriching Wikidata with Frame Semantics](http://www.akbc.ws/2016/).


