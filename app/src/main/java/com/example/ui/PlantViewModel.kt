package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.BotanicalData
import com.example.data.GlossaryTerm
import com.example.data.Plant
import com.example.data.PlantCategory
import com.example.data.db.PlantRepository
import com.example.data.db.UserPlant
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PlantViewModel(private val repository: PlantRepository) : ViewModel() {

    // Theme state: true = Forest Moss (Greens/Browns/Cappuccino), false = Blue Meadow (Blues/Whites)
    private val _isGreenTheme = MutableStateFlow(true)
    val isGreenTheme: StateFlow<Boolean> = _isGreenTheme.asStateFlow()

    // Search query for plants
    private val _plantSearchQuery = MutableStateFlow("")
    val plantSearchQuery: StateFlow<String> = _plantSearchQuery.asStateFlow()

    // Selected category filter
    private val _selectedCategory = MutableStateFlow<PlantCategory?>(null)
    val selectedCategory: StateFlow<PlantCategory?> = _selectedCategory.asStateFlow()

    // Search query for glossary words
    private val _glossarySearchQuery = MutableStateFlow("")
    val glossarySearchQuery: StateFlow<String> = _glossarySearchQuery.asStateFlow()

    // Selected plant detail (if any, so users can tap to view)
    private val _selectedPlant = MutableStateFlow<Plant?>(null)
    val selectedPlant: StateFlow<Plant?> = _selectedPlant.asStateFlow()

    // Selected glossary term detail (if any, so users can tap to view word description in details)
    private val _selectedTerm = MutableStateFlow<GlossaryTerm?>(null)
    val selectedTerm: StateFlow<GlossaryTerm?> = _selectedTerm.asStateFlow()

    // Active bottom navigation destination
    private val _currentTab = MutableStateFlow("catalog") // "catalog", "dictionary", "my_garden"
    val currentTab: StateFlow<String> = _currentTab.asStateFlow()

    // Read user plants livedata from Room
    val userPlants: StateFlow<List<UserPlant>> = repository.allUserPlants
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun toggleTheme() {
        _isGreenTheme.value = !_isGreenTheme.value
    }

    fun setPlantSearchQuery(query: String) {
        _plantSearchQuery.value = query
    }

    fun selectCategory(category: PlantCategory?) {
        _selectedCategory.value = category
    }

    fun setGlossarySearchQuery(query: String) {
        _glossarySearchQuery.value = query
    }

    fun selectPlant(plant: Plant?) {
        _selectedPlant.value = plant
        if (plant != null) {
            _selectedTerm.value = null // dismiss term detail
        }
    }

    fun selectTerm(term: GlossaryTerm?) {
        _selectedTerm.value = term
        if (term != null) {
            _selectedPlant.value = null // dismiss plant detail
        }
    }

    fun selectTab(tab: String) {
        _currentTab.value = tab
    }

    // Filter static plants based on category selection & search query
    val filteredPlants: StateFlow<List<Plant>> = combine(
        _plantSearchQuery,
        _selectedCategory
    ) { query, category ->
        BotanicalData.plants.filter { plant ->
            val matchesCategory = (category == null || plant.category == category)
            val matchesQuery = plant.name.contains(query, ignoreCase = true) ||
                    plant.scientificName.contains(query, ignoreCase = true) ||
                    plant.briefDescription.contains(query, ignoreCase = true)
            matchesCategory && matchesQuery
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), BotanicalData.plants)

    // Filter dictionary words based on search query
    val filteredGlossary: StateFlow<List<GlossaryTerm>> = _glossarySearchQuery
        .combine(MutableStateFlow(BotanicalData.glossary)) { query, list ->
            if (query.isBlank()) {
                list
            } else {
                list.filter { term ->
                    term.term.contains(query, ignoreCase = true) ||
                            term.definition.contains(query, ignoreCase = true)
                }
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), BotanicalData.glossary)

    // DB Operations
    fun addPlantToGarden(plant: Plant, intervalDays: Int, notes: String) {
        viewModelScope.launch {
            val userPlant = UserPlant(
                name = plant.name,
                scientificName = plant.scientificName,
                category = plant.category.name,
                customNotes = notes,
                feedingIntervalDays = intervalDays,
                lastFedTimestamp = 0L // Never fed initially
            )
            repository.insert(userPlant)
        }
    }

    fun addNewCustomPlant(name: String, scientificName: String, category: String, intervalDays: Int, notes: String) {
        viewModelScope.launch {
            val userPlant = UserPlant(
                name = name,
                scientificName = scientificName,
                category = category,
                customNotes = notes,
                feedingIntervalDays = intervalDays,
                lastFedTimestamp = System.currentTimeMillis() // mark fed at birth
            )
            repository.insert(userPlant)
        }
    }

    fun deleteFromGarden(plantId: Int) {
        viewModelScope.launch {
            repository.deleteById(plantId)
        }
    }

    fun feedPlant(plantId: Int) {
        viewModelScope.launch {
            repository.feedPlant(plantId, System.currentTimeMillis())
        }
    }

    // Feeding time helper
    fun getFeedingProgressAndStatus(plant: UserPlant): Pair<Float, String> {
        if (plant.lastFedTimestamp == 0L) {
            return Pair(0.0f, "Needs immediate feed 💧")
        }
        val elapsed = System.currentTimeMillis() - plant.lastFedTimestamp
        val limitMillis = plant.feedingIntervalDays * 24L * 60 * 60 * 1000
        val progress = (1.0f - (elapsed.toFloat() / limitMillis.toFloat())).coerceIn(0.0f, 1.0f)
        
        val remaining = limitMillis - elapsed
        return if (remaining <= 0) {
            Pair(0.0f, "Overdue! ⚠️ Need feed now!")
        } else {
            val remainingHours = remaining / (1000 * 60 * 60)
            if (remainingHours < 24) {
                Pair(progress, "Due in $remainingHours Hours ⏱️")
            } else {
                val remainingDays = remainingHours / 24
                Pair(progress, "Due in $remainingDays Days 🌿")
            }
        }
    }
}

class PlantViewModelFactory(private val repository: PlantRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PlantViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PlantViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
