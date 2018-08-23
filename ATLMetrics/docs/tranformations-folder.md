# Model Transformations folder

This document explains how the folder that contains the model transformations (MTs) **must** be built.

## Content

In this folder, we find the following data:

- The ATL model transformations source files (*.atl*, *.asm*, *.emftvm*)
- All needed meta-models (at least 1 or 2 for each model transformation).
- Input models. They are separated by origin (generation tool or hand-crafted).
- Some additional text information about the MT: module, name of meta-models, etc.
- A file containing information about the complexity of rules for the model transformation.

## Tree structure

The following tree structure is mandatory. All the model transformations you bring for the tool have to follow it.

- **TransformationsDir**
	- **Trafo1**
		- trafo1.atl
		- trafo1.asm
		- trafo1.emftvm
		- *trafo1.infos*
		- *trafo1.rules*
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
			- **output**
			- **traces**
	- **trafo2**
	- **trafo3**
	- execution.log					
	- execution-resutls.csv
	- failed-models.log
	- TOOL-MT.csv

# Additional input files

### MTname.infos

This file contains a short summary about the model transformation:

- Name
- Module name
- Input meta-model package name
- Input meta-model relative path (.ecore)
- Output meta-model package name
- Output meta-model relative path (.ecore)

Here is an example of .infos file for  `HSM2FSM` model transformation

```
Transformation name=HSM2FSM
module=HSM2FSM
inMM=HSM
inMMRelativePath=HSM.ecore
outMM=FSM
outMMRelativePath=FSM.ecore
```

### MTname.rules

This files contains score evaluation for the rules of a model transformation.

Rules are divided into several categories:

- Empty: without guards (1)
- Simple: very simple guards (2)
- Medium: guards are not very complex (3) 
- Complex: the guards are very complex (4)


# Output files

### execution-results.csv

This **csv** file contains the result of execution for all the transformations and for all the tools.

We use it to produce then the charts, statistics, etc.

The file has the following shape:


| ToolName | TrafoName | #Models | #ExeRules | #Rules | RulesNames     | coverScore | maxScore | 
|----------|-----------|---------|-----------|--------|----------------|------------|----------|
| GRIMM    | HSM2FSM   | 20      | 3         | 5      | [machin, truc] | 5          | 10       |
| GRIMM    | RML2RDML  | 20      | 5         | 15     | [bidul, chose] | 10         | 15       |      
| PRAMANA  | HSM2FSM   | 20      | 3         | 5      | [machin, truc] | 8          | 10       |
| PRAMANA  | RML2RDML  | 20      | 8         | 15     | [bidul, chose] | 11         | 15       |

More information can be found in this file: executed rules, executed rules per category (empty, simple, complex), etc. 

### execution.log

It contains all the details about the execution. This files reports all the events that occurred during the execution of the model transformations.

### failed-models.log

This file contains the list of models that were not transformed.

### TOOL-MT.csv

For each given tool and model transformation, a **TOOL-MT.csv** file is created. It contains the details of execution for each xmi model.

| Model      | ExeTime   | #ExeRules | #TotalRules | RulesNames     |
|------------|-----------|-----------|-------------|----------------|
| bidule.xmi | 120.21    | 3         | 10          | [machin, truc] |