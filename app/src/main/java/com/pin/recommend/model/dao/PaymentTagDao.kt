package com.pin.recommend.model.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.pin.recommend.model.entity.PaymentTag

@Dao
interface PaymentTagDao {
    @Insert
    fun insertPaymentTag(tag: PaymentTag): Long

    @Update
    fun updatePaymentTag(tag: PaymentTag): Int

    @Delete
    fun deletePaymentTag(tag: PaymentTag)

    @Query("DELETE FROM PaymentTag")
    fun deleteAll()

    @Query("SELECT * FROM PaymentTag")
    fun findAll(): List<PaymentTag>

    @Query("SELECT * FROM PaymentTag WHERE id = :id")
    fun findById(id: Long): PaymentTag?

    @Query("SELECT * FROM PaymentTag WHERE id = :id")
    fun findByTrackedId(id: Long): LiveData<PaymentTag?>

    @Query("SELECT * FROM PaymentTag")
    fun findTrackedAll(): LiveData<List<PaymentTag>>

}
