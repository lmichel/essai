# vodml-lite-mapping

This goal of this project is to investigate the possibility of simplifying the syntax of the VO-DML mapping in VOTable.
It s currently based on the SimpleTimeSeries data model (10/2018).
This model still being in development,some physical quantities used in the project could be obsolete.

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
