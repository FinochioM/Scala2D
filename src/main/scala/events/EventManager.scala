package events

import scala.collection.mutable

class EventManager {
  private val listeners: mutable.Map[Class[_ <: Event], mutable.ListBuffer[Event => Unit]] = mutable.Map()
  
  def subscribe[T <: Event](eventClass: Class[T])(handler: T => Unit): Unit = {
    val handlers = listeners.getOrElseUpdate(eventClass, mutable.ListBuffer())
    handlers += (handler.asInstanceOf[Event => Unit])
  }
  
  def unsubscribe[T <: Event](eventClass: Class[T])(handler: T => Unit): Unit = {
    listeners.get(eventClass).foreach { handlers =>
      handlers -= (handler.asInstanceOf[Event => Unit])
    }
  }
  
  def publish(event: Event): Unit = {
    listeners.get(event.getClass).foreach { handlers =>
      handlers.foreach(handler => handler(event))
    }
  }
}