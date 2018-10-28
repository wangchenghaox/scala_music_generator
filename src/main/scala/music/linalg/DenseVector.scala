package music.linalg

class DenseVector(val values: Array[Double]) {

  def this(vs: Array[Int]) = {
    this(vs.map(_.toDouble))
  }

  def size: Int = values.length

  def dot(that: DenseVector): Double = {
    values.zip(that.values).foldLeft(0.0)(
      (res, pair) => res + pair._1 * pair._2)
  }

  def vectorProduct(that: DenseVector): Matrix = {
    val res: Array[DenseVector] = values.map(v1 => {
      val row: Array[Double] = that.values.map(v2 => v1 * v2)
      new DenseVector(row)
    })
    new Matrix(res)
  }

  def matmul(matrix: Matrix): DenseVector = {
    val res: Array[Double] = matrix.cols.map(c => c.dot(this))
    new DenseVector(res)
  }

  def +(that: DenseVector): DenseVector = {
    DenseVector.add(this, that)
  }

  def -(that: DenseVector): DenseVector = {
    DenseVector.subtract(this, that)
  }

  def *(k: Double): DenseVector = {
    DenseVector.multiply(this, k)
  }

  def /(k: Double): DenseVector = {
    DenseVector.div(this, k)
  }


  def apply(ind: Int): Double = {
    values(ind)
  }

  override def toString: String = {
    values.map(_.toString).mkString(",")
  }

}

object DenseVector {
  def zero(n: Int): DenseVector = {
    new DenseVector(Array.fill(n)(0.0))
  }

  def add(va: DenseVector, vb: DenseVector): DenseVector = {
    val res: Array[Double] = va.values.zip(vb.values).map{
      case (a, b) => a + b
    }
    new DenseVector(res)
  }

  def subtract(va: DenseVector, vb: DenseVector): DenseVector = {
    val res: Array[Double] = va.values.zip(vb.values).map{
      case (a, b) => a - b
    }
    new DenseVector(res)
  }

  def multiply(v: DenseVector, k: Double): DenseVector = {
    val res = v.values.map(v => v * k)
    new DenseVector(res)
  }

  def div(v: DenseVector, k: Double): DenseVector = {
    multiply(v, 1 / k)
  }
}
