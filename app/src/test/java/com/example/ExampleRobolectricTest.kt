package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.PersonaRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("AI CONNECT", appName)
  }

  @Test
  fun `verify default personas loaded`() {
    val repository = PersonaRepository()
    val personas = repository.personas.value
    assertTrue(personas.size >= 4)
    assertTrue(personas.any { it.id == "nova" })
    assertTrue(personas.any { it.id == "layla" })
  }

  @Test
  fun `verify persona gallery data structure`() {
    val repository = PersonaRepository()
    val personas = repository.personas.value
    personas.forEach { persona ->
      assertTrue(persona.nameAr.isNotBlank())
      assertTrue(persona.bioAr.isNotBlank())
      assertTrue(persona.avatarRes != 0)
    }
  }

  @Test
  fun `verify search filtering by name and category`() {
    val repository = PersonaRepository()
    val personas = repository.personas.value

    // Filter by name
    val filteredByName = personas.filter { it.nameAr.contains("نوفا") }
    assertEquals(1, filteredByName.size)
    assertEquals("nova", filteredByName.first().id)

    // Filter by category
    val filteredByCategory = personas.filter { it.category == "tutor" }
    assertEquals(1, filteredByCategory.size)
    assertEquals("layla", filteredByCategory.first().id)
  }
}
