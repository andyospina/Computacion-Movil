package com.example.myapplication.ui.screens.FollowList

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.myapplication.R
import com.example.myapplication.ui.components.BarraSuperior
import com.example.myapplication.ui.components.CargaOError
import com.example.myapplication.ui.components.FilaUsuario
import com.example.myapplication.ui.components.TopBarNavigation
import com.example.myapplication.ui.theme.GraySecondary

@Composable
fun FollowListScreen(
    userId: String,
    tabInicial: FollowTab,
    modifier: Modifier = Modifier,
    viewModel: FollowListViewModel = hiltViewModel(),
    onBackClick: () -> Unit,
    onUserClick: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(userId) { viewModel.getListas(userId, tabInicial) }

    FollowListContent(
        modifier = modifier,
        uiState = uiState,
        onTabChange = viewModel::updateTab,
        onRetry = viewModel::reintentar,
        onBackClick = onBackClick,
        onUserClick = onUserClick
    )
}

@Composable
fun FollowListContent(
    modifier: Modifier = Modifier,
    uiState: FollowListState,
    onTabChange: (FollowTab) -> Unit,
    onRetry: () -> Unit,
    onBackClick: () -> Unit,
    onUserClick: (String) -> Unit
) {
    Column(modifier = modifier.fillMaxSize()) {

        BarraSuperior(
            title = stringResource(R.string.follow_list_title),
            navigation = TopBarNavigation.BACK,
            onNavigationClick = onBackClick
        )

        PrimaryTabRow(selectedTabIndex = uiState.tab.ordinal) {
            Tab(
                selected = uiState.tab == FollowTab.SEGUIDORES,
                onClick = { onTabChange(FollowTab.SEGUIDORES) },
                text = { Text(stringResource(R.string.follow_list_followers_tab, uiState.seguidores.size)) }
            )
            Tab(
                selected = uiState.tab == FollowTab.SIGUIENDO,
                onClick = { onTabChange(FollowTab.SIGUIENDO) },
                text = { Text(stringResource(R.string.follow_list_following_tab, uiState.seguidos.size)) }
            )
        }

        CargaOError(
            isLoading = uiState.isLoading,
            errorMessageRes = uiState.errorMessageRes,
            onRetry = onRetry
        ) {
            val usuarios = if (uiState.tab == FollowTab.SEGUIDORES) uiState.seguidores else uiState.seguidos

            if (usuarios.isEmpty()) {
                Text(
                    text = stringResource(
                        if (uiState.tab == FollowTab.SEGUIDORES) R.string.follow_list_empty_followers
                        else R.string.follow_list_empty_following
                    ),
                    color = GraySecondary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp)
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(20.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(usuarios, key = { it.id }) { usuario ->
                        FilaUsuario(user = usuario, onClick = { onUserClick(usuario.id) })
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, name = "FollowListScreen - Preview")
@Composable
fun FollowListScreenPreview() {
    FollowListContent(
        uiState = FollowListState(isLoading = false),
        onTabChange = {},
        onRetry = {},
        onBackClick = {},
        onUserClick = {}
    )
}
