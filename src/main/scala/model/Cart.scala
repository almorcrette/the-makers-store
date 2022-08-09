//import java.util.UUID
import io.jvm.uuid._
import main.model.Item
import main.payment.{Payment, PaymentAdapter, PaymentAdapterBase}

import scala.::
import scala.collection.immutable.Map

class Cart(
            val location: String,
            val uuidFactory: FactoryBase[UUID] = UUIDFactory,
            val itemsController: ItemsController = new ItemsController,
            val paymentAdapter: PaymentAdapterBase = PaymentAdapter
          ) {
  val uuid = uuidFactory.create
  private var items: Map[String, Int] = Map()

  def getUUID(): UUID = {
    uuid
  }

  def viewItems(): Map[String, Int] = {
    items
  }

  def reset(): Unit = {
    items = Map()
  }

  def addItem(itemName: String, number: Int = 1): Unit = {
    val availableItems = itemsController.retrieveByLocation(location)
    if (availableItems.map(item => item.name.toLowerCase()).contains(itemName.toLowerCase())) {
      if (availableItems.filter(item => item.name.toLowerCase() == itemName.toLowerCase()).last.quantity >= number) {
        val numberThisItemInBasket = items get itemName.toLowerCase()
        if (numberThisItemInBasket.isEmpty) {
          items += (itemName.toLowerCase() -> number)
        } else {
          items += (itemName.toLowerCase() -> (items(itemName.toLowerCase()) + number))
        }
      } else {
        throw new Exception("Not enough in stock")
      }
    } else {
      throw new Exception("Item not found")
    }
  }

  def changeAmount(itemName: String, amount: Int, direction: String = "+"): Unit = {
    val itemNameLC = itemName.toLowerCase
    items.get(itemNameLC) match {
      case None => throw new Exception("Item not in cart")
      case Some(currentNumber) =>
        direction match {
          case "+" =>
            if (itemAmountAvailable(itemName, amount + currentNumber)) {
              items += (itemNameLC -> (items(itemNameLC) + amount))
            } else {
              throw new Exception("Not enough in stock")
            }
          case "-" => items += (itemNameLC -> (items(itemNameLC) - amount))
        }
    }
  }

  def onPaymentSuccess(payment: Payment = new Payment(0, true)): Unit = {
    val itemsPurchased = mapCartToInventoryItems()
    instructInventoryUpdate(itemsPurchased)
    reset()
  }

  def onPaymentFailed(payment: Payment = new Payment(0, false)): Unit = {
    reset()
  }

  def checkout(): Unit = {
    paymentAdapter.makePayment(
      4.95,
      onPaymentSuccess,
      onPaymentFailed
    )

  }

  private def mapCartToInventoryItems(): Map[Item, Int] = {
    viewItems.map(item => (itemsController.retrieveByName(item._1) -> item._2))
  }

  private def instructInventoryUpdate(itemsPurchased: Map[Item, Int]): Unit = {
    itemsPurchased.foreach(
      itemOrder => itemsController.update(
        itemOrder._1.id,
        quantity = Some(itemOrder._1.quantity - itemOrder._2)
      )
    )
  }

  private def itemAmountAvailable(itemName: String, amount: Int): Boolean = {
    val availableItems = itemsController.retrieveByLocation(location)
    availableItems.filter(item => item.name.toLowerCase() == itemName.toLowerCase()).last.quantity >= amount
  }



}