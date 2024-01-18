package com.example.solox3_dit2b21;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class Session extends ViewModel {

    private final MutableLiveData<User> userLiveData = new MutableLiveData<>();

    public Session() {
        // trigger user load.
    }

    public LiveData<User> getUser() {
        return userLiveData;
    }

}
