package tools

import java.util.concurrent.ConcurrentLinkedQueue

object TaskQueue {
  private val tasks = new ConcurrentLinkedQueue[() => Unit]()
  
  def enqueue(task: () => Unit): Unit = {
    tasks.add(task)
  }
  
  def executeAll(): Unit = {
    while (!tasks.isEmpty){
      val task = tasks.poll()
      if (task != null){
        task()
      }
    }
  }
}