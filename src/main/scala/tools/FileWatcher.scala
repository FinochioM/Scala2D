package tools

import java.nio.file._
import scala.concurrent.{Future, ExecutionContext}
import scala.util.control.NonFatal

class FileWatcher(paths: List[Path], onChange: Path => Unit)(implicit ec: ExecutionContext) {
  private val watchService = FileSystems.getDefault.newWatchService()
  private val keys = paths.map { path =>
    val key = path.register(watchService, StandardWatchEventKinds.ENTRY_MODIFY)
    key -> path
  }.toMap

  private val watcherThread = Future {
    try {
      while (true) {
        val key = watchService.take()
        val dir = keys(key)
        key.pollEvents().forEach { event =>
          val kind = event.kind()
          if (kind == StandardWatchEventKinds.ENTRY_MODIFY) {
            val relativePath = event.context().asInstanceOf[Path]
            val fullPath = dir.resolve(relativePath).toAbsolutePath
            onChange(fullPath)
          }
        }
        key.reset()
      }
    } catch {
      case NonFatal(e) => e.printStackTrace()
    }
  }

  def stop(): Unit = {
    watchService.close()
  }
}