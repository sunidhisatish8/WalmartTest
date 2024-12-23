package com.example.walmart.presentation.viewModel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.walmart.domain.error.ErrorFormatter
import com.example.walmart.domain.model.Country
import com.example.walmart.domain.provider.DispatcherProvider
import com.example.walmart.domain.repo.CountryRepo
import com.example.walmart.presentation.countries.CountriesViewModel
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TestDispatchersProvider : DispatcherProvider {
    private val testCoroutineDispatcher = StandardTestDispatcher()
    val main: CoroutineDispatcher = Dispatchers.Main
    val io: CoroutineDispatcher = Dispatchers.IO
    val default: CoroutineDispatcher = Dispatchers.Default
    fun testDispatcher(): CoroutineDispatcher = testCoroutineDispatcher
}

@OptIn(ExperimentalCoroutinesApi::class)
class CountriesViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var viewModel: CountriesViewModel
    private val countryRepo: CountryRepo = mockk()
    private val errorFormatter: ErrorFormatter = mockk()
    private val dispatcherProvider = TestDispatchersProvider()

    @Before
    fun setup() {
        // Set the test dispatcher
        Dispatchers.setMain(dispatcherProvider.testDispatcher())
        viewModel = CountriesViewModel(countryRepo, dispatcherProvider, errorFormatter)
    }

    @After
    fun tearDown() {
        // Reset the Main dispatcher after tests
        Dispatchers.resetMain()
    }

    @Test
    fun `test reloadList loads countries and updates state`() = runTest(dispatcherProvider.testDispatcher()) {
        val countries = listOf(
            Country(name = "Angola", code = "AO", region = "AF", capital = "Luanda"),
            Country(name = "Argentina", code = "AR", region = "SA", capital = "Buenos Aires")
        )
        coEvery { countryRepo.getCountries() } returns countries

        viewModel.reloadList()
        advanceUntilIdle()

        val state = viewModel.state.first()
        assertEquals(countries, state.items)
        assertEquals(false, state.loading)
    }

    @Test
    fun `test reloadList handles error`() = runTest(dispatcherProvider.testDispatcher()) {
        val throwable = Throwable("Failed to load countries")
        coEvery { countryRepo.getCountries() } throws throwable
        every { errorFormatter.getDisplayErrorMessage(throwable) } returns throwable.message.toString()

        viewModel.reloadList()
        advanceUntilIdle()

        val state = viewModel.state.first()
        assertEquals(false, state.loading)
        assertEquals(throwable.message, state.errorMessage)
    }

    @Test
    fun `test search updates state with filtered items`() =
        runTest(dispatcherProvider.testDispatcher()) {
            val firstCountry =
                Country(name = "Angola", code = "AO", region = "AF", capital = "Luanda")
            val secondCountry =
                Country(name = "Argentina", code = "AR", region = "SA", capital = "Buenos Aires")
            val countries = listOf(firstCountry, secondCountry)
            coEvery { countryRepo.getCountries() } returns countries

            viewModel.reloadList()
            advanceUntilIdle()

            viewModel.search("Angola")
            advanceUntilIdle()

            val state = viewModel.state.first()
            assertEquals(listOf(firstCountry), state.items)
        }

    @Test
    fun `test onItemClick triggers OpenDetails effect`() =
        runTest(dispatcherProvider.testDispatcher()) {
            val country = Country(name = "Angola", code = "AO", region = "AF", capital = "Luanda")

            viewModel.onItemClick(country)

            val effect = viewModel.effectFlow.first()
            assertTrue(effect is CountriesViewModel.Effect.OpenDetails)
            assertEquals("AO", (effect as CountriesViewModel.Effect.OpenDetails).countryCode)
        }
}

