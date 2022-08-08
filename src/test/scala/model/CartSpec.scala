import io.jvm.uuid.{StaticUUID, UUID}
import main.model.Item
import org.scalamock.scalatest.MockFactory
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

import scala.collection.mutable.ArrayBuffer

class CartSpec extends AnyWordSpec with Matchers with MockFactory {
  "Cart.getUUID" should {
    "return the cart's UUID" in {
      val mockUuidFactory = mock[FactoryBase[UUID]]
      val mockUuid = UUIDFactory.create

      (mockUuidFactory.create _).expects().anyNumberOfTimes.returning(mockUuid)

      val cart = new Cart("London", mockUuidFactory)
      cart.getUUID() should equal (mockUuid)
    }
  }

  "Cart.addItem" should {

    "add an item to the cart if it is available in the cart location and the item is in stock" in {
      val mockUuidFactory = mock[FactoryBase[UUID]]
      val mockUuid = UUIDFactory.create

      (mockUuidFactory.create _).expects().anyNumberOfTimes.returning(mockUuid)

      val mockItemsController = mock[ItemsController]

      val scoop = new Item(2, "Icecream scoop", 4.95, 1000, List("Europe"))
      val blender = new Item(3, "Blender", 44.50, 200, List("Europe", "NA"))
      val breadMaker = new Item(4, "Bread maker", 99.99, 50, List("NA"))
      val mockItemsInventory = ArrayBuffer(scoop, blender, breadMaker)

      val cart = new Cart("London", mockUuidFactory, mockItemsController)
      (mockItemsController.retrieveByLocation _).expects("London").returns(mockItemsInventory)

      cart.addItem("Icecream scoop")

      cart.viewItems()("Icecream scoop") should equal(1)
    }
    "raises an error if the name of the item is not found in the inventory" in {
      val mockUuidFactory = mock[FactoryBase[UUID]]
      val mockUuid = UUIDFactory.create

      (mockUuidFactory.create _).expects().anyNumberOfTimes.returning(mockUuid)

      val mockItemsController = mock[ItemsController]

      val scoop = new Item(2, "Icecream scoop", 4.95, 1000, List("Europe"))
      val blender = new Item(3, "Blender", 44.50, 200, List("Europe", "NA"))
      val breadMaker = new Item(4, "Bread maker", 99.99, 50, List("NA"))
      val mockItemsInventory = ArrayBuffer(scoop, blender, breadMaker)

      val cart = new Cart("London", mockUuidFactory, mockItemsController)
      (mockItemsController.retrieveByLocation _).expects("London").returns(mockItemsInventory)

      val thrown = the [Exception] thrownBy {
        cart.addItem("Typo")
      }
      thrown.getMessage should equal ("Item not found")




    }
  }
}