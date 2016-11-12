package com.berlinsmartdata.simpleLoader.utils


object Utils {

  def measureDuration[R](block: => R): R = {
    val t0 = System.nanoTime()
    val result = block
    val t1 = System.nanoTime()
    val elapsedTime = (t1 - t0) / 1000000000.0
    println("Elapsed time: " + elapsedTime + " seconds")
    result
  }

}
