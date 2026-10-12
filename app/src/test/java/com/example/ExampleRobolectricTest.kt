package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.model.CharacterData
import com.example.util.SafetyFilter
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `read app name from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Dragon Ball Messenger", appName)
    }

    @Test
    fun `verify all six characters exist including frieza`() {
        val characters = CharacterData.characters
        assertEquals(6, characters.size)
        val ids = characters.map { it.id }
        assertTrue(ids.contains("goku"))
        assertTrue(ids.contains("vegeta"))
        assertTrue(ids.contains("beerus"))
        assertTrue(ids.contains("whis"))
        assertTrue(ids.contains("zeno"))
        assertTrue(ids.contains("frieza"))
    }

    @Test
    fun `verify persona engine generates responses for all characters`() {
        val characters = CharacterData.characters
        characters.forEach { char ->
            val reply = com.example.util.DragonBallPersonaEngine.generateContextualResponse(char, "مرحبا")
            assertTrue(reply.isNotEmpty())
        }
    }
}
