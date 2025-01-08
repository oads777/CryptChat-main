package com.worldsvoice.cryptchat.model

import java.io.Serializable

data class UserModel(
    var userId: String? = null,
    var username: String? = null,
    var email: String? = null,
    var password: String? = null,
    var passKey: String? = null
): Serializable