package com.meng.fastboot

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import kotlinx.coroutines.launch
import java.io.*

private val AppTypography = Typography(
    displayLarge = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 57.sp),
    displayMedium = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 45.sp),
    displaySmall = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 36.sp),
    headlineLarge = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 32.sp, fontWeight = FontWeight.Bold),
    headlineMedium = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 28.sp, fontWeight = FontWeight.Bold),
    headlineSmall = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 24.sp, fontWeight = FontWeight.Bold),
    titleLarge = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 22.sp),
    titleMedium = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 16.sp, fontWeight = FontWeight.Medium),
    titleSmall = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 14.sp, fontWeight = FontWeight.Medium),
    bodyLarge = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 16.sp),
    bodyMedium = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 14.sp),
    bodySmall = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 12.sp),
    labelLarge = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 14.sp),
    labelMedium = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 12.sp),
    labelSmall = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 11.sp)
)

class MainActivity : ComponentActivity() {

    private lateinit var binDir: File

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binDir = File(filesDir, "bin")
        if (!binDir.exists()) binDir.mkdirs()

        extractAssets()

        setContent {
            val darkTheme = isSystemInDarkTheme()
            val colorScheme = if (darkTheme) {
                darkColorScheme(
                    primary = Color(0xFF1E88E5),
                    secondary = Color(0xFF03DAC5),
                    background = Color(0xFF1A1A1A),
                    surface = Color(0xFF242424),
                    onPrimary = Color.White,
                    onSecondary = Color.Black,
                    onBackground = Color.White,
                    onSurface = Color.White
                )
            } else {
                lightColorScheme(
                    primary = Color(0xFF1E88E5),
                    secondary = Color(0xFF03DAC5),
                    background = Color(0xFFFAFAFA),
                    surface = Color.White,
                    onPrimary = Color.White,
                    onSecondary = Color.Black,
                    onBackground = Color.Black,
                    onSurface = Color.Black
                )
            }

            MaterialTheme(
                colorScheme = colorScheme,
                typography = AppTypography
            ) {
                FastbootAppWithDrawer(binDir)
            }
        }
    }

    private fun extractAssets() {
        val assetManager = assets
        val files = assetManager.list("bin") ?: return
        for (file in files) {
            val outFile = File(binDir, file)
            if (outFile.exists()) outFile.delete()
            assetManager.open("bin/$file").use { input ->
                FileOutputStream(outFile).use { output ->
                    input.copyTo(output)
                }
            }
            outFile.setExecutable(true)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FastbootAppWithDrawer(binDir: File) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val logList = remember { mutableStateListOf<String>() }
    val listState = rememberLazyListState()

    // State untuk dialog konfirmasi format
    var showFormatConfirm by remember { mutableStateOf(false) }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = MaterialTheme.colorScheme.background,
                drawerContentColor = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.fillMaxHeight()
            ) {
                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = ">Meng Pedia",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier.padding(horizontal = 24.dp)
                )

                HorizontalDivider(
                    color = MaterialTheme.colorScheme.outline,
                    thickness = 1.dp,
                    modifier = Modifier.padding(vertical = 16.dp, horizontal = 24.dp)
                )

                Column(
                    modifier = Modifier
                        .padding(horizontal = 24.dp)
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(
                        text = "About",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    val uriHandler = LocalUriHandler.current
                    val githubLink = "https://github.com/juns37/meng-fastboot"

                    val annotatedString = buildAnnotatedString {
                        append("App simpel buat bantu fix HP yang mentok di fastboot/bootloop.\n\n")
                        append("Bisa cek devices, Flash recovery, flash boot/vendor_boot, format data, langsung reboot ke recovery atau system.\n\n")
                        append("Cocok untuk kamu yang suka oprek kapanpun dan dimana saja, Cukup 2 hp, Kabel USB, dan Aplikasi ini.\n\n")
                        append("Made with ♥ by Juni.\n\n")

                        append("Checkout the source code at ")
                        withStyle(
                            style = SpanStyle(
                                color = MaterialTheme.colorScheme.primary,
                                textDecoration = TextDecoration.Underline
                            )
                        ) {
                            append("Github")
                        }
                    }

                    ClickableText(
                        text = annotatedString,
                        style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onBackground),
                        onClick = { offset ->
                            if (offset >= annotatedString.indexOf("Github") && offset <= annotatedString.indexOf("Github") + "Github".length) {
                                uriHandler.openUri(githubLink)
                            }
                        }
                    )

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFF006400)
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lightbulb,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )

                            Column {
                                Text(
                                    text = "NOTE",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold
                                    )
                                )

                                Text(
                                    text = "Taruh boot.img, recovery.img dan vendor_boot.img di /sdcard/fastboot/",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = Color.White
                                    )
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "design by juni",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.align(Alignment.End)
                    )
                }

                NavigationDrawerItem(
                    label = { Text("Close") },
                    selected = false,
                    onClick = { scope.launch { drawerState.close() } },
                    icon = { Icon(Icons.Default.Close, contentDescription = null) },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = ">Meng Fastboot",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        )
                    },
                    actions = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = "Menu",
                                tint = MaterialTheme.colorScheme.onBackground
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background
                    )
                )
            },
            containerColor = MaterialTheme.colorScheme.background
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                createMengButton("Fastboot Device") {
                    Thread { 
                        logList.add("Cek Devices...")
                        runCommands(binDir, listOf("fastboot devices"), logList) 
                    }.start()
                }

                createMengButton(
                    text = "Fix Fastboot",
                    backgroundColor = Color(0xFF43A047) // hijau
                ) {
                    logList.add("Memulai proses Fix Fastboot...")
                    val bootFile = File("/sdcard/fastboot/boot.img")
                    val vendorBootFile = File("/sdcard/fastboot/vendor_boot.img")

                    if (!bootFile.exists() || bootFile.length() == 0L ||
                        !vendorBootFile.exists() || vendorBootFile.length() == 0L
                    ) {
                        logList.add("❌ File boot.img atau vendor_boot.img tidak ditemukan pada folder /sdcard/fastboot/, pastikan namanya sesuai. Proses dihentikan.")
                        return@createMengButton
                    }

                    Thread {
                        runCommands(binDir, listOf(
                            "fastboot flash boot_ab /sdcard/fastboot/boot.img",
                            "fastboot flash vendor_boot_ab /sdcard/fastboot/vendor_boot.img",
                            "fastboot reboot recovery"
                        ), logList)
                    }.start()
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = { Thread { logList.add("Reboot Reco..."); runCommands(binDir, listOf("fastboot reboot recovery"), logList) }.start() },
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_reboot_recovery),
                            contentDescription = "Reboot Recovery",
                            modifier = Modifier.size(40.dp)
                        )
                    }

                    Button(
                        onClick = { Thread { logList.add("Reboot System..."); runCommands(binDir, listOf("fastboot reboot"), logList) }.start() },
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_reboot_system),
                            contentDescription = "Reboot System",
                            modifier = Modifier.size(40.dp)
                        )
                    }
                }
                
                createMengButton(
                    text = "Recovery Flasher"
                ) {
                    logList.add("Reco Flasher...")
                    val recoveryFile = File("/sdcard/fastboot/recovery.img")

                    if (!recoveryFile.exists() || recoveryFile.length() == 0L) {
                        logList.add("❌ recovery.img tidak ditemukan pada folder /sdcard/fastboot/, pastikan namanya sesuai. Proses dihentikan.")
                        return@createMengButton
                    }

                    Thread {
                        runCommands(binDir, listOf(
                            "fastboot flash recovery_ab /sdcard/fastboot/recovery.img",
                            "fastboot reboot recovery"
                        ), logList)
                    }.start()
                }

                createMengButton(
                    text = "Fastboot Format (-w)",
                    backgroundColor = Color(0xFFE53935)
                ) {
                    showFormatConfirm = true
                }

                // Dialog konfirmasi format
                if (showFormatConfirm) {
                    AlertDialog(
                        onDismissRequest = { showFormatConfirm = false },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = "Peringatan",
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(40.dp)
                            )
                        },
                        title = {
                            Text(
                                text = "Konfirmasi Format Data",
                                style = MaterialTheme.typography.titleLarge
                            )
                        },
                        text = {
                            Text(
                                text = "Perintah ini akan menjalankan 'fastboot -w'.\n" +
                                       "Ini akan menghapus SELURUH data di /data dan /cache (factory reset).\n\n" +
                                       "Semua foto, video, aplikasi, akun Google, pengaturan akan hilang permanen!\n\n" +
                                       "Pastikan perangkat terhubung via USB dan dalam mode fastboot.\n\n" +
                                       "Yakin ingin melanjutkan?",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        },
                        confirmButton = {
                            TextButton(
                                onClick = {
                                    showFormatConfirm = false
                                    logList.add("→ Memulai format data (fastboot -w)...")
                                    Thread {
                                        runCommands(binDir, listOf("fastboot -w"), logList)
                                    }.start()
                                }
                            ) {
                                Text(
                                    "Ya, Format!",
                                    color = MaterialTheme.colorScheme.error,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = { showFormatConfirm = false }) {
                                Text("Batal")
                            }
                        }
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(8.dp)
                ) {
                    items(logList) { log ->
                        Text(
                            text = log,
                            color = MaterialTheme.colorScheme.onSurface,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }

                LaunchedEffect(logList.size) {
                    if (logList.isNotEmpty()) {
                        listState.animateScrollToItem(logList.size - 1)
                    }
                }
            }
        }
    }
}

