import main.db.{DbAdapter, DbAdapterBase}
import main.model.{Item, Location}
import org.scalamock.scalatest.MockFactory
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

import scala.collection.mutable
import scala.collection.mutable.{ArrayBuffer, ListBuffer}

class ItemsControllerSpec extends AnyWordSpec with Matchers with MockFactory {
  val anItem = new Item(
    0,
    "Useless plastic",
    10.99,
    10,
    List("Paris")
  )

  "ItemsController.retrieveAll" should {
    "fetch all items" in {
      val mockDbAdapter = mock[DbAdapterBase]
      val mockItem = mock[Item]
      val mockDbItemsArray = ArrayBuffer(anItem)

      val itemsController = new ItemsController(mockDbAdapter)


      (mockDbAdapter.getItems _).expects().returns(mockDbItemsArray)
      itemsController.retrieveAll() should equal(mockDbItemsArray)
    }
  }
  "ItemsController.retrieveById" should {
    "fetch the item with matching id" in {
      val mockDbAdapter = mock[DbAdapterBase]
      val mockDbItemsArray = ArrayBuffer(anItem)

      val itemsController = new ItemsController(mockDbAdapter)
      (mockDbAdapter.getItems _).expects().returns(mockDbItemsArray)
      itemsController.retrieveById(0) should equal(anItem)
    }
    "throw error if no item with matching id" in {
      val mockDbAdapter = mock[DbAdapterBase]
      val mockDbItemsArray = ArrayBuffer(anItem)

      val itemsController = new ItemsController(mockDbAdapter)
      (mockDbAdapter.getItems _).expects().returns(mockDbItemsArray)

      val thrown = the [Exception] thrownBy {
        itemsController.retrieveById(1)
      }
      thrown.getMessage should equal ("Item not found")
    }
  }
  "ItemsController.create" should {
    "create a new item" in {
      val mockDbAdapter = mock[DbAdapterBase]

      val mockDbItemsArray = ArrayBuffer(anItem)

      val itemsController = new ItemsController(mockDbAdapter)
      (mockDbAdapter.getItems _).expects().anyNumberOfTimes().returns(mockDbItemsArray)
      (mockDbAdapter.createItem _).expects(*).anyNumberOfTimes() // problem: doesn't check what is passed as argument


//      (mockDbAdapter.createItem _).expects (where {
//        newItem: Item =>
//          newItem.id == 1 &&
//          newItem.name == "Junk" &&
//          newItem.price == 49.95 &&
//          newItem.quantity == 100 &&
//          newItem.availableLocales == List("Paris")
//      })

      itemsController.create(
        "Junk",
        49.95,
        100,
        List("Paris")).id should equal (1)
      itemsController.create(
        "Junk",
        49.95,
        100,
        List("Paris")).name should equal ("Junk")
      itemsController.create(
        "Junk",
        49.95,
        100,
        List("Paris")).price should equal (49.95)
      itemsController.create(
        "Junk",
        49.95,
        100,
        List("Paris")).quantity should equal (100)
      itemsController.create(
        "Junk",
        49.95,
        100,
        List("Paris")).availableLocales should equal (List("Paris"))
    }
  }
  "ItemsController.update" should {
    "update an item with matching id" should {
      "updating name if new name given" in {
        val mockDbAdapter = mock[DbAdapterBase]
        val mockDbItemsArray = ArrayBuffer(anItem)
        val itemsController = new ItemsController(mockDbAdapter)
        (mockDbAdapter.getItems _).expects().anyNumberOfTimes().returns(mockDbItemsArray)
        (mockDbAdapter.updateItem _).expects(0, *).anyNumberOfTimes() // problem: doesn't check what item is passed as argument

        itemsController.update(0, name = Option("Junk")).id should equal(0)
        itemsController.update(0, name = Option("Junk")).name should equal("Junk")
        itemsController.update(0, name = Option("Junk")).price should equal(10.99)
        itemsController.update(0, name = Option("Junk")).quantity should equal(10)
        itemsController.update(0, name = Option("Junk")).availableLocales should equal(List("Paris"))

      }
      "updating price if new price given" in {
        val mockDbAdapter = mock[DbAdapterBase]
        val mockDbItemsArray = ArrayBuffer(anItem)
        val itemsController = new ItemsController(mockDbAdapter)
        (mockDbAdapter.getItems _).expects().anyNumberOfTimes().returns(mockDbItemsArray)
        (mockDbAdapter.updateItem _).expects(0, *).anyNumberOfTimes() // problem: doesn't check what item is passed as argument

        itemsController.update(0, price = Option(49.95)).id should equal(0)
        itemsController.update(0, price = Option(49.95)).name should equal("Useless plastic")
        itemsController.update(0, price = Option(49.95)).price should equal(49.95)
        itemsController.update(0, price = Option(49.95)).quantity should equal(10)
        itemsController.update(0, price = Option(49.95)).availableLocales should equal(List("Paris"))
      }
      "updating quantity if new quantity given" in {
        val mockDbAdapter = mock[DbAdapterBase]
        val mockDbItemsArray = ArrayBuffer(anItem)
        val itemsController = new ItemsController(mockDbAdapter)
        (mockDbAdapter.getItems _).expects().anyNumberOfTimes().returns(mockDbItemsArray)
        (mockDbAdapter.updateItem _).expects(0, *).anyNumberOfTimes() // problem: doesn't check what item is passed as argument

        itemsController.update(0, quantity = Option(100)).id should equal(0)
        itemsController.update(0, quantity = Option(100)).name should equal("Useless plastic")
        itemsController.update(0, quantity = Option(100)).price should equal(10.99)
        itemsController.update(0, quantity = Option(100)).quantity should equal(100)
        itemsController.update(0, quantity = Option(100)).availableLocales should equal(List("Paris"))
      }
      "updating available locales if new available locales given" in {
        val mockDbAdapter = mock[DbAdapterBase]
        val mockDbItemsArray = ArrayBuffer(anItem)
        val itemsController = new ItemsController(mockDbAdapter)
        (mockDbAdapter.getItems _).expects().anyNumberOfTimes().returns(mockDbItemsArray)
        (mockDbAdapter.updateItem _).expects(0, *).anyNumberOfTimes() // problem: doesn't check what item is passed as argument

        itemsController.update(0, availableLocales = Option(List("Berlin"))).id should equal(0)
        itemsController.update(0, availableLocales = Option(List("Berlin"))).name should equal("Useless plastic")
        itemsController.update(0, availableLocales = Option(List("Berlin"))).price should equal(10.99)
        itemsController.update(0, availableLocales = Option(List("Berlin"))).quantity should equal(10)
        itemsController.update(0, availableLocales = Option(List("Berlin"))).availableLocales should equal(List("Berlin"))
      }
    }

    "ItemsController.retrieveByLocation" should {
      "fetch all items available in location by name" in {
        val mockDbAdapter = mock[DbAdapterBase]
        val anotherItemsController = new ItemsController(mockDbAdapter)

        val scoop = new Item(2, "Icecream scoop", 4.95, 1000, List("Europe"))
        val blender = new Item(3, "Blender", 44.50, 200, List("Europe", "NA"))
        val breadMaker = new Item(4, "Bread maker", 99.99, 50, List("NA"))
        val mockItemsInventory = ArrayBuffer(scoop, blender, breadMaker)
        val woodbridgeItems = ArrayBuffer(scoop, blender)

        val aLocation = new Location(0, "Woodbridge")
        val anotherLocation = new Location(2, "Reading")
        val aFrenchLocation = new Location(3, "Le Vigan")

        val mockLocations = mutable.LinkedHashMap(
          "Europe" -> mutable.LinkedHashMap(
            "UK" -> Seq(aLocation, anotherLocation),
            "France" -> Seq(aFrenchLocation)
          )
        )

        (mockDbAdapter.getItems _).expects().anyNumberOfTimes.returns(mockItemsInventory)
        (mockDbAdapter.getLocations _).expects().anyNumberOfTimes.returns(mockLocations)

        anotherItemsController.retrieveByLocation("Woodbridge") should equal(woodbridgeItems)
      }


    }

  }
}