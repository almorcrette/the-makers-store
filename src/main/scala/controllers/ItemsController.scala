import main.db.{DbAdapter, DbAdapterBase, ItemInterface}
import main.model.Item

import scala.collection.mutable.ArrayBuffer

class ItemsController(val dBAdapter: DbAdapterBase[ArrayBuffer[ItemInterface]] = DbAdapter) {
  def retrieveAll(): ArrayBuffer[ItemInterface] = {
    dBAdapter.getItems()
  }

  def retrieveById(id: Int): ItemInterface = {
    dBAdapter.getItems().filter(item => item.id == id )(0)
  }
}