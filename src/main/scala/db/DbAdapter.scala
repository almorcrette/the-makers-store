package main.db

import scala.collection.mutable.ArrayBuffer
import main.model.Item
import scala.collection.mutable.LinkedHashMap
import main.model.Location
import scala.collection.immutable.HashMap

trait DbAdapterBase {
  def dropAndReset: Unit
  def getItems(): ArrayBuffer[Item]
  def getLocations()
  : LinkedHashMap[String, LinkedHashMap[String, Seq[Location]]]

  def updateItem(id: Int, newItem: Item): Unit
  def createItem(newItem: Item): Unit
}

//TODO: This needs to be refactored and tested properly, but it works for now
object DbAdapter extends DbAdapterBase {
  private def readProducts(table: String): ArrayBuffer[ujson.Obj] = {
    val filename = "items" + ".json"
    val jsonString = os.read(os.pwd / "src" / "main" / "resources" / filename)
    val data = ujson.read(jsonString)
    data.value.asInstanceOf[ArrayBuffer[ujson.Obj]]
  }

  private def readProductsRaw(table: String): ujson.Arr = {
    val filename = "items" + ".json"
    val jsonString = os.read(os.pwd / "src" / "main" / "resources" / filename)
    ujson.read(jsonString).asInstanceOf[ujson.Arr]
  }

  private def readLocations(): ujson.Value = {
    val filename = "locations" + ".json"
    val jsonString = os.read(os.pwd / "src" / "main" / "resources" / filename)
    val data = ujson.read(jsonString)
    data
  }

  private def write(table: String, data: ArrayBuffer[Item]) = {
    val filename = table + ".json"

    val newData = ujson.Arr.from(data.map(item => {
      val id = ujson.Num(item.id.toDouble)
      val name = ujson.Str(item.name)
      val price = ujson.Num(item.price.toDouble)
      val quantity = ujson.Num(item.quantity.toDouble)
      val availableLocales = ujson.Arr.from(item.availableLocales)

      LinkedHashMap(
        "id" -> id,
        "name" -> name,
        "price" -> price,
        "quantity" -> quantity,
        "availableLocales" -> availableLocales
      )
    }))

    os.remove(os.pwd / "src" / "main" / "resources" / "items.json")
    os.write(os.pwd / "src" / "main" / "resources" / filename, newData)
  }

  def dropAndReset: Unit = {
    os.remove(os.pwd / "src" / "main" / "resources" / "items.json")
    os.write(
      os.pwd / "src" / "main" / "resources" / "items.json",
      os.read(os.pwd / "src" / "main" / "resources" / "items.base.json")
    )

    os.remove(os.pwd / "src" / "main" / "resources" / "locations.json")
    os.write(
      os.pwd / "src" / "main" / "resources" / "locations.json",
      os.read(os.pwd / "src" / "main" / "resources" / "locations.base.json")
    )
  }

  def getItems(): ArrayBuffer[Item] = {
    val db = DbAdapter.readProducts("items")

    db.map(item => {
      val id = item("id").value.asInstanceOf[Double].toInt
      val name = item("name").value.asInstanceOf[String]
      val price = item("price").value.asInstanceOf[Double]
      val quantity = item("quantity").value.asInstanceOf[Double].toInt
      val availableLocales =
        item("availableLocales").arr.map(_.value.asInstanceOf[String]).toList
      new Item(id, name, price, quantity, availableLocales)
    })
  }

  def getLocations()
  : LinkedHashMap[String, LinkedHashMap[String, Seq[Location]]] = {
    val db = DbAdapter.readLocations()
    val dbHashMap = db.value.asInstanceOf[LinkedHashMap[String, ujson.Obj]]

    var eu = LinkedHashMap[String, Seq[Location]]()
    var na = LinkedHashMap[String, Seq[Location]]()

    dbHashMap.foreach(o => {
      val (key, value) = o
      var locations = Seq[(String, ArrayBuffer[ujson.Value])]()

      if (key == "EU") {
        val uk = ("UK", value("UK").arr)
        val fr = ("FR", value("FR").arr)
        val es = ("ES", value("ES").arr)
        val de = ("DE", value("DE").arr)
        val it = ("IT", value("IT").arr)
        locations = Seq(uk, fr, es, de, it)

        locations.foreach(loc => {
          val (region, city) = loc
          eu.addOne(
            (
              region,
              city
                .map(x => new Location(x("id").num.toInt, x("name").str))
                .toSeq
            )
          )
        })
      } else if (key == "NA") {
        val us = ("US", value("US").arr)
        val ca = ("CA", value("CA").arr)
        locations = Seq(us, ca)

        locations.foreach(loc => {
          val (region, city) = loc

          na.addOne(
            (
              region,
              city
                .map(x => new Location(x("id").num.toInt, x("name").str))
                .toSeq
            )
          )
        })
      }
    })

    LinkedHashMap("EU" -> eu, "NA" -> na)
  }

  def updateItem(id: Int, newItem: Item): Unit = {
    val items = DbAdapter.getItems()
    val newData = items.map(i =>
      i.id match {
        case x if x == id => newItem
        case _            => i
      }
    )

    DbAdapter.write("items", newData)
  }

  def createItem(newItem: Item): Unit = {
    val id = ujson.Num(newItem.id.toDouble)
    val name = ujson.Str(newItem.name)
    val price = ujson.Num(newItem.price.toDouble)
    val quantity = ujson.Num(newItem.quantity.toDouble)
    val availableLocales = ujson.Arr.from(newItem.availableLocales)
    val newJsonObj = LinkedHashMap(
      "id" -> id,
      "name" -> name,
      "price" -> price,
      "quantity" -> quantity,
      "availableLocales" -> availableLocales
    )
    val newJson = ujson.Value(newJsonObj)

    val db = DbAdapter.readProductsRaw("items")
    db.arr.append(newJson)

    os.remove(os.pwd / "src" / "main" / "resources" / "items.json")
    os.write(os.pwd / "src" / "main" / "resources" / "items.json", db)
  }
}
