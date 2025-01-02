package com.worldsvoice.cryptchat.model

data class MessagesModel(
    var id: String? = null,
    var senderId: String? = null,
    var receiverId: String? = null,
    var encryptedMessage: String? = null,
    var encryptKey: String? = null,
    var dateTime: String? = null
)

