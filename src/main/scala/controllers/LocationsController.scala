import main.db.{DbAdapter, DbAdapterBase}
import main.model.Location

import scala.collection.mutable.LinkedHashMap

class LocationsController(val dBAdapter: DbAdapterBase = DbAdapter) {
  def retrieveAll(): LinkedHashMap[String, LinkedHashMap[String, Seq[Location]]] = {
    dBAdapter.getLocations()
  }

}