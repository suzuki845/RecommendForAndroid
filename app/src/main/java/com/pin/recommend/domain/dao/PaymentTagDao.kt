package com.pin.recommend.domain.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.pin.recommend.domain.entity.PaymentTag

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
    fun watchById(id: Long): LiveData<PaymentTag?>

    @Query("SELECT * FROM PaymentTag")
    fun watchAll(): LiveData<List<PaymentTag>>

}
