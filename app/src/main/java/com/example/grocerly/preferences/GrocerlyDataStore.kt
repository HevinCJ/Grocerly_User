package com.example.grocerly.preferences


import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.IOException
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map


private val Context.datastore: DataStore<Preferences> by preferencesDataStore(name = "GROCERLY")

class GrocerlyDataStore(context: Context) {

    private val dataStore = context.datastore


    companion object {
        val isLoggedInKey = booleanPreferencesKey("IS_LOGGED")

    }

    suspend fun setLoginState(isLoggedIn:Boolean){
        dataStore.edit {pref->
            pref[isLoggedInKey] = isLoggedIn
        }
    }

    fun getLoginState():Flow<Boolean>{
        return dataStore.data
            .catch {exception->
                if (exception is IOException){
                    emit(emptyPreferences())
                }else{
                    throw exception
                }
            }

            .map {pref->
                val loginState= pref[isLoggedInKey] ?: false
                loginState
            }
    }



}