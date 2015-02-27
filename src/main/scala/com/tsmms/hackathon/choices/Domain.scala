import com.google.appengine.api.datastore.{EmbeddedEntity, Key, PropertyContainer}

/** Provides some functionality to store in google datastore. This should work per annotations or reflection or
  * JPA or whatnot, but for now we just to it by hand.
  * We put that into the domain classes directly, so that we can't forget it when an attribute is added. */
trait Storable {
  def copyToEntity(container: PropertyContainer)

  def toEmbeddedEntity = {
    val embeddedEntity = new EmbeddedEntity
    copyToEntity(embeddedEntity)
    embeddedEntity
  }
}

case class Poll(
                 id: Key,
                 adminId: String,
                 name: String,
                 description: String,
                 choices: List[Choice],
                 votes: List[Vote]
                 ) extends Storable {
  def copyToEntity(container: PropertyContainer) = {
    // for now "by hand" , TODO: replace by some sensible way (reflection etc.)
    container.setProperty("adminId", adminId)
    container.setUnindexedProperty("name", name)
    container.setUnindexedProperty("description", description)
    container.setUnindexedProperty("choices", choices map (_.toEmbeddedEntity))
    container.setUnindexedProperty("votes", votes map (_.toEmbeddedEntity))
  }
}

case class Choice(
                   id: String,
                   name: String
                   ) extends Storable {
  def copyToEntity(container: PropertyContainer) = {
    container.setProperty("id", id)
    container.setProperty("name", name)
  }
}

case class Vote(
                 id: String,
                 ratings: List[Rating]
                 ) extends Storable {
  def copyToEntity(container: PropertyContainer) = {
    container.setProperty("id", id)
    container.setProperty("ratings", ratings map (_.toEmbeddedEntity))
  }
}

case class Rating(
                   choiceId: String,

                   /** Rating between 0-9 */
                   rating: Int
                   )
  extends Storable {
  def copyToEntity(container: PropertyContainer) = {
    container.setProperty("choiceId", choiceId)
    container.setProperty("rating", rating)
  }
}
