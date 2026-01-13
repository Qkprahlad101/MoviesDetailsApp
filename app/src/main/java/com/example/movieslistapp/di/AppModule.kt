package com.example.movieslistapp.di

import com.example.movieslistapp.BuildConfig
import com.example.movieslistapp.data.ApiService
import com.example.movieslistapp.data.repository.GetMoviesRepository
import com.example.movieslistapp.ui.viewModel.MoviesViewModel
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val appModule = module {
    single {
        HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    single {
        OkHttpClient.Builder()
            .addInterceptor { chain ->
                val original = chain.request()
                val url = original.url.newBuilder()
                    .addQueryParameter("apikey", BuildConfig.API_KEY)
                    .build()
                chain.proceed(original.newBuilder().url(url).build())
            }
            .addInterceptor(get<HttpLoggingInterceptor>())
            .build()
    }


    single {
        Retrofit.Builder()
            .baseUrl("https://www.omdbapi.com/")
            .client(get())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    single {
        get<Retrofit>().create(ApiService::class.java)
    }
    single { GetMoviesRepository(apiService = get()) }
    viewModel { MoviesViewModel(getMoviesRepository = get()) }
}