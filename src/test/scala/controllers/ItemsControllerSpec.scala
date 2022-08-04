import main.db.DbAdapterBase
import main.model.Item
import org.scalamock.scalatest.MockFactory
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

import scala.collection.mutable.ArrayBuffer

class ItemsControllerSpec extends AnyWordSpec with Matchers with MockFactory {
  "ItemsController.retrieveAll" should {
    "fetch all items" in {
      val mockDbAdapter = mock[DbAdapterBase[ArrayBuffer[Item]]]
      val mockItem = mock[Item]
      val mockDbItemsArray = ArrayBuffer(
        mockItem
      )

      val itemsController = new ItemsController(mockDbAdapter)


      (mockDbAdapter.getItems _).expects().returns(mockDbItemsArray)
      itemsController.retrieveAll() should equal(mockDbItemsArray)
    }
  }
  "ItemsController.retrieveById" should {
    "fetch the item with matching id" in {
      val mockDbAdapter = mock[DbAdapterBase[ArrayBuffer[Item]]]
      val mockItem = mock[Item]
      val mockDbItemsArray = ArrayBuffer(mockItem)

      val itemsController = new ItemsController(mockDbAdapter)
      (mockDbAdapter.getItems _).expects().returns(mockDbItemsArray)
      (mockItem.id _).expects().returns(0)
      itemsController.retrieveById(0) should equal(mockItem)
    }
    "throw error if no item with matching id" in {
      // call DbAdapter.getItems
      // filter DbAdapter.getItems return value by id
      // to throw error
    }
  }
}