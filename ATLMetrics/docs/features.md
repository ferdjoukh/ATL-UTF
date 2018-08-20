# ATLrunner

This module is the core part of our work. It is used to run all the model transformations using all the models that come from different origins.

This document lists the main feature of **ATLrunner**.

## Model Transformations Folder reader

A given folder of MTs is read and all the data that it contains are collected (MTs, additional input files, ecore meta-models, xmi models, and the list of model finding/generation tools).

### Model Transformations data collector

All the data of a transformation folder are collected and the desired object are created. They are now ready for running the MTs.

### Output files

-  **@TODO** execution.csv
-  **@TODO** execution.log

### Model Transformations runner

**@TODO**

## ATL launcher

This is the module that runs an ATL transformation on a set of given xmi models.

### Single ATL transformation launcher

Given an ATL MT and a set of xmi file, the module launches the ATL transformation, produces the output model and the trace model.

**important**: the ATL transformation has to be compiled into .emftvm byte code file.

### Execution metrics computer

executed rules

**@TODO**: continue to have more accurate metrics (based on MT.rules file)

### Execution log printer

- **@TODO** MTname-toolName.log files

## Rule complexity calculator

**@TODO**

### Generate a csv file containing all rules

**@TODO**

### automatically compute the complexity of a rule

**@TODO**