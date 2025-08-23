package com.iamashad.speakit.di

import com.iamashad.speakit.BuildConfig
import com.iamashad.speakit.data.api.WhisperApiService
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {


    @Provides
    @Singleton
    fun provideMoshi(): Moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()


    @Provides
    @Singleton
    @Named("groqBaseUrl")
    fun provideBaseUrl(): String = BuildConfig.GROQ_BASE_URL

    @Provides
    @Singleton
    @Named("groqApiKey")
    fun provideGroqApiKey(): String = BuildConfig.GROQ_API_KEY

    @Provides
    @Singleton
    fun provideAuthInterceptor(@Named("groqApiKey") apiKey: String): Interceptor =
        Interceptor { chain ->
            val req = chain.request().newBuilder()
                .addHeader("Authorization", "Bearer $apiKey")
                .build()
            chain.proceed(req)
        }

    @Provides
    @Singleton
    fun provideOkHttpClient(auth: Interceptor): OkHttpClient {
        val logging = HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY
            else HttpLoggingInterceptor.Level.NONE
        }
        return OkHttpClient.Builder()
            .addInterceptor(auth)
            .addInterceptor(logging)
            // timeouts OK because you enabled desugaring
            .callTimeout(java.time.Duration.ofSeconds(60))
            .connectTimeout(java.time.Duration.ofSeconds(20))
            .readTimeout(java.time.Duration.ofSeconds(60))
            .writeTimeout(java.time.Duration.ofSeconds(60))
            .build()
    }

    @Provides @Singleton
    fun provideRetrofit(
        @Named("groqBaseUrl") baseUrl: String,
        client: OkHttpClient,
        moshi: Moshi
    ): Retrofit = Retrofit.Builder()
        .baseUrl(if (baseUrl.endsWith("/")) baseUrl else "$baseUrl/")
        .client(client)
        .addConverterFactory(retrofit2.converter.scalars.ScalarsConverterFactory.create())
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()


    @Provides
    @Singleton
    fun provideWhisperApi(retrofit: Retrofit): WhisperApiService =
        retrofit.create(WhisperApiService::class.java)
}
