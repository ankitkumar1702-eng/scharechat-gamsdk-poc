package com.example.gamsdkpoc.core.di

import com.example.gamsdkpoc.data.repository.AdRepositoryImpl
import com.example.gamsdkpoc.domain.repository.AdRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module for ad-related dependencies.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class AdModule {

    /**
     * Binds the AdRepository implementation.
     */
    @Binds
    @Singleton
    abstract fun bindAdRepository(
        adRepositoryImpl: AdRepositoryImpl
    ): AdRepository
}
