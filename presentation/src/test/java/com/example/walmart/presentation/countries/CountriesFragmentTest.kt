package com.example.walmart.presentation.countries

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
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.swipeDown
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.scrollTo
import androidx.test.espresso.matcher.ViewMatchers.hasDescendant
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
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.containsString
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class CountriesFragmentTest {
    private lateinit var navController: TestNavHostController
    private lateinit var viewModelFactory: CountriesViewModelFactory
    private lateinit var dispatchers: DispatcherProvider
    private lateinit var errorFormatter: ErrorFormatter

    @Before
    fun setup() {
        navController = TestNavHostController(ApplicationProvider.getApplicationContext())
        navController.setGraph(R.navigation.main_graph)

        val repository = FakeCountriesRepository()
        dispatchers = TestDispatchersProvider()
        errorFormatter = TestErrorFormatter()
        viewModelFactory = CountriesViewModelFactory(repository, dispatchers, errorFormatter)
        val scenario = launchFragment()
        scenario.onFragment { fragment ->
            Navigation.setViewNavController(fragment.requireView(), navController)
        }
    }

    /**
     * Verify that the search action button is displayed and clickable.
     */
    @Test
    fun verifySearchActionButtonIsDisplayed() {
        onView(withId(R.id.action_search)).check(matches(isDisplayed()))
        onView(withId(R.id.action_search)).perform(click())
    }

    /**
     * Verify that the search action button is clickable.
     */
    @Test
    fun verifySearchActionButtonIsClickable() {
        onView(withId(R.id.action_search)).check(matches(isDisplayed()))
        onView(withId(R.id.action_search)).perform(click())
    }

    /**
     * Verify that the recycler view is visible
     */
    @Test
    fun verifyRecyclerViewIsDisplayed() {
        onView(withId(R.id.country_recycler_view))
            .check(matches(isDisplayed()))
    }

    /**
     * Verify that the swipe refresh layout is visible
     */
    @Test
    fun verifySwipeRefreshLayoutIsDisplayed() {
        onView(withId(R.id.swipe_refresh_layout))
            .check(matches(isDisplayed()))
    }

    /**
     * verify that performing a swipe-down will reload the data.
     */
    @Test
    fun verifySwipeToRefreshReloadsData() {
        onView(withId(R.id.swipe_refresh_layout)).perform(swipeDown())
        onView(withId(R.id.country_recycler_view)).check(matches(isDisplayed()))
    }

    /**
     * verify that the toolbar title is displayed as expected.
     */
    @Test
    fun verifyToolbarTitleIsCorrect() {
        onView(withId(R.id.action_bar))
            .check(matches(hasDescendant(withText("Countries List"))))
    }

    /**
     * verify that the back button is displayed.
     */
    @Test
    fun verifyBackButtonIsDisplayed() {
        onView(withId(R.id.action_search)).check(matches(isDisplayed()))
        onView(withId(R.id.action_search)).perform(click())
        onView(
            allOf(
                isDescendantOfA(withId(R.id.action_bar)),
                withClassName(containsString("ImageButton"))
            )
        ).check(matches(isDisplayed()))
    }

    /**
     * verify that entering valid search text updates the RecyclerView content.
     */
    @Test
    fun verifyValidSearchTextUpdatesRecyclerView() {
        onView(withId(R.id.action_search)).check(matches(isDisplayed()))
        onView(withId(R.id.action_search)).perform(click())
        onView(withId(androidx.appcompat.R.id.search_src_text)).perform(
            typeText("Arg"),
            closeSoftKeyboard()
        )
        Thread.sleep(2000)
        onView(withId(R.id.country_recycler_view))
            .perform(
                scrollTo<RecyclerView.ViewHolder>(
                    hasDescendant(withText(containsString("Argentina")))
                )
            )
            .check(matches(isDisplayed()))
    }

    /**
     * verify that snackbar is displayed when there is an error
     */
    @Test
    fun verifySnackBarDisplayForAnError() {
        val errorRepository = object : CountryRepo {
            override suspend fun getCountries(): List<Country> {
                throw RuntimeException("Network error")
            }

            override suspend fun getCountryDetailsByCode(code: String): Country? {
                throw RuntimeException("Network error")
            }
        }

        viewModelFactory = CountriesViewModelFactory(errorRepository, dispatchers, errorFormatter)

        val scenario = launchFragment()
        scenario.onFragment { fragment ->
            Navigation.setViewNavController(fragment.requireView(), navController)
        }

        onView(withId(R.id.swipe_refresh_layout)).perform(swipeDown())
        Thread.sleep(2000)
        onView(withText(containsString("Retry"))).check(matches(isDisplayed()))
    }

    private fun launchFragment(): FragmentScenario<CountriesFragment> {
        return launchFragmentInContainer<CountriesFragment>(
            Bundle(),
            R.style.TestTheme,
            factory = object : FragmentFactory() {
                override fun instantiate(classLoader: ClassLoader, className: String): Fragment {
                    return if (className == CountriesFragment::class.java.name) {
                        CountriesFragment().apply {
                            setViewModelFactory(this@CountriesFragmentTest.viewModelFactory)
                        }
                    } else {
                        super.instantiate(classLoader, className)
                    }
                }
            }
        )
    }
}

// Fake Repository for Testing
class FakeCountriesRepository : CountryRepo {
    override suspend fun getCountries(): List<Country> {
        return listOf(
            Country(
                name = "Angola",
                code = "AO",
                region = "AF",
                capital = "Launda"
            ), Country(
                name = "Argentina",
                code = "AR",
                region = "SA",
                capital = "Buenos Aries"
            ), Country(
                name = "Anguilla",
                code = "AI",
                region = "NA",
                capital = "The Valley"
            ), Country(
                name = "Bangladesh",
                code = "BD",
                region = "AS",
                capital = "Dhaka"
            )
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