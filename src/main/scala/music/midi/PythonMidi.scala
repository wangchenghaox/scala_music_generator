package music.midi

/**
 * Implement in python
 */
trait PythonMidi {
  def read(file: String): Array[Array[Int]]
  def write(midi: Array[Array[Int]], file: String): Unit
  def test(str: String): Array[Int]
}
