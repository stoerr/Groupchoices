package com.tsmms.hackathon.choices

import com.google.appengine.api.datastore.{EmbeddedEntity, Entity, PropertyContainer}
import com.tsmms.hackathon.choices.DataStoreStorable._

/** Contains all data about a poll - incl. answers by all users. Stored as one entity since google has quotas
  * on free datastore accesses. :-) */
case class Poll(
                 id: Option[Long],
                 adminId: String,
                 name: String,
                 description: String,
                 choices: List[Choice],
                 votes: List[Vote]
                 ) extends DataStoreStorable {
  def copyToEntity(container: PropertyContainer) = {
    // for now "by hand" , TODO: replace by some sensible way (reflection etc.)
    container.setProperty("adminId", adminId)
    container.setUnindexedProperty("name", name)
    container.setUnindexedProperty("description", description)
    container.setUnindexedProperty("choices", listToEmbeddedEntity(choices map (_.toEmbeddedEntity)))
    container.setUnindexedProperty("votes", listToEmbeddedEntity(votes map (_.toEmbeddedEntity)))
  }

  def this(entity: Entity) =
    this(id = Some(entity.getKey.getId), adminId = entity.getProperty("adminId").asInstanceOf[String],
      name = entity.getProperty("name").asInstanceOf[String], description = entity.getProperty("description")
        .asInstanceOf[String],
      choices = readEmbeddedList(entity, "choices", new Choice(_)), votes = readEmbeddedList(entity, "votes", new
          Vote(_)))
}

case class Choice(
                   id: String,
                   name: String
                   ) extends DataStoreStorable {
  def copyToEntity(container: PropertyContainer) = {
    container.setProperty("id", id)
    container.setProperty("name", name)
  }

  def this(entity: EmbeddedEntity) = this(id = entity.getProperty("id").asInstanceOf[String], name = entity
    .getProperty("name").asInstanceOf[String])
}

case class Vote(
                 id: String,
                 ratings: List[Rating]
                 ) extends DataStoreStorable {
  def copyToEntity(container: PropertyContainer) = {
    container.setProperty("id", id)
    container.setProperty("ratings", listToEmbeddedEntity(ratings map (_.toEmbeddedEntity)))
  }

  def this(entity: EmbeddedEntity) = this(id = entity.getProperty("id").asInstanceOf[String],
    ratings = readEmbeddedList(entity, "ratings", new Rating(_)))
}

case class Rating(
                   choiceId: String,

                   /** Rating between 0-9 */
                   rating: Int
                   )
  extends DataStoreStorable {
  def copyToEntity(container: PropertyContainer) = {
    container.setProperty("choiceId", choiceId)
    container.setProperty("rating", rating)
  }

  def this(entity: EmbeddedEntity) = this(choiceId = entity.getProperty("choiceId").asInstanceOf[String], entity
    .getProperty("rating").asInstanceOf[Number].intValue())
}
