package pml.enkripsi.ridho

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import pml.enkripsi.ridho.ui.theme.PmlEnkripsiTheme

class MainActivity : ComponentActivity() {
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
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EncryptionForm(modifier: Modifier = Modifier) {
    // Variabel state untuk menyimpan nilai dari text fields
    var textToEncrypt by remember { mutableStateOf("") }
    var encryptionKey by remember { mutableStateOf("") }
    var encryptedText by remember { mutableStateOf("") }

    // Column untuk menata kartu di layar
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp), // Padding tambahan untuk tepi layar
        verticalArrangement = Arrangement.Top, // Menempatkan kartu di bagian atas (di bawah TopAppBar)
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(), // Kartu akan mengisi lebar
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFFBAFC03) // Warna kartu yang diminta
            )
        ) {
            // Column untuk konten di dalam kartu
            Column(
                modifier = Modifier
                    .padding(16.dp) // Padding internal untuk konten kartu
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp) // Jarak antar elemen
            ) {
                // 1. Text field "masukan teks untuk si enkripsi"
                OutlinedTextField(
                    value = textToEncrypt,
                    onValueChange = { textToEncrypt = it },
                    label = { Text("masukan teks untuk di enkripsi") },
                    modifier = Modifier.fillMaxWidth()
                )

                // 2. Text field "encription key"
                OutlinedTextField(
                    value = encryptionKey,
                    onValueChange = { encryptionKey = it },
                    label = { Text("encription key") },
                    modifier = Modifier.fillMaxWidth()
                )

                // 3. Tombol "enkripsi"
                Button(
                    onClick = {
                        // Logika placeholder. Ganti ini dengan logika enkripsi Anda.
                        encryptedText = "Teks '$textToEncrypt' dienkripsi dengan kunci '$encryptionKey' (logika belum diimplementasi)"
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("enkripsi")
                }

                // 4. Text field "text ter enkripsi"
                OutlinedTextField(
                    value = encryptedText,
                    onValueChange = { }, // Tidak bisa diubah, hanya-baca
                    label = { Text("text ter enkripsi") },
                    readOnly = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun EncryptionFormPreview() {
    PmlEnkripsiTheme {
        // Preview dengan Scaffold agar terlihat mirip dengan aplikasi
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
            EncryptionForm(modifier = Modifier.padding(innerPadding))
        }
    }
}