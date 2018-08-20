# Features

## MTs Folder reader

A given folder of MTs is read and all the data that it contains are collected (MTs, additional input files, ecore meta-models, xmi models, and the list of model finding/generation tools).

### Collect MTs data

All the data of a transformation folder are collected and the desired object are created. They are now ready for running the MTs.

### Output files

-  **@TODO** execution.csv
-  **@TODO** execution.log

### Running all the MTs

**@TODO**

## ATL Launcher

This is the module that runs an ATL transformation on a set of given xmi models.

### Launch an ATL MT on a set of models

Given an ATL MT and a set of xmi file, the module launches the ATL transformation, produces the output model and the trace model.

**important**: the ATL transformation has to be compiled into .emftvm byte code file.

### Compute execution metrics

executed rules

**@TODO**: continue to have more accurate metrics (based on MT.rules file)

### Return a detailed log

**@TODO**

## Complexity of MT rules

**@TODO**

### Generate a csv file containing all rules

**@TODO**

### automatically compute the complexity of a rule

**@TODO**