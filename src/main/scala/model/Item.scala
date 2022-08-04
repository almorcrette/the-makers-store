package main.model

import main.db.ItemInterface

class Item(val id: Int, val name: String, val price: Double, val quantity: Int, val availableLocales: List[String]) extends ItemInterface

