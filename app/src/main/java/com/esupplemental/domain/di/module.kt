package com.esupplemental.domain.di

import androidx.room.Room
import com.esupplemental.data.local.AppDatabase
import com.esupplemental.data.local.repository.AuthRepository
import com.esupplemental.data.local.repository.GameRepository
import com.esupplemental.data.local.repository.HomeRepository
import com.esupplemental.data.local.repository.MediaRepository
import com.esupplemental.data.local.repository.ProgressRepository
import com.esupplemental.data.local.repository.QuizRepository
import com.esupplemental.data.local.repository.impl.AuthRepositoryImpl
import com.esupplemental.data.local.repository.impl.GameRepositoryImpl
import com.esupplemental.data.local.repository.impl.HomeRepositoryImpl
import com.esupplemental.data.local.repository.impl.MediaRepositoryImpl
import com.esupplemental.data.local.repository.impl.ProgressRepositoryImpl
import com.esupplemental.data.local.repository.impl.QuizRepositoryImpl
import com.esupplemental.domain.manager.MediaPlaybackManager
import com.esupplemental.data.local.manager.MediaPlaybackManagerImpl
import com.esupplemental.data.local.repository.NoteRepository
import com.esupplemental.data.local.repository.impl.NoteRepositoryImpl
import com.esupplemental.data.remote.AudioStoryApi
import com.esupplemental.domain.usecases.GetHomeDataUseCase
import com.esupplemental.domain.usecases.GetLatestQuizResultUseCase
import com.esupplemental.domain.usecases.GetUserInfoUseCase
import com.esupplemental.domain.usecases.GetUserProgressUseCase
import com.esupplemental.domain.usecases.LoginUseCase
import com.esupplemental.domain.usecases.RegisterUseCase
import com.esupplemental.domain.usecases.games.GetGamesUseCase
import com.esupplemental.domain.usecases.games.SaveGameResultUseCase
import com.esupplemental.domain.usecases.games.SyncInitialGamesUseCase
import com.esupplemental.domain.usecases.media.GetMediaDetailUseCase
import com.esupplemental.domain.usecases.media.GetMediaItemsUseCase
import com.esupplemental.domain.usecases.media.ProcessTranscriptUseCase
import com.esupplemental.domain.usecases.media.SyncInitialMediaUseCase
import com.esupplemental.domain.utils.UserPreferences
import com.esupplemental.domain.utils.StoryLoader
import com.esupplemental.presentation.viewmodel.DisappearingTextViewModel
import com.esupplemental.presentation.viewmodel.TwoTruthsLieViewModel
import com.esupplemental.presentation.viewmodel.MinimalPairsViewModel
import com.esupplemental.presentation.viewmodel.SpeedTyperViewModel
import com.esupplemental.presentation.viewmodel.FollowDirectionsViewModel
import com.esupplemental.presentation.viewmodel.StoryRecallViewModel
import com.esupplemental.presentation.viewmodel.AuthViewModel
import com.esupplemental.presentation.viewmodel.GameScreenViewModel
import com.esupplemental.presentation.viewmodel.HomeViewModel
import com.esupplemental.presentation.viewmodel.WordMasterViewModel
import com.esupplemental.presentation.viewmodel.LibraryViewModel
import com.esupplemental.presentation.viewmodel.NoteViewModel
import com.esupplemental.presentation.viewmodel.PlayerViewModel
import com.esupplemental.presentation.viewmodel.ProgressViewModel
import com.esupplemental.presentation.viewmodel.QuizResultViewModel
import com.esupplemental.presentation.viewmodel.StoryExerciseViewModel
import com.esupplemental.presentation.viewmodel.StoryOrderViewModel
import com.esupplemental.presentation.viewmodel.CharacterQuestViewModel
import com.esupplemental.domain.worker.AudioPrefetchWorker
import com.esupplemental.BuildConfig
import com.esupplemental.presentation.viewmodel.SettingsViewModel
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.workmanager.dsl.worker
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

