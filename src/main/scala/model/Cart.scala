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

  def viewItems(): Map[String, Int] = {
    items
  }

  def reset(): Unit = {
    items = Map()
  }

  def addItem(itemName: String, number: Int = 1): Unit = {
    val availableItems = itemsController.retrieveByLocation(location)
    if (availableItems.map(item => item.name.toLowerCase()).contains(itemName.toLowerCase())) {
      if (availableItems.filter(item => item.name.toLowerCase() == itemName.toLowerCase()).last.quantity >= number) {
        val numberThisItemInBasket = items get itemName.toLowerCase()
        if (numberThisItemInBasket.isEmpty) {
          items += (itemName.toLowerCase() -> number)
        } else {
          items += (itemName.toLowerCase() -> (items(itemName.toLowerCase()) + number))
        }
      } else {
        throw new Exception("Not enough in stock")
      }
    } else {
      throw new Exception("Item not found")
    }
  }

  def changeAmount(itemName: String, amount: Int, direction: String = "increase"): Unit = {
    items += (itemName.toLowerCase() -> (items(itemName.toLowerCase()) + amount))
  }



}