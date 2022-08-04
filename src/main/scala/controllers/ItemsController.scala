import main.db.{DbAdapter, DbAdapterBase}
import main.model.Item

import scala.collection.mutable.ArrayBuffer

class ItemsController(val dBAdapter: DbAdapterBase = DbAdapter) {
  def retrieveAll(): ArrayBuffer[Item] = {
    dBAdapter.getItems()
  }

  def retrieveById(id: Int): Item = {
    dBAdapter.getItems().filter(item => item.id == id )(0)
  }
}