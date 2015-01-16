package eu.execom.FabutPresentation.service

import eu.execom.FabutPresentation.util._

object CREDENTIALS_ARE_INVALID extends UnauthorizedException("CREDENTIALS_ARE_INVALID")

object ACCESS_TOKEN_IS_EXPIRED extends UnauthorizedException("ACCESS_TOKEN_IS_EXPIRED")

object INSUFFICIENT_RIGHTS extends UnauthorizedException("INSUFFICIENT_RIGHTS")

object REFRESH_TOKEN_IS_EXPIRED extends BadRequestException("REFRESH_TOKEN_IS_EXPIRED")

object REFRESH_TOKEN_IS_INVALID extends BadRequestException("REFRESH_TOKEN_IS_INVALID")

object USERS_ALREADY_CONNECTED extends BadRequestException("USERS_ALREADY_CONNECTED")

object FRIENDLIST_REQUEST_ALREADY_SENT extends BadRequestException("FRIENDLIST_REQUEST_ALREADY_SENT")
