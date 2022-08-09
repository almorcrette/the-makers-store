import main.db.{DbAdapter, DbAdapterBase}
import main.model.{Item, Location}

import scala.collection.mutable
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

  def update(
              id: Int,
              name: Option[String] = None,
              price: Option[Double] = None,
              quantity: Option[Int] = None,
              availableLocales: Option[List[String]] = None
            )
  : Item = {
    val itemToUpdate = retrieveById(id)
    val nameUpdate = name match {
      case Some(newName) => newName
      case None => itemToUpdate.name
    }
    val priceUpdate = price match {
      case Some(newPrice) => newPrice
      case None => itemToUpdate.price
    }
    val quantityUpdate = quantity match {
      case Some(newQuantity) => newQuantity
      case None => itemToUpdate.quantity
    }
    val availableLocalesUpdate = availableLocales match {
      case Some(newLocales) => newLocales
      case None => itemToUpdate.availableLocales
    }
    val itemUpdate = new Item(id, nameUpdate, priceUpdate, quantityUpdate, availableLocalesUpdate)
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

  def retrieveByName(name: String): Item = {
    val retrievedItems = dBAdapter.getItems().filter(item => item.name.toLowerCase == name.toLowerCase)
    if (retrievedItems.length == 0) {
      throw new Exception("Item not found")
    } else {
      retrievedItems(0)
    }
  }

  def retrieveByLocation(locationName: String): ArrayBuffer[Item] = {
    dBAdapter.getItems().filter(
      (item) => availableAtLocation(item, locationName)
    )
  }

  private def availableAtLocation(item: Item, locationName: String): Boolean = {
    item.availableLocales.contains(
      getContinentOfLocation(locationName)
    )
  }

  private def getContinentOfLocation(locationName: String): String  = {
    findContinentOfLocation(locationName, dBAdapter.getLocations()) match {
      case Some(continent) => continent._1
      case None => "N/A"
    }
  }

  private def findContinentOfLocation(
                                       locationName: String,
                                       allLocations: mutable.LinkedHashMap[String, mutable.LinkedHashMap[String, Seq[Location]]]
                                     ): Option[(String, mutable.LinkedHashMap[String, Seq[Location]])] = {
    allLocations.find(
      (continent) => listCountries(continent).exists(
        country => country.exists(
          location => location.name == locationName
        )
      )
    )
  }

  private def listCountries(continent: (String, mutable.LinkedHashMap[String, Seq[Location]])): Iterable[Seq[Location]] = {
    continent._2.values
  }

}