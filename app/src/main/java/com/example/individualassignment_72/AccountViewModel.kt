package com.example.individualassignment_72

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AccountViewModel : ViewModel() {

    private val _searchState = MutableStateFlow<SearchState>(SearchState.Initial)
    val searchState: StateFlow<SearchState> = _searchState

    fun fetchUser(name: String, offset: Int, limit: Int) {
        viewModelScope.launch {
            _searchState.value = SearchState.Loading
            try {
                val searchResult = ApiClient.apiService.getAccount(name, offset, limit)
                _searchState.value = SearchState.Success(searchResult)
            } catch (e: Exception) {
                _searchState.value = SearchState.Error(e.message ?: "Something went wrong")
            }
        }
    }

    sealed class SearchState {
        object Initial: SearchState()
        object Loading: SearchState()
        data class Success(val accountInfo: List<Repo>): SearchState()
        data class Error(val errorMessage: String): SearchState()
    }
}