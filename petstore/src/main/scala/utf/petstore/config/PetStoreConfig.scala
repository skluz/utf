package utf.petstore.config

import com.typesafe.config.ConfigFactory

/**
  * Created by Sławomir Kluz on 13/10/2017.
  */
object PetStoreConfig {

  private val config = ConfigFactory.load("reference-petstore.conf")
  def environment = config.getString("environment")

  private val env = config.getConfig("environments").getConfig(environment)

  object Api {
    lazy val publicUrl: String = env.getString("api.public.url")
  }

  object Web {
    lazy val url: String = env.getString("web.url")
  }

}
