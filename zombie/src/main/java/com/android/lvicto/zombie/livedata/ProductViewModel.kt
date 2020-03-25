package com.android.lvicto.zombie.livedata

import android.util.Log
import androidx.lifecycle.*
import androidx.lifecycle.Transformations.map

class ProductViewModel : LifecycleEventObserver {

    private val repo = ProductRepository()

    val styleColor: MutableLiveData<String> = MutableLiveData()

    val pid: MutableLiveData<String> = MutableLiveData()

    val product: MediatorLiveData<Product?> = MediatorLiveData<Product?>()

    fun initProduct() {
        product.addSource(styleColor) {
            val prod = repo.getProductByStyle(it)
            product.postValue(prod)
        }

        product.addSource(pid) {
            val prod = repo.getProductByPid(it)
            product.postValue(prod)
        }
    }

    val avail: LiveData<Boolean> = map(product) { product ->
        product?.availability?.any { entry ->
            entry.value.any {
                it
            }
        }
    }

    val availability: MediatorLiveData<Boolean> = MediatorLiveData()

    fun initAvailability() {
        availability.addSource(map(product) { product ->
            product?.availability?.any { entry ->
                entry.value.any {
                    it
                }
            }
        }) {
            availability.postValue(it ?: false)
        }
    }

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        Log.d(TAG, "onStateChanged()")

        when (source.lifecycle.currentState) {
            Lifecycle.State.CREATED -> {
                Log.d(TAG, "CREATED ${event.name}")
            }
            Lifecycle.State.STARTED -> {
                Log.d(TAG, "STARTED ${event.name}")
            }
            Lifecycle.State.RESUMED -> {
                Log.d(TAG, "RESUMED ${event.name}")
            }
            Lifecycle.State.DESTROYED -> {
                Log.d(TAG, "DESTROYED ${event.name}")
            }
            else -> {
                Log.d(TAG, "else ${event.name}")
            }
        }
    }

    companion object {
        const val TAG = "ProductViewModel"
    }

}