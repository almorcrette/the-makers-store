//import java.util.UUID
import io.jvm.uuid._

class Cart(val uuidFactory: FactoryBase[UUID] = UUIDFactory) {
  val uuid = uuidFactory.create

  def getUUID() = {
    uuid
  }
}