package main.db

trait ItemInterface {
  def id(): Int

  def name(): String

  def price(): Double

  def quantity(): Int

  def availableLocales(): List[String]
}