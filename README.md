# vodml-lite-mapping

The goal of this project is to investigate the possibility of simplifying the syntax of the VO-DML mapping in VOTable and to make it more flexible to be usable over a large variety of datasets.
It is currently based on the SimpleTimeSeries data model (10/2018).
This model is still in development, some model features used in the project could be obsolete.

You can get more about our mapping sntax on the [wiki](https://github.com/lmichel/vodml-lite-mapping/wiki)

## Mapping block Generation
The Python submodule can transform a VODML model into a set of mapping components. 
These components must be rearranged by hand to build the final mapping block
```bash
% cd vodml-lite-mapping/mapping-factory/python/
% python transform_ts.py
```
## Annotated VOTable Processing
The test package contains multiple VOTable which have been used for the developpement.
One of them `src/test/java/test/xml/annot_tsmodel_filter.xml` is a real Gaia time series properly annotated.

It can be explored by running the class `src/main/java/sample/TimeSeriesExample.java`
(there is no Gradle launcher setup at that time)


# PDF preview

The PDF preview can be seen [here](https://github.com/lmichel/vodml-lite-mapping/releases/download/auto-pdf-preview/vodml-lite-mapping-draft.pdf) thanks to the ADQL team who has written a workflow generating it automatically.
