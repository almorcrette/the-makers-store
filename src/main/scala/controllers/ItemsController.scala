import main.db.{DbAdapter, DbAdapterBase}
import main.model.Item

import scala.collection.mutable.ArrayBuffer

class ItemsController(val dBAdapter: DbAdapterBase = DbAdapter) {
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