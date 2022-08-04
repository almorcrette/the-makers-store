import main.db.{DbAdapter, DbAdapterBase}
import main.model.Item

import scala.collection.mutable.ArrayBuffer

class ItemsController(val dBAdapter: DbAdapterBase[ArrayBuffer[Item]] = DbAdapter) {
  def retrieveAll(): ArrayBuffer[Item] = {
    dBAdapter.getItems()
  }
}