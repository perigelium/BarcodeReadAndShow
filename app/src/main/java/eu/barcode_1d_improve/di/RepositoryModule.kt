package eu.barcode_1d_improve.di

import eu.barcode_1d_improve.data.repository.MainRepositoryImpl
import eu.barcode_1d_improve.domain.repository.MainRepository
import org.koin.dsl.module

val repositoryModule = module {
	single<MainRepository> { MainRepositoryImpl() }
}