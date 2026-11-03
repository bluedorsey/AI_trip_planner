package com.souldream

import android.app.Application
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BeachAccess
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Terrain
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.souldream.data.BackendApi
import com.souldream.data.HttpException
import com.souldream.data.StoredTrip
import com.souldream.data.TripStore
import com.souldream.model.Destination
import com.souldream.model.RecentPlan
import com.souldream.model.TripCategory
import com.souldream.ui.screens.explore.ExploreUiState
import com.souldream.ui.screens.home.HomeUiState
import com.souldream.ui.screens.saved.SavedUiState
import com.souldream.ui.screens.tripplan.TripPlanUiState
import com.souldream.ui.state.Async
import com.souldream.ui.state.dataOrNull
import com.souldream.ui.state.map
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/** Owns the app's data: Firestore (trips, destinations) and the backend (plan generation). */
@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModel(app: Application) : AndroidViewModel(app) {
    private val auth = FirebaseAuth.getInstance()
    private val uid = MutableStateFlow(auth.currentUser?.uid)
    private val authListener = FirebaseAuth.AuthStateListener { uid.value = it.currentUser?.uid }

    private val categories = listOf(
        TripCategory("beach", "Beach", Icons.Filled.BeachAccess),
        TripCategory("mountains", "Mountains", Icons.Filled.Terrain),
        TripCategory("city", "City", Icons.Filled.LocationCity),
        TripCategory("food", "Food", Icons.Filled.Restaurant),
    )

    private val trips: StateFlow<Async<List<StoredTrip>>> = uid.flatMapLatest { u ->
        if (u == null) flowOf(Async.Loading) else TripStore.observeTrips(u).asAsync()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), Async.Loading)

    private val destinations: StateFlow<Async<List<Destination>>> = uid.flatMapLatest { u ->
        if (u == null) flowOf(Async.Loading) else TripStore.observeDestinations().asAsync()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), Async.Loading)

    val homeState: StateFlow<HomeUiState> = combine(trips, destinations) { t, d ->
        HomeUiState(
            categories = Async.Success(categories),
            destinations = d,
            recentPlans = t.map { list -> list.take(10).map { it.plan.toRecent() } },
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), HomeUiState())

    val savedState: StateFlow<SavedUiState> = trips
        .map { t -> SavedUiState(t.map { list -> list.filter { it.saved }.map { it.plan.toRecent() } }) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), SavedUiState())

    val exploreState: StateFlow<ExploreUiState> = destinations
        .map { ExploreUiState(it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ExploreUiState())

    private val _tripPlan = MutableStateFlow(TripPlanUiState())
    val tripPlanState: StateFlow<TripPlanUiState> = _tripPlan.asStateFlow()

    private var currentTripId: String? = null
    private var generation: Job? = null

    init {
        auth.addAuthStateListener(authListener)
    }

    /** Generates a plan from the Home search box, stores it in Firestore and shows it. */
    fun generate(query: String, categoryId: String?) {
        generation?.cancel()
        currentTripId = null
        _tripPlan.value = TripPlanUiState() // loading
        val style = categories.firstOrNull { it.id == categoryId }?.let { " (trip style: ${it.label})" }.orEmpty()
        generation = viewModelScope.launch {
            try {
                val plan = BackendApi.plan(query.trim() + style)
                uid.value?.let { TripStore.add(it, plan, query) }
                currentTripId = plan.id
                _tripPlan.value = TripPlanUiState(Async.Success(plan), isSaved = false)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                android.util.Log.e("Generate", "plan failed", e)
                val res = if (e is HttpException && e.code == 401) R.string.error_sign_in_again else R.string.error_plan
                _tripPlan.value = TripPlanUiState(Async.Failure(getApplication<Application>().getString(res)))
            }
        }
    }

    fun openTrip(id: String) {
        generation?.cancel()
        val stored = trips.value.dataOrNull()?.firstOrNull { it.plan.id == id } ?: return
        currentTripId = id
        _tripPlan.value = TripPlanUiState(Async.Success(stored.plan), isSaved = stored.saved)
    }

    fun closeTrip() {
        generation?.cancel()
        currentTripId = null
        _tripPlan.value = TripPlanUiState()
    }

    fun setSaved(saved: Boolean) {
        val u = uid.value ?: return
        val id = currentTripId ?: return
        TripStore.setSaved(u, id, saved)
    }

    override fun onCleared() {
        auth.removeAuthStateListener(authListener)
    }
}

private fun <T> kotlinx.coroutines.flow.Flow<T>.asAsync() =
    map<T, Async<T>> { Async.Success(it) }.catch { emit(Async.Failure(it.message)) }

private fun com.souldream.model.TripPlan.toRecent() =
    RecentPlan(id = id, title = title, dateRange = dateRange, location = location, imageUrl = heroImageUrl)
