package amu.wmi.eyelearner.app.auth.presentation.profile

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import kotlinx.browser.document
import org.w3c.dom.HTMLInputElement
import org.w3c.files.FileReader

@Composable
actual fun rememberAvatarPicker(onPicked: (String) -> Unit): () -> Unit {
    val currentOnPicked by rememberUpdatedState(onPicked)

    return remember {
        {
            val input = document.createElement("input") as HTMLInputElement
            input.type = "file"
            input.accept = "image/*"
            input.style.display = "none"

            input.onchange = {
                val file = input.files?.item(0)
                if (file != null) {
                    val reader = FileReader()
                    reader.onload = {
                        // Data URL, e.g. "data:image/png;base64,..."
                        reader.result?.toString()?.let { currentOnPicked(it) }
                        null
                    }
                    reader.readAsDataURL(file)
                }
                null
            }

            document.body?.appendChild(input)
            input.click()
            input.remove()
        }
    }
}