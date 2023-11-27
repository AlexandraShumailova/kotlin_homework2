package com.example.hwapi

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hwapi.data.Product
import com.example.hwapi.data.Result
import com.example.hwapi.data.ProductsRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProductsViewModel(private val productsRepository: ProductsRepository): ViewModel() {

    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products = _products.asStateFlow()

    private val _showErrorToastChannel = Channel<Boolean>()
    val showErrorToastChannel = _showErrorToastChannel.receiveAsFlow()

    private val _iserror = MutableStateFlow(false)
    val iserror = _iserror.asStateFlow()

    private val _loading = MutableStateFlow(true)
    val loading = _loading.asStateFlow()

    fun loadingItems() {
        _loading.value = true
        viewModelScope.launch {
            productsRepository.getProductsList().collectLatest { result ->
                when(result) {
                    is Result.Error -> {
                        _showErrorToastChannel.send(true)
                        _iserror.value = true
                        _loading.value = false
                    }
                    is Result.Success -> {
                        result.data?.let { products ->
                            _products.update { products }
                        }
                        _iserror.value = false
                        _loading.value = false
                    }
                }
            }
        }
    }
}