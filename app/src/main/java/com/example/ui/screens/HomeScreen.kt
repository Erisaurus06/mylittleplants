package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.GlossaryTerm
import com.example.data.Plant
import com.example.data.PlantCategory
import com.example.data.db.UserPlant
import com.example.ui.PlantViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: PlantViewModel,
    modifier: Modifier = Modifier
) {
    val isGreenTheme by viewModel.isGreenTheme.collectAsStateWithLifecycle()
    val currentTab by viewModel.currentTab.collectAsStateWithLifecycle()

    val selectedPlant by viewModel.selectedPlant.collectAsStateWithLifecycle()
    val selectedTerm by viewModel.selectedTerm.collectAsStateWithLifecycle()

    var showAddCustomDialog by remember { mutableStateOf(false) }

    // Outer layout respecting safe drawing areas (notch, bottom system bar)
    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .testTag("home_scaffold"),
        topBar = {
            TopAppBar(
                title = {
                    Column(modifier = Modifier.padding(vertical = 4.dp)) {
                        Text(
                            text = "My Little Plant",
                            fontWeight = FontWeight.Bold,
                            fontSize = 24.sp,
                            color = Color(0xFF1B3D2F) // #1B3D2F NaturalHeaderGreen
                        )
                        Text(
                            text = "BOTANICAL CATALOG",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF7A7C78) // #7A7C78 NaturalTextSecondary
                        )
                    }
                },
                actions = {
                    // Cute dynamic theme switcher badge styled exactly like the prototype's avatar
                    Row(
                        modifier = Modifier
                            .padding(end = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(androidx.compose.foundation.shape.CircleShape)
                                .background(Color(0xFFE5EBE0)) // #E5EBE0 sage light
                                .border(2.dp, Color.White, androidx.compose.foundation.shape.CircleShape)
                                .clickable { viewModel.toggleTheme() }
                                .padding(2.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = if (isGreenTheme) "🌿" else "💧", fontSize = 22.sp)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = Color(0xFF1B3D2F)
                ),
                modifier = Modifier.testTag("app_top_bar")
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp,
                modifier = Modifier.testTag("bottom_nav_bar")
            ) {
                NavigationBarItem(
                    selected = currentTab == "catalog",
                    onClick = { viewModel.selectTab("catalog") },
                    icon = { Text("🌿", fontSize = 20.sp) },
                    label = { Text("Catalog") },
                    modifier = Modifier.testTag("nav_item_catalog")
                )
                NavigationBarItem(
                    selected = currentTab == "dictionary",
                    onClick = { viewModel.selectTab("dictionary") },
                    icon = { Text("📖", fontSize = 20.sp) },
                    label = { Text("Glossary") },
                    modifier = Modifier.testTag("nav_item_dictionary")
                )
                NavigationBarItem(
                    selected = currentTab == "my_garden",
                    onClick = { viewModel.selectTab("my_garden") },
                    icon = { Text("🏡", fontSize = 20.sp) },
                    label = { Text("My Garden") },
                    modifier = Modifier.testTag("nav_item_garden")
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.surface,
                            MaterialTheme.colorScheme.background
                        )
                    )
                )
                .padding(innerPadding)
        ) {
            // Screen content switcher
            AnimatedContent(
                targetState = currentTab,
                transitionSpec = {
                    fadeIn(animationSpec = spring()) togetherWith fadeOut(animationSpec = spring())
                },
                label = "tab_animation"
            ) { activeTab ->
                when (activeTab) {
                    "catalog" -> CatalogTabScreen(viewModel = viewModel)
                    "dictionary" -> GlossaryTabScreen(viewModel = viewModel)
                    "my_garden" -> GardenTabScreen(
                        viewModel = viewModel,
                        onAddPlantClick = { showAddCustomDialog = true }
                    )
                }
            }

            // Detail Panel Overlay for Plants
            selectedPlant?.let { plant ->
                PlantDetailSheet(
                    plant = plant,
                    onDismiss = { viewModel.selectPlant(null) },
                    onAddToGarden = { interval, notes ->
                        viewModel.addPlantToGarden(plant, interval, notes)
                        viewModel.selectPlant(null)
                    }
                )
            }

            // Detail Panel Overlay for Glossary Words
            selectedTerm?.let { term ->
                GlossaryTermDetailSheet(
                    term = term,
                    onDismiss = { viewModel.selectTerm(null) }
                )
            }

            // Dialog for adding custom plants
            if (showAddCustomDialog) {
                AddCustomPlantDialog(
                    onDismiss = { showAddCustomDialog = false },
                    onConfirm = { name, scientific, category, interval, notes ->
                        viewModel.addNewCustomPlant(name, scientific, category, interval, notes)
                        showAddCustomDialog = false
                    }
                )
            }
        }
    }
}

