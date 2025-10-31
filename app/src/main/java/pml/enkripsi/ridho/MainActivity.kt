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
    // State untuk kartu Enkripsi
    var textToEncrypt by remember { mutableStateOf("") }
    var encryptionKeyEncrypt by remember { mutableStateOf("") }
    var encryptedText by remember { mutableStateOf("") }

    // State untuk kartu Dekripsi
    var textToDecrypt by remember { mutableStateOf("") }
    var encryptionKeyDecrypt by remember { mutableStateOf("") }
    var decryptedText by remember { mutableStateOf("") }

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
                            encryptedText = "Teks '$textToEncrypt' dienkripsi dengan kunci '$encryptionKeyEncrypt' (logika belum diimplementasi)"
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
                    containerColor = Color(0xFFA103FC) // Warna kartu dekripsi
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
                            // Logika placeholder. Ganti ini dengan logika dekripsi Anda.
                            decryptedText = "Teks '$textToDecrypt' didekripsi dengan kunci '$encryptionKeyDecrypt' (logika belum diimplementasi)"
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
            EncryptionForm(modifier = Modifier.padding(innerPadding))
        }
    }
}