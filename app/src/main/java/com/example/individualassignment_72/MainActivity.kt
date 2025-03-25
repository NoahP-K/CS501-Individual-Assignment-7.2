package com.example.individualassignment_72

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.individualassignment_72.ui.theme.IndividualAssignment_72Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            IndividualAssignment_72Theme {
                GitSearchScreen()
            }
        }
    }
}

//function to handle checking the state of the viewmodel
@Composable
fun MakeSearch(searchState: AccountViewModel.SearchState,
               viewModel: AccountViewModel,
               onSuccess: ()->Unit,
               onEndOfResults: ()->Unit,
               perPage: Int){
    when (searchState) {
        AccountViewModel.SearchState.Initial -> {}  //show nothing if in the initial state
        AccountViewModel.SearchState.Loading -> {   //show loading icon if loading
            CircularProgressIndicator()
        }

        is AccountViewModel.SearchState.Success -> {    //add next page of results to currently displayed results if search success
            val searchResult = (searchState as AccountViewModel.SearchState.Success).accountInfo
            if(!viewModel.currentResults.containsAll(searchResult)) {
                viewModel.currentResults.addAll(searchResult)
            }
            onSuccess()     //handle display on successful search
            if(searchResult.size < perPage){    //handle when there are no more results to display
                onEndOfResults()
            }
        }

        is AccountViewModel.SearchState.Error -> {  //on error, display error
            val errorMessage =
                (searchState as AccountViewModel.SearchState.Error).errorMessage
            Text("Error: $errorMessage", fontSize = 20.sp)
        }
    }
}

//function to show search and results
@Composable
fun GitSearchScreen(viewModel: AccountViewModel = viewModel()) {
    var login by rememberSaveable { mutableStateOf("") }
    var searchedLogin by rememberSaveable { mutableStateOf("") }    //holds the name last searched for display purposes
    val searchState by viewModel.searchState.collectAsState()
    val perPage = 10    //the number of results per page to load
    var page by rememberSaveable { mutableStateOf(1)}   //the current page loaded. Starts at 1.
    var displayResults by rememberSaveable { mutableStateOf(false) }    //Indicates whether to treat successful search state as new results or not.
    var endOfResults by rememberSaveable { mutableStateOf(false) }      //indicates if there are no more results to load
    val scrollState = rememberSaveable(saver = LazyListState.Saver) { LazyListState() }

    Scaffold() { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding).fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TextField(
                value = login,
                onValueChange = { login = it },
                label = { Text("Enter Username", fontSize = 20.sp) }
            )
            Button(onClick = {      //on click, reset display flags and fetch results of current search entry
                if (login.isNotEmpty()) {
                    displayResults = false
                    endOfResults = false
                    viewModel.currentResults.clear()
                    page = 1
                    searchedLogin = login
                    viewModel.fetchUser(searchedLogin, page, perPage)
                }
            }) {
                Text("Get Repos", fontSize = 20.sp)
            }

            MakeSearch(     //handle current search state
                searchState,
                viewModel,
                { displayResults = true },
                { endOfResults = true },
                perPage
            )
            if(displayResults) {    //display search results
                DisplayResults(
                    searchedLogin,
                    viewModel,
                    {
                        page++
                        viewModel.fetchUser(searchedLogin, page, perPage)
                    },
                    scrollState,
                    endOfResults
                )
            }
        }
    }
}

//function to display search results
@Composable
fun DisplayResults(
    login: String,
    viewModel: AccountViewModel,
    loadMore: ()->Unit,
    scrollState: LazyListState,
    endOfResults: Boolean
    ) {
    val repos = viewModel.currentResults
    Column(){
        Text(
            text = login,
            fontSize = 22.sp
        )
        Spacer(modifier = Modifier.size(20.dp))
        LazyColumn(
            userScrollEnabled = true,
            state = scrollState,
        ) {
            items(repos.size) { i ->
                val repo = repos[i]
                Column(

                ) {
                    Divider(
                        color = Color.Black,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                    )
                    Text(
                        text = repo.name,
                        fontSize = 18.sp
                    )
                    Text(
                        text = repo.description ?: "",
                        fontSize = 15.sp
                    )
                }

            }
            if(!endOfResults) {     //display a button that can fetch new entries, but only if entries still can be fetched
                item {
                    Button(
                        onClick = {
                            loadMore()
                        }
                    ) {
                        Text(text = "Load more")
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    IndividualAssignment_72Theme {

    }
}