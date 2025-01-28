package com.pin.recommend.domain.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.pin.recommend.domain.entity.Payment
import com.pin.recommend.domain.entity.PaymentAndTag
import java.util.*

@Dao
interface PaymentDao {
    @Insert
    fun insertPayment(payment: Payment): Long

    @Update
    fun updatePayment(payment: Payment): Int

    @Delete
    fun deletePayment(payment: Payment)

    @Query("DELETE FROM Payment")
    fun deleteAll()

    @Query("SELECT * FROM Payment")
    fun findAll(): List<Payment>

    @Query("SELECT * FROM Payment WHERE id = :id")
    fun findById(id: Long): Payment?

    @Query("SELECT * FROM Payment WHERE id = :id")
    fun findByTrackedId(id: Long): LiveData<Payment?>

    @Query("SELECT * FROM Payment WHERE characterId = :characterId")
    fun findByCharacterId(characterId: Long): List<Payment>

    @Transaction
    @Query("SELECT * FROM Payment WHERE characterId = :characterId")
    fun findByTrackedCharacterIdPaymentAndTag(characterId: Long): LiveData<List<PaymentAndTag>>

    @Transaction
    @Query("SELECT * FROM Payment WHERE characterId = :characterId AND (createdAt >= :start AND createdAt <= :end) ORDER BY updatedAt ASC")
    fun findByTrackedCharacterIdPaymentAndTagInDate(
        characterId: Long,
        start: Date,
        end: Date
    ): LiveData<List<PaymentAndTag>>

    @Transaction
    @Query("SELECT * FROM Payment WHERE id = :id")
    fun findByTrackedIdPaymentAndTag(id: Long): LiveData<PaymentAndTag?>

    @Transaction
    @Query("SELECT * FROM Payment WHERE id = :id")
    fun findByIdPaymentAndTag(id: Long): PaymentAndTag?


}
