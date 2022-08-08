import main.db.{DbAdapter, DbAdapterBase}
import main.model.Location

import scala.collection.mutable
import scala.collection.mutable.LinkedHashMap

class LocationsController(val dBAdapter: DbAdapterBase = DbAdapter) {
  def retrieveAll(): LinkedHashMap[String, LinkedHashMap[String, Seq[Location]]] = {
    dBAdapter.getLocations()
  }

  def retrieveByContinent(continent: String) = { // Probably should be returning the Location objects
    dBAdapter.getLocations().get(continent) match {
      case Some(countries) =>
        countries.map((pair) => pair._2).flatten.map((location) => location.name)
      case None =>
        throw new Exception("Continent not found")
    }
  }

}