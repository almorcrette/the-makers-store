import main.db.DbAdapterBase
import main.model.Location
import org.scalamock.scalatest.MockFactory
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

import scala.collection.mutable

class LocationsControllerSpec extends AnyWordSpec with Matchers with MockFactory {
  val aLocation = new Location(
    0,
    "Woodbridge"
  )
  "LocationsController.retrieveAll" should {
    "fetch all locations" in {
      val mockDbAdapter = mock[DbAdapterBase]
      val mockDbLocationsLinkedHashMap = mutable.LinkedHashMap(
        "Europe" -> mutable.LinkedHashMap(
          "UK" -> Seq(aLocation)
        )
      )
      val locationsController = new LocationsController(mockDbAdapter)

      (mockDbAdapter.getLocations _).expects().returns(mockDbLocationsLinkedHashMap)
      locationsController.retrieveAll() should equal(mockDbLocationsLinkedHashMap)
    }
  }
  "LocationsController.retrieveByContinent" should {

  }
}