package com.example.myapplication.data

import android.content.Context
import android.widget.Toast
import com.example.myapplication.domain.Movie
import com.example.myapplication.hilt.Network
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class MovieRepository @Inject constructor(
    private val remoteDataSource: IRemoteDataSource,
    private val localDataSource: ILocalDataSource,
    private val networkUtils: Network,
    @ApplicationContext private val context: Context
) {
    suspend fun obtainMovies(): List<Movie> {
        // Check internet connectivity first
        if (networkUtils.isInternetAvailable()) {
            withContext(Dispatchers.Main) {
                Toast.makeText(
                    context,
                    "Conectado a Internet",
                    Toast.LENGTH_SHORT
                ).show()
            }

            try {
                // If internet is available, try to fetch from remote
                val moviesRemote = remoteDataSource.fetchData()
                when (moviesRemote) {
                    is NetworkResult.Success -> {
                        localDataSource.deleteAll()
                        localDataSource.insertMovies(moviesRemote.data)
                        withContext(Dispatchers.Main) {
                            Toast.makeText(
                                context,
                                "Datos actualizados",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                    is NetworkResult.Error -> {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(
                                context,
                                "Error al cargar los datos: ${moviesRemote.error}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        context,
                        "Error de red internet: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        } else {
            withContext(Dispatchers.Main) {
                Toast.makeText(
                    context,
                    "Sin conexión",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        // Always return from local database, whether we updated it or not
        val moviesLocal = localDataSource.getList()
        return when (moviesLocal) {
            is NetworkResult.Success -> moviesLocal.data
            is NetworkResult.Error -> {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        context,
                        "Error al cargar datos: ${moviesLocal.error}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                emptyList()
            }
        }
    }

    suspend fun findById(id: String): Movie? {
        if (!networkUtils.isInternetAvailable()) {
            withContext(Dispatchers.Main) {
                Toast.makeText(
                    context,
                    "Sin conexión",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        val movieLocal = localDataSource.findById(id)
        return when (movieLocal) {
            is NetworkResult.Success -> movieLocal.data
            is NetworkResult.Error -> {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        context,
                        "No se encontró la película: ${movieLocal.error}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                null
            }
        }
    }
}