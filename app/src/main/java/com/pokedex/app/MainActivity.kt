package com.pokedex.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.pokedex.app.data.remote.PokeApiClient
import com.pokedex.app.repository.PokemonRepository
import com.pokedex.app.ui.screen.PantallaPokedex
import com.pokedex.app.viewmodel.PokedexViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            // En tu proyecto puedes usar el tema Material 3 generado por Android Studio.
            MaterialTheme {
                val vm: PokedexViewModel = viewModel(factory = viewModelFactory {
                    initializer {
                        PokedexViewModel(PokemonRepository(PokeApiClient.api))
                    }
                })
                PantallaPokedex(viewModel = vm)
            }
        }
    }
}
