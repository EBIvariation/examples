import pandas as pd, sys, functools, numpy as np, operator
from itertools import islice, groupby, count

if sys.argv.__len__() != 2:
    print("Usage: Assess_Genotype_Distribution.py <VCF_FilePath>")
    sys.exit(0)

vcfFileName = sys.argv[1]

# Number of rows to be skipped
numRowsToSkip = 0
processedLinesLimit = 5000
lineChunkSize = 100
inputFileHandle = open(vcfFileName)
groups = groupby(inputFileHandle, key=lambda k, line=count(): next(line) // lineChunkSize)
foundHeaderRow = False

for k, group in groups:
    for line in group:
        if line.strip().upper().startswith("#CHROM"):
            foundHeaderRow = True
            break
        numRowsToSkip += 1
        if numRowsToSkip >= processedLinesLimit:
            break
    if foundHeaderRow:
        break

reader = pd.read_table(vcfFileName, sep = '\t', skiprows=numRowsToSkip, chunksize=10, iterator=True, low_memory=False, engine='c')
genotypeCountDict = {}
vcfDF = reader.get_chunk(10)
columnList = vcfDF.columns.tolist()
sampleColumnList = columnList[columnList.index("FORMAT")+1:]

reader = pd.read_table(vcfFileName, sep = '\t', skiprows=numRowsToSkip, chunksize=10000, iterator=True, low_memory=False, engine='c')
chunkNo = 0
for chunk in reader:
    vcfDF = chunk
    sampleDFValues = pd.melt(vcfDF[sampleColumnList], id_vars=[], var_name="sample")
    sampleDFValues["value"] = sampleDFValues["value"].map(lambda x: x.split(":")[0])
    resultDict = dict(sampleDFValues["value"].value_counts())
    if not genotypeCountDict:
        genotypeCountDict = resultDict
    else:
        for genotype in resultDict.keys():
            if genotypeCountDict.has_key(genotype):
                genotypeCountDict[genotype] = genotypeCountDict[genotype] + resultDict[genotype]
            else:
                genotypeCountDict[genotype] = resultDict[genotype]
    chunkNo += 1
    print("Processed Chunk: {0}".format(chunkNo))

sorted_genotypeCountTuple = sorted(genotypeCountDict.items(), key=operator.itemgetter(1),reverse=True)
print("Most Dominant Genotype: {0}, Count: {1}".format(sorted_genotypeCountTuple[0][0], sorted_genotypeCountTuple[0][1]))
print("Second most Dominant Genotype: {0}, Count: {1}".format(sorted_genotypeCountTuple[1][0], sorted_genotypeCountTuple[1][1]))
print("Third most Dominant Genotype: {0}, Count: {1}".format(sorted_genotypeCountTuple[2][0], sorted_genotypeCountTuple[2][1]))
print("Genotype counts: \n{0}".format(sorted_genotypeCountTuple))