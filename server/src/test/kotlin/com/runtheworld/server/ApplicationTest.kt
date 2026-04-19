package com.runtheworld.server

import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlinx.serialization.json.Json
import kotlin.test.*

class ApplicationTest {
    @Test
    fun testRoot() = testApplication {
        application {
            module()
        }
        val response = client.get("/")
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals("Run The World Server is Online!", response.bodyAsText())
    }

    @Test
    fun testProfileSync() = testApplication {
        application {
            module()
        }
        val response = client.post("/profiles") {
            header(HttpHeaders.ContentType, ContentType.Application.Json)
            setBody(
                """
                {
                    "uid": "test_auto",
                    "username": "auto_tester",
                    "displayName": "Auto Tester",
                    "colorHex": "#00FF00",
                    "totalAreaKm2": 50.0,
                    "runCount": 20
                }
                """.trimIndent()
            )
        }
        assertEquals(HttpStatusCode.OK, response.status)
        assertTrue(response.bodyAsText().contains("synced"))
    }

    @Test
    fun testGetProfiles() = testApplication {
        application {
            module()
        }
        val response = client.get("/profiles")
        assertEquals(HttpStatusCode.OK, response.status)
        assertTrue(response.bodyAsText().contains("auto_tester"))
    }
}
