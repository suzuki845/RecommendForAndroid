package com.pin.recommend.model.viewmodel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.pin.recommend.model.AppDatabase;
import com.pin.recommend.model.dao.AccountDao;
import com.pin.recommend.model.entity.Account;

public class AccountViewModel extends AndroidViewModel {

    private AccountDao dao;

    private LiveData<Account> accountLiveData;

    public AccountViewModel(Application application){
        super(application);
        dao = AppDatabase.getDatabase(application.getApplicationContext()).accountDao();
    }

    public LiveData<Account> getAccount(){
        initializeAccount();
        if(accountLiveData == null) {
           accountLiveData = dao.findTrackedById(Account.ACCOUNT_ID);
        }
        return accountLiveData;
    }

    private void initializeAccount(){
        AppDatabase.executor.execute(new Runnable() {
            @Override
            public void run() {
                Account model = dao.findById(Account.ACCOUNT_ID);
                if(model == null) {
                    model = new Account();
                    dao.insertAccount(model);
                }
            }
        });
    }

    public void saveAccount(final Account account){
        AppDatabase.executor.execute(new Runnable() {
            @Override
            public void run() {
                dao.updateAccount(account);
            }
        });
    }

}
