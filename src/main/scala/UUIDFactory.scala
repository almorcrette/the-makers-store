import io.jvm.uuid.UUID

object UUIDFactory extends FactoryBase[UUID] {
  def create: UUID = {
    UUID.random
  }
}