package com.android.lvicto.zombie.coroutines.retailxsimu.api

import com.android.lvicto.zombie.coroutines.retailxsimu.data.ProductResponse
import com.android.lvicto.zombie.coroutines.retailxsimu.data.ProductRollUpResponse

class ProductApi {

    companion object {
        var delay = 0L // delay to simulated the network speed
    }

    suspend fun getProductFamilyByGtin(gtin: String): ProductRollUpResponse {
        // todo
        return ProductRollUpResponse()
    }

    suspend fun getProductFamilyByRollup(key: String): ProductResponse {
        // todo
        return ProductResponse()
    }

}