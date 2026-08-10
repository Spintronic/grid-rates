package com.example.gridrates.ui.onboarding

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.gridrates.data.local.entity.UtilityProvider

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProviderSelectionScreen(
    viewModel: ProviderViewModel,
    onProviderSelected: (String) -> Unit,
    onBack: (() -> Unit)? = null
) {
    val providers by viewModel.providers.collectAsStateWithLifecycle(initialValue = emptyList())

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Select Utility Provider") },
                navigationIcon = {
                    if (onBack != null) {
                        IconButton(onClick = onBack) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                                contentDescription = "Back"
                            )
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(providers) { provider ->
                ProviderItem(
                    provider = provider,
                    onClick = { onProviderSelected(provider.id) }
                )
            }
        }
    }
}

@Composable
fun ProviderItem(
    provider: UtilityProvider,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = provider.name,
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                text = provider.region,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
