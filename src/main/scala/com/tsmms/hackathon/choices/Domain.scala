package com.tsmms.hackathon.choices

import java.util.Date

/** Contains all data about a poll - incl. answers by all users. Stored as one entity since google has quotas
  * on free datastore accesses. :-) */
case class Poll(
                 id: Option[Long],
                 adminId: String,
                 name: String,
                 description: String,
                 expires: Date,
                 created: Date = new Date(),
                 choices: List[Choice] = List.empty,
                 votes: List[Vote] = List.empty
                 )

case class Choice(
                   id: String,
                   name: String
                   )

case class Vote(
                 id: String,
                 username: String,
                 ratings: List[Rating]
                 )

case class Rating(
                   choiceId: String,

                   /** Rating between 0-9 */
                   rating: Int
                   )
