package music.rbm

import music.linalg.{DenseVector, Matrix, functions}
import music.util.Logger

import scala.util.Random

class RBM(val nVisible: Int, val nHidden: Int,
          val lr: Double = 0.005,
          val batchSize: Int = 300,
          val nEpoch: Int = 30) extends Logger {

  var W: Matrix = Matrix.randomInit(nVisible, nHidden)
  var bh: DenseVector = DenseVector.zero(nHidden)
  var bv: DenseVector = DenseVector.zero(nVisible)

  def generate(): DenseVector = {
    val init = DenseVector.zero(nVisible)
    gibbsSampling(3, init)
  }


  def fit(xs: Array[DenseVector]): Unit = {

    info(s"start training, input size is (${xs.length}, ${xs(0).size})")
//    info(s"init logistic loss = ${logisticLoss(xs)}")
    info(s"init rmse = ${rmse(xs)}")
    for (i <- 0 until nEpoch) {
      val shuffledX: Seq[DenseVector] = Random.shuffle(xs.toSeq)
      var begInd = 0
      while (begInd < xs.length) {
        val endInd = if (begInd + batchSize < xs.length) begInd + batchSize else xs.length
        val xBatch = shuffledX.slice(begInd, endInd)
        iter(i, xBatch)
        begInd += batchSize
      }
//      info(s"epoch $i done with logistic loss = ${logisticLoss(xs)}")
      info(s"epoch $i done with rmse = ${rmse(xs)}")
    }
  }

  def rmse(xs: Array[DenseVector]): Double = {
    val squaredError = xs.map(x => {
      val sampledX = gibbsStep(x)
      val deltaX = x - sampledX
      functions.norm2(deltaX)
    })
    math.sqrt(squaredError.sum / squaredError.length)
  }

  def logisticLoss(xs: Array[DenseVector]): Double = {
    val losses = xs.map(x => {
      val sampleX = gibbsStep(x)
      val loss = x.dot(sampleX)
      math.log(1 + math.exp(-1 * loss))
    })
    losses.sum / losses.length
  }

  private def getSampleTime(i: Int): Int = {
    if (i >= 200) {
      5
    } else if (i >= 100) {
      3
    } else {
      1
    }
  }

  def iter(i: Int, xBatch: Seq[DenseVector]): Unit = {

    var WDelta: Matrix = Matrix.zero(nVisible, nHidden)
    var bvDelta: DenseVector = DenseVector.zero(nVisible)
    var bhDelta: DenseVector = DenseVector.zero(nHidden)

    xBatch.foreach (
      (xk: DenseVector) => {
        val h: DenseVector = sampleH(xk)
        val xSample: DenseVector = gibbsSampling(getSampleTime(i), xk)
        val hSample: DenseVector = sampleH(xSample)

        WDelta += xk.vectorProduct(h) - xSample.vectorProduct(hSample)
        bvDelta += xk - xSample
        bhDelta += h - hSample
      }
    )

    val curBatchSize = xBatch.length

    W = W + WDelta / curBatchSize * lr
    bv = bv + bvDelta / curBatchSize * lr
    bh = bh + bhDelta / curBatchSize * lr

//    info(s"bv = ${bh.toString}")
//    info(s"delta bv = ${bhDelta.toString}")

  }

  def sampleH(xk: DenseVector): DenseVector = {
    val hkProb: DenseVector = functions.sigmoid(xk.matmul(W) + bh)
    sample(hkProb)
  }

  def gibbsStep(xk: DenseVector): DenseVector = {
    val hkProb: DenseVector = functions.sigmoid(xk.matmul(W) + bh)
    val hk: DenseVector = sample(hkProb)

    val xProb: DenseVector = functions.sigmoid(
      hk.matmul(W.transpose) + bv)
    sample(xProb)
  }

  def gibbsSampling(k: Int, xk: DenseVector): DenseVector = {

    var xSample = xk
    for (_ <- Range(0, k)) {
      xSample = gibbsStep(xSample)
    }
    xSample
  }

  def sample(prob: Double): Double = {
//    math.floor(prob + Random.nextGaussian())
    if (prob + Random.nextGaussian() >= 0.5) {
      1.0
    } else {
      0.0
    }
  }

  def sample(pv: DenseVector): DenseVector = {
    val res = pv.values.map(sample)
    new DenseVector(res)
  }

}
