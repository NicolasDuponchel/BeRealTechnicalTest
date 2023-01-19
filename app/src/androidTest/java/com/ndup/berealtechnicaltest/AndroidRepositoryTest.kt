package com.ndup.berealtechnicaltest

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.ndup.berealtechnicaltest.repository.IRepository
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class AndroidRepositoryTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var repository: IRepository

    @Before
    fun injectStuff() {
        hiltRule.inject()
    }

    @Test
    fun ensureGettingCurrentUserWorks() = runTest {
        val currentUser = repository.getCurrentUser()
        assertEquals(currentUser.firstName, "Noel")
        assertEquals(currentUser.lastName, "Flantier")
    }

    /**
     * Currently having this issue. I'll see how to fix this latter.
     *
     * Hilt test, com.ndup.berealtechnicaltest.AndroidRepositoryTest, cannot use a @HiltAndroidApp application
     * but found com.ndup.berealtechnicaltest.MainApp.
     * To fix, configure the test to use HiltTestApplication
     * or a custom Hilt test application generated with @CustomTestApplication.
     */

}