@Composable
fun createMengButton(
    text: String,
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colorScheme.primary,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = backgroundColor,
            contentColor = MaterialTheme.colorScheme.onPrimary
        )
    ) {
        Text(text, style = MaterialTheme.typography.titleMedium)
    }
}

fun runCommands(
    binDir: File,
    commands: List<String>,
    logList: MutableList<String>,
    onOutput: ((String) -> Unit)? = null
) {
    for (cmd in commands) {
        try {
            val fullCmd = if (isRootAvailable()) "su -c 'cd ${binDir.absolutePath}; $cmd'"
            else "cd ${binDir.absolutePath}; $cmd"

            val process = ProcessBuilder("sh", "-c", fullCmd)
                .redirectErrorStream(true)
                .start()

            val reader = BufferedReader(InputStreamReader(process.inputStream))
            var line: String?
            var hasErrorInOutput = false

            while (reader.readLine().also { line = it } != null) {
                val outputLine = line ?: ""
                logList.add(outputLine)
                onOutput?.invoke(outputLine)

                if (outputLine.contains("FAILED", ignoreCase = true) ||
                    outputLine.contains("error", ignoreCase = true) ||
                    outputLine.contains("waiting for device", ignoreCase = true) ||
                    outputLine.contains("no devices found", ignoreCase = true)
                ) {
                    hasErrorInOutput = true
                }
            }

            val exitCode = process.waitFor()

            if (exitCode == 0 && !hasErrorInOutput) {
                logList.add("OK")
            } else {
                logList.add("Gagal (exit code $exitCode)")
            }
        } catch (e: Exception) {
            logList.add("Error menjalankan '$cmd': ${e.message}")
        }
    }
}

fun isRootAvailable(): Boolean {
    return try {
        val process = Runtime.getRuntime().exec("su -c echo rooted")
        val reader = BufferedReader(InputStreamReader(process.inputStream))
        val output = reader.readLine()
        process.waitFor()
        output == "rooted"
    } catch (e: Exception) {
        false
    }
}