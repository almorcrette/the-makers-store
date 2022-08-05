import main.db.{DbAdapter, DbAdapterBase}
import main.model.Item

import scala.collection.mutable.ArrayBuffer

class ItemsController(val dBAdapter: DbAdapterBase = DbAdapter) {

  def create(name: String, price: Double, quantity: Int, availableLocales: List[String]): Item = {
    val lastId = retrieveAll.last.id
    val newItem = new Item((lastId + 1), name, price, quantity, availableLocales)
//    println("newItem.id: ", newItem.id)
//    println("newItem.name: ", newItem.name)
//    println("newItem.price: ", newItem.price)
//    println("newItem.quantity: ", newItem.quantity)
//    println("newItem.availableLocales: ", newItem.availableLocales)
    dBAdapter.createItem(newItem)
    newItem
  }

  def update(id: Int, name: String, price: Double, quantity: Int, availableLocales: List[String]): Item = {
    val itemUpdate = new Item(id, name, price, quantity, availableLocales)
    DbAdapter.updateItem(id, itemUpdate)
    itemUpdate
  }

  def retrieveAll(): ArrayBuffer[Item] = {
    dBAdapter.getItems()
  }

  def retrieveById(id: Int): Item = {
    val retrievedItems = dBAdapter.getItems().filter(item => item.id == id)
    if (retrievedItems.length == 0) {
      throw new Exception("Item not found")
    } else {
      retrievedItems(0)
    }
  }
}