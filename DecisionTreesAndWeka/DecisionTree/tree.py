# CSE6242/CX4242 Homework 4 Pseudocode
# You can use this skeleton code for Task 1 of Homework 4.
# You don't have to use this code. You can implement your own code from scratch if you want.

import csv
import sys

import numpy as np
import copy

from collections import Counter

import random
import math

# Implement your decision tree below
class DecisionTree():
	tree = {}
	defaultLabel = ""

	def maxLabel(self, data, targetAttr):
		# print data[0][targetAttr]

		classCounts = Counter([instance[targetAttr] for instance in data])
		# print classCounts
	
		return classCounts.most_common(1)[0][0]
	
	
	def popBest(self, attribList):
		random.shuffle(attribList)
		
		attribIndex = attribList.pop(-1)
		
		# print attribIndex
		return attribIndex


	def findUniqueValues(self, data, bestAttribIndex):
		uniqueValues = []
		seen = {}
		for i in range(0, len(data)):
			if data[i][bestAttribIndex] not in seen:
				seen[data[i][bestAttribIndex]] = 1
				uniqueValues.append(data[i][bestAttribIndex])

		return uniqueValues

	# find subset of data such that subset-instance[i][bestAttributeIndex] == value
	def findSubset(self, data, bestAttribIndex, value):
		dataSubset = []

		for i in range(0, len(data)):
			if (data[i][bestAttribIndex] == value):
				dataSubset.append(data[i])

		return dataSubset


	def isLeaf(self, tree):
		# in my implementation, leaves are not dicts but list elements
	
		# print tree

		if (not isinstance (tree, dict)):
			return True
	
		# if (tree.keys()[])
		return False;



	# return index of element into given list  
	def findIndex(self, element, list_l1):		
		for i in range(0, len(list_l1)):
			if element == list_l1[i]:
				return i
		
		return -1



	# return entropy of a given list
	def calcEntropy(self, list_l1):
		totalSum = 0
		for i in range (0, len(list_l1)):
			totalSum += list_l1[i]
		
		entropy = 0
		if (totalSum != 0):
			for i in range (0, len(list_l1)):
				ratio    = (list_l1[i]*1.0/totalSum)
				if (ratio != 0):
					entropy += -1*ratio*math.log(ratio, 2)
		
		# print list_l1
		# print entropy
		return entropy	
	
	
	## ---------------------------------------------------------------------##
	# given a 2-D table, finds sum(row_i)/totalSum(row_i for all i's) for each row
	def findWeights(self, valueLabels, weightList):
		
		totalSum = 0
		
		for i in range(0, valueLabels.shape[0]):
			totalSum += sum(valueLabels[i])
		
		for j in range(0, valueLabels.shape[0]):
			weightList[0, j] = (sum(valueLabels[j])*1.0)/totalSum
			
		# print valueLabels
		# print weightList



	def findExtraneousAttrib(self, data, attribList, targetAttr, extraneousList):


		# step 1: find number of unique labels for target attrib 
		labels = []
		
		# seen = {}
		for i in range(0, len(data)):
			if data[i][targetAttr] not in labels:
				# seen[data[i][targetAttr]] = 1
				labels.append(data[i][targetAttr])
		# print labels
		
		
		# step 2: for each feature calculate information gain 
		
		# for each feature/attribute
		for i in range(0, len(attribList)):
		
			# find number of unique values for this feature
			valueSet = []
			seen = {}
			for j in range(0, len(data)):
				# print data[j][i]
				if data[j][i] not in seen:
					seen[data[j][i]] = 1
					valueSet.append(data[j][i])
			# print valueSet
			

			if ( (1.0*len(labels)/len(valueSet)) < 0.001):		
				extraneousList.append(attribList[i])		
		
		# print extraneousList
		return
		#end
		
	## -------------------------------------------------------------------- ##
	# returns  index of attribute with max info gain or minimum aggregate entropy
	# this index is essentially attribList[i] for some i
	# return -> attribIndex = attribList.pop(bestIndex)
	def maxInfoGain(self, data, attribList, targetAttr):
		print "InfoGain!"
		
		
		bestIndex = -1 # let us keep default best index as -1:last index
		minAggrEntropy = 100 # this is some arbitrary MAX value
		# aggrEntropyList = [] # this will contain aggregate entropy for all attributes 
		# entropyDict = {} # this will reverse map calculated aggregate entropy to attribute index
		
		# step 1: find number of unique labels for target attrib 
		labels = []
		
		# seen = {}
		for i in range(0, len(data)):
			if data[i][targetAttr] not in labels:
				# seen[data[i][targetAttr]] = 1
				labels.append(data[i][targetAttr])
		# print labels
		
		
		# step 2: for each feature calculate information gain 
		
		# for each feature/attribute
		for i in range(0, len(attribList)):
		
			# find number of unique values for this feature
			valueSet = []
			seen = {}
			for j in range(0, len(data)):
				# print data[j][i]
				if data[j][i] not in seen:
					seen[data[j][i]] = 1
					valueSet.append(data[j][i])

			
			# very essential hack for addressing efficiency concern: checks the maximum branching!
			# if number of unique values are very large, number of branches will be large
			# check rato of number of classes to number of unique labels
			# if it very low, don't consider entropy for this attribute
			if ((1.0*len(labels)/len(valueSet)) < 0.008):
				continue;
			
			# create table T of size len(valueSet) x len(labels) 
			valueLabels = np.zeros((len(valueSet), len(labels)))
			
			# fill the above table with label counts
			for k in range(0, len(data)):
				rowIndex = self.findIndex(data[k][i], valueSet)
				if (rowIndex != -1): 
					colIndex = self.findIndex(data[k][targetAttr], labels)
					
				if ((rowIndex != -1) and (colIndex != -1)):
					valueLabels[rowIndex, colIndex] += 1
			
			# we need to calculate entropy for each individual row
			entropyList = np.zeros((1, valueLabels.shape[0]))
			weightList  = np.zeros((1, valueLabels.shape[0]))
			
			for m in range(0, valueLabels.shape[0]):
				entropyList[0,m] = self.calcEntropy(valueLabels[m])
			
			# fills up weightList for each row of valueLabel
			self.findWeights(valueLabels, weightList)
			
			aggrEntropy = 0
			
			for m in range(0, valueLabels.shape[0]):
				aggrEntropy += weightList[0,m]*entropyList[0,m]
			
			
			print attribList[i],"->",aggrEntropy
		
			# aggrEntropyList.append(aggrEntropy)
			# entropyDict[aggrEntropy] = i
				
			if (aggrEntropy < minAggrEntropy):
				bestIndex      = i
				minAggrEntropy = aggrEntropy 
			# print valueLabels
			# print weightList
			# print entropyList
			# print aggrEntropy
			# print "---------"
			
			
		# print bestIndex
		"""
		if (len(aggrEntropyList) > 5):
			print aggrEntropyList
			aggrEntropyList.sort()
			number = random.randint(1, 10)
			minaggrEntropy = aggrEntropyList[number%1]
			print minaggrEntropy
			bestIndex = entropyDict[minaggrEntropy]
			
			# sys.exit()
		"""
		attribIndex = attribList.pop(bestIndex)
		
		toStop = False
		# if (minAggrEntropy > 0.85): toStop = True
		# print attribIndex
		# sys.exit()		
		return attribIndex, toStop;		



	## -------------------------------------------------------------------- ##
	# attribList contains indices of attributes except targetAttribute index
	def buildTree(self, data, attribList, targetAttr, trainingDatalen):


		# print data
		default = self.maxLabel(data, targetAttr)	
	
		labels = []
		for i in range(0, len(data)):
			labels.append(data[i][targetAttr])
	
		# print labels
	
		# Pruning if data to be classified is less than 2% of the training data
		if ((len(data)*1.0)/trainingDatalen <= 0.02): 
			return default
		
		# this code won't be hit because of pruning.
		if (data is None) or len(attribList) <= 0:
			# print 'None----------------'
			return default

		## if count of 0th element in the list is equal to length of the list
		## meaning all the instances have same labels -> return that label
		if (labels.count(labels[0]) == len(labels)):
			# print 'here'
			# print labels[0]
			return labels[0]	
	
	
		# experiments with entropy : 5:15
		# this function will return
		toStop = False
		if (len(attribList) > -1):
			bestAttribIndex, toStop = self.maxInfoGain(data, attribList, targetAttr)
			print bestAttribIndex
			# if (toStop): return default
	
		# from attribs list select best attributeIndex and remove from the list
		# number = random.randint(1, 10)
		else:
			print 'here'
			bestAttribIndex = self.popBest(attribList)
		
		
		
		# print bestAttribIndex
	
		tree = {bestAttribIndex : {}}
	
		values = self.findUniqueValues(data, bestAttribIndex)

		# print values
		print "--- noOfBranches->",len(values)
		for value in values:
		
			# find instances having bestAttrib-value = value
			dataSubset = self.findSubset(data, bestAttribIndex, value)
			# print value
			# print dataSubset	
			# print "------------------------------------------------"
			subTree = self.buildTree(dataSubset, attribList, targetAttr, trainingDatalen)
			tree[bestAttribIndex][value] = subTree
	
		return tree


	# find out label for given instanvce
	def myClassify(self, tree, instance, maxCount, defaultLabel = None):
	
		# print tree
	
		# print "=========================================="
	
		if (tree is None):
			# print "returning -> None"
			return None 
	
		if (self.isLeaf(tree) == True):
		
			# print "returning ->", tree
			return tree;
	
		rootAttrib = tree.keys()[0]
		subTrees   = tree.values()[0]
	
		 
		noOfBranches = len(tree.values()[0])
		# print noOfBranches
	
		if (noOfBranches > maxCount[0]): maxCount[0] = noOfBranches
	
		rootAttribValue = instance[rootAttrib]
	
		if rootAttribValue not in subTrees:
			return None;
		 
		return self.myClassify(subTrees[rootAttribValue], instance, maxCount)



	def learn(self, training_set):
		
		
		attribs 	 = range(0,(len(training_set[0])-1))
		targetAttrib = len(training_set[0])-1
		extraneousList = []
	
		# find default label for this dataset
		self.defaultLabel = self.maxLabel( training_set, targetAttrib)
		
		# this function checks if number of unique values are very very large compared to number of unique labels
		# if so then such attributes are going to hamper the decision tree performance
		# chuck them
		self.findExtraneousAttrib(training_set, attribs, targetAttrib, extraneousList)
		
		modifiedAttribs = []
		
		for i in range(0,len(attribs)):
			if(attribs[i] not in extraneousList):
				modifiedAttribs.append(attribs[i])
		
		attribs = modifiedAttribs
		
			
		self.tree = self.buildTree(training_set, attribs, targetAttrib, len(training_set))
		
		
	def classify(self, test_instance):
		
		maxCount = [0]
		result = self.myClassify(self.tree, test_instance, maxCount)	
		
		if result is None:
			result = self.defaultLabel
		
		return result




def run_decision_tree():
	
	# Load data set
	with open("hw4-task1-data.tsv") as tsv:
		data = [tuple(line) for line in csv.reader(tsv, delimiter="\t")]
	
	K = 10
	totalSum = 0
	for j in range (0,10):
		training_set = [x for i, x in enumerate(data) if i % K != j]
		test_set = [x for i, x in enumerate(data) if i % K == j]

	
		tree = DecisionTree()
		# Construct a tree using training set
		tree.learn( training_set )

		# Classify the test set using the tree we just constructed
		results = []
		for instance in test_set:
			result = tree.classify( instance[:-1] )
			results.append( result == instance[-1] )
	
		# Accuracy
		accuracy = float(results.count(True))/float(len(results))
		totalSum += accuracy
		print "-------------------accuracy: %.4f" % accuracy

	accuracy = totalSum/10
	print accuracy
	# Writing results to a file (DO NOT CHANGE)
	f = open("result.txt", "w")
	f.write("accuracy: %.4f" % accuracy)
	f.close()	
	
	
if __name__ == "__main__":
	run_decision_tree()
