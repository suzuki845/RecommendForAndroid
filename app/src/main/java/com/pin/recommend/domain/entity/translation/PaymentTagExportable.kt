package com.pin.recommend.domain.entity.translation

import com.pin.recommend.domain.entity.PaymentTag
import java.util.*

class PaymentTagExportable {
    var type: Int
    var createdAt: Date
    var updatedAt: Date
    var name: String

    constructor(tag: PaymentTag){
        type = tag.type
        createdAt = tag.createdAt
        updatedAt = tag.updatedAt
        name = tag.tagName
    }

    fun importable(): PaymentTag {
        return PaymentTag(
                id = 0,
                tagName = name,
                type = type,
                createdAt = createdAt,
                updatedAt = updatedAt
        )
    }
}