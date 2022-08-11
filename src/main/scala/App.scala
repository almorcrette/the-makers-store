import main.db.DbAdapter
//package main

// This class simply exists so we have a main method to run the application. Feel free to do with it what you please
object App {
  def main(args: Array[String]): Unit = {
    val cart = App.start(args(0))
    App.featureTest(cart)
  }
  
  def start(location: String): Cart= {
    val dBAdapter = DbAdapter
    DbAdapter.dropAndReset
    val itemsController = new ItemsController(dBAdapter)
    new Cart(location, itemsController = itemsController)
  }


  def featureTest(cart: Cart): Unit = {
    println("Adding one delicious soup")
    cart.addItem("Delicious Soup") // Available in EU and NA
    println("Checking cart: " + cart.viewItems)
    println("Adding four lovely apples")
    cart.addItem("Lovely Apple", 4) // Available in EU and NA
    println("Checking cart: " + cart.viewItems)
    println("Attempting to add another lovely apple: ")
    cart.addItem("Lovely Apple")
    println("Checking cart: " + cart.viewItems)
    println("Adding two strange gourds")
    cart.addItem("Strange Gourd", 2) //Only available in EU
    println("Checking cart: " + cart.viewItems)
    println("Adding four orange peels")
    cart.addItem("Orange Peel", 4)
    println("Checking cart: " + cart.viewItems)
    println("Increasing the amount of delicious soups by three")
    cart.changeAmount("Delicious Soup", 3)
    println("Checking cart: " + cart.viewItems)
    print("Attempt to increase lovely apples by 2: ")
    cart.changeAmount("Lovely Apple", 2)
    println("Reducing the amount of lovely apples by two")
    cart.changeAmount("Lovely Apple", 2, "-")
    println("Checking cart: " + cart.viewItems)
    println("Attempt to reduce orange peels by 6: ")
    cart.changeAmount("Orange Peel", 6, "-")
    println("Checking cart: " + cart.viewItems)
    println("Checking cart: " + cart.viewItems)
    print("Attempting to add Yankee Salami: ")
    cart.addItem("Yankee Salami", 2) // Only available in NA
    println("Checking cart: " + cart.viewItems)

    cart.checkout

    println("Checking cart is empty: " + cart.viewItems)

  }
}
