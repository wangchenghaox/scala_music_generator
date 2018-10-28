package music.util

import java.text.SimpleDateFormat
import java.util.Date

trait Logger {

  private val format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

  private def log(level: String, str: String): Unit = {
    println(s"${format.format(new Date())} - ${level.toUpperCase} - $str")
  }

  def info(str: String): Unit = {
    log("info", str)
  }

  def warn(str: String): Unit = {
    log("warn", str)
  }

}
