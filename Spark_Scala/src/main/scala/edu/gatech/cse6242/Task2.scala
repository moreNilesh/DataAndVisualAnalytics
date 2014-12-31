package edu.gatech.cse6242

import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._
import org.apache.spark.SparkConf




object Task2 {

   def tokenize(s: String): (Long, Long) = {
				
	val tokens = s.split("\t")
	// System.out.println(tokens(1))
        val src    = tokens(0).toLong
	val dst    = tokens(1).toLong
	val weight = tokens(2).toLong
	(dst, weight)							
   }

   def filterZeroWeights (s: String) : Boolean = {

	val tokens = s.split("\t")

	(tokens(2) != "0")
   }


  def main(args: Array[String]) {
    val sc = new SparkContext(new SparkConf().setAppName("Task2"))

    // read the file
    val file = sc.textFile("hdfs://localhost:8020" + args(0))

    // val line = file.flatMap(line => line.split("\t"))
    // file.flatMap(tokenize)
    
   

    System.out.println ("---------------My output----------------")

    // val pairs = line.map(s => (s,1))
    // val pairs = file.map(s => (s,1))
    var filteredRecs = file.filter(filterZeroWeights)
 
    val pairs = filteredRecs.map(tokenize)
   
    val countPairs = pairs.reduceByKey((a,b)=> a + b).map(x => x._1.toString() + "\t" + x._2.toString())

    // val stringPairs = countPairs.map(toStrings)


// val count = countPairs(0).toString + "\t" + 


// countPairs(1).toString
    // System.out.println(count)

    System.out.println ("---------------My output----------------")



    /* need to be implemented */

    // store output on given HDFS path.
    // YOU NEED TO CHANGE THIS
    // count.saveAsTextFile("hdfs://localhost:8020" + args(1))
    countPairs.saveAsTextFile("hdfs://localhost:8020" + args(1))
  }
}
