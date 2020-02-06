/*import model.User
import org.mongodb.scala.{MongoClient, MongoCollection, MongoDatabase}
import model.{DBUser, _}

trait DBConnection {
  val mongoClient: MongoClient
}

class MongoDB extends DBConnection{
  val mongoClient = MongoClient()
  val database: MongoDatabase = mongoClient.getDatabase("test").withCodecRegistry(DBUser.codecRegistry)
  val collection: MongoCollection[DBUser] = database.getCollection("user")

}

 */
