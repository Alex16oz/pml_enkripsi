package pml.enkripsi.ridho

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import pml.enkripsi.ridho.ui.theme.PmlEnkripsiTheme

import java.security.MessageDigest
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec
import android.util.Base64

class MainActivity : ComponentActivity() {


    private fun getAESKey(key: String): SecretKeySpec {
        val sha = MessageDigest.getInstance("SHA-256")
        val keyBytes = sha.digest(key.toByteArray(Charsets.UTF_8))

        val truncatedKeyBytes = keyBytes.copyOfRange(0, 16)
        return SecretKeySpec(truncatedKeyBytes, "AES")
    }


    private fun encrypt(text: String, key: String): String {
        return try {
            val secretKey = getAESKey(key)
            val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")


            val ivBytes = ByteArray(16)
            SecureRandom().nextBytes(ivBytes)
            val ivSpec = IvParameterSpec(ivBytes)

            cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivSpec)
            val encryptedBytes = cipher.doFinal(text.toByteArray(Charsets.UTF_8))


            val combined = ivBytes + encryptedBytes
            Base64.encodeToString(combined, Base64.DEFAULT)
        } catch (e: Exception) {
            e.printStackTrace()
            "Error: ${e.message}"
        }
    }


    private fun decrypt(base64EncryptedText: String, key: String): String {
        return try {
            val combined = Base64.decode(base64EncryptedText, Base64.DEFAULT)
            if (combined.size < 16) return "Error: Data korup/salah."


            val ivBytes = combined.copyOfRange(0, 16)
            val encryptedBytes = combined.copyOfRange(16, combined.size)

            val secretKey = getAESKey(key)
            val ivSpec = IvParameterSpec(ivBytes)
            val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")

            cipher.init(Cipher.DECRYPT_MODE, secretKey, ivSpec)
            val decryptedBytes = cipher.doFinal(encryptedBytes)

            String(decryptedBytes, Charsets.UTF_8)
        } catch (e: Exception) {
            e.printStackTrace()

            "Error: Gagal dekripsi (kunci salah atau data korup)."
        }
    }


    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PmlEnkripsiTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        TopAppBar(
                            title = { Text("PML Enkripsi") },
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = Color(0xFF03FCE8)
                            )
                        )
                    }
                ) { innerPadding ->

                    EncryptionForm(
                        modifier = Modifier.padding(innerPadding),

                        onEncrypt = ::encrypt,
                        onDecrypt = ::decrypt
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EncryptionForm(
    modifier: Modifier = Modifier,
    onEncrypt: (text: String, key: String) -> String,
    onDecrypt: (text: String, key: String) -> String
) {

    var textToEncrypt by remember { mutableStateOf("") }
    var encryptionKeyEncrypt by remember { mutableStateOf("") }
    var encryptedText by remember { mutableStateOf("") }


    var textToDecrypt by remember { mutableStateOf("") }
    var encryptionKeyDecrypt by remember { mutableStateOf("") }
    var decryptedText by remember { mutableStateOf("") }


    val context = LocalContext.current


    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFBAFC03)
                )
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = textToEncrypt,
                        onValueChange = { textToEncrypt = it },
                        label = { Text("masukan teks untuk di enkripsi") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = encryptionKeyEncrypt,
                        onValueChange = { encryptionKeyEncrypt = it },
                        label = { Text("encription key") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Button(
                        onClick = {

                            if (textToEncrypt.isBlank() || encryptionKeyEncrypt.isBlank()) {
                                encryptedText = "Teks dan Kunci tidak boleh kosong"
                            } else if (encryptionKeyEncrypt.length != 16) {

                                Toast.makeText(context, "Kunci enkripsi harus 16 karakter!", Toast.LENGTH_SHORT).show()
                                encryptedText = ""
                            } else {

                                encryptedText = onEncrypt(textToEncrypt, encryptionKeyEncrypt)
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("enkripsi")
                    }
                    OutlinedTextField(
                        value = encryptedText,
                        onValueChange = { },
                        label = { Text("text terenkripsi") },
                        readOnly = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }


        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFf07dd7)
                )
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {

                    OutlinedTextField(
                        value = textToDecrypt,
                        onValueChange = { textToDecrypt = it },
                        label = { Text("masukan teks untuk di dekripsi") },
                        modifier = Modifier.fillMaxWidth()
                    )


                    OutlinedTextField(
                        value = encryptionKeyDecrypt,
                        onValueChange = { encryptionKeyDecrypt = it },
                        label = { Text("encription key") },
                        modifier = Modifier.fillMaxWidth()
                    )


                    Button(
                        onClick = {

                            if (textToDecrypt.isBlank() || encryptionKeyDecrypt.isBlank()) {
                                decryptedText = "Teks dan Kunci tidak boleh kosong"
                            } else if (encryptionKeyDecrypt.length != 16) {

                                Toast.makeText(context, "Kunci dekripsi harus 16 karakter!", Toast.LENGTH_SHORT).show()
                                decryptedText = ""
                            } else {

                                decryptedText = onDecrypt(textToDecrypt, encryptionKeyDecrypt)
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("dekripsi")
                    }


                    OutlinedTextField(
                        value = decryptedText,
                        onValueChange = { },
                        label = { Text("text terdekripsi") },
                        readOnly = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun EncryptionFormPreview() {
    PmlEnkripsiTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("PML Enkripsi") },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color(0xFF03FCE8)
                    )
                )
            }
        ) { innerPadding ->

            EncryptionForm(
                modifier = Modifier.padding(innerPadding),
                onEncrypt = { text, key -> "Encrypted: $text with $key" },
                onDecrypt = { text, key -> "Decrypted: $text with $key" }
            )
        }
    }
}