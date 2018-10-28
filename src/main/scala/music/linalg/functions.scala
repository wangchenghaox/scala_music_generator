package music.linalg

import scala.util.Random

object functions {

  def sigmoid(x: Double): Double = {
    1 / (1 + math.exp(-1 * x))
  }

  def sigmoid(v: DenseVector): DenseVector = {
    val res = v.values.map(sigmoid)
    new DenseVector(res)
  }

  def norm2(v: DenseVector): Double = {
    val squareV = v.values.map(x => x * x)
    squareV.sum / squareV.length
  }


}
