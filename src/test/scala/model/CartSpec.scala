import io.jvm.uuid.{StaticUUID, UUID}
import org.scalamock.scalatest.MockFactory
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class CartSpec extends AnyWordSpec with Matchers with MockFactory {
  "Cart.getUUID" should {
    "return the cart's UUID" in {
      val mockUuidFactory = mock[FactoryBase[UUID]]
      val mockUuid = UUIDFactory.create

      (mockUuidFactory.create _).expects().anyNumberOfTimes.returning(mockUuid)

      val cart = new Cart(mockUuidFactory)
      cart.getUUID() should equal (mockUuid)
    }
  }
}