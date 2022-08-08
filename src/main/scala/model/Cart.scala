//import java.util.UUID
import io.jvm.uuid._

import scala.::
import scala.collection.immutable.Map

class Cart(
            val location: String,
            val uuidFactory: FactoryBase[UUID] = UUIDFactory,
            val itemsController: ItemsController = new ItemsController
          ) {
  val uuid = uuidFactory.create
  private var items: Map[String, Int] = Map()

  def getUUID(): UUID = {
    uuid
  }

  def addItem(itemName: String): Unit = {
    val availableItems = itemsController.retrieveByLocation(location)
    if (availableItems.map(item => item.name).contains(itemName)) {
      if (availableItems.filter(item => item.name == itemName).last.quantity > 0) {
        items += (itemName -> 1)
      } else {
        throw new Exception("Out of stock")
      }
    } else {
      throw new Exception("Item not found")
    }
  }

  def viewItems(): Map[String, Int] = {
    items
  }

}