// ==========================================
// 1. CATALOG TAB SCREEN
// ==========================================
@Composable
fun CatalogTabScreen(viewModel: PlantViewModel) {
    val plants by viewModel.filteredPlants.collectAsStateWithLifecycle()
    val searchQuery by viewModel.plantSearchQuery.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Welcoming Card
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.05f)
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 14.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Welcome to your Botany Companion!",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Browse species categorized by functional attributes. Click any plant to reveal detailed cellular structure, origin, toxic alerts, and specific watering & feeding needs.",
                    fontSize = 12.sp,
                    lineHeight = 17.sp,
                    modifier = Modifier.padding(top = 4.dp),
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                )
            }
        }

        // Search Bar with testTag
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { viewModel.setPlantSearchQuery(it) },
            placeholder = { Text("Search catalog by name, family, description...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", tint = Color(0xFF8B7E6D)) },
            trailingIcon = if (searchQuery.isNotEmpty()) {
                {
                    IconButton(onClick = { viewModel.setPlantSearchQuery("") }) {
                        Icon(Icons.Default.Close, contentDescription = "Clear", tint = Color(0xFF8B7E6D))
                    }
                }
            } else null,
            shape = RoundedCornerShape(24.dp),
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("catalog_search_bar")
                .padding(bottom = 12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFFE0D8CE).copy(alpha = 0.5f),
                unfocusedBorderColor = Color(0xFFE0D8CE).copy(alpha = 0.5f),
                focusedContainerColor = Color(0xFFF2EDE7),
                unfocusedContainerColor = Color(0xFFF2EDE7),
                focusedTextColor = Color(0xFF2D312E),
                unfocusedTextColor = Color(0xFF2D312E)
            )
        )

        // Categorization Tabs (Edible, Poisonous, Medicinal, Trees)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // "All" Filter Chip
            CategoryChip(
                label = "All Plants",
                emoji = "🌱",
                selected = selectedCategory == null,
                onClick = { viewModel.selectCategory(null) },
                modifier = Modifier.weight(1f)
            )

            // Loop and render categories
            PlantCategory.values().forEach { category ->
                CategoryChip(
                    label = category.name.lowercase().replaceFirstChar { it.uppercase() },
                    emoji = category.icon,
                    selected = selectedCategory == category,
                    onClick = { viewModel.selectCategory(category) },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // List Header
        Text(
            text = selectedCategory?.displayName ?: "All Catalog Entries",
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(bottom = 10.dp, start = 4.dp)
        )

        // List of Plants with custom padded items
        if (plants.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("🔍", fontSize = 48.sp)
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "No specimen matches your query",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f).testTag("catalog_plant_list"),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 8.dp)
            ) {
                items(plants, key = { it.id }) { plant ->
                    PlantCatalogRow(
                        plant = plant,
                        onClick = { viewModel.selectPlant(plant) }
                    )
                }
            }
        }
    }
}

