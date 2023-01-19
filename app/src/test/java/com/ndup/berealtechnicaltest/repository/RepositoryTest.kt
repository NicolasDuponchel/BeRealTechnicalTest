package com.ndup.berealtechnicaltest.repository

import com.ndup.berealtechnicaltest.MainActivity
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import okhttp3.logging.HttpLoggingInterceptor
import org.junit.Assert.*
import org.junit.Test


@OptIn(ExperimentalCoroutinesApi::class)
internal class RepositoryTest {

    private val repository by lazy {
        Repository(
            ServiceFactory.service(
                baseUrl = MainActivity.BaseUrl,
                userName = MainActivity.UserName,
                userPassword = MainActivity.UserPassword,
                logLevel = HttpLoggingInterceptor.Level.BODY,
            )
        )
    }

    /**
     * I wont be able to achieve this unit testing because http is blocked by my VPN :
     * "Your organization has selected Zscaler to protect you from internet threats."
     * I'll try to update this on my personal laptop if I have time.
     */
    @Test
    fun `ensure getting current user works`() = runTest {
        val currentUser = repository.getCurrentUser()
        assertEquals(currentUser, "")
    }

}