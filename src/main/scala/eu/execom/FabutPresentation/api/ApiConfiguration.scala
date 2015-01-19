package eu.execom.FabutPresentation.api

import eu.execom.FabutPresentation.service._

trait ApiConfiguration extends ServiceConfiguration {

  lazy val authenticationApi: AuthenticationApi = new AuthenticationApi(userDao, securedService)
}