@Composable
fun CategoryChip(
    label: String,
    emoji: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val containerColor = if (selected) {
        Color(0xFF3A5A40) // NaturalActiveGreen
    } else {
        Color.White
    }
    val contentColor = if (selected) {
        Color.White
    } else {
        Color(0xFF4A4C48)
    }
    val borderStroke = if (selected) null else BorderStroke(1.dp, Color(0xFFE0E2DB)) // NaturalBorderSage

    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        color = containerColor,
        contentColor = contentColor,
        border = borderStroke,
        tonalElevation = if (selected) 3.dp else 0.dp,
        modifier = modifier.height(38.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(text = emoji, fontSize = 14.sp)
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun PlantCatalogRow(
    plant: Plant,
    onClick: () -> Unit
) {
    // Elegant catalog card matching Entry 1 & Entry 2 of Natural Tones Theme
    Card(
        shape = RoundedCornerShape(32.dp), // rounded-[2rem]
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        border = BorderStroke(1.dp, Color(0xFFF2EDE7)), // border-[#F2EDE7]
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("plant_catalog_row_${plant.id}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp), // p-5 in Tailwind (~20.dp)
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Visual badge/avatar base
            val avatarBg = when (plant.category) {
                PlantCategory.EDIBLE -> Color(0xFFE5EBE0) // #E5EBE0 Soft Sage
                PlantCategory.POISONOUS -> Color(0xFFFDE9E9) // #FDE9E9 Soft Red
                PlantCategory.MEDICINAL -> Color(0xFFE5EBE0)
                PlantCategory.TREE -> Color(0xFFF2EDE7)
            }

            Box(
                modifier = Modifier
                    .size(80.dp) // w-24 h-24 is ~80dp–96dp
                    .clip(RoundedCornerShape(24.dp)) // rounded-3xl
                    .background(avatarBg),
                contentAlignment = Alignment.Center
            ) {
                Text(text = plant.category.icon, fontSize = 36.sp)
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Text description
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = plant.name,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Color(0xFF1B3D2F) // #1B3D2F Header Green
                    )
                    
                    val badgeText = when (plant.category) {
                        PlantCategory.EDIBLE -> "EDIBLE"
                        PlantCategory.POISONOUS -> "POISON"
                        PlantCategory.MEDICINAL -> "MEDICINAL"
                        PlantCategory.TREE -> "TREE"
                    }
                    val badgeBg = when (plant.category) {
                        PlantCategory.EDIBLE -> Color(0xFFE9F5E9)
                        PlantCategory.POISONOUS -> Color(0xFFFDE9E9)
                        PlantCategory.MEDICINAL -> Color(0xFFE5EBE0)
                        PlantCategory.TREE -> Color(0xFFF2EDE7)
                    }
                    val badgeTextClr = when (plant.category) {
                        PlantCategory.EDIBLE -> Color(0xFF2D6A4F)
                        PlantCategory.POISONOUS -> Color(0xFF9D0208)
                        PlantCategory.MEDICINAL -> Color(0xFF3A5A40)
                        PlantCategory.TREE -> Color(0xFF8B7E6D)
                    }
                    val badgeBorder = badgeTextClr.copy(alpha = 0.2f)

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = badgeBg,
                        border = BorderStroke(1.dp, badgeBorder),
                        modifier = Modifier.padding(start = 4.dp)
                    ) {
                        Text(
                            text = badgeText,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            color = badgeTextClr,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }
                }
                
                Text(
                    text = plant.scientificName,
                    fontStyle = FontStyle.Italic,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF6B705C), // #6B705C Sage gray-green
                    modifier = Modifier.padding(top = 2.dp)
                )

                Text(
                    text = plant.briefDescription,
                    fontSize = 12.sp,
                    maxLines = 1,
                    color = Color(0xFF6B705C),
                    modifier = Modifier.padding(top = 4.dp)
                )

                // Sun & Water details micro indicator row
                Row(
                    modifier = Modifier.padding(top = 10.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Sun dot
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(androidx.compose.foundation.shape.CircleShape)
                                .background(Color(0xFFA3B18A))
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "SUN: ${plant.sunlight.uppercase()}",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF8B7E6D)
                        )
                    }

                    // Water dot
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(androidx.compose.foundation.shape.CircleShape)
                                .background(Color(0xFF344E41))
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "WATER: DAILY",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF8B7E6D)
                        )
                    }
                }
            }
        }
    }
}

