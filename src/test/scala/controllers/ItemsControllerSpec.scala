import main.db.DbAdapterBase
import main.model.Item
import org.scalamock.scalatest.MockFactory
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

import scala.collection.mutable.ArrayBuffer

class ItemsControllerSpec extends AnyWordSpec with Matchers with MockFactory {
  val anItem = new Item(
    0,
    "Useless plastic",
    10.99,
    10,
    List("France")
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
//          newItem.availableLocales == List("France")
//      })

      itemsController.create(
        "Junk",
        49.95,
        100,
        List("France")).id should equal (1)
      itemsController.create(
        "Junk",
        49.95,
        100,
        List("France")).name should equal ("Junk")
      itemsController.create(
        "Junk",
        49.95,
        100,
        List("France")).price should equal (49.95)
      itemsController.create(
        "Junk",
        49.95,
        100,
        List("France")).quantity should equal (100)
      itemsController.create(
        "Junk",
        49.95,
        100,
        List("France")).availableLocales should equal (List("France"))
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

        itemsController.update(0, name = Option["Junk"]).id should equal(0)
        itemsController.update(0, name = Option["Junk"]).name should equal("Junk")
        itemsController.update(0, name = Option["Junk"]).price should equal(10.99)


      }
      "updating price if new price given" in {
      }
      "updating quantity if new quantity given" in {
      }
      "updating available locales if new available locales given" in {
      }

    }

    // throw error if no matching id?
  }
}