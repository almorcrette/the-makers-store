import io.jvm.uuid.{StaticUUID, UUID}
import main.model.Item
import org.scalamock.scalatest.MockFactory
import org.scalatest.{BeforeAndAfterAll, BeforeAndAfterEach}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

import scala.collection.mutable.ArrayBuffer

class CartSpec extends AnyWordSpec with Matchers with MockFactory with BeforeAndAfterEach with BeforeAndAfterAll {

  val mockUuidFactory = stub[FactoryBase[UUID]]
  val mockUuid = UUIDFactory.create
  (mockUuidFactory.create _).when().returns(mockUuid)

  val scoop = new Item(2, "Icecream scoop", 4.95, 1000, List("Europe"))
  val blender = new Item(3, "Blender", 44.50, 200, List("Europe", "NA"))
  val breadMaker = new Item(4, "Bread maker", 99.99, 50, List("NA"))
  val ghost = new Item(5, "Ghost", 20, 0, List("Europe"))
  val zombie = new Item(5, "Zombie", 20, 3, List("Europe"))
  val mockItemsInventory = ArrayBuffer(scoop, blender, breadMaker, ghost, zombie)
  val londonInventory = ArrayBuffer(scoop, blender, ghost, zombie)

  val mockItemsController = stub[ItemsController]

  val cart = new Cart("London", mockUuidFactory, mockItemsController)

  override def beforeEach(): Unit = {
    cart.reset()
  }

  "Cart.getUUID" should {
    "return the cart's UUID" in {
      cart.getUUID() should equal (mockUuid)
    }
  }

  "Cart.addItem" should {
    "add an item to the cart if it is available in the cart location and the item is in stock" in {
      (mockItemsController.retrieveByLocation _).when("London").returns(londonInventory)
      cart.addItem("icecream scoop")
      cart.viewItems()("icecream scoop") should equal(1)
    }
    "add an item to the cart even if typed with different case (if available)" in {
      (mockItemsController.retrieveByLocation _).when("London").returns(londonInventory)
      cart.addItem("iceCREam scoop")
      cart.viewItems()("icecream scoop") should equal(1)
    }
    "add more than one of an item to the cart if it is available in the cart location and the item is in stock" in {
      (mockItemsController.retrieveByLocation _).when("London").returns(londonInventory)
      cart.addItem("Icecream scoop", 5)
      cart.viewItems()("icecream scoop") should equal(5)
    }
    "add more of an item already in the basket (if available)" in {
      (mockItemsController.retrieveByLocation _).when("London").returns(londonInventory)
      cart.addItem("Icecream scoop", 5)
      cart.addItem("Icecream scoop", 5)
      cart.viewItems()("icecream scoop") should equal(10)
    }
    "raises an error if the name of the item is not found in the inventory" in {
      (mockItemsController.retrieveByLocation _).when("London").returns(londonInventory)
      val thrown = the [Exception] thrownBy {
        cart.addItem("Typo")
      }
      thrown.getMessage should equal ("Item not found")
    }
    "raises an error if item is not available in the customer's location" in {
      (mockItemsController.retrieveByLocation _).when("London").returns(londonInventory)
      val thrown = the [Exception] thrownBy {
        cart.addItem("Bread maker")
      }
      thrown.getMessage should equal ("Item not found")
    }
    "raises an error if item is out of stock in the customer's location" in {
      (mockItemsController.retrieveByLocation _).when("London").returns(londonInventory)
      val thrown = the [Exception] thrownBy {
        cart.addItem("Ghost")
      }
      thrown.getMessage should equal ("Not enough in stock")
    }
    "raises an error if trying to add more of an item than is in stock" in {
      (mockItemsController.retrieveByLocation _).when("London").returns(londonInventory)
      val thrown = the [Exception] thrownBy {
        cart.addItem("Zombie", 5)
      }
      thrown.getMessage should equal ("Not enough in stock")
    }

  }
}