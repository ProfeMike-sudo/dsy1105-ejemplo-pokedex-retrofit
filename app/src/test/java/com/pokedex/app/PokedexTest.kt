package com.pokedex.app

import com.pokedex.app.data.model.*
import com.pokedex.app.data.remote.PokeApi
import com.pokedex.app.repository.PokemonRepository
import com.pokedex.app.viewmodel.PokedexViewModel
import java.io.IOException
import kotlinx.coroutines.*
import kotlinx.coroutines.test.*
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.Assert.*
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@OptIn(ExperimentalCoroutinesApi::class)
class PokedexTest {
    private val ficha = PokemonDto(25, "pikachu", 4, 60, SpritesDto(null), listOf(PokemonTypeDto(1, TypeDto("electric"))))
    private fun api(block: suspend (String) -> PokemonDto) = object : PokeApi {
        override suspend fun obtenerPokemon(nombre: String) = block(nombre)
    }

    @Test fun normalizaYReutilizaCachePorNombreEId() = runTest {
        var llamadas = 0
        val repo = PokemonRepository(api { nombre ->
            assertEquals("pikachu", nombre)
            llamadas++
            ficha
        })
        assertEquals(ficha, repo.buscar(" PIKACHU "))
        assertEquals(ficha, repo.buscar("25"))
        assertEquals(ficha, repo.buscar("pikachu"))
        assertEquals(1, llamadas)
    }

    @Test fun retrofitMapeaJsonYRutaReales() = runTest {
        val server = MockWebServer()
        server.enqueue(MockResponse().setBody("""{"id":25,"name":"pikachu","height":4,"weight":60,"sprites":{"front_default":"https://example.com/pikachu.png"},"types":[{"slot":1,"type":{"name":"electric"}}]}"""))
        server.start()
        try {
            val service = Retrofit.Builder().baseUrl(server.url("/api/v2/"))
                .addConverterFactory(GsonConverterFactory.create()).build().create(PokeApi::class.java)
            val result = PokemonRepository(service).buscar("pikachu")
            assertEquals("/api/v2/pokemon/pikachu", server.takeRequest().path)
            assertEquals(25, result.id)
            assertEquals("https://example.com/pikachu.png", result.sprites.frontDefault)
            assertEquals("electric", result.types.first().type.name)
        } finally { server.shutdown() }
    }

    @Test fun consultaVaciaNoHacePeticion() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        try {
            val vm = PokedexViewModel(PokemonRepository(api { error("No debe llamar") }))
            vm.actualizarConsulta("  ")
            vm.buscar()
            assertEquals("Escribe un nombre o número.", vm.uiState.value.error)
            assertFalse(vm.uiState.value.cargando)
        } finally { Dispatchers.resetMain() }
    }

    @Test fun publicaCargaExitoYEvitaDobleConsulta() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        try {
            var llamadas = 0
            val vm = PokedexViewModel(PokemonRepository(api { llamadas++; ficha }))
            vm.buscar(); vm.buscar()
            assertTrue(vm.uiState.value.cargando)
            advanceUntilIdle()
            assertEquals(1, llamadas)
            assertEquals(ficha, vm.uiState.value.pokemon)
            assertFalse(vm.uiState.value.cargando)
            assertNull(vm.uiState.value.error)
        } finally { Dispatchers.resetMain() }
    }

    @Test fun errorDeRedPermiteReintento() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        try {
            var intentos = 0
            val vm = PokedexViewModel(PokemonRepository(api {
                intentos++
                if (intentos == 1) throw IOException("sin red")
                ficha
            }))
            vm.buscar(); advanceUntilIdle()
            assertTrue(vm.uiState.value.error!!.contains("internet"))
            assertFalse(vm.uiState.value.cargando)
            vm.buscar(); advanceUntilIdle()
            assertEquals(ficha, vm.uiState.value.pokemon)
            assertNull(vm.uiState.value.error)
        } finally { Dispatchers.resetMain() }
    }

    @Test fun respuesta404ProduceMensajeEspecifico() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        val server = MockWebServer()
        server.enqueue(MockResponse().setResponseCode(404).setBody("Not Found"))
        server.start()
        try {
            val service = Retrofit.Builder().baseUrl(server.url("/api/v2/"))
                .addConverterFactory(GsonConverterFactory.create()).build().create(PokeApi::class.java)
            val vm = PokedexViewModel(PokemonRepository(service))
            vm.buscar()
            withContext(Dispatchers.Default) {
                withTimeout(5000) {
                    while(vm.uiState.value.cargando) {
                        testScheduler.runCurrent()
                        delay(10)
                    }
                }
            }
            assertTrue(vm.uiState.value.error!!.contains("No encontré"))
            assertNull(vm.uiState.value.pokemon)
        } finally { server.shutdown(); Dispatchers.resetMain() }
    }
}
