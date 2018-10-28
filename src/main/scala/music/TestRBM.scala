package music

import java.nio.file.Paths

import music.linalg.DenseVector
import music.midi.MidiHandler
import music.rbm.RBM

object TestRBM {

  def main(args: Array[String]): Unit = {
    val midiHandler = new MidiHandler()

    val train_path = Paths.get(
      "src", "main", "resources", "train_data").toString

    val numTimeStamp = 30
    val xs: Array[DenseVector] = midiHandler
      .readMidiFromDirectory(train_path)
      .flatMap((song: Array[Array[Int]]) => {
        Range(0, song.length / numTimeStamp)
          .toArray
          .map(i => song.slice(i, i + numTimeStamp).flatten)
      })
      .map(arr => new DenseVector(arr))


    val nVisible = xs(0).size
    val rbm = new RBM(
      nVisible,
      nHidden = 100,
      lr = 0.005,
      batchSize = 50,
      nEpoch = 50)

    rbm.fit(xs)

    val timeStampSize = nVisible / numTimeStamp
    val res = rbm.generate()
      .values
      .map(_.toInt)
      .sliding(timeStampSize, timeStampSize)
      .toArray

    val res_path = Paths.get(
      "src", "main", "resources", "tmp", "res").toString
    midiHandler.writeMidiToFile(res, res_path)

    midiHandler.closeGateWay()

  }

}