// ==========================================
// 2. DICTIONARY GLOSSARY TAB SCREEN
// ==========================================
@Composable
fun GlossaryTabScreen(viewModel: PlantViewModel) {
    val glossary by viewModel.filteredGlossary.collectAsStateWithLifecycle()
    val searchQuery by viewModel.glossarySearchQuery.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Explanatory header about dictionary words
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.05f)
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Botanical Word Dictionary",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "A curated collection of educational terms describing vascular anatomy, cellular processing, and reproductive strategies of wild flora. Structured with robust padding for ultimate readability.",
                    fontSize = 12.sp,
                    lineHeight = 17.sp,
                    modifier = Modifier.padding(top = 4.dp),
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                )
            }
        }

        // Dictionary Word Search Input with testTag
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { viewModel.setGlossarySearchQuery(it) },
            placeholder = { Text("Search word entries (e.g. Chlorophyll, Xylem)...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search Word", tint = Color(0xFF8B7E6D)) },
            trailingIcon = if (searchQuery.isNotEmpty()) {
                {
                    IconButton(onClick = { viewModel.setGlossarySearchQuery("") }) {
                        Icon(Icons.Default.Close, contentDescription = "Reset", tint = Color(0xFF8B7E6D))
                    }
                }
            } else null,
            shape = RoundedCornerShape(24.dp),
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("glossary_search_bar")
                .padding(bottom = 14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFFE0D8CE).copy(alpha = 0.5f),
                unfocusedBorderColor = Color(0xFFE0D8CE).copy(alpha = 0.5f),
                focusedContainerColor = Color(0xFFF2EDE7),
                unfocusedContainerColor = Color(0xFFF2EDE7),
                focusedTextColor = Color(0xFF2D312E),
                unfocusedTextColor = Color(0xFF2D312E)
            )
        )

        Text(
            text = "Botanical Vocabulary",
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(bottom = 10.dp, start = 4.dp)
        )

        if (glossary.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("📖", fontSize = 48.sp)
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "No definitions found for '$searchQuery'",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                    )
                }
            }
        } else {
            // Recycler displaying terms
            LazyColumn(
                modifier = Modifier.weight(1f).testTag("glossary_word_list"),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 8.dp)
            ) {
                items(glossary, key = { it.term }) { entry ->
                    GlossaryWordCard(
                        entry = entry,
                        onClick = { viewModel.selectTerm(entry) }
                    )
                }
            }
        }
    }
}

@Composable
fun GlossaryWordCard(
    entry: GlossaryTerm,
    onClick: () -> Unit
) {
    // Modern dictionary entry design styled strictly with robust padding and distinct outline aesthetics
    Card(
        shape = RoundedCornerShape(32.dp),
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        border = BorderStroke(1.dp, Color(0xFFF2EDE7)),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("glossary_word_card_${entry.term}")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp) // Well-structured robust padding inside the card
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = entry.term,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.primary,
                    fontFamily = FontFamily.Serif
                )
                
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)
                ) {
                    Text(
                        text = "/ ${entry.pronunciation} /",
                        fontSize = 10.sp,
                        fontStyle = FontStyle.Italic,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Text(
                text = entry.definition,
                fontSize = 13.sp,
                lineHeight = 19.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f),
                modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
            )

            Divider(
                modifier = Modifier.padding(vertical = 8.dp),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f)
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Etymology: ",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
                Text(
                    text = entry.origin,
                    fontSize = 11.sp,
                    fontStyle = FontStyle.Italic,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        }
    }
}

