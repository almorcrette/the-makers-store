package main.db

trait DbAdapterBase[W] {
  def getItems(): W
}