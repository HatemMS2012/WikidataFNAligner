# Wikidata FrameNet Aligner

The goal of this project is to align [Wikidata](https://www.wikidata.org) properties with [FrameNet](framenet2.icsi.berkeley.edu) frames and to map property arguments to the corresponding semantic roles, known as Frame Elements (FEs). For instance consider the the property [educated at](https://www.wikidata.org/wiki/Property:P69) which links a student (ARG1) to an educational institution (ARG2). The program uses different kinds of meta data available in the KB and also in FrameNet to align identify an alignment as given in the table below:


## Running the code

### Setting up Word Embedding

First you need to download the word embedding indicies which can be found [here](https://goo.gl/ysKmQ8). The folder contains two folders:

- leveyDepDisco.zip: word embedding based on dependency graphs (more on information [here](https://levyomer.wordpress.com/2014/04/25/dependency-based-word-embeddings/))
- GoogleNewsDicoIndexCOL.zip: the GoogleNews word embeddings provided by [word2vec](http://word2vec.googlecode.com/).    

Next, unzip these files and update hms.embedding.WordEmbeddingSpace to refer to the location of the containing folders on your disk.

### NLP Jar

You also need to add the following jar to your class path.

### Running an Example

Take a look on the main method in hms.alignment.PropertyFrameMatcher and see an example for running the code to reproduced the results of the AKBC 2016 paper.


