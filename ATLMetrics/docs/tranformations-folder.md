# Transformations folder

This documents explains how the folder that contains the transformations **must** be built.

## What is in it ?

In this folder, we found the following data:

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
		- trafo1.infos
		- trafo1.rules
		- **meta-models**
			- **input**
				- intputMM.ecore
			- **output**
				- outputM.ecore
		- **models**
			- **input**
				- **GRIMM**
					- input-models-by-grimm.xmi
				- **PRAMANA**
					- input-models-by-pramana.xmi	
			- **output**
			- **traces**			
	- executions.csv

## Details about special files

### trafo.infos

This file may contain a short summary about the model transformation:

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

### trafo.rules

This files contains score evaluation for the rules of a model transformation.

Rules are divided into several categories:

- Empty: without guards
- Simple: very simple guards
- Medium: guards are not very complex 
- Complex: the guards are very complex

### executions.csv

This *csv* file contains the result of execution for all the transformations and for all the tools.

We use it to produce then the charts, statistics, etc.

The file has the following shape:


| ToolName | TrafoName | #Models | ExeTime | #Rules | coverage |
|----------|-----------|---------|---------|--------|----------|
| GRIMM    | HSM2FSM   | 20      | 300     | 5      | 30%      |
| GRIMM    | RML2RDML  | 20      | 5800    | 15     | 20%      |
| PRAMANA  | HSM2FSM   | 20      | 300     | 5      | 30%      |
| PRAMANA  | RML2RDML  | 20      | 800     | 6      | 32%      |

More information can be found in this file: executed rules, executed rules per category (empty, simple, complex), etc. 
