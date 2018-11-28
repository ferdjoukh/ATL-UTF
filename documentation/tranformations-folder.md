# Model Transformations folder

ATL Unit Testing Framework needs input folder containing one or many model transformations.

This document explains how these folders **have to be structured**.

### Namespace

- folder = A folder containing **1** Model Transformation is used for unit testing
- transformations folder = A folder containing several Model Transformations is used for coverage calculation

## folder


### Unit testing

The folder that contains **1** model transformation must include the following files and subfolders:

- The ATL model transformation source file: *.atl* (use emftvm to compile it to produce *.emftvm* file).
- All needed meta-models (at least 1 or 2 for each model transformation).
- Some additional information about the MT: module, name of meta-models, rootClass (written in *file.infos*).

### Coverage calculation

For coverage calculation, we also need: 

- Input models. Separated in sub-folders.

## Tree structure

The following tree structure is mandatory. All the model transformations you bring for the tool have to follow it.

- **transformations folder**
	- **folder1**
		- folder1.atl
		- folder1.emftvm
		- **folder1.infos**
		- **meta-models**
			- **input**
				- intputMM.ecore
			- **output**
				- outputMM.ecore
		- **models**
			- **input**
				- **GRIMM**
					- input-models-by-grimm.xmi
				- **PRAMANA**
					- input-models-by-pramana.xmi	
				- **HAND**
					- input-models-handcrafted.xmi	
			- **output**
			- **traces**
	- **folder2**
	- **folder3**

# Additional input files

### folder.infos

This file contains a short summary about the model transformation:

- Name
- Module name
- Input meta-model package name
- Input meta-model relative path (.ecore)
- rootClass
- Output meta-model package name
- Output meta-model relative path (.ecore)

Here is an example of **HSM2FSM.infos** file for `HSM2FSM` model transformation

```
Transformation name=HSM2FSM
module=HSM2FSM
inMM=HSM
inMMRelativePath=HSM.ecore
rootClass=StateMachine
outMM=FSM
outMMRelativePath=FSM.ecore
```

# Output files

### folder.rules

This files contains score evaluation for the rules of a model transformation.

Rules are divided into several categories:

- Empty: without guards (1)
- Simple: very simple guards (2)
- Medium: guards are not very complex (3) 
- Complex: the guards are very complex (4)

### folder-atl.metrics

Computed ATL metrics for the given transformation

### folder-ecore.metrics

Computed Ecore metrics for the given input meta-model

### execution-results.csv

This **csv** file contains the result of execution for all the transformations and for all the tools.

We use it to produce then the charts, statistics, etc.

The file has the following shape:


| ToolName | TrafoName | #Models | #ExecRules | #Rules | RulesNames     | coverScore | maxScore | 
|----------|-----------|---------|------------|--------|----------------|------------|----------|
| GRIMM    | HSM2FSM   | 20      | 3          | 5      | [machin, truc] | 5          | 10       |
| GRIMM    | RML2RDML  | 20      | 5          | 15     | [bidul, chose] | 10         | 15       |      
| PRAMANA  | HSM2FSM   | 20      | 3          | 5      | [machin, truc] | 8          | 10       |
| PRAMANA  | RML2RDML  | 20      | 8          | 15     | [bidul, chose] | 11         | 15       |

More information can be found in this file: executed rules, executed rules per category (empty, simple, complex), etc. 

### execution.log

It contains all the details about the execution. This files reports all the events that occurred during the execution of the model transformations.

### failed-models.log

This file contains the list of models that were not transformed.

### tool-mt.csv

For each given tool and model transformation, a **TOOL-MT.csv** file is created. It contains the details of execution for each xmi model.

| Model      | ExeTime   | #ExecRules | #TotalRules | RulesNames     |
|------------|-----------|------------|-------------|----------------|
| bidule.xmi | 120.21    | 3          | 10          | [machin, truc] |