package com.example.envii

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import androidx.lifecycle.MutableLiveData

class AuthViewModel : ViewModel(){
    private val auth : FirebaseAuth= FirebaseAuth.getInstance()

    private val _authState = MutableLiveData<AuthState>()
    val authState : LiveData<AuthState> = _authState

    init {
        checkAuthStatus()
    }

    fun checkAuthStatus(){
        if(auth.currentUser != null){
            _authState.value = AuthState.Authenticated
        }else{
            _authState.value = AuthState.UnAuthenticated
        }
    }

    fun login(email : String, password : String){

        if(email.isEmpty() || password.isEmpty()){
            _authState.value = AuthState.Error("Email or password cannot be empty")
            return
        }

        _authState.value = AuthState.Loading
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener {task->
                if(task.isSuccessful){
                    _authState.value = AuthState.Authenticated
                }else{
                    _authState.value = AuthState.Error(task.exception?.message ?: "Something went wrong")
                }
            }
    }

    fun signup(email : String, password : String, cpassword : String){

        if(email.isEmpty() || password.isEmpty() || cpassword.isEmpty()){
            if(password != cpassword){
                _authState.value = AuthState.Error("Passwords do not match")
            }else {
                _authState.value = AuthState.Error("Email or password cannot be empty")
            }
            return
        }

        if(password != cpassword){
            _authState.value = AuthState.Error("Passwords do not match")
            return
        }

        _authState.value = AuthState.Loading
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {task->
                if(task.isSuccessful){
                    _authState.value = AuthState.Authenticated
                }else{
                    _authState.value = AuthState.Error(task.exception?.message ?: "Something went wrong")
                }
            }
    }

    fun signout(){
        auth.signOut()
        _authState.value = AuthState.UnAuthenticated
    }
}

sealed class AuthState{
    object Authenticated : AuthState()
    object UnAuthenticated : AuthState()
    object Loading : AuthState()
    data class Error(val message : String) : AuthState()
}