// ==========================================
// 3. MY GARDEN (SCHEDULER DATABASE DRIVEN)
// ==========================================
@Composable
fun GardenTabScreen(
    viewModel: PlantViewModel,
    onAddPlantClick: () -> Unit
) {
    val gardenPlants by viewModel.userPlants.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "My Little Garden",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Logged flora needing local care",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                )
            }

            Button(
                onClick = onAddPlantClick,
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.testTag("add_custom_plant_btn")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Sprout")
                Spacer(modifier = Modifier.width(4.dp))
                Text("Add Sprout", fontSize = 12.sp)
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        if (gardenPlants.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("🪴", fontSize = 64.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Your Garden is currently quiet",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "Grow your little garden! Navigate to the 'Catalog' tab on the bottom, select a plant, and click 'Schedule Feeding' to track its watering schedule offline.",
                        fontSize = 12.sp,
                        lineHeight = 18.sp,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedButton(
                        onClick = { viewModel.selectTab("catalog") },
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Explore Catalog 🌿")
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f).testTag("garden_plant_list"),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(gardenPlants, key = { it.id }) { plant ->
                    GardenPlantCard(
                        userPlant = plant,
                        onFeedClick = { viewModel.feedPlant(plant.id) },
                        onDeleteClick = { viewModel.deleteFromGarden(plant.id) },
                        viewModel = viewModel
                    )
                }
            }
        }
    }
}

