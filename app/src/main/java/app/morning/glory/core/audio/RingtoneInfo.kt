package app.morning.glory.core.audio

import android.net.Uri
import androidx.core.net.toUri
import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter

data class RingtoneInfo(
    val name: String,
    val uri: Uri
)


/**
 * Type adapter for Gson to help adapting Uri instances
 */
class UriTypeAdapter : TypeAdapter<Uri>() {

    /**
     * This tells Gson how to write a Uri object into JSON.
     * We simply write its string representation.
     */
    override fun write(out: JsonWriter, value: Uri?) {
        if (value == null) {
            out.nullValue()
        } else {
            out.value(value.toString())
        }
    }

    /**
     * This tells Gson how to read JSON and create a Uri object from it.
     * We read the string and parse it back into a Uri.
     */
    override fun read(input: JsonReader): Uri? {
        if (input.peek() == JsonToken.NULL) {
            input.nextNull()
            return null
        }
        return input.nextString().toUri()
    }
}