//import java.util.UUID
import io.jvm.uuid._
import main.model.Item
import main.payment.{Payment, PaymentAdapter, PaymentAdapterBase}

import scala.::
import scala.collection.immutable.Map
import scala.collection.mutable.ArrayBuffer

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
    val itemsAvailableInLocation = itemsController.retrieveByLocation(location)
    if (isItemAvailableInLocation(itemName, itemsAvailableInLocation)) {
      if (isNumberItemsRequestedAvailable(itemName, number, itemsAvailableInLocation)) {
        val numberThisItemInBasket = items get itemName.toLowerCase()
        if (numberThisItemInBasket.isEmpty) {
          items += (itemName.toLowerCase() -> number)
        } else {
          items += (itemName.toLowerCase() -> (items(itemName.toLowerCase()) + number))
        }
      } else {
        println("Not enough in stock")
      }
    } else {
      println("Item not found")
    }
  }

  private def isNumberItemsRequestedAvailable(
                                               itemName: String,
                                               number: Int,
                                               itemsAvailableInLocation: ArrayBuffer[Item]
                                             ) = {
    itemsAvailableInLocation.filter(
      item => item.name.toLowerCase() == itemName.toLowerCase()
    ).last.quantity >= (number + viewItems().getOrElse(itemName.toLowerCase, 0))
  }

  private def isItemAvailableInLocation(itemName: String, itemsAvailableInLocation: ArrayBuffer[Item]) = {
    itemsAvailableInLocation.map(itemLowerCaseName).contains(itemName.toLowerCase())
  }

  private def itemLowerCaseName(item: Item) = {
    item.name.toLowerCase
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
              println("Not enough in stock")
            }
          case "-" =>
            if (items(itemNameLC) < amount) {
          println("Not enough in cart")
        } else {
              items += (itemNameLC -> (items(itemNameLC) - amount))
            }
        }
    }
  }

  def onPaymentSuccess(payment: Payment = new Payment(0, true)): Unit = {
    val itemsPurchased = mapCartToInventoryItems()
    instructInventoryUpdate(itemsPurchased)
    println(f"payment successful. ${payment.amount} charged")
    reset()
  }

  def onPaymentFailed(payment: Payment = new Payment(0, false)): Unit = {
    println(f"payment failed. ${payment.amount} not charged")
    reset()
  }

  def checkout(): Unit = {
    val onSuccess = onPaymentSuccess _
    val onFailure = onPaymentFailed _
    paymentAdapter.makePayment(
      getTotal(),
      onSuccess,
      onFailure
    )
  }

  private def getTotal(): Double = {
    viewItems.foldLeft(0.0)((runningTotal, cartItem) => {
      val inventoryReferenceItem = itemsController.retrieveByName(cartItem._1)
      runningTotal + inventoryReferenceItem.price * cartItem._2
    })
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
    val itemsAvailableInLocation = itemsController.retrieveByLocation(location)
    itemsAvailableInLocation.filter(item => item.name.toLowerCase() == itemName.toLowerCase()).last.quantity >= amount
  }



}