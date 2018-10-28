package music.midi

import java.nio.file.{Files, Path, Paths}

import py4j.GatewayServer
import music.util.Logger

class MidiHandler extends Logger {

  private val server = new GatewayServer()
  server.start()
  private val pythonImpl: PythonMidi = server
    .getPythonServerEntryPoint(Array[Class[_]](classOf[PythonMidi]))
    .asInstanceOf[PythonMidi]

  def closeGateWay(): Unit = server.shutdown()


  def readMidiFromDirectory(dir: String): Array[Array[Array[Int]]] = {

    val path: Path = Paths.get(dir).toAbsolutePath
    val midiFiles: Array[String] = Files.list(path).toArray.map(_.toString)
    midiFiles
      .map(f => {
        info(s"read midi from $f")
        readMidiFromFile(f)
      })
  }

  def readMidiFromFile(file: String): Array[Array[Int]] = {
    pythonImpl.read(file)
  }

  def writeMidiToFile(data: Array[Array[Int]], file: String): Unit = {
    val absolutePath = Paths.get(file).toAbsolutePath.toString
    pythonImpl.write(data, absolutePath)
  }

}

object MidiHandler {
  def main(args: Array[String]): Unit = {
    val reader = new MidiHandler
    val res = reader.readMidiFromDirectory("src/main/resources/train_data")
    val resFile: String = "src/main/resources/tmp/tmp"
    reader.writeMidiToFile(res(0), resFile)

    reader.closeGateWay()
  }
}
