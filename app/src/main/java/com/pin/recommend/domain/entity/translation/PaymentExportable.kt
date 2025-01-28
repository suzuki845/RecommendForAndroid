package com.pin.recommend.domain.entity.translation

import com.pin.recommend.domain.entity.Payment
import java.util.*

class PaymentExportable {

    var tag: PaymentTagExportable? = null

    var createdAt: Date
    var updatedAt: Date
    var amount: Double
    var body: String?
    var type: Int

    constructor(payment: Payment){
        createdAt = payment.createdAt
        updatedAt = payment.updatedAt
        amount = payment.amount
        body = payment.memo
        type = payment.type
    }

    fun importable(): Payment{
        val payment = Payment(
                id = 0,
                characterId = -1,
                paymentTagId = null,
                createdAt = createdAt,
                updatedAt = updatedAt,
                type = type,
                amount = amount,
                memo = body
        )
        payment.paymentTag =  tag?.importable()
        return payment
    }

}