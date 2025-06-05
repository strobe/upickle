package upickle

import upickle.default._
import utest._

object ReaderDefaultsTests extends TestSuite {

  case class Data(x: Float, y: Float)
  case class Person(
    @upickle.implicits.key("full-name") name: String,
    @upickle.implicits.serializeDefaults(true) age: Option[Int] = Some(42),
    isStudent: Option[Boolean],

    @upickle.implicits.readerDefaults(false)
    isAdmin: Boolean,

    @upickle.implicits.readerDefaults(1333)
    rating: Int,

    @upickle.implicits.readerDefaults(Seq(1,2,3))
    array: Seq[Int],

    @upickle.implicits.readerDefaults(2.99)
    double: Double,

    @upickle.implicits.readerDefaults(Data(0.5,0.5))
    data: Data
  )

  object Data {
    // This generates both Reader and Writer for optional fields
    implicit val rw: ReadWriter[Data] = macroRW
  }
  object Person {
    // This generates both Reader and Writer for optional fields
    implicit val rw: ReadWriter[Person] = macroRW
  }

  val tests = Tests {
    test("@readerDefaults(false) behavior") {
      import Data._
      import Person._

      val jsonWithAll  = """{"full-name":"Alice","age":42,"isStudent":false, "isAdmin":true, "rating":10, "array":[3,4,5], "double":1.0, "data":{"x":1,"y":1}}"""
      val jsonWithout  = """{"full-name":"Alice","age":42,"isStudent":false }"""

      val reconstructedPerson1 = read[Person](jsonWithAll)
      val reconstructedPerson2 = read[Person](jsonWithout)

      println(s"Deserialized2 reconstructedPerson1: $reconstructedPerson1")
      println(s"Deserialized2 reconstructedPerson2: $reconstructedPerson2")

      assert(reconstructedPerson1 == Person("Alice", Some(42), Some(false), true, 10, Seq(3,4,5), 1.0, Data(1,1)))
      assert(reconstructedPerson2 == Person("Alice", Some(42), Some(false), false, 1333, Seq(1,2,3), 2.99, Data(0.5,0.5)))
    }
  }

}
