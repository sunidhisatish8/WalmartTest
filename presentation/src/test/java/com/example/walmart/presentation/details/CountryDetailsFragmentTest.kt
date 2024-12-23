package com.example.walmart.presentation.details

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.swipeDown
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.isDescendantOfA
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withClassName
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import com.example.walmart.domain.error.ErrorFormatter
import com.example.walmart.domain.model.Country
import com.example.walmart.domain.provider.DispatcherProvider
import com.example.walmart.domain.repo.CountryRepo
import com.example.walmart.presentation.R
import com.example.walmart.presentation.countries.CountriesFragment
import com.example.walmart.presentation.countries.CountriesViewModelFactory
import com.example.walmart.presentation.countries.FakeCountriesRepository
import com.example.walmart.presentation.countries.TestDispatchersProvider
import com.example.walmart.presentation.countries.TestErrorFormatter
import com.example.walmart.presentation.databinding.CountryDetailsFragmentBinding
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.containsString
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.lang.reflect.Field
import java.lang.reflect.Method

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class CountryDetailsFragmentTest {

    private lateinit var navController: TestNavHostController
    private lateinit var viewModelFactory: CountriesViewModelFactory
    private lateinit var dispatchers: DispatcherProvider
    private lateinit var errorFormatter: ErrorFormatter
    val repository = FakeCountriesRepository()

    @Before
    fun setup() {
        navController = TestNavHostController(ApplicationProvider.getApplicationContext())
        navController.setGraph(R.navigation.main_graph)

        val repository = FakeCountriesRepository()
        dispatchers = TestDispatchersProvider()
        errorFormatter = TestErrorFormatter()
        viewModelFactory = CountriesViewModelFactory(repository, dispatchers, errorFormatter)
    }

    /**
     * Verify the navigation to country details screen and expected country code details is visible.
     */
    @Test
    fun verifyNavigationToCountryDetailsFragment() {
        val scenario = launchCountriesFragment()
        scenario.onFragment { fragment ->
            Navigation.setViewNavController(fragment.requireView(), navController)
        }

        onView(withId(R.id.country_recycler_view))
            .perform(
                RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                    0,
                    click()
                )
            )

        assertEquals(R.id.countryDetailsFragment, navController.currentDestination?.id)
        assertEquals(
            "AO",
            navController.currentBackStackEntry?.arguments?.getString(CountryDetailsArg.COUNTRY_CODE)
        )
    }

    /**
     * verify if back button is visible in the action bar.
     */
    @Test
    fun verifyBackButtonIsDisplayed() {
        launchCountryDetailsFragment("AO")
        onView(
            allOf(
                isDescendantOfA(withId(R.id.action_bar)),
                withClassName(containsString("ImageButton"))
            )
        ).check(matches(isDisplayed()))
    }

    /**
     * verify that the country item layout is displayed.
     */
    @Test
    fun verifyCountryItemLayoutIsDisplayed() {
        launchCountryDetailsFragment("AO")
        onView(withId(R.id.country_item_layout))
            .check(matches(isDisplayed()))
    }

    /**
     * verify that the toolbar title is displayed as expected.
     */
    @Test
    fun verifyToolbarTitleIsCorrect() {
        launchCountryDetailsFragment("AR")
        onView(withText(containsString("Country Details")))
            .check(matches(isDisplayed()))
    }

    /**
     * verify that back button press returns to countries fragment.
     */
    @Test
    fun verifyBackButtonTakesToCountriesFragment() {
        val scenario = launchCountriesFragment()
        scenario.onFragment { fragment ->
            Navigation.setViewNavController(fragment.requireView(), navController)
        }

        onView(withId(R.id.country_recycler_view))
            .perform(
                RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                    0,
                    click()
                )
            )
        assertEquals(R.id.countryDetailsFragment, navController.currentDestination?.id)
        assertEquals(
            "AO",
            navController.currentBackStackEntry?.arguments?.getString(CountryDetailsArg.COUNTRY_CODE)
        )
        pressBack()
        onView(withId(R.id.country_recycler_view)).check(matches(isDisplayed()))
    }

    /**
     * Verify that the swipe refresh layout is visible
     */
    @Test
    fun verifySwipeRefreshLayoutIsDisplayed() {
        launchCountryDetailsFragment("AR")
        onView(withId(R.id.swipe_refresh_layout))
            .check(matches(isDisplayed()))
    }

    /**
     * verify that performing a swipe-down will reload the data.
     */
    @Test
    fun verifySwipeRefreshReloadsData() {
        launchCountryDetailsFragment("AO")

        onView(withId(R.id.swipe_refresh_layout)).perform(swipeDown())

        onView(withId(R.id.swipe_refresh_layout))
            .check(matches(isDisplayed()))
    }

    /**
     * verify empty state when wrong country code is entered
     */
    @Test
    fun verifyEmptyStateWhenWrongCountryData() {
        launchCountryDetailsFragment("XX")
        onView(withId(R.id.error_message))
            .check(matches(withText("")))
    }

    /**
     * verify render state is loading
     */
    @Test
    fun verifyRenderStateIsLoading() {
        val scenario = launchCountryDetailsFragment("AO")
        val country = Country(name = "Angola", code = "AO", region = "AF", capital = "Luanda")
        scenario.onFragment { fragment ->
            val renderStateMethod: Method = fragment::class.java.getDeclaredMethod(
                "renderState",
                CountryDetailsViewModel.State::class.java
            )
            renderStateMethod.isAccessible = true
            val initialState = CountryDetailsViewModel.State(
                loading = true,
                country = country,
                errorMessage = null
            )
            renderStateMethod.invoke(fragment, initialState)

            onView(withId(R.id.swipe_refresh_layout)).check(matches(isDisplayed()))
        }
    }

    /**
     * verify render state is success
     */
    @Test
    fun verifyRenderStateIsSuccess() {
        val scenario = launchCountryDetailsFragment("AO")
        val country = Country(name = "Angola", code = "AO", region = "AF", capital = "Luanda")
        scenario.onFragment { fragment ->
            val renderStateMethod: Method = fragment::class.java.getDeclaredMethod(
                "renderState",
                CountryDetailsViewModel.State::class.java
            )
            renderStateMethod.isAccessible = true

            val successState = CountryDetailsViewModel.State(
                loading = false,
                country = country,
                errorMessage = null
            )
            renderStateMethod.invoke(fragment, successState)

            onView(withText(containsString("Angola"))).check(matches(isDisplayed()))
            onView(withText("Luanda")).check(matches(isDisplayed()))
        }
    }

    /**
     * verify render state is error
     */
    @Test
    fun verifyRenderStateIsError() {
        val scenario = launchCountryDetailsFragment("ZZ")
        val country = Country(name = "Unknown", code = "ZZ", region = "Unknown", capital = "Unknown")
        scenario.onFragment { fragment ->
            val renderStateMethod: Method = fragment::class.java.getDeclaredMethod(
                "renderState",
                CountryDetailsViewModel.State::class.java
            )
            renderStateMethod.isAccessible = true

            val errorState = CountryDetailsViewModel.State(
                loading = false,
                country = country,
                errorMessage = "Country not found"
            )
            renderStateMethod.invoke(fragment, errorState)

            onView(withText("Country not found")).check(matches(isDisplayed()))
        }
    }

    /**
     * verify render state is empty when wrong country code is entered
     */
    @Test
    fun verifyRenderStateIsEmpty() {
        val scenario = launchCountryDetailsFragment("XX")
        scenario.onFragment { fragment ->
            val renderStateMethod: Method = fragment::class.java.getDeclaredMethod(
                "renderState",
                CountryDetailsViewModel.State::class.java
            )
            renderStateMethod.isAccessible = true

            val emptyState = CountryDetailsViewModel.State(
                loading = false,
                country = null,
                errorMessage = "No data available"
            )
            renderStateMethod.invoke(fragment, emptyState)

            onView(withText("No data available")).check(matches(isDisplayed()))
        }
    }

    /**
     * verify render state shows specific error message
     */
    @Test
    fun verifyRenderStateShowsSpecificErrorMessage() {
        val scenario = launchCountryDetailsFragment("ZZ")
        val country = Country(name = "Unknown", code = "ZZ", region = "Unknown", capital = "Unknown")
        scenario.onFragment { fragment ->
            val renderStateMethod: Method = fragment::class.java.getDeclaredMethod(
                "renderState",
                CountryDetailsViewModel.State::class.java
            )
            renderStateMethod.isAccessible = true

            val errorState = CountryDetailsViewModel.State(
                loading = false,
                country = country,
                errorMessage = "Failed to load country details"
            )
            renderStateMethod.invoke(fragment, errorState)

            onView(withText("Failed to load country details")).check(matches(isDisplayed()))
        }
    }


    private fun launchCountriesFragment(): FragmentScenario<CountriesFragment> {
        return launchFragmentInContainer<CountriesFragment>(
            Bundle(),
            R.style.TestTheme,
            factory = object : FragmentFactory() {
                override fun instantiate(classLoader: ClassLoader, className: String): Fragment {
                    return if (className == CountriesFragment::class.java.name) {
                        CountriesFragment().apply {
                            setViewModelFactory(this@CountryDetailsFragmentTest.viewModelFactory)
                        }
                    } else {
                        super.instantiate(classLoader, className)
                    }
                }
            })
    }

    private fun launchCountryDetailsFragment(countryCode: String): FragmentScenario<CountryDetailsFragment> {
        return launchFragmentInContainer<CountryDetailsFragment>(
            fragmentArgs = Bundle().apply {
                putString(CountryDetailsArg.COUNTRY_CODE, countryCode)
            },
            themeResId = R.style.TestTheme
        )
    }

    fun getViewModel(fragment: CountryDetailsFragment): CountryDetailsViewModel {
        val field: Field = CountryDetailsFragment::class.java.getDeclaredField("viewModel")
        field.isAccessible = true
        return field.get(fragment) as CountryDetailsViewModel
    }

    fun getViewBinding(fragment: CountryDetailsFragment): CountryDetailsFragmentBinding {
        val field: Field = CountryDetailsFragment::class.java.getDeclaredField("viewBinding")
        field.isAccessible = true
        return field.get(fragment) as CountryDetailsFragmentBinding
    }

}

// Fake Repository for Testing
class FakeCountriesRepository : CountryRepo {
    override suspend fun getCountries(): List<Country> {
        return listOf(
            Country(name = "Angola", code = "AO", region = "AF", capital = "Launda"),
            Country(name = "Argentina", code = "AR", region = "SA", capital = "Buenos Aires"),
            Country(name = "Bangladesh", code = "BD", region = "AS", capital = "Dhaka")
        )
    }

    override suspend fun getCountryDetailsByCode(code: String): Country? {
        return getCountries().firstOrNull { it.code == code }
    }
}

// Dispatcher for Testing
class TestDispatchersProvider : DispatcherProvider {
    val main: CoroutineDispatcher = Dispatchers.Main
    val io: CoroutineDispatcher = Dispatchers.IO
    val default: CoroutineDispatcher = Dispatchers.Default
}

// Error Formatter for Testing
class TestErrorFormatter : ErrorFormatter {
    override fun getDisplayErrorMessage(throwable: Throwable): String {
        return "Error"
    }
}
