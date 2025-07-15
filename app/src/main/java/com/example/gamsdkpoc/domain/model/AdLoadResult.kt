package com.example.gamsdkpoc.domain.model

/**
 * Sealed class representing the result of an ad loading operation.
 */
sealed class AdLoadResult {
    /**
     * Ad loaded successfully.
     */
    object Success : AdLoadResult()
    
    /**
     * Ad loading failed.
     * 
     * @param errorCode The error code from GAM SDK
     * @param message The error message
     */
    data class Error(
        val errorCode: Int,
        val message: String
    ) : AdLoadResult()
    
    /**
     * Ad is currently loading.
     */
    object Loading : AdLoadResult()
}
