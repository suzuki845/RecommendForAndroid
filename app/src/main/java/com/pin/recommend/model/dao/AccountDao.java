package com.pin.recommend.model.dao;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.pin.recommend.model.entity.Account;

import java.util.List;

@Dao
public interface AccountDao {

    @Insert
    public long insertAccount(Account account);

    @Update
    public int updateAccount(Account account);

    @Query("DELETE FROM Account")
    public void deleteAll();

    @Query("SELECT * FROM Account WHERE id = :id")
    public LiveData<Account> findTrackedById(long id);

    @Query("SELECT * FROM Account WHERE id = :id")
    public Account findById(long id);

    @Query("SELECT * FROM Account")
    public List<Account> test();


}
