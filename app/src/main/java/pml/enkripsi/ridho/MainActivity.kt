package pml.enkripsi.ridho

import android.os.Bundle
import android.widget.Toast // Import untuk Toast
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
import androidx.compose.ui.platform.LocalContext // Import untuk LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import pml.enkripsi.ridho.ui.theme.PmlEnkripsiTheme

// Import yang diperlukan untuk AES
import java.security.MessageDigest
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec
import android.util.Base64

class MainActivity : ComponentActivity() {

    /**
     * Membuat SecretKeySpec 128-bit (16 byte) dari string kunci apa pun.
     * Ini menggunakan 16 byte pertama dari hash SHA-256 dari kunci yang diberikan.
     */
    private fun getAESKey(key: String): SecretKeySpec {
        val sha = MessageDigest.getInstance("SHA-256")
        val keyBytes = sha.digest(key.toByteArray(Charsets.UTF_8))
        // Gunakan 128 bit (16 byte) pertama dari hash sebagai kunci
        val truncatedKeyBytes = keyBytes.copyOfRange(0, 16)
        return SecretKeySpec(truncatedKeyBytes, "AES")
    }

    /**
     * Mengenkripsi teks menggunakan AES-128/CBC/PKCS5Padding.
     * IV (Initialization Vector) acak 16 byte dibuat dan
     * ditambahkan di awal ciphertext.
     * Hasilnya di-encode ke Base64.
     */
    private fun encrypt(text: String, key: String): String {
        return try {
            val secretKey = getAESKey(key)
            val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")

            // Buat IV acak 16 byte
            val ivBytes = ByteArray(16)
            SecureRandom().nextBytes(ivBytes)
            val ivSpec = IvParameterSpec(ivBytes)

            cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivSpec)
            val encryptedBytes = cipher.doFinal(text.toByteArray(Charsets.UTF_8))

            // Gabungkan IV dan ciphertext, lalu encode ke Base64
            val combined = ivBytes + encryptedBytes
            Base64.encodeToString(combined, Base64.DEFAULT)
        } catch (e: Exception) {
            e.printStackTrace()
            "Error: ${e.message}"
        }
    }

    /**
     * Mendekripsi teks terenkripsi Base64 (yang berisi IV + ciphertext).
     * Menggunakan AES-128/CBC/PKCS5Padding.
     */
    private fun decrypt(base64EncryptedText: String, key: String): String {
        return try {
            val combined = Base64.decode(base64EncryptedText, Base64.DEFAULT)
            if (combined.size < 16) return "Error: Data korup/salah."

            // Ekstrak IV (16 byte pertama) dan ciphertext (sisanya)
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
            // Error umum jika kunci salah atau data korup (gagal padding)
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
                                containerColor = Color(0xFF03FCE8) // Warna TopAppBar dari file asli
                            )
                        )
                    }
                ) { innerPadding ->
                    // Memanggil Composable baru dengan padding dari Scaffold
                    EncryptionForm(
                        modifier = Modifier.padding(innerPadding),
                        // Teruskan fungsi encrypt/decrypt ke composable
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
    onEncrypt: (text: String, key: String) -> String, // Terima fungsi enkripsi
    onDecrypt: (text: String, key: String) -> String  // Terima fungsi dekripsi
) {
    // State untuk kartu Enkripsi
    var textToEncrypt by remember { mutableStateOf("") }
    var encryptionKeyEncrypt by remember { mutableStateOf("") }
    var encryptedText by remember { mutableStateOf("") }

    // State untuk kartu Dekripsi
    var textToDecrypt by remember { mutableStateOf("") }
    var encryptionKeyDecrypt by remember { mutableStateOf("") }
    var decryptedText by remember { mutableStateOf("") }

    // Dapatkan Context untuk Toast
    val context = LocalContext.current

    // LazyColumn untuk menampung kedua kartu dan memungkinkan scrolling
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp), // Padding horizontal untuk tepi layar
        verticalArrangement = Arrangement.spacedBy(16.dp), // Jarak antar kartu
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Kartu Enkripsi
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp), // Padding di atas kartu pertama
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFBAFC03) // Warna kartu enkripsi
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
                            // <-- PERUBAHAN: Logika validasi kunci 16 karakter
                            if (textToEncrypt.isBlank() || encryptionKeyEncrypt.isBlank()) {
                                encryptedText = "Teks dan Kunci tidak boleh kosong"
                            } else if (encryptionKeyEncrypt.length != 16) {
                                // Tampilkan Toast jika kunci tidak 16 karakter
                                Toast.makeText(context, "Kunci enkripsi harus 16 karakter!", Toast.LENGTH_SHORT).show()
                                encryptedText = "" // Kosongkan hasil jika kunci salah
                            } else {
                                // Panggil logika enkripsi AES 128 bit
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

        // Kartu Dekripsi
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp), // Padding di bawah kartu kedua
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFf07dd7) // Warna kartu dekripsi
                )
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // 1. Text field "masukan teks untuk di dekripsi"
                    OutlinedTextField(
                        value = textToDecrypt,
                        onValueChange = { textToDecrypt = it },
                        label = { Text("masukan teks untuk di dekripsi") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    // 2. Text field "encription key"
                    OutlinedTextField(
                        value = encryptionKeyDecrypt,
                        onValueChange = { encryptionKeyDecrypt = it },
                        label = { Text("encription key") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    // 3. Tombol "dekripsi"
                    Button(
                        onClick = {
                            // <-- PERUBAHAN: Logika validasi kunci 16 karakter
                            if (textToDecrypt.isBlank() || encryptionKeyDecrypt.isBlank()) {
                                decryptedText = "Teks dan Kunci tidak boleh kosong"
                            } else if (encryptionKeyDecrypt.length != 16) {
                                // Tampilkan Toast jika kunci tidak 16 karakter
                                Toast.makeText(context, "Kunci dekripsi harus 16 karakter!", Toast.LENGTH_SHORT).show()
                                decryptedText = "" // Kosongkan hasil jika kunci salah
                            } else {
                                // Panggil logika dekripsi AES 128 bit
                                decryptedText = onDecrypt(textToDecrypt, encryptionKeyDecrypt)
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("dekripsi")
                    }

                    // 4. Text field "text terdekripsi"
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
            // Untuk preview, kita bisa teruskan lambda kosong atau placeholder
            EncryptionForm(
                modifier = Modifier.padding(innerPadding),
                onEncrypt = { text, key -> "Encrypted: $text with $key" },
                onDecrypt = { text, key -> "Decrypted: $text with $key" }
            )
        }
    }
}