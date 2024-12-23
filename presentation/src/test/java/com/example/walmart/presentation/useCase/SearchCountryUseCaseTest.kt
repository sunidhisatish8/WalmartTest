package com.example.walmart.presentation.useCase

import com.example.walmart.domain.model.Country
import com.example.walmart.domain.usecase.SearchCountryUseCase
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class SearchCountryUseCaseTest {
    private lateinit var searchCountryUseCase: SearchCountryUseCase

    @Before
    fun setUp() {
        searchCountryUseCase = SearchCountryUseCase()
    }

    @Test
    fun `when query is blank return the complete list`() {
        val mockCountryList = listOf(
            Country(
                name = "Angola",
                code = "AO",
                region = "AF",
                capital = "Launda"
            )
        )

        val actualCountryList = searchCountryUseCase.invoke(mockCountryList, "")
        Assert.assertEquals(mockCountryList, actualCountryList)
    }

    @Test
    fun `when query matches the country name return the matched country list`() {
        val mockCountryList = listOf(
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
            )
        )
        val expectedCountryList = listOf(
            Country(
                name = "Argentina",
                code = "AR",
                region = "SA",
                capital = "Buenos Aries"
            )
        )
        val actualCountryList = searchCountryUseCase.invoke(mockCountryList, "Argentina")
        Assert.assertEquals(expectedCountryList, actualCountryList)
    }

    @Test
    fun `when query does not match the country name return the empty list`() {
        val mockCountryList = listOf(
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
            )
        )
        val actualCountryList = searchCountryUseCase.invoke(mockCountryList, "Argenina")
        Assert.assertEquals(emptyList<List<Country>>(), actualCountryList)
    }

    @Test
    fun `when query matches the region name return the matched country list`() {
        val mockCountryList = listOf(
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
            )
        )
        val expectedCountryList = listOf(
            Country(
                name = "Angola",
                code = "AO",
                region = "AF",
                capital = "Launda"
            )
        )
        val actualCountryList = searchCountryUseCase.invoke(mockCountryList, "AF")
        Assert.assertEquals(expectedCountryList, actualCountryList)
    }

    @Test
    fun `when query does not match the region name return the empty list`() {
        val mockCountryList = listOf(
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
            )
        )
        val actualCountryList = searchCountryUseCase.invoke(mockCountryList, "EU")
        Assert.assertEquals(emptyList<List<Country>>(), actualCountryList)
    }

    @Test
    fun `when query matches the capital return the empty list`() {
        val mockCountryList = listOf(
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
            )
        )
        val actualCountryList = searchCountryUseCase.invoke(mockCountryList, "Launda")
        Assert.assertEquals(emptyList<List<Country>>(), actualCountryList)
    }

    @Test
    fun `when query matches the code return the empty list`() {
        val mockCountryList = listOf(
            Country(
                name = "Latvia",
                code = "LV",
                region = "EU",
                capital = "Riga"
            ), Country(
                name = "Mali",
                code = "ML",
                region = "AF",
                capital = "Bamako"
            )
        )
        val actualCountryList = searchCountryUseCase.invoke(mockCountryList, "ML")
        Assert.assertEquals(emptyList<List<Country>>(), actualCountryList)
    }

    @Test
    fun `when query contains the country name return all matched country list`() {
        val mockCountryList = listOf(
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
        val expectedCountryList = listOf(
            Country(
                name = "Angola",
                code = "AO",
                region = "AF",
                capital = "Launda"
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
        val actualCountryList = searchCountryUseCase.invoke(mockCountryList, "Ang")
        Assert.assertEquals(expectedCountryList, actualCountryList)
    }

    @Test
    fun `when query is not case sensitive return the matched country list`() {
        val mockCountryList = listOf(
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
            )
        )
        val expectedCountryList = listOf(
            Country(
                name = "Angola",
                code = "AO",
                region = "AF",
                capital = "Launda"
            )
        )
        val actualCountryList = searchCountryUseCase.invoke(mockCountryList, "AnGoLA")
        Assert.assertEquals(expectedCountryList, actualCountryList)
    }
}