@Composable
fun GardenPlantCard(
    userPlant: UserPlant,
    onFeedClick: () -> Unit,
    onDeleteClick: () -> Unit,
    viewModel: PlantViewModel
) {
    val (progress, statusText) = viewModel.getFeedingProgressAndStatus(userPlant)
    val categoryIcon = try {
        PlantCategory.valueOf(userPlant.category).icon
    } catch (e: Exception) {
        "🌱"
    }

    val avatarBg = try {
        when (PlantCategory.valueOf(userPlant.category)) {
            PlantCategory.EDIBLE -> Color(0xFFE5EBE0)
            PlantCategory.POISONOUS -> Color(0xFFFDE9E9)
            PlantCategory.MEDICINAL -> Color(0xFFE5EBE0)
            PlantCategory.TREE -> Color(0xFFF2EDE7)
        }
    } catch (e: Exception) {
        Color(0xFFE5EBE0)
    }

    Card(
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        border = BorderStroke(1.dp, Color(0xFFF2EDE7)),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("garden_card_${userPlant.id}")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row(modifier = Modifier.weight(1f)) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(avatarBg),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = categoryIcon, fontSize = 28.sp)
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = userPlant.name,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = userPlant.scientificName,
                            fontStyle = FontStyle.Italic,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        )
                    }
                }

                IconButton(
                    onClick = onDeleteClick,
                    modifier = Modifier
                        .size(32.dp)
                        .testTag("delete_garden_plant_${userPlant.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Release Plant",
                        tint = MaterialTheme.colorScheme.error.copy(alpha = 0.6f),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            if (userPlant.customNotes.isNotBlank()) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.03f),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Text(
                        text = "“${userPlant.customNotes}”",
                        fontSize = 11.sp,
                        fontStyle = FontStyle.Italic,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }
            } else {
                Spacer(modifier = Modifier.height(10.dp))
            }

            // Watering schedule tracker details
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("⏰ Feed every ", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                    Text(
                        text = "${userPlant.feedingIntervalDays} days",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Text(
                    text = statusText,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (progress <= 0.2f) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                )
            }

            // Progress Bar of Watering Satisfaction
            LinearProgressIndicator(
                progress = progress,
                trackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f),
                color = if (progress <= 0.2f) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp))
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Water button with testTag
            Button(
                onClick = onFeedClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(38.dp)
                    .testTag("feed_button_${userPlant.id}"),
                contentPadding = PaddingValues(0.dp)
            ) {
                Text("💧 Log Feeding / Hydrated", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

// ==========================================
// 4. PLANT DETAIL BOTTOM SHEET / DIALOG STYLE OVERLAY
// ==========================================
@Composable
fun PlantDetailSheet(
    plant: Plant,
    onDismiss: () -> Unit,
    onAddToGarden: (interval: Int, notes: String) -> Unit
) {
    var expandScheduler by remember { mutableStateOf(false) }
    var intervalDays by remember { mutableStateOf(3) }
    var customRemarks by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp)
                .testTag("plant_detail_sheet")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp) // Well-structured robust padding inside detail card
            ) {
                // Top Exit Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = plant.category.displayName.uppercase(),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(28.dp)
                            .testTag("close_detail_sheet")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Typography pairings: Title & Technical Name
                Text(
                    text = plant.name,
                    fontWeight = FontWeight.Black,
                    fontSize = 26.sp,
                    fontFamily = FontFamily.Serif,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = plant.scientificName,
                    fontStyle = FontStyle.Italic,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                    modifier = Modifier.padding(top = 2.dp, bottom = 14.dp)
                )

                // Divider
                Divider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))

                // Scrollable details content container
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f, fill = false)
                ) {
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        item {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = plant.briefDescription,
                                fontSize = 13.sp,
                                lineHeight = 19.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                            )
                        }

                        // Geographic Origin
                        item {
                            DetailItem(title = "🌎 Geographic Origin", desc = plant.origin)
                        }

                        // Light exposure
                        item {
                            DetailItem(title = "☀️ Sunlight Requirement", desc = plant.sunlight)
                        }

                        // Feeding care details (Watering instructions)
                        item {
                            DetailItem(
                                title = "💧 Watering Guidance (How to feed)",
                                desc = plant.wateringInstructions,
                                containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.05f)
                            )
                        }

                        // Nutritional needs
                        item {
                            DetailItem(
                                title = "🧪 Soil Nutrition & Fertilization",
                                desc = plant.feedingNeeds,
                                containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.05f)
                            )
                        }

                        // Benefits / Warning badge
                        plant.benefits?.let {
                            item {
                                DetailItem(title = "🌿 Medicinal/Functional Benefits", desc = it)
                            }
                        }

                        plant.toxicityWarning?.let {
                            item {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(MaterialTheme.colorScheme.error.copy(alpha = 0.08f))
                                        .padding(12.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text("⚠️", fontSize = 16.sp)
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "TOXIC ALERTA",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Black,
                                            color = MaterialTheme.colorScheme.error
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = it,
                                        fontSize = 11.sp,
                                        lineHeight = 16.sp,
                                        color = MaterialTheme.colorScheme.error
                                    )
                                }
                            }
                        }

                        // Fun facts
                        item {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.03f))
                                    .padding(12.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("💡", fontSize = 14.sp)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Did you know?",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                                Text(
                                    text = plant.funFact,
                                    fontSize = 12.sp,
                                    lineHeight = 17.sp,
                                    fontStyle = FontStyle.Italic,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Integrated Garden Scheduler trigger inside details view
                if (!expandScheduler) {
                    Button(
                        onClick = { expandScheduler = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("schedule_care_btn"),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Add to My Garden Schedule 📋", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                } else {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.02f))
                            .padding(12.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column {
                            Text(
                                text = "Setup Feeding Schedule",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(bottom = 6.dp)
                            )
                            
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Feed/Water Every:", fontSize = 11.sp)
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    IconButton(
                                        onClick = { if (intervalDays > 1) intervalDays-- },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Text("-", fontWeight = FontWeight.Black, fontSize = 16.sp, color = MaterialTheme.colorScheme.primary)
                                    }
                                    Text(
                                        text = "$intervalDays Days",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        modifier = Modifier.padding(horizontal = 8.dp)
                                    )
                                    IconButton(
                                        onClick = { if (intervalDays < 30) intervalDays++ },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Text("+", fontWeight = FontWeight.Black, fontSize = 16.sp, color = MaterialTheme.colorScheme.primary)
                                    }
                                }
                            }

                            OutlinedTextField(
                                value = customRemarks,
                                onValueChange = { customRemarks = it },
                                placeholder = { Text("e.g. 'Leave on kitchen window sill', 'Check leaf yellowing'") },
                                label = { Text("Custom Notes") },
                                textStyle = TextStyle(fontSize = 11.sp),
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp),
                                shape = RoundedCornerShape(8.dp)
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedButton(
                                    onClick = { expandScheduler = false },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(0.dp)
                                ) {
                                    Text("Cancel", fontSize = 11.sp)
                                }
                                Button(
                                    onClick = { onAddToGarden(intervalDays, customRemarks) },
                                    modifier = Modifier.weight(1f).testTag("confirm_garden_schedule"),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(0.dp)
                                ) {
                                    Text("Add to Garden", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DetailItem(
    title: String,
    desc: String,
    containerColor: Color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.02f)
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(containerColor)
            .padding(12.dp)
    ) {
        Text(
            text = title,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = desc,
            fontSize = 12.sp,
            lineHeight = 17.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.82f),
            modifier = Modifier.padding(top = 3.dp)
        )
    }
}

// ==========================================
// 5. GLOSSARY TERM DETAIL SCREEN
// ==========================================
@Composable
fun GlossaryTermDetailSheet(
    term: GlossaryTerm,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp)
                .testTag("glossary_detail_sheet")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp).testTag("close_glossary_detail")) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                    }
                }

                Text(
                    text = "Botanical Definition",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = 2.dp)
                )

                Text(
                    text = term.term,
                    fontWeight = FontWeight.Bold,
                    fontSize = 28.sp,
                    fontFamily = FontFamily.Serif,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(4.dp))

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)
                ) {
                    Text(
                        text = "Phonetic Pronunciation: / ${term.pronunciation} /",
                        fontSize = 11.sp,
                        fontStyle = FontStyle.Italic,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }

                Divider(modifier = Modifier.padding(vertical = 16.dp), color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))

                // Well-structured padded dictionary definition
                Text(
                    text = term.definition,
                    fontSize = 15.sp,
                    lineHeight = 22.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                DetailItem(title = "📚 Etymological Origin", desc = term.origin)
                Spacer(modifier = Modifier.height(10.dp))
                DetailItem(
                    title = "🔬 Importance to Botany Ecosystems",
                    desc = term.contextualImportance,
                    containerColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.04f)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Understood, Back to Glossary", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// ==========================================
// 6. ADD CUSTOM PLANT DIALOG
// ==========================================
@Composable
fun AddCustomPlantDialog(
    onDismiss: () -> Unit,
    onConfirm: (name: String, scientific: String, category: String, interval: Int, notes: String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var scientificName by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("EDIBLE") }
    var intervalDays by remember { mutableStateOf(3) }
    var notes by remember { mutableStateOf("") }

    val categories = listOf("EDIBLE", "POISONOUS", "MEDICINAL", "TREE")

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp)
                .testTag("add_custom_plant_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Text(
                    text = "Add Custom Sprout",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = 14.dp)
                )

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Common Name (e.g., Rosemary)") },
                    shape = RoundedCornerShape(8.dp),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("add_plant_name")
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = scientificName,
                    onValueChange = { scientificName = it },
                    label = { Text("Botanical Scientific Name") },
                    shape = RoundedCornerShape(8.dp),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text("Plant Classification:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    categories.forEach { cat ->
                        val isSelected = category == cat
                        Surface(
                            onClick = { category = cat },
                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
                            contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                            shape = RoundedCornerShape(6.dp),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)),
                            modifier = Modifier.weight(1f).height(32.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                                Text(cat.lowercase().replaceFirstChar { it.uppercase() }, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Feeding/Watering Cycle:", fontSize = 12.sp)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = { if (intervalDays > 1) intervalDays-- }) {
                            Text("-", fontWeight = FontWeight.Black, fontSize = 16.sp, color = MaterialTheme.colorScheme.primary)
                        }
                        Text("$intervalDays Days", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        IconButton(onClick = { if (intervalDays < 30) intervalDays++ }) {
                            Text("+", fontWeight = FontWeight.Black, fontSize = 16.sp, color = MaterialTheme.colorScheme.primary)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Special Care Notes") },
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(18.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f)) {
                        Text("Cancel")
                    }
                    Button(
                        onClick = {
                            if (name.isNotBlank()) {
                                onConfirm(
                                    name,
                                    scientificName.ifBlank { "Flora Specimen" },
                                    category,
                                    intervalDays,
                                    notes
                                )
                            }
                        },
                        enabled = name.isNotBlank(),
                        modifier = Modifier.weight(1f).testTag("save_custom_plant_btn")
                    ) {
                        Text("Add Sprout", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
