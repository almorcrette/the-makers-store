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

  def addItem(itemName: String, number: Int = 1): Unit = {
    val availableItems = itemsController.retrieveByLocation(location)
    if (availableItems.map(item => item.name.toLowerCase()).contains(itemName.toLowerCase())) {
      if (availableItems.filter(item => item.name.toLowerCase() == itemName.toLowerCase()).last.quantity >= number) {
        items += (itemName.toLowerCase() -> number)
      } else {
        throw new Exception("Not enough in stock")
      }
    } else {
      throw new Exception("Item not found")
    }
  }

  def viewItems(): Map[String, Int] = {
    items
  }

}