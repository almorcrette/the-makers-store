package main.payment

import scala.util.Random

class Payment(val amount: Double, val success: Boolean)

trait PaymentAdapterBase {
  def makePayment(amount:Double, onSuccess: (Payment) => Any, onFailure: (Payment) => Any): Unit
}

object PaymentAdapter {
  def makePayment(amount:Double, onSuccess: (Payment) => Any, onFailure: (Payment) => Any) = {
    if(Random.nextInt(10) >= 8) {
      onSuccess(new Payment(amount, false))
    }
    else {
      onFailure(new Payment(amount, true))
    }
  }
}
