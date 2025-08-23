package com.iamashad.speakit.di


import com.iamashad.speakit.data.repository.AudioRepository
import com.iamashad.speakit.data.repository.AudioRepositoryImpl
import com.iamashad.speakit.data.repository.TranscriptionRepository
import com.iamashad.speakit.data.repository.TranscriptionRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {


    @Binds
    @Singleton
    abstract fun bindAudioRepository(impl: AudioRepositoryImpl): AudioRepository


    @Binds
    @Singleton
    abstract fun bindTranscriptionRepository(impl: TranscriptionRepositoryImpl): TranscriptionRepository
}