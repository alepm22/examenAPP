package com.example.myapplication.hilt



import android.content.Context

import com.calyr.framework.network.RemoteDataSource
import com.calyr.framework.network.RetrofitBuilder
import com.example.myapplication.data.MovieRepository
import com.example.myapplication.local.LocalDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton



@Module
@InstallIn(SingletonComponent::class)
object AppModules {

    @Provides
    fun localDataSource(@ApplicationContext context: Context): LocalDataSource {
        return LocalDataSource(context = context)
    }

    @Provides
    fun provideRetrofit(): RetrofitBuilder {
        return RetrofitBuilder
    }

    @Provides
    @Singleton
    fun provideRemoteDataSource(
        retrofit: RetrofitBuilder
    ): RemoteDataSource {
        return RemoteDataSource(retrofit = retrofit)
    }

    @Provides
    @Singleton
    fun provideNetworkUtils(@ApplicationContext context: Context): Network {
        return Network(context)
    }

    @Provides
    @Singleton
    fun provideMovieRepository(
        remoteDataSource: RemoteDataSource,
        localDataSource: LocalDataSource,
        networkUtils: Network,
        @ApplicationContext context: Context
    ): MovieRepository {
        return MovieRepository(remoteDataSource, localDataSource, networkUtils, context)
    }
}
