package music.linalg

import scala.util.Random

class Matrix(val vectors: Array[DenseVector]) {

  def nrow: Int = vectors.length
  def ncol: Int = if (vectors.length == 0) 0 else vectors(0).size

  def this(mat: Array[Array[Double]]) = {
    this(mat.map(arr => new DenseVector(arr)))
  }

  def this(mat: Array[Array[Int]]) = {
    this(mat.map(arr => new DenseVector(arr)))
  }

  def matmul(that: Matrix): Matrix = {
    // skip size check
    val res: Array[DenseVector] = vectors.map(row => {
      val newRowData: Array[Double] = that.cols.map(col => col.dot(row))
      new DenseVector(newRowData)
    })
    new Matrix(res)
  }

  def getRow(ind: Int): DenseVector = {
    vectors(ind)
  }

  def getCol(ind: Int): DenseVector = {
    new DenseVector(vectors.map(arr => arr(ind)))
  }

  def cols: Array[DenseVector] = {
    Range(0, ncol).map(getCol).toArray
  }

  def transpose: Matrix = {
    new Matrix(cols)
  }

  def -(that: Matrix): Matrix = {
    Matrix.subtract(this, that)
  }

  def +(that: Matrix): Matrix = {
    Matrix.add(this, that)
  }

  def *(k: Double): Matrix = {
    Matrix.multiply(this, k)
  }

  def /(k: Double): Matrix = {
    Matrix.div(this, k)
  }

}

object Matrix {

  def zero(n: Int, m: Int): Matrix = {
    val f: () => DenseVector = () => DenseVector.zero(m)
    val res = Array.fill[DenseVector](n)(f())
    new Matrix(res)
  }

  def randomInit(n: Int, m: Int): Matrix = {
    val init: () => DenseVector = () => {
      val d: Seq[Double] = Range(0, m).map(_ => Random.nextGaussian() / 1000)
      new DenseVector(d.toArray)
    }
    val res = Array.fill[DenseVector](n)(init())
    new Matrix(res)
  }

  def subtract(a: Matrix, b: Matrix): Matrix = {
    val res = a.vectors.zip(b.vectors).map(p => p._1 - p._2)
    new Matrix(res)
  }

  def add(a: Matrix, b: Matrix): Matrix = {
    val res = a.vectors.zip(b.vectors).map(p => p._1 + p._2)
    new Matrix(res)
  }

  def multiply(m: Matrix, k: Double): Matrix = {
    val res = m.vectors.map(v => v * k)
    new Matrix(res)
  }

  def div(m: Matrix, k: Double): Matrix = {
    multiply(m, 1 / k)
  }

}
