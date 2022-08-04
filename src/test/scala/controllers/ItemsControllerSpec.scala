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
      (mockDbAdapter.getItems _).expects().returns(mockDbItemsArray)
      (mockDbAdapter.createItem _).expects(*)


      (mockDbAdapter.createItem _).expects (where {
        newItem: Item =>
          newItem.id == 1 &&
          newItem.name == "Junk" &&
          newItem.price == 49.95 &&
          newItem.quantity == 100 &&
          newItem.availableLocales == List("France")
      })

      itemsController.create(
        "Junk",
        49.95,
        100,
        List("France")
      )
    }
  }
}