val appModule = module {

    // ── Networking ────────────────────────────────────────────────────────
    single {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }
        OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(1, TimeUnit.HOURS)
            .readTimeout(1, TimeUnit.HOURS)
            .writeTimeout(1, TimeUnit.HOURS)
            .build()
    }

    single {
        Retrofit.Builder()
            .baseUrl(AudioStoryApi.BASE_URL)
            .client(get())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    single { get<Retrofit>().create(AudioStoryApi::class.java) }

    // ── Room database (single instance for the whole app lifetime) ────────
    single {
        Room.databaseBuilder(
            androidContext(),
            AppDatabase::class.java,
            "esupplemental.db"
        )
            .addMigrations(AppDatabase.MIGRATION_15_16)
            .addMigrations(AppDatabase.MIGRATION_16_17)
            .addMigrations(AppDatabase.MIGRATION_17_18)
            .fallbackToDestructiveMigration(true)
            .build()
    }

    // ── DAO (derived from the singleton database) ─────────────────────────
    single { get<AppDatabase>().userDao() }
    single { get<AppDatabase>().homeDao() }
    single { get<AppDatabase>().mediaDao() }
    single { get<AppDatabase>().quizDao() }
    single { get<AppDatabase>().progressDao() }
    single { get<AppDatabase>().gameDao() }
    single { get<AppDatabase>().noteDao() }
    single { UserPreferences(androidContext()) }

    // ── Repository (single: one shared instance, holds no mutable state) ──
    single<AuthRepository> { AuthRepositoryImpl(get()) }
    single<HomeRepository> { HomeRepositoryImpl(get()) }
    single<MediaRepository> {
        MediaRepositoryImpl(
            mediaDao = get(),
            audioStoryApi = get(),
            storyAssetReader = { assetPath -> StoryLoader.load(androidContext(), assetPath) }
        )
    }
    single<QuizRepository> { QuizRepositoryImpl(get(), get()) }
    single<ProgressRepository> { ProgressRepositoryImpl(get()) }
    single<GameRepository> { GameRepositoryImpl(get(), get()) }
    single<NoteRepository> { NoteRepositoryImpl(get()) }
    factory<MediaPlaybackManager> { MediaPlaybackManagerImpl(androidApplication()) }

    // ── Use cases (factory: lightweight, stateless → new instance each time)
    factory { LoginUseCase(get()) }
    factory { RegisterUseCase(get()) }
    factory { GetUserInfoUseCase(get()) }
    factory { GetHomeDataUseCase(get()) }
    factory { GetLatestQuizResultUseCase(get()) }
    factory { GetUserProgressUseCase(get()) }
    factory { GetGamesUseCase(get()) }
    factory { SyncInitialGamesUseCase(get()) }
    factory { SaveGameResultUseCase(get()) }
    factory { SyncInitialMediaUseCase(get(), androidApplication()) }
    factory { GetMediaItemsUseCase(get()) }
    factory { GetMediaDetailUseCase(get()) }
    factory { ProcessTranscriptUseCase() }

    // ── ViewModel (scoped to the Activity/NavGraph by Koin automatically) ─
    viewModel { AuthViewModel(get(), get(), get()) }
    viewModel { HomeViewModel(get(), get()) }
    viewModel { LibraryViewModel(get()) }
    viewModel { PlayerViewModel(get(), get(), get(), get(), get()) }
    viewModel { StoryExerciseViewModel(get(), get(), get(), androidApplication()) }
    viewModel { QuizResultViewModel(get()) }
    viewModel { ProgressViewModel(get(), get(), get(), get(), get()) }
    viewModel { (gameId: String, mediaId: String?) ->
        WordMasterViewModel(
            application = androidApplication(),
            repository = get(),
            playbackManager = get(),
            saveGameResultUseCase = get(),
            gameId = gameId,
            mediaId = mediaId
        )
    }
    viewModel { (gameId: String, mediaId: String?) ->
        StoryOrderViewModel(
            application = androidApplication(),
            repository = get(),
            playbackManager = get(),
            saveGameResultUseCase = get(),
            gameId = gameId,
            mediaId = mediaId
        )
    }
    viewModel { (gameId: String, mediaId: String?) ->
        CharacterQuestViewModel(
            application = androidApplication(),
            repository = get(),
            playbackManager = get(),
            saveGameResultUseCase = get(),
            gameId = gameId,
            mediaId = mediaId
        )
    }
    viewModel { (gameId: String, mediaId: String?) ->
        DisappearingTextViewModel(
            application = androidApplication(),
            repository = get(),
            playbackManager = get(),
            saveGameResultUseCase = get(),
            gameId = gameId,
            mediaId = mediaId
        )
    }
    viewModel { (gameId: String, mediaId: String?) ->
        TwoTruthsLieViewModel(
            application = androidApplication(),
            repository = get(),
            playbackManager = get(),
            saveGameResultUseCase = get(),
            gameId = gameId,
            mediaId = mediaId
        )
    }
    viewModel { (gameId: String, mediaId: String?) ->
        MinimalPairsViewModel(
            application = androidApplication(),
            repository = get(),
            playbackManager = get(),
            saveGameResultUseCase = get(),
            gameId = gameId,
            mediaId = mediaId
        )
    }
    viewModel { (gameId: String, mediaId: String?) ->
        SpeedTyperViewModel(
            application = androidApplication(),
            repository = get(),
            playbackManager = get(),
            saveGameResultUseCase = get(),
            gameId = gameId,
            initialMediaId = mediaId
        )
    }
    viewModel { (gameId: String, mediaId: String?) ->
        FollowDirectionsViewModel(
            application = androidApplication(),
            repository = get(),
            playbackManager = get(),
            saveGameResultUseCase = get(),
            gameId = gameId,
            initialMediaId = mediaId
        )
    }
    viewModel { (gameId: String, mediaId: String?) ->
        StoryRecallViewModel(
            application = androidApplication(),
            repository = get(),
            playbackManager = get(),
            saveGameResultUseCase = get(),
            gameId = gameId,
            initialMediaId = mediaId
        )
    }
    viewModel { GameScreenViewModel(get(), get()) }
    viewModel { NoteViewModel(get(), get()) }
    viewModel { SettingsViewModel(get(), get()) }

    // ── Worker ────────────────────────────────────────────────────────────
    worker { AudioPrefetchWorker(get(), get()